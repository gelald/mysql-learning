package com.github.gelald.mpcommon.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author WuYingBin
 * date: 2023/3/16
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {
    FEMALE(0, "女"),
    MALE(1, "男");

    /**
     * 标记存储时使用的时code
     */
    @EnumValue
    private final Integer code;
    private final String sexName;
}
