package com.bbg.generatorid.entity;    /**
 * @Title:
 * @Package
 * @Description:
 * @author xwq
 * @date 2020/4/29 002917:14
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *   基于分段leaf-segment 的优化策略, 使用双Buffer实现。
 *   接收参数
 * @author xwq
 * @create 2020-04-29 17:14
 **/
@ApiModel("获取id、序列 请求参数实体")
public class SegmentRequestEntity {

    /**
     * 算法  1 gene(基因法)    2 说明是用的分段算法 3 使用前缀 + 分段算法
     * 递增序列 当机器停机时 会产生序号不连续，但整体保证递增
     */
    @ApiModelProperty("1、全局序列 2、递增序列 3、前缀拼接递增序列")
    private String algorithm;

    /**
     * 业务标识（系统标志）
     */
    @ApiModelProperty("业务标识（系统标志）,不传默认 XXX")
    private String bizTag;

    /**
     * 前缀
     */
    @ApiModelProperty("前缀,不传默认 XXX")
    private String prefix;

    /**
     * 个数
     */
    @ApiModelProperty("一次获取id的个数")
    private Integer count;

    /**
     * 类型   1.返回为long    2.返回为String
     */
    @ApiModelProperty("返回类型 1.返回为long 2.返回为String")
    private String type;


    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getBizTag() {
        return bizTag;
    }

    public void setBizTag(String bizTag) {
        this.bizTag = bizTag;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
