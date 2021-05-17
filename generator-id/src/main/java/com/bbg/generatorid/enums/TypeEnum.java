package com.bbg.generatorid.enums;

import lombok.Getter;

/**
 * 返回类型 枚举    1.返回为long    2.返回为String
 */
@Getter
public enum TypeEnum {
    LONG("1","返回为long"),
    STR("2","返回为String"),
    ;

    private String code;

    private String message;

    TypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
