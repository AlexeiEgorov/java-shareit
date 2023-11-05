package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Long add(User user);

    void update(String oldEmail, String newEmail);

    Collection<User> getAll();

    void delete(long id);

    Optional<User> get(long id);

    boolean checkEmailExists(String email);

    String getUserEmail(long id);
}
