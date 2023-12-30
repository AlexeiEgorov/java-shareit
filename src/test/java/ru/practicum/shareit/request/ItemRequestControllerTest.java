package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestControllerTest {
    private final ItemRequestController controller;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestService service;
    private final User user;
    private final User user2;

    @Autowired
    public ItemRequestControllerTest(ItemRequestController controller, UserRepository userRepository,
                                     ItemRepository itemRepository, ItemRequestService service) {
        this.controller = controller;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.service = service;
        this.user = new User();
        user.setName("Carrol");
        user.setEmail("lightbringer@nargothrond.uk");
        this.user2 = new User();
        user2.setName("Michael");
        user2.setEmail("dontsteponme@nargothrond.uk");
    }

    @Test
    void add() {

    }

    @Test
    void getAllNonOwnedRequests() {
        User created = userRepository.save(user);
        User created2 = userRepository.save(user2);
        ItemRequestDto req = new ItemRequestDto();
        req.setDescription("Green sea-dragon egg");
        ItemRequestResponseDto resp = controller.add(created2.getId(), req);
        Item item = new Item();
        item.setOwner(created);
        item.setRequest(service.get(created2.getId(), resp.getId()));
        item.setName("Green sea-dragon egg with red fern patterns");
        Item createdItem = itemRepository.save(item);
        resp.setItems(List.of(new ItemForRequestDto(createdItem.getId(), item.getName(), null, null,
                resp.getId())));
        assertThat(controller.getAllNonOwnedRequests(created.getId(), 0, 10), equalTo(List.of(resp)));
    }

    @Test
    void getUserRequests() {
        User created = userRepository.save(user);
        User created2 = userRepository.save(user2);
        ItemRequestDto req = new ItemRequestDto();
        req.setDescription("Green sea-dragon egg");
        ItemRequestResponseDto resp = controller.add(created2.getId(), req);
        Item item = new Item();
        item.setOwner(created);
        item.setRequest(service.get(created2.getId(), resp.getId()));
        item.setName("Green sea-dragon egg with red fern patterns");
        Item createdItem = itemRepository.save(item);
        resp.setItems(List.of(new ItemForRequestDto(createdItem.getId(), item.getName(), null, null,
                resp.getId())));
        assertThat(controller.getUserRequests(created2.getId()), equalTo(List.of(resp)));
    }
}