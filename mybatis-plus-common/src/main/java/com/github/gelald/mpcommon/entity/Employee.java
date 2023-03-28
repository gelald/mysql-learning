package com.github.gelald.mpcommon.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.gelald.mpcommon.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author WuYingBin
 * date: 2023/3/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mpcommon_employee")
public class Employee {
    /**
     * 主键字段
     */
    @TableId
    private Long id;
    private String userName;
    private Integer age;
    /**
     * 枚举值字段
     */
    private GenderEnum gender;
    /**
     * 自动填充的字段
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
    /**
     * 逻辑删除字段
     * 布尔型字段不使用is开头，所以要配合TableField
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Boolean logicDelete;
}
