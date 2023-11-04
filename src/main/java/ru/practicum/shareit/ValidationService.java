package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.constraints.Email;

import static ru.practicum.shareit.Constants.ITEM;
import static ru.practicum.shareit.Constants.USER;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public void checkEmailAvailability(@Email String email) {
        if (userStorage.checkEmailExists(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }

    public void checkUpdatedEmailAvailability(UserPatchDto patch, long userId) {
        if (patch.getEmail() != null) {
            if (userStorage.checkEmailExists(patch.getEmail())) {
                if (!userStorage.getUserEmail(userId).equals(patch.getEmail())) {
                    throw new EmailAlreadyRegisteredException(patch.getEmail());
                }
            }
        }
    }

    public void checkUserRegistration(long id) {
        if (!userStorage.containsUser(id)) {
            throw new EntityNotFoundException(USER, id);
        }
    }

    public void checkUserHasItem(long ownerId, long id) {
        if (!itemStorage.checkUserHasItem(ownerId, id)) {
            throw new EntityNotFoundException(ITEM, id);
        }
    }

    public void checkItemRegistration(long id) {
        if (!itemStorage.containsItem(id)) {
            throw new EntityNotFoundException(ITEM, id);
        }
    }
}
