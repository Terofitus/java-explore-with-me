package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Integer> events;
    private Boolean pinned;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
