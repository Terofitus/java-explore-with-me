package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.client.Client;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.util.PageableCreator;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final Client statClient;


    @Override
    public List<Compilation> getCompilations(boolean pinned, Integer from, Integer size, HttpServletRequest request) {
        Pageable pageable = PageableCreator.toPageable(from, size, null);
        statClient.addHit(request);
        log.info("Запрошены подборки событий с позиции {}", from);
        if (pinned) {
            return compilationRepository.findAllByPinned(true, pageable);
        } else {
            return (List<Compilation>) compilationRepository.findAll(pageable);
        }
    }

    @Override
    public Compilation getCompilationById(Integer compId, HttpServletRequest request) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        statClient.addHit(request);
        if (compilation.isPresent()) {
            log.info("Запрошена подборка с id={}", compId);
            return compilation.get();
        } else {
            log.warn("Запрошена подборка с несуществующим id={}", compId);
            throw new NotFoundException(String.format("Не найдено ни одной подборки с id=%d", compId));
        }
    }
}
