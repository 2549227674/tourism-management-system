package com.tourism.dto;

import com.tourism.entity.SpotCategory;
import lombok.Data;

@Data
public class CategoryDTO {

    private Long id;
    private String name;
    private Integer sortOrder;
    private String status;

    public static CategoryDTO fromEntity(SpotCategory category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSortOrder(category.getSortOrder());
        dto.setStatus(category.getStatus());
        return dto;
    }
}
