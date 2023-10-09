package ru.practicum.util;

import dto.CategoryDto;
import lombok.experimental.UtilityClass;
import ru.practicum.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category toCategory(CategoryDto dto) {
        return new Category(null, dto.getName());
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
