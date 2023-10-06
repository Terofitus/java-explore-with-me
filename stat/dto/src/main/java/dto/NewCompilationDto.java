package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class NewCompilationDto {
    @NotNull
    List<Integer> events;
    Boolean pinned;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    String title;
}
