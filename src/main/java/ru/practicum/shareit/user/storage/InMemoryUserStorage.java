package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private final Set<String> emails;
    private Long nextUserId = 1L;

    @Override
    public Long add(User user) {
        user.setId(nextUserId);
        emails.add(user.getEmail());
        users.put(nextUserId, user);
        return nextUserId++;
    }

    @Override
    public void update(String oldEmail, String newEmail) {
        emails.remove(oldEmail);
        emails.add(newEmail);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(long id) {
        emails.remove(users.remove(id).getEmail());
    }

    @Override
    public boolean checkEmailExists(String email) {
        return emails.contains(email);
    }

    @Override
    public String getUserEmail(long id) {
        return users.get(id).getEmail();
    }
}
