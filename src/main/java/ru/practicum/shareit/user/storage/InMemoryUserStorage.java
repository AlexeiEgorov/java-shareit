package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private final Set<String> emails;
    private long nextUserId = 1;
    private final UserMapper mapper;

    @Override
    public UserCreationDto add(UserCreationDto user) {
        user.setId(nextUserId);
        emails.add(user.getEmail());
        users.put(nextUserId++, mapper.toUser(user));
        return user;
    }

    @Override
    public ResponseUserDto update(UserPatchDto patch, long id) {
        User user = users.get(id);
        if (patch.getName() != null && !patch.getName().isBlank()) {
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null  && !patch.getEmail().isBlank()) {
            emails.remove(users.get(id).getEmail());
            user.setEmail(patch.getEmail());
            emails.add(patch.getEmail());
        }
        return mapper.toDto(user);
    }

    @Override
    public Collection<ResponseUserDto> getAll() {
        return users.values().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ResponseUserDto get(long id) {
        return mapper.toDto(users.get(id));
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

    @Override
    public boolean containsUser(long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }
}
