package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class EndpointHit {
    @Null
    Integer id;
    @NotNull
    String app;
    @NotNull
    String uri;
    @NotNull
    String ip;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
