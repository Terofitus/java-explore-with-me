package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public void addHit(Hit hit) {
        statRepository.save(hit);
    }

    @Override
    public List<Hit> getStats(String start, String end, List<String> uris) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (start == null || end == null) {
            throw new IllegalArgumentException("Запрос должен содержать \"start\" и \"end\" параметры");
        } else if (LocalDateTime.parse(start, formatter)
                .isAfter(LocalDateTime.parse(end, formatter))) {
            throw new IllegalArgumentException("\"Start\" не может быть позже \"end\"");
        }
        if (uris.isEmpty()) return Collections.emptyList();
        return statRepository.getStats(start, end, uris);
    }
}
