package ru.practicum.util.mapper;

import dto.CompilationDto;
import dto.NewCompilationDto;
import dto.UpdateCompilationRequest;
import org.mapstruct.Mapper;
import ru.practicum.model.Compilation;

@Mapper(uses = {EventMapper.class})
public interface CompilationMapper {
    CompilationDto toDto(Compilation compilation);

    default Compilation toCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);

        return compilation;
    }

    default Compilation toCompilation(UpdateCompilationRequest dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);

        return compilation;
    }
}
