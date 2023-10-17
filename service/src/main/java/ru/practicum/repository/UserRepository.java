package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, QuerydslPredicateExecutor<User> {
    @Query(value = "select count(el.*) from Events_Likes el join Events e on el.event_id=e.id " +
            "where e.initiator_id=?1", nativeQuery = true)
    Integer countOwnerEventLikes(Integer userId);
}
