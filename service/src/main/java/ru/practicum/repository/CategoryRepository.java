package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, QuerydslPredicateExecutor<Category> {
    List<Category> findAllBy(Pageable pageable);

    @Query("select count(c) from Category c join Event e on c.id=e.category.id where c.id=?1 and e.category.id=?1")
    Integer countCategoriesByIdRelatedEvents(Integer categoriesId);
}
