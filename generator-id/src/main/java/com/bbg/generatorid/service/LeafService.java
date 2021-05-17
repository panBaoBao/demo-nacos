package com.bbg.generatorid.service;

import com.bbg.generatorid.entity.IdSegmentEntity;
import com.bbg.generatorid.entity.SegmentRequestEntity;

import java.util.List;

/**
 * @author xwq
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/28 002821:58
 */
public interface LeafService extends IdWorkerService{

    Long getIdLongOne(String bizTag,String prefix,String oldPrefix);

    List<Long> getIdLongList(SegmentRequestEntity entity,String oldPrefix);

    String getIdStringOne(SegmentRequestEntity entity,String oldPrefix);

    List<String> getIdStringList(SegmentRequestEntity entity,String oldPrefix);

}
