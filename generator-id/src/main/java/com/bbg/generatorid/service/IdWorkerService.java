package com.bbg.generatorid.service;

import com.bbg.generatorid.entity.SegmentRequestEntity;

/**
 * 获取id顶层入口
 */
public interface IdWorkerService {
    Object getId(SegmentRequestEntity entity);
}
