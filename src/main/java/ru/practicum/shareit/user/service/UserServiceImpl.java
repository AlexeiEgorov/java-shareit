package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Set;

import static ru.practicum.shareit.Constants.USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public User patch(UserDto user, Long id) {
        User patched = get(id);
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            patched.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            patched.setName(user.getName());
        }
        return repository.save(patched);
    }

    @Override
    public Collection<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User get(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(USER, id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        get(id);
        repository.deleteById(id);
    }

    @Override
    public Collection<BookerDto> findBookers(Set<Long> usersIds) {
        return repository.findAllByIdIn(usersIds);
    }
}
