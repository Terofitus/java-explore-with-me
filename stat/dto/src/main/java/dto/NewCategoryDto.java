package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class NewCategoryDto {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    String name;
}
