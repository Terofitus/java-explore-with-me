package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer>, QuerydslPredicateExecutor<Event> {
    List<Category> findListAll(Pageable pageable);
}