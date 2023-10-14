package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request,Integer>, QuerydslPredicateExecutor<Request> {
    boolean existsRequestByRequesterIdAndEventId(Integer userId, Integer eventId);

    List<Request> findAllByRequesterIdAndEventId(Integer userId, Integer eventId);
}
