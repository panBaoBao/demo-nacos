package com.bbg.generatorid.leaf;    /**
 * @Title:
 * @Package
 * @Description:
 * @author xwq
 * @date 2020/4/28 002816:18
 */

/**
 *  枚举
 * @author xwq
 * @create 2020-04-28 16:18
 **/
public enum IdSegmentEnums {

    STEP(100L,"步长"),
    MAXID(0L,"最大值id")
    ;

    private Long code;

    private String message;

    IdSegmentEnums(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
