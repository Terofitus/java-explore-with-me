package ru.practicum.service.compilation;

import ru.practicum.model.Compilation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CompilationService {
    List<Compilation> getCompilations(boolean pinned, Integer from, Integer size, HttpServletRequest request);

    Compilation getCompilationById(Integer compId, HttpServletRequest request);
}
