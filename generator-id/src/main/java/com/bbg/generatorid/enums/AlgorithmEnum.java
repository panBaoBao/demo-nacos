package com.bbg.generatorid.enums;

import lombok.Getter;

/**
 * 算法枚举
 */
@Getter
public enum AlgorithmEnum {
    GENE("1","默认的基因法"),
    LEAF("2","分段算法"),
    PREFIX_LEAF("3","前缀拼接分段算法串"),
    ;

    private String code;

    private String message;

    AlgorithmEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
