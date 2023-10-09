package ru.practicum.util;

import dto.LocationDto;
import lombok.experimental.UtilityClass;
import ru.practicum.model.Location;

@UtilityClass
public class LocationMapper {
    Location toLocation(LocationDto locationDto) {
        return new Location(null, locationDto.getLat(), locationDto.getLon());
    }

    LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}
