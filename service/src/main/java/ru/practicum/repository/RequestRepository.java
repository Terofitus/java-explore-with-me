package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer>, QuerydslPredicateExecutor<Request> {
    boolean existsRequestByRequesterIdAndEventId(Integer userId, Integer eventId);

    @Query("select r from Request r where r.event.id=?1 and r.event.initiator.id=?2")
    List<Request> findAllRequestsForOwnerOfEvent(Integer eventId, Integer userId);

    boolean existsRequestByEventIdAndEventInitiatorId(Integer eventId, Integer userId);
}
