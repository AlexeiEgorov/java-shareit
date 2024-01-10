package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@Import(PersistenceConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final TestEntityManager em;
    private final ItemRepository repository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findItemsByText() {
        Item item = new Item();
        item.setName("Castle");
        item.setDescription("In the sky");
        item.setAvailable(true);
        Item item2 = new Item();
        item2.setName("Liberty");
        item2.setDescription("Among a garbage");
        item2.setAvailable(true);
        Item item3 = new Item();
        item3.setName("The Toxicity");
        item3.setDescription("Of an actor");
        item3.setAvailable(true);

        assertNull(item.getId());
        repository.save(item);
        repository.save(item2);
        repository.save(item3);
        assertNotNull(item.getId());

        assertThat(repository.findItemsByText("%THE%", PageRequest.of(0, 10)).getContent(),
                equalTo(List.of(item, item3)));
    }
}