package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    List<Integer> requestIds;
    EventRequestStatus status;
}
