package ru.practicum.service;

import ru.practicum.model.Hit;

import java.util.List;

public interface StatService {
    void addHit(Hit hit);

    List<Hit> getStats(String start, String end, List<String> uris);
}
