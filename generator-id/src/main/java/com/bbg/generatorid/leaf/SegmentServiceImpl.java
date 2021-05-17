package com.bbg.generatorid.leaf;


import com.bbg.generatorid.constant.BizConstant;
import com.bbg.generatorid.dao.IdSegmentDao;
import com.bbg.generatorid.entity.IdSegmentEntity;
import com.bbg.generatorid.util.NamingThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @类名称 SegmentServiceImpl.java
 * @类描述 <pre>Segment 策略id生成实现类</pre>
 * @作者 庄梦蝶殇 linhuaichuan1989@126.com
 * @创建时间 2018年9月6日 下午4:28:36
 * @版本 1.0.2
 *
 * @修改记录
 * <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.0.0    庄梦蝶殇    2018年09月06日             
 *     1.0.1    庄梦蝶殇    2019年02月28日             更改阈值碰撞时重复执行问题
 *     1.0.2    庄梦蝶殇    2019年03月06日             突破并发数被step限制的bug
 *     ----------------------------------------------
 * </pre>
 */
@Slf4j
public class SegmentServiceImpl implements ISegmentService {

    private static IdSegmentDao idSegmentDao;


    /**
     * 线程名-心跳
     */
    public static final String THREAD_BUFFER_NAME = "leaf_buffer_sw";

    private static ReentrantLock lock = new ReentrantLock();

    /**
     * 创建线程池
     */
    private static ExecutorService taskExecutor;

    /**
     * 两段buffer
     */
    private volatile IdSegment[] segment = new IdSegment[2];

    /**
     * 缓冲切换标识(true-切换，false-不切换)
     */
    private volatile boolean sw;

    /**
     * 当前id
     */
    private AtomicLong currentId;


    /**
     * 业务标识
     */
    private static String bizTag = BizConstant.DEFAULT_TAG;

    /**
     * 业务标识
     */
    private static String prefix = BizConstant.DEFAULT_TAG;;

    /**
     * 异步标识(true-异步，false-同步)
     */
    private static boolean asynLoadingSegment = true;

    /**
     * 异步线程任务
     */
    static FutureTask<Boolean> asynLoadSegmentTask = null;

    /**
     * 读写锁
     */
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock wLock = readWriteLock.writeLock();

    public SegmentServiceImpl(IdSegmentDao idSegmentDao) {
        this.idSegmentDao = idSegmentDao;
        if (taskExecutor == null) {
            taskExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory(THREAD_BUFFER_NAME));
        }

    }

    @Override
    public Long getId(String bizTag,String prefix) {

        if(null == currentId){
            //第一次获取序列 或者 系统重启 刷新号段
            setSw(false);
            // 获取第一段buffer缓冲
            segment[0] = doUpdateNextSegment(bizTag,prefix);
            if(null == segment[0]){
                log.info("segment[0] is null");
            }
            if(null == segment[index()].getMinId()){
                log.info("segment[index()].getMinId() is null");
            }
            currentId = new AtomicLong(segment[index()].getMinId());
        }
        // 下一个id
        Long nextId = null;
        //这里会产生并发
        if (segment[index()].getMiddleId().equals(currentId.longValue()) || segment[index()].getMaxId().equals(currentId.longValue())) {
            try {
                //上锁 排队
                lock.lock();
                if(segment[index()].getMiddleId().equals(currentId.longValue()) || segment[index()].getMaxId().equals(currentId.longValue())){
                    // 阈值50%时，加载下一个buffer
                    if (segment[index()].getMiddleId().equals(currentId.longValue())) {
                        thresholdHandler();
                        nextId = currentId.incrementAndGet();
                    } else if (segment[index()].getMaxId().equals(currentId.longValue())) {//等于最大值切换
                        fullHandler();
                        // 进行切换
                        currentId = new AtomicLong(segment[index()].getMinId());
                        nextId = currentId.incrementAndGet();
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        nextId = null == nextId ? currentId.incrementAndGet() : nextId;
        if (nextId > segment[index()].getMaxId()){
            log.info("leaf wait ,nextId:{}, cMaxId:{},index:{}",nextId,segment[index()].getMaxId(),index());
            lock.lock();
            try {
                while(nextId > segment[index()].getMaxId()){
                    log.info("leaf try to change 1-1 index:{},maxId:{}",index(),segment[index()].getMaxId());
                    //先切换下
                    setSw(!isSw());
                    if(null == segment[index()]){
                        //强制缓存号段
                        segment[index()] = doUpdateNextSegment(bizTag,prefix);
                    }
                    log.info("leaf try to change 1-2 index:{},maxId:{}",index(),segment[index()].getMaxId());
                    if(nextId <= segment[index()].getMaxId()){
                        log.info("leaf try to change 1-3 index:{},maxId:{}",index(),segment[index()].getMaxId());
                        return nextId;
                    }else {
                        //切换还是不行  重新获取
                        setSw(false);
                        // 获取第一段buffer缓冲
                        segment[0] = doUpdateNextSegment(bizTag,prefix);
                        if(nextId <= segment[index()].getMaxId()){
                            log.info("leaf try to change 2-1 index:{},maxId:{}",index(),segment[index()].getMaxId());
                            return nextId;
                        }
                        log.info("leaf try to change roll-1 index:{},maxId:{}",index(),segment[index()].getMaxId());
                        //切回来，这个切换回来 是对应  change 1-1 那一步的
                        setSw(!isSw());
                        if(null == segment[index()]){
                            //强制缓存号段
                            segment[index()] = doUpdateNextSegment(bizTag,prefix);
                        }
                        log.info("leaf try to change roll-2 index:{},maxId:{}",index(),segment[index()].getMaxId());
                    }

                }
            }finally {
                lock.unlock();
            }
        }
        return nextId;
    }

    /**
     * 阈值处理，是否同/异步加载下一个buffer的值(即更新DB)
     */
    private void thresholdHandler() {
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
    public void fullHandler() {
        if (asynLoadingSegment) {
            // 异步时，需判断 异步线程的状态(是否已经执行)
            try {
                Boolean flag = asynLoadSegmentTask.get();
                log.info("fullHandler   flag:{}",flag);
            } catch (Exception e) {
                log.error("fullHandler->ex",e);
                log.error("fullHandler force reIndex");
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

        IdSegment currentSegment = new IdSegment();

        IdSegmentEntity dto = new IdSegmentEntity();
        dto.setBizTag(bizTag);
        dto.setPrefix(prefix);
        dto = idSegmentDao.findOne(dto);

        if(dto != null){
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
                log.info("leaf bizTag:{},prefix:{},change once",bizTag,prefix);
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


    @Override
    public void setBizTag(String bizTag) {
        this.bizTag = bizTag;
    }

    public void setAsynLoadingSegment(boolean asynLoadingSegment) {
        this.asynLoadingSegment = asynLoadingSegment;
    }
}
