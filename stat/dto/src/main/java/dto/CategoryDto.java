package dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
public class CategoryDto {
    @Null
    Integer id;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    String name;
}
