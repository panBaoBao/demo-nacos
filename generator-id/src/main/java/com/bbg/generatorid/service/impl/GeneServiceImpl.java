package com.bbg.generatorid.service.impl;

import com.bbg.generatorid.annotation.Algorithm;
import com.bbg.generatorid.constant.BizConstant;
import com.bbg.generatorid.entity.SegmentRequestEntity;
import com.bbg.generatorid.enums.TypeEnum;
import com.bbg.generatorid.gene.UidContext;
import com.bbg.generatorid.service.GeneService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局序列生成器
 */
@Service
@Algorithm(type = BizConstant.GENE)
public class GeneServiceImpl implements GeneService {

    private UidContext context = UidContext.getInstance();

    @Override
    public Object getId(SegmentRequestEntity entity) {

        if(TypeEnum.LONG.getCode().equals(entity.getType())){//  1 ----- 说明返回的是long
            if(null != entity.getCount() && entity.getCount()>1){//说明返回的是list
                return getIdLongList(entity);
            }else{
                return getIdLongOne(entity);
            }
        }else{ //说明返回的是字符串
            if(null != entity.getCount() && entity.getCount()>1){ //说明返回的是list
                return getIdStringList(entity);
            }else{
                return getIdStringOne(entity);
            }
        }
    }

    /**
     * 获取一个long类型的id
     *
     * @param entity
     * @return
     */
    @Override
    public Long getIdLongOne(SegmentRequestEntity entity) {

        return context.getUID(entity.getBizTag());
    }

    /**
     * 获取多个long类型的id
     *
     * @param entity
     * @return
     */
    @Override
    public List<Long> getIdLongList(SegmentRequestEntity entity) {
        List<Long> list = new ArrayList<>();
        int i = 0;
        while (i < entity.getCount()){
            ++i;
            list.add(context.getUID(entity.getBizTag()));
        }
        return list;
    }

    /**
     * 获取一个String类型的id
     * 默认前缀为业务
     * @param entity
     * @return
     */
    @Override
    public String getIdStringOne(SegmentRequestEntity entity) {
        return  "" + context.getUID(entity.getBizTag());
    }

    @Override
    public List<String> getIdStringList(SegmentRequestEntity entity) {
        List<String> list = new ArrayList<>();
        int i = 0;
        while (i < entity.getCount()){
            ++i;
            list.add("" + context.getUID(entity.getBizTag()));
        }
        return list;
    }
}
