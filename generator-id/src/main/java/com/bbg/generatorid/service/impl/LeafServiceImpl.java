package com.bbg.generatorid.service.impl;

import com.bbg.generatorid.annotation.Algorithm;
import com.bbg.generatorid.constant.BizConstant;
import com.bbg.generatorid.dao.IdSegmentDao;
import com.bbg.generatorid.entity.IdSegmentEntity;
import com.bbg.generatorid.entity.SegmentRequestEntity;
import com.bbg.generatorid.enums.TypeEnum;
import com.bbg.generatorid.leaf.SegmentServiceImpl;
import com.bbg.generatorid.service.LeafService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 分段序列生成器
 */
@Service
@Slf4j
@Algorithm(type = BizConstant.LEAF)
public class LeafServiceImpl implements LeafService {

    @Autowired
    private IdSegmentDao idSegmentDao;

    private SegmentServiceImpl segmentServiceImpl;


    @Override
    public Object getId(SegmentRequestEntity entity) {
        IdSegmentEntity idSegmentEntity = new IdSegmentEntity();
        idSegmentEntity.setBizTag(entity.getBizTag());
        IdSegmentEntity segmentEntity =   idSegmentDao.findOne(idSegmentEntity);
        if(segmentEntity != null){
            if(!segmentEntity.getBizTag().equals(entity.getBizTag())){
                segmentEntity.setMaxId(0l);
            }
            idSegmentDao.updateEntity(segmentEntity);
        }
        if(TypeEnum.LONG.getCode().equals(entity.getType())){
            if( null != entity.getCount() && entity.getCount()>1){//说明返回的是list
                return getIdLongList(entity,null);
            }else{
                return getIdLongOne(entity.getBizTag(),entity.getPrefix(),null);
            }
        }else{ //说明返回的是字符串
            if( null != entity.getCount() &&  entity.getCount()>1){ //说明返回的是list
                return getIdStringList(entity,null);
            }else{
                return getIdStringOne(entity,null);
            }
        }
    }

    /**
     * 获取一个long类型的有序id
     * @param bizTag
     * @return
     */
    @Override
    public Long getIdLongOne(String bizTag,String prefix,String oldPrefix) {
        segmentServiceImpl = new SegmentServiceImpl(idSegmentDao);
        return segmentServiceImpl.getId(bizTag,prefix);
    }

    /**
     * 获取多个Long类型的有序id
     * @param entity
     * @return
     */
    @Override
    public List<Long> getIdLongList(SegmentRequestEntity entity,String oldPrefix) {
        segmentServiceImpl = new SegmentServiceImpl(idSegmentDao);

        List<Long> list = new ArrayList<>(entity.getCount());
        int i = 0;
        while (i < entity.getCount()){
            ++i;
            list.add(segmentServiceImpl.getId(entity.getBizTag(),entity.getPrefix()));
        }

        return list;
    }

    /**
     * 获取一个String类型的有序id
     * 默认前缀为业务
     * @param entity
     * @return
     */
    public String getIdStringOne(SegmentRequestEntity entity,String oldPrefix) {

        segmentServiceImpl = new SegmentServiceImpl(idSegmentDao);

        long id = segmentServiceImpl.getId(entity.getBizTag(),entity.getPrefix());

        return id + "";
    }

    /**
     * 获取多个String类型的有序id
     *  默认前缀为业务
     * @param entity
     * @return
     */
    @Override
    public List<String> getIdStringList(SegmentRequestEntity entity,String oldPrefix) {
        segmentServiceImpl = new SegmentServiceImpl(idSegmentDao);

        List<String> list = new ArrayList<>(entity.getCount());
        int i = 0;
        while (i < entity.getCount()){
            ++i;
            list.add("" + segmentServiceImpl.getId(entity.getBizTag(),entity.getPrefix()));
        }

        return list;
    }

}
