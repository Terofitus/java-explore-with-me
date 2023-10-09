package ru.practicum.util;

import dto.UserDto;
import dto.UserShortDto;
import lombok.experimental.UtilityClass;
import ru.practicum.model.User;

@UtilityClass
public class UserMapper {
    User toUser(UserDto dto) {
        return new User(null, dto.getEmail(), dto.getName());
    }

    UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
