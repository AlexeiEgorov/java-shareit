package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final UserMapper mapper;

    @Override
    public Long add(UserDto user) {
        checkEmailAvailability(user.getEmail());
        Long id = userStorage.add(mapper.toUser(user));
        itemStorage.addUserItems(id);
        return id;
    }

    @Override
    public UserDto update(UserDto patch, Long id) {
        User user = get(id);
        if (patch.getName() != null && !patch.getName().isBlank()) {
            user.setName(patch.getName());
        } else {
            patch.setName(user.getName());
        }
        if (patch.getEmail() != null  && !patch.getEmail().isBlank()) {
            checkUpdatedEmailAvailability(patch, id);
            userStorage.update(user.getEmail(), patch.getEmail());
            user.setEmail(patch.getEmail());
        } else {
            patch.setEmail(user.getEmail());
        }
        patch.setId(user.getId());
        return patch;
    }

    @Override
    public Collection<ResponseUserDto> getAll() {
        return userStorage.getAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public User get(Long id) {
        return userStorage.get(id).orElseThrow(() -> new EntityNotFoundException(USER, id));
    }

    @Override
    public void delete(Long id) {
        get(id);
        itemStorage.deleteAllUserItems(id);
        userStorage.delete(id);
    }

    public void checkEmailAvailability(@Email String email) {
        if (userStorage.checkEmailExists(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }

    public void checkUpdatedEmailAvailability(UserDto patch, Long userId) {
        if (patch.getEmail() != null) {
            if (userStorage.checkEmailExists(patch.getEmail())) {
                if (!userStorage.getUserEmail(userId).equals(patch.getEmail())) {
                    throw new EmailAlreadyRegisteredException(patch.getEmail());
                }
            }
        }
    }
}
