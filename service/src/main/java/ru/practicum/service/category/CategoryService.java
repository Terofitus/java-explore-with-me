package ru.practicum.service.category;

import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Integer id);
}
