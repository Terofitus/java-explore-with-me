package ru.practicum.util.mapper;

import dto.NewUserRequest;
import dto.UserDto;
import dto.UserShortDto;
import lombok.experimental.UtilityClass;
import ru.practicum.model.User;

@UtilityClass
public class UserMapper {
    public User toUser(UserDto dto) {
        return new User(null, dto.getEmail(), dto.getName());
    }

    public User toUser(NewUserRequest dto) {
        return new User(null, dto.getEmail(), dto.getName());
    }

    public UserDto toUserDto(User user, Integer rating) {
        return new UserDto(user.getId(), user.getEmail(), user.getName(), rating);
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
