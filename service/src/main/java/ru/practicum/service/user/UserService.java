package ru.practicum.service.user;

import dto.NewUserRequest;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers(List<Integer> ids, Integer from, Integer size);

    User addUser(NewUserRequest newUserRequest);

    void deleteUser(Integer userId);
}
