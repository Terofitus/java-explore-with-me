package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserShortDto {
    @NotNull
    private Integer id;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
