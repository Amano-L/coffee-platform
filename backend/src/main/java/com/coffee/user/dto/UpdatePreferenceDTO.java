package com.coffee.user.dto;

import lombok.Data;
import java.util.List;

/**
 * 保存咖啡偏好入参
 */
@Data
public class UpdatePreferenceDTO {

    // 烘焙度
    private String roastLevel;

    // 风味标签列表
    private List<String> flavorTags;

    // 处理法
    private String processMethod;
}