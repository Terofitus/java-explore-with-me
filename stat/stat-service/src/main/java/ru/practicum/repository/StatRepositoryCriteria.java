package ru.practicum.repository;

import ru.practicum.model.Hit;

import java.util.List;

public interface StatRepositoryCriteria {
    List<Hit> getStats(String start, String end, List<String> uris);
}
