package ru.practicum.util.mapper;

import dto.ParticipationRequestDto;
import lombok.experimental.UtilityClass;
import ru.practicum.model.Request;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toDto(Request request) {
        if (request == null) {
            return null;
        }

        ParticipationRequestDto dto = new ParticipationRequestDto();

        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setCreated(request.getCreated());
        dto.setEvent(request.getEvent().getId());
        dto.setStatus(request.getStatus());

        return dto;
    }
}