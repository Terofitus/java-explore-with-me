package ru.practicum.controller;

import dto.EndpointHit;
import dto.ViewStats;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatService;
import ru.practicum.util.StatMapper;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping(path = "/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addHit(@RequestBody @Valid EndpointHit hit) {
        statService.addHit(StatMapper.toHit(hit));
    }

    @GetMapping(path = "/stats")
    public List<ViewStats> getStats(@RequestParam String start, @RequestParam String end,
                                    @RequestParam(required = false) Boolean unique,
                                    @RequestParam(required = false) List<String> uris) {

        return StatMapper.toListView(statService.getStats(start, end, uris), unique);
    }
}
