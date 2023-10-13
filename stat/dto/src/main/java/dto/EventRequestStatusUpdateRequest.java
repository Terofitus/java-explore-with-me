package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private Set<Integer> requestIds;
    private EventRequestStatus status;
}
