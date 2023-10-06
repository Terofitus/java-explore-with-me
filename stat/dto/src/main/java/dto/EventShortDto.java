package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Integer id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Integer views;
}
