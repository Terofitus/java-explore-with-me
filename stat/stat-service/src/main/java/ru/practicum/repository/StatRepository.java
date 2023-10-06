package ru.practicum.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

import java.util.List;

@Repository
public interface StatRepository extends CrudRepository<Hit, Integer>, StatRepositoryCriteria {
    List<Hit> getStats(String start, String end, List<String> uris);
}
