package ru.practicum.controller;

import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.model_attribute.AdminEventSearchParam;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.service.event.EventServiceAdmin;
import ru.practicum.service.event.EventServiceUser;
import ru.practicum.service.user.UserService;
import ru.practicum.util.mapper.CategoryMapper;
import ru.practicum.util.mapper.CompilationMapper;
import ru.practicum.util.mapper.EventMapper;
import ru.practicum.util.mapper.UserMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminController {
    private final CategoryService categoryService;
    private final EventServiceAdmin eventServiceAdmin;
    private final EventServiceUser eventServiceUser;
    private final UserService userService;
    private final CompilationService compilationService;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        return CategoryMapper.toCategoryDto(categoryService.addCategory(categoryDto));
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Integer catId, @Valid @RequestBody NewCategoryDto category) {
        return CategoryMapper.toCategoryDto(categoryService.updateCategory(catId, category));
    }

    @GetMapping("/events")
    public List<EventFullDto> eventsSearch(@ModelAttribute AdminEventSearchParam params) {
        return eventServiceAdmin.eventSearch(params).stream()
                .map(event -> eventMapper.toDto(event, eventServiceUser.getLikesForEvent(event.getId())))
                .sorted((o1, o2) -> o2.getRating() - o1.getRating())
                .collect(Collectors.toList());
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Integer eventId,
                                    @Valid @RequestBody(required = false) UpdateEventAdminRequest requestBody) {
        return eventMapper.toDto(eventServiceAdmin.updateEvent(eventId, requestBody),
                eventServiceUser.getLikesForEvent(eventId));
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size).stream()
                .map(user -> UserMapper.toUserDto(user, userService.getUserRating(user.getId())))
                .sorted((o1, o2) -> o2.getRating() - o1.getRating())
                .collect(Collectors.toList());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return UserMapper.toUserDto(userService.addUser(newUserRequest), 0);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto dto) {
        return compilationMapper.toDto(compilationService.addCompilation(dto));
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@Valid @RequestBody UpdateCompilationRequest dto,
                                            @PathVariable Integer compId) {
        return compilationMapper.toDto(compilationService.updateCompilation(dto, compId));
    }
}
