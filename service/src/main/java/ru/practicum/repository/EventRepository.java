package ru.practicum.repository;

import dto.EventState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiatorId(Integer id, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "insert into Events_Likes (event_id, user_id) values (?1, ?2)", nativeQuery = true)
    void addLike(Integer eventId, Integer userId);

    @Transactional
    @Modifying
    @Query(value = "delete from Events_Likes e where e.event_id=?1 and e.user_id=?2", nativeQuery = true)
    void deleteLike(Integer eventId, Integer userId);

    @Query(value = "select count(user_id) from Events_Likes el where el.event_id=?1", nativeQuery = true)
    Integer countEventLikes(Integer eventId);

    boolean existsByIdAndStateEnum(Integer eventId, EventState state);
}
