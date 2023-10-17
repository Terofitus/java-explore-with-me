package ru.practicum.util.mapper;

import dto.CategoryDto;
import dto.NewCategoryDto;
import lombok.experimental.UtilityClass;
import ru.practicum.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category toCategory(NewCategoryDto dto, Integer id) {
        return new Category(id, dto.getName());
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
