package ru.practicum.service.compilation;

import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationService {
    List<Compilation> getCompilations(boolean pinned, Integer from, Integer size);

    Compilation getCompilationById(Integer compId);
}
