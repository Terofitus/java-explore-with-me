package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    List<EventShortDto> events;
    Integer id;
    Boolean pinned;
    String title;
}
