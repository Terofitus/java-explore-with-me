package ru.practicum.service.compilation;

import dto.NewCompilationDto;
import dto.UpdateCompilationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.event.EventService;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.mapper.CompilationMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventService eventService;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;


    @Transactional
    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size, HttpServletRequest request) {
        Pageable pageable = PageableCreator.toPageable(from, size, null);

        List<Compilation> compilations;
        if (pinned != null && pinned) {
            compilations = compilationRepository.findAllByPinned(true, pageable);
        } else {
            compilations = compilationRepository.findCompilationsBy(pageable);
        }
        log.info("Запрошены подборки событий с позиции {}", from);
        List<Event> events = compilations.stream().flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toList());
        eventService.addHitsToEvents(events);

        return compilations;
    }

    @Transactional
    @Override
    public Compilation getCompilationById(Integer compId, HttpServletRequest request) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isPresent()) {
            log.info("Запрошена подборка с id={}", compId);
            eventService.addHitsToEvents(new ArrayList<>(compilation.get().getEvents()));
            return compilation.get();
        } else {
            log.warn("Запрошена подборка с несуществующим id={}", compId);
            throw new NotFoundException(String.format("Не найдено ни одной подборки с id=%d", compId));
        }
    }

    @Override
    public Compilation addCompilation(NewCompilationDto dto) {
        try {
            Compilation compilationFromDto = mapper.toCompilation(dto);
            List<Event> events = new ArrayList<>();
            if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
                events = eventRepository.findAllById(dto.getEvents());
                if (events.isEmpty()) {
                    throw new NotFoundException("По переданным id не найдено ни одного события");
                }
            }
            compilationFromDto.setEvents(events);
            Compilation compilation = compilationRepository.save(compilationFromDto);
            log.info("Добавлена новая подборка " + compilation);
            return compilation;
        } catch (ConstraintViolationException e) {
            log.warn("Нарушение уникальности названия подборки, title=" + dto.getTitle());
            throw new ConflictArgumentException("Подборка с таким названием уже существует");
        }
    }

    @Override
    public void deleteCompilation(Integer id) {
        if (compilationRepository.existsById(id)) {
            compilationRepository.deleteById(id);
            log.info("Удалена подборка событий с id={}", id);
        } else {
            log.warn("Попытка удаления несуществующей подборки с id={}", id);
            throw new NotFoundException("Подборка событий с id=" + id + " не найдена");
        }
    }

    @Transactional
    @Override
    public Compilation updateCompilation(UpdateCompilationRequest dto, Integer id) {
        Optional<Compilation> comp = compilationRepository.findById(id);
        if (comp.isPresent()) {
            Compilation compilation = comp.get();
            prepareCompilationForUpdate(compilation, dto);
            compilation = compilationRepository.save(compilation);
            log.info("Обновлена подборка с id={}", id);
            return compilation;
        } else {
            log.warn("Попытка обновления несуществующей подборки с id={}", id);
            throw new NotFoundException("Подборка событий с id=" + id + " не найдена");
        }
    }

    private void prepareCompilationForUpdate(Compilation compilation, UpdateCompilationRequest dto) {
        List<Integer> events = dto.getEvents();
        Boolean pinned = dto.getPinned();
        String title = dto.getTitle();

        if (title != null && !title.isBlank()) {
            compilation.setTitle(title);
        }

        if (pinned != null) {
            compilation.setPinned(pinned);
        }

        if (events != null && !events.isEmpty()) {
            List<Event> eventList = eventRepository.findAllById(events);
            if (eventList.isEmpty()) {
                throw new NotFoundException("Не найдено событий по переданным id=" + events);
            }
            compilation.setEvents(eventList);
        }
    }
}
