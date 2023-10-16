package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        if (start == null || end == null) {
            throw new IllegalArgumentException("Запрос должен содержать \"start\" и \"end\" параметры");
        }
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime;
        LocalDateTime endTime;
        if(start.length() > 23) start = start.substring(0, 23);
        if(end.length() > 23) end = end.substring(0, 23);
        try {
            startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter1);
            endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter1);
        } catch (DateTimeParseException e) {
            startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter2);
            endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter2);
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("\"Start\" не может быть позже \"end\"");
        } else {
            return statRepository.getStats(startTime, endTime, uris);
        }
    }
}
