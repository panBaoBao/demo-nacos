package com.wl.entity;

import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 区域表
 * </p>
 *
 * @author ptm
 * @since 2021-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysArea implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId("ID")
    private String id;

    /**
     * 父级编号
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 完整编号(所有父级编号+自己的编号)
     */
    @TableField("FULL_IDS")
    private String fullIds;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 短名称
     */
    @TableField("SHORT_NAME")
    private String shortName;

    /**
     * 排序
     */
    @TableField("SORT")
    private Integer sort;

    /**
     * 区域编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 区域类型
     */
    @TableField("TYPE")
    private String type;

    /**
     * 经度
     */
    @TableField("LNG")
    private String lng;

    /**
     * 纬度
     */
    @TableField("LAT")
    private String lat;

    /**
     * 创建者
     */
    @TableField("CREATOR")
    private String creator;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDate createTime;

    /**
     * 更新者
     */
    @TableField("MODIFIER")
    private String modifier;

    /**
     * 更新时间
     */
    @TableField("MODIFY_TIME")
    private LocalDate modifyTime;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 删除标记:0=否，1=是
     */
    @TableField("DEL_FLAG")
    private Integer delFlag;


}
