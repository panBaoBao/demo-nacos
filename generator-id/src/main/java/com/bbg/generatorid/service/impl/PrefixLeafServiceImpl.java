package com.bbg.generatorid.service.impl;

import com.bbg.generatorid.annotation.Algorithm;
import com.bbg.generatorid.constant.BizConstant;
import com.bbg.generatorid.dao.IdSegmentDao;
import com.bbg.generatorid.entity.IdSegmentEntity;
import com.bbg.generatorid.entity.SegmentRequestEntity;
import com.bbg.generatorid.leaf.IdSegment;
import com.bbg.generatorid.leaf.IdSegmentEnums;
import com.bbg.generatorid.service.PrefixLeafService;
import com.bbg.generatorid.util.NamingThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
@Algorithm(type = BizConstant.PREFIX_LEAF)
public class PrefixLeafServiceImpl implements PrefixLeafService {
    @Autowired
    private IdSegmentDao idSegmentDao;

    private PrefixLeafSegment prefixLeafSegment = new PrefixLeafSegment();

    @Override
    public Object getId(SegmentRequestEntity entity) {
        List<String> list = new ArrayList<>();
        String bizTag = entity.getBizTag();
        String prefix = entity.getPrefix();
        String pre = bizTag + prefix;
        if(entity.getCount() > 0){
            int i = 0;
            while (i < entity.getCount()){
                ++i;
                list.add(pre + prefixLeafSegment.getId(entity.getBizTag(),entity.getPrefix()));
            }
            return list;
        }
        return pre + prefixLeafSegment.getId(entity.getBizTag(),entity.getPrefix());
    }


    private class PrefixLeafSegment{

        /**
         * 线程名-心跳
         */
        public final String THREAD_BUFFER_NAME = "prefix_leaf_buffer_sw";

        private  ReentrantLock lock = new ReentrantLock();

        /**
         * 创建线程池
         */
        private ExecutorService taskExecutor;



        /**
         * 缓冲切换标识(true-切换，false-不切换)
         */
        private volatile boolean sw;

        /**
         * 异步标识(true-异步，false-同步)
         */
        private boolean asynLoadingSegment = true;

        /**
         * 异步线程任务
         */
        FutureTask<Boolean> asynLoadSegmentTask = null;

        /**
         * 读写锁
         */
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        /**
         * 存放对应前缀的号段
         */
        private final ConcurrentHashMap<String,IdSegment[]> mapIdSegment = new ConcurrentHashMap(1 << 10);
        /**
         * 存放对应前缀的序列号
         */
        private final ConcurrentHashMap<String,AtomicLong> mapAtomicLong = new ConcurrentHashMap(1 << 10 );

        private final Lock wLock = readWriteLock.writeLock();

        public PrefixLeafSegment() {
            if (taskExecutor == null) {
                taskExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory(THREAD_BUFFER_NAME));
            }

        }

        public Long getId(String bizTag,String prefix) {
            String key = bizTag + prefix;
            AtomicLong currentId ;
            //两段buffer
            IdSegment[] segment;
            if(!mapIdSegment.containsKey(key)) {
                //系统重启 或者 新增加进来的 bizTag 和 prefix
                //会存在并发修改
                lock.lock();
                try {
                    if(!mapIdSegment.containsKey(key)){
                        segment = new IdSegment[2];
                        setSw(false);
                        // 获取第一段buffer缓冲
                        segment[0] = doUpdateNextSegment(bizTag,prefix);
                        // 初始id
                        currentId = new AtomicLong(segment[index()].getMinId());
                        // 保存起来
                        mapIdSegment.put(key,segment);
                        mapAtomicLong.put(key,currentId);
                    }
                }finally {
                 lock.unlock();
                }
            }
            currentId = mapAtomicLong.get(key);
            //两段buffer
            segment = mapIdSegment.get(key);
            // 1.0.1 fix:uid:ecp-190227001 #1(github)更改阈值(middle与max)lock在高速碰撞时的可能多次执行
            // 下一个id
            Long nextId = null;
            if (segment[index()].getMiddleId().equals(currentId.longValue()) || segment[index()].getMaxId().equals(currentId.longValue())) {
                lock.lock();
                try {
                    //上锁 排队
                    if(segment[index()].getMiddleId().equals(currentId.longValue()) || segment[index()].getMaxId().equals(currentId.longValue())){
                        // 阈值50%时，加载下一个buffer
                        if (segment[index()].getMiddleId().equals(currentId.longValue())) {
                            thresholdHandler(segment,bizTag,prefix);
                            nextId = currentId.incrementAndGet();
                        }
                        if (segment[index()].getMaxId().equals(currentId.longValue())) {
                            fullHandler(segment,bizTag,prefix);
                            nextId = currentId.incrementAndGet();

                        }
                    }
                }finally {
                    lock.unlock();
                }
            }
            nextId = null == nextId ? currentId.incrementAndGet() : nextId;
            //并发导致越界了，锁起来，直到数据库的号段更新完毕
            if (nextId > segment[index()].getMaxId()){
                log.info("prefix leaf wait ,nextId:{}, cMaxId:{},index:{}",nextId,segment[index()].getMaxId(),index());
                log.info("prefix leaf segment  -> hashCode:{}",segment.hashCode());
                lock.lock();
                try {
                    while(nextId > segment[index()].getMaxId()){
                        log.info("prefix leaf try to change 1-1 index:{},maxId:{}",index(),segment[index()].getMaxId());
                        //先切换下
                        setSw(!isSw());
                        if(null == segment[index()]){
                            //强制缓存号段
                            segment[index()] = doUpdateNextSegment(bizTag,prefix);
                        }
                        log.info("prefix leaf try to change 1-2 index:{},maxId:{}",index(),segment[index()].getMaxId());
                        if(nextId <= segment[index()].getMaxId()){
                            log.info("prefix leaf try to change 1-3 index:{},maxId:{}",index(),segment[index()].getMaxId());
                            return nextId;
                        }else {
                            //切换还是不行  重新获取
                            setSw(false);
                            // 获取第一段buffer缓冲
                            segment[0] = doUpdateNextSegment(bizTag,prefix);
                            if(nextId <= segment[index()].getMaxId()){
                                log.info("prefix leaf try to change 2-1 index:{},maxId:{}",index(),segment[index()].getMaxId());
                                return nextId;
                            }
                            log.info("prefix leaf try to change roll-1 index:{},maxId:{}",index(),segment[index()].getMaxId());
                            //切回来
                            setSw(!isSw());
                            if(null == segment[index()]){
                                //强制缓存号段
                                segment[index()] = doUpdateNextSegment(bizTag,prefix);
                            }
                            log.info("prefix leaf try to change roll-2 index:{},maxId:{}",index(),segment[index()].getMaxId());
                        }

                    }
                }finally {
                    lock.unlock();
                }
            }
            // 1.0.2 fix:uid:ecp-190306001 突破并发数被step限制的bug
            return nextId ;
        }

        /**
         * 阈值处理，是否同/异步加载下一个buffer的值(即更新DB)
         */
        private void thresholdHandler(IdSegment[] segment,String bizTag,String prefix) {
            if (asynLoadingSegment) {
                // 异步处理-启动线程更新DB，由线程池执行
                asynLoadSegmentTask = new FutureTask<>(new Callable<Boolean>() {
                    @Override
                    public Boolean call()
                            throws Exception {
                        final int currentIndex = reIndex();
                        segment[currentIndex] = doUpdateNextSegment(bizTag,prefix);
                        return true;
                    }
                });
                taskExecutor.submit(asynLoadSegmentTask);
            } else {
                // 同步处理，直接更新DB
                final int currentIndex = reIndex();
                segment[currentIndex] = doUpdateNextSegment(bizTag,prefix);
            }
        }

        /**
         * buffer使用完时切换buffer。
         */
        public void fullHandler(IdSegment[] segment,String bizTag,String prefix) {
            if (asynLoadingSegment) {
                // 异步时，需判断 异步线程的状态(是否已经执行)
                try {
                    asynLoadSegmentTask.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 未执行，强制同步切换
                    segment[reIndex()] = doUpdateNextSegment(bizTag,prefix);
                }
            }
            // 设置切换标识
            setSw(!isSw());
        }

        /**
         * 获取下一个buffer的索引
         */
        private int reIndex() {
            return isSw() ? 0 : 1;
        }

        /**
         * 获取当前buffer的索引
         */
        private int index() {
            return isSw() ? 1 : 0;
        }

        /**
         * @方法名称 doUpdateNextSegment
         * @功能描述 <pre>更新下一个buffer</pre>
         * @param bizTag 业务标识
         * @return 下一个buffer的分段id实体
         */
        private IdSegment doUpdateNextSegment(String bizTag,String prefix) {
            try {
                return updateId(bizTag,prefix);
            } catch (Exception e) {
                log.error("doUpdateNextSegment ex bizTag:{},prefix:{}",bizTag,prefix);
                log.error("doUpdateNextSegment->ex",e);
            }
            return null;
        }


        /**
         * 肖文庆  重写
         *   bug  bizTag 在数据中不存在的时候报错
         * @param bizTag
         * @return
         * @throws Exception
         */
        private IdSegment updateId(String bizTag,String prefix) throws Exception {

            final IdSegment currentSegment = new IdSegment();

            IdSegmentEntity dto = new IdSegmentEntity();
            dto.setBizTag(bizTag);
            dto.setPrefix(prefix);
            dto = idSegmentDao.findOne(dto);

            if(dto != null ){
                currentSegment.setStep(dto.getStep());
                currentSegment.setMaxId(dto.getMaxId());
                currentSegment.setLastUpdateTime(dto.getLastUpdateTime() != null ?dto.getLastUpdateTime() : new Date());
                currentSegment.setCurrentUpdateTime(dto.getCurrentUpdateTime() != null ?dto.getCurrentUpdateTime() : new Date());
            }else{
                //需要根据bizTag去新增一条数据
                dto = new IdSegmentEntity();
                dto.setBizTag(bizTag);
                dto.setStep(IdSegmentEnums.STEP.getCode());//步长
                dto.setMaxId(IdSegmentEnums.MAXID.getCode());//最大值
                dto.setLastUpdateTime(new Date());
                dto.setCurrentUpdateTime(new Date());
                dto.setPrefix(prefix);
                wLock.lock();
                try {
                    idSegmentDao.insert(dto);//新增
                } finally {
                    wLock.unlock();
                }

                return updateId(bizTag,prefix); // 递归
            }
            Long newMaxId = currentSegment.getMaxId() + currentSegment.getStep();
            dto.setMaxId(newMaxId);
            dto.setLastUpdateTime(currentSegment.getLastUpdateTime());
            dto.setCurrentUpdateTime(currentSegment.getCurrentUpdateTime());

            if(dto.getMaxId()!= currentSegment.getMaxId()){
                wLock.lock();

                int row = 0;

                try {
                    row = idSegmentDao.update(dto,currentSegment.getMaxId());
                } finally {
                    wLock.unlock();
                }
                if (row == 1) {
                    log.info("prefix leaf bizTag:{},prefix:{},change once",bizTag,prefix);
                    IdSegment newSegment = new IdSegment();
                    newSegment.setStep(currentSegment.getStep());
                    newSegment.setMaxId(newMaxId);
                    return newSegment;
                }
            }
            // 递归，直至更新成功
            return updateId(bizTag,prefix);
        }

        public void setTaskExecutor(ExecutorService taskExecutor) {
            this.taskExecutor = taskExecutor;
        }

        private boolean isSw() {
            return sw;
        }

        private void setSw(boolean sw) {
            this.sw = sw;
        }



        public void setAsynLoadingSegment(boolean asynLoadingSegment) {
            this.asynLoadingSegment = asynLoadingSegment;
        }

    }
}
