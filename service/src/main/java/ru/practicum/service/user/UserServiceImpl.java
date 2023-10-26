package ru.practicum.service.user;

import dto.NewUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.mapper.UserMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers(List<Integer> ids, Integer from, Integer size) {
        if (ids != null) {
            log.info("Запрос пользователей с id=" + ids);
            return userRepository.findAllById(ids);
        } else {
            log.info("Запрос всех пользователей");
            return userRepository.findAll(PageableCreator.toPageable(from, size, null)).getContent();
        }
    }

    @Override
    public User addUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        try {
            User u = userRepository.save(user);
            log.info("Добавление нового пользователя " + u);
            return u;
        } catch (DataIntegrityViolationException e) {
            log.warn("Нарушение уникальности имени или email при добавлении нового пользователя " + user);
            throw new ConflictArgumentException("Имя или email нового пользователя заняты");
        }
    }

    @Transactional
    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Попытка удаления несуществующего пользователя с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
        userRepository.deleteById(userId);
        log.info("Удален пользователь с id=" + userId);
    }

    @Override
    public Integer getUserRating(Integer userId) {
        return userRepository.countOwnerEventLikes(userId);
    }
}
