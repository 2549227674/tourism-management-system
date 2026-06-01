package com.tourism.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("spot_category")
public class SpotCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer sortOrder;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
