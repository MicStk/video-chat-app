package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTopicRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.TopicServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@SpringBootTest
@ActiveProfiles("test")
public class TopicServiceImplTest {

    @Autowired
    TopicServiceImpl topicService;

    @Autowired
    ContainerTopicRepository containerTopicRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    private ContainerTopic containerTopic;

    private ApplicationUser user;

    private Message message;

    @BeforeEach
    public void setUp() {
        containerTopicRepository.deleteAll();
        this.user = TestData.USER();
        this.message = TestData.MESSAGE(user);
        userRepository.save(user);
        this.containerTopic = TestData.CONTAINER_TOPIC(user);
    }

    @AfterEach
    void tearDown() {
        containerTopicRepository.deleteAll();
    }

    @Transactional
    @Test
    void createValidTopicReturnsTopic() throws ValidationException, ConflictException {
        ContainerTopic returnedTopic = topicService.create(
            containerTopic
        );

        assertThat(returnedTopic.getName()).isEqualTo(containerTopic.getName());
        assertThat(returnedTopic.getAuthor()).isEqualTo(containerTopic.getAuthor());
        assertThat(returnedTopic.getTimestamp()).isEqualTo(containerTopic.getTimestamp());
        assertThat(returnedTopic.getPinned()).isEqualTo(containerTopic.getPinned());
        assertThat(returnedTopic.getMessages()).isEqualTo(containerTopic.getMessages());
    }

    @Transactional
    @Test
    void createInvalidTopicThrowsException() {
        ContainerTopic invalid = new ContainerTopic();

        assertThrows(
            ValidationException.class,
            () -> topicService.create(invalid)
        );
    }

    @Transactional
    @Test
    void updateValidTopicReturnsTopic() throws ValidationException, ConflictException {
        containerTopic = containerTopicRepository.save(containerTopic);

        ContainerTopic valid = new ContainerTopic(
            "New Name",
            containerTopic.getAuthor(),
            containerTopic.getTimestamp(),
            containerTopic.getPinned(),
            containerTopic.getMessages()
        );
        ContainerTopic returnedTopic = topicService.update(
            containerTopic.getId(),
            valid
        );

        assertThat(returnedTopic.getName()).isEqualTo("New Name");
        assertThat(returnedTopic.getAuthor()).isEqualTo(containerTopic.getAuthor());
        assertThat(returnedTopic.getTimestamp()).isEqualTo(containerTopic.getTimestamp());
        assertThat(returnedTopic.getPinned()).isEqualTo(containerTopic.getPinned());
        assertThat(returnedTopic.getMessages()).isEqualTo(containerTopic.getMessages());
    }

    @Transactional
    @Test
    void updateInvalidTopicThrowsException() {
        containerTopic = containerTopicRepository.save(containerTopic);

        ContainerTopic invalid = new ContainerTopic();

        assertThrows(
            ValidationException.class,
            () -> topicService.update(containerTopic.getId(), invalid)
        );
    }

    @Transactional
    @Test
    void getTopicByIdGivesTopic() {
        containerTopic = containerTopicRepository.save(containerTopic);

        ContainerTopic returnedTopic = topicService.getById(
            containerTopic.getId()
        );

        assertThat(returnedTopic.getName()).isEqualTo(containerTopic.getName());
        assertThat(returnedTopic.getTimestamp()).isEqualTo(containerTopic.getTimestamp());
        assertThat(returnedTopic.getPinned()).isEqualTo(containerTopic.getPinned());
        assertThat(returnedTopic.getMessages()).isEqualTo(containerTopic.getMessages());
    }

    @Transactional
    @Test
    void getNonExistentTopicByIdThrowsException() {
        assertThrows(
            NotFoundException.class,
            () -> topicService.getById(1000L)
        );
    }

    @Transactional
    @Test
    void getAllTopicsGivesAllTopic() {
        userRepository.save(user);
        ContainerTopic ct0 = TestData.CONTAINER_TOPIC(user);
        ContainerTopic ct1 = TestData.CONTAINER_TOPIC(user);
        ContainerTopic ct2 = TestData.CONTAINER_TOPIC(user);
        ContainerTopic ct3 = TestData.CONTAINER_TOPIC(user);

        ct0.setId(null);
        ct0 = containerTopicRepository.save(ct0);
        ct1.setId(null);
        ct1 = containerTopicRepository.save(ct1);
        ct2.setId(null);
        ct2 = containerTopicRepository.save(ct2);
        ct3.setId(null);
        ct3 = containerTopicRepository.save(ct3);

        List<ContainerTopic> all = topicService.getAll();

        assertThat(all.size()).isEqualTo(4);
        assertThat(ct0.getId()).isEqualTo(all.get(0).getId());
        assertThat(ct1.getId()).isEqualTo(all.get(1).getId());
        assertThat(ct2.getId()).isEqualTo(all.get(2).getId());
        assertThat(ct3.getId()).isEqualTo(all.get(3).getId());
    }

    @Transactional
    @Test
    void deleteTopicByIdDeletesTopic() {
        containerTopic = containerTopicRepository.save(containerTopic);
        message.setContainer(containerTopic);
        messageRepository.save(message);
        topicService.deleteTopic(containerTopic.getId());

        assertThrows(NotFoundException.class,
            () -> topicService.getById(containerTopic.getId()));
    }
}
