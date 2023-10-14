package ru.practicum.service.compilation;

import dto.NewCompilationDto;
import dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CompilationService {
    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size, HttpServletRequest request);

    Compilation getCompilationById(Integer compId, HttpServletRequest request);

    Compilation addCompilation(NewCompilationDto dto);

    void deleteCompilation(Integer id);

    Compilation updateCompilation(UpdateCompilationRequest dto, Integer id);
}
