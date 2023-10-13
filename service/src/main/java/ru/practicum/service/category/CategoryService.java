package ru.practicum.service.category;

import ru.practicum.model.Category;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CategoryService {
    List<Category> getCategories(Integer from, Integer size, HttpServletRequest request);

    Category getCategoryById(Integer id, HttpServletRequest request);
}
