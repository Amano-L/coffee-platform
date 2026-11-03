package com.coffee.catalog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 咖啡商品实体
 */
@Data
@TableName("coffee_product")
public class CoffeeProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String origin;
    private String roastLevel;
    private String flavorTags;
    private String processMethod;
    private String description;
    private String coverUrl;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}