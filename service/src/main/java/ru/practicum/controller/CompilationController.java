package ru.practicum.controller;

import dto.CompilationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.util.CompilationMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CompilationController {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) boolean pinned,
                                                @RequestParam(required = false) int from,
                                                @RequestParam(required = false, defaultValue = "10") int size) {
        return compilationService.getCompilations(pinned, from, size).stream().
                map(compilationMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable int compId) {
        return compilationMapper.toDto(compilationService.getCompilationById(compId));
    }
}
