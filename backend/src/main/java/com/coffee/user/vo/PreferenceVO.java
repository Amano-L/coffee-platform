package com.coffee.user.vo;

import lombok.Data;
import java.util.List;

/**
 * 咖啡偏好出参
 */
@Data
public class PreferenceVO {

    // 烘焙度
    private String roastLevel;

    // 风味标签列表
    private List<String> flavorTags;

    // 处理法
    private String processMethod;
}