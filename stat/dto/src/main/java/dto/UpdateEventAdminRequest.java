package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    private Integer category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto locationDto;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private ActionStateAdmin stateAction;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
