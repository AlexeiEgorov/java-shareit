package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.ValidationService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ValidationService validationService;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public UserCreationDto add(UserCreationDto user) {
        validationService.checkEmailAvailability(user.getEmail());
        UserCreationDto created = userStorage.add(user);
        itemStorage.addUserItems(created.getId());
        return created;
    }

    @Override
    public ResponseUserDto update(UserPatchDto patch, long id) {
        validationService.checkUserRegistration(id);
        validationService.checkUpdatedEmailAvailability(patch, id);
        return userStorage.update(patch, id);
    }

    @Override
    public Collection<ResponseUserDto> getAll() {
        return userStorage.getAll();
    }

    @Override
    public ResponseUserDto get(long id) {
        validationService.checkUserRegistration(id);
        return userStorage.get(id);
    }

    @Override
    public void delete(long id) {
        validationService.checkUserRegistration(id);
        itemStorage.deleteAllUserItems(id);
        userStorage.delete(id);
    }
}
