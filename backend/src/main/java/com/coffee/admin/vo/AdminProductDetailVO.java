package com.coffee.admin.vo;

import com.coffee.catalog.vo.SkuVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台商品详情出参，与前台不同：包含下架商品、状态与创建时间。
 */
@Data
public class AdminProductDetailVO {
    private Long id;
    private String name;
    private String origin;
    private String roastLevel;
    private List<String> flavorTags;
    private String processMethod;
    private String description;
    private String coverUrl;
    private Integer status;
    private LocalDateTime createTime;
    private List<SkuVO> skus;
}
