package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.util.PageableCreator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        Pageable pageable = PageableCreator.toPageable(from, size, null);
        log.info("Запрошены категории с позиции {}", from);
        return categoryRepository.findListAll(pageable);
    }

    @Override
    public Category getCategoryById(Integer id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            log.info("Запрошена категория с id={}", id);
            return category.get();
        } else {
            log.warn("Запрошена категория с несуществующим id={}", id);
            throw new NotFoundException(String.format("Не найдено ни одной категории с id=%d", id));
        }
    }
}
