package ru.practicum.service.category;

import dto.NewCategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.Client;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.mapper.CategoryMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final Client statClient;
    @Override
    public List<Category> getCategories(Integer from, Integer size, HttpServletRequest request) {
        Pageable pageable = PageableCreator.toPageable(from, size, null);
        statClient.addHit(request);
        log.info("Запрошены категории с позиции {}", from);
        return categoryRepository.findAllBy(pageable);
    }

    @Override
    public Category getCategoryById(Integer id, HttpServletRequest request) {
        Optional<Category> category = categoryRepository.findById(id);
        if (request != null) {
            statClient.addHit(request);
        }
        if (category.isPresent()) {
            log.info("Запрошена категория с id={}", id);
            return category.get();
        } else {
            log.warn("Запрошена категория с несуществующим id={}", id);
            throw new NotFoundException(String.format("Не найдено ни одной категории с id=%d", id));
        }
    }

    @Override
    public Category addCategory(NewCategoryDto dto) {
        Category category = categoryRepository.save(new Category(null, dto.getName()));
        log.info("Добавлена новая категория:" + category);
        return category;
    }

    @Transactional
    @Override
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            log.warn("Попытка удаления несуществующей категории с id={}", id);
            throw new NotFoundException("Не существует категории с id=" + id);
        } else if (categoryRepository.countCategoriesByIdRelatedEvents(id) > 0) {
            log.warn("Попытка удаления категории с относящимися к ней существующими событиями");
            throw new ConflictArgumentException("Существуют события относящиеся к категории с id=" + id);
        } else {
            categoryRepository.deleteById(id);
            log.info("Удалена категория с id={}", id);
        }
    }

    @Transactional
    @Override
    public Category updateCategory(Integer id, NewCategoryDto dto) {
        Category category = CategoryMapper.toCategory(dto, id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Попытка обновления несуществующей категории с id=" + id);
            throw new NotFoundException("Не существует категории с id=" + id);
        }
        try {
            category =  categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.warn("Попытка обновления имени категории с id=" + id + " на уже существующее");
            throw new ConflictArgumentException("Изменение имени категории на '" + dto.getName() + "' " +
                    "невозможно, так как оно уже занято");
        }
        return category;
    }
}
