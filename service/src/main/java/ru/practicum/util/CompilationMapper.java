package ru.practicum.util;

import dto.CompilationDto;
import dto.NewCompilationDto;
import org.mapstruct.Mapper;
import ru.practicum.model.Compilation;
import ru.practicum.util.event.EventMapper;

@Mapper(uses = {EventMapper.class})
public interface
CompilationMapper {
    CompilationDto toDto(Compilation compilation);

    default Compilation toCompilation(NewCompilationDto dto) {
        if (dto == null) {
            return null;
        }

        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);

        return compilation;
    }
}
