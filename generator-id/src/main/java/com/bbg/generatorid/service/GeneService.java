package com.bbg.generatorid.service;

import com.bbg.generatorid.entity.SegmentRequestEntity;

import java.util.List;

/**
 * @author xwq
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/30 003015:51
 */
public interface GeneService extends IdWorkerService{

    Long getIdLongOne(SegmentRequestEntity entity);

    List<Long> getIdLongList(SegmentRequestEntity entity);

    String getIdStringOne(SegmentRequestEntity entity);

    List<String> getIdStringList(SegmentRequestEntity entity);

}
