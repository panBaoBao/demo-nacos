package com.bbg.generatorid.dao;    /**
 * @Title:
 * @Package
 * @Description:
 * @author xwq
 * @date 2020/4/28 002815:49
 */


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbg.generatorid.entity.IdSegmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 *  基于分段leaf-segment 的优化策略, 使用双Buffer实现 dao层
 * @author xwq
 * @create 2020-04-28 15:49
 **/
@Mapper
@Repository
public interface IdSegmentDao extends BaseMapper<IdSegmentEntity> {

    /**
     * 查询单条
     *
     */
    IdSegmentEntity findOne(@Param("entity") IdSegmentEntity entity);

    /**
     * 新增
     * @return
     */
    int insert(@Param("entity") IdSegmentEntity entity);

    /**
     * 修改
     * @return
     */
    int update(@Param("entity") IdSegmentEntity entity, @Param("maxId")Long maxId);

    /**
     * 修改
     * @return
     */
    int updateEntity(@Param("entity") IdSegmentEntity entity);

}
