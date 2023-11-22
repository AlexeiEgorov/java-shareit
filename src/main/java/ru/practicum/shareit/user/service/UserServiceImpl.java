package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

import static ru.practicum.shareit.Constants.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public Long add(User user) {
        checkEmailAvailability(user.getEmail());
        return userStorage.add(user);
    }

    @Override
    public User update(UserDto patch, Long id) {
        User user = get(id);
        if (patch.getName() != null && !patch.getName().isBlank()) {
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null  && !patch.getEmail().isBlank()) {
            checkUpdatedEmailAvailability(patch, id);
            userStorage.update(user.getEmail(), patch.getEmail());
            user.setEmail(patch.getEmail());
        }
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
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

    public void checkEmailAvailability(String email) {
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
