package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateEventUserRequest {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    Integer category;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    ActionStateUser stateAction;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}
