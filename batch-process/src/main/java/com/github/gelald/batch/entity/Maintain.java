package com.github.gelald.batch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "batch_maintain")
public class Maintain implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer maintainId;

    private String maintainNum;

    private String maintainName;

    private Long equipmentNum;

    private Integer maintainType;

    private String functionary;

    private String maintainDuration;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean maintainStatus;

    public Maintain(String maintainNum, String maintainName, Long equipmentNum, Integer maintainType, String functionary, String maintainDuration, LocalDateTime startTime, LocalDateTime endTime, Boolean maintainStatus) {
        this.maintainNum = maintainNum;
        this.maintainName = maintainName;
        this.equipmentNum = equipmentNum;
        this.maintainType = maintainType;
        this.functionary = functionary;
        this.maintainDuration = maintainDuration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maintainStatus = maintainStatus;
    }
}
