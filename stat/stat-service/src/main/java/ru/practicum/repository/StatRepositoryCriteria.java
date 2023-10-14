package ru.practicum.repository;

import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepositoryCriteria {
    List<Hit> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
