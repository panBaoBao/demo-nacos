package com.bbg.generatorid.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class IdSegmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 业务
     */
    @TableField("BIZ_TAG")
    private String bizTag;


    /**
     * 步长
     */
    @TableField("STEP")
    private Long step;

    /**
     * 最大id
     */
    @TableField("MAX_ID")
    private Long maxId ;

    @TableField("PREFIX")
    private String prefix;

    /**
     * 上次更新时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @TableField("LAST_UPDATE_TIME")
    private Date lastUpdateTime;

    /**
     * 本次更新时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @TableField("CURRENT_UPDATE_TIME")
    private Date currentUpdateTime;

}
