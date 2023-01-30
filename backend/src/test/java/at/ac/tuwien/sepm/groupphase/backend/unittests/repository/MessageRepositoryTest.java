package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTopicRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest implements TestData {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContainerTopicRepository containerTopicRepository;

    private ApplicationUser user;
    private Message message;
    private ContainerTopic containerTopic;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        messageRepository.deleteAll();
        containerTopicRepository.deleteAll();
        this.user = TestData.USER();
        this.message = TestData.MESSAGE(user);
        this.containerTopic = TestData.CONTAINER_TOPIC(user);
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        containerTopicRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenSaveMessage_thenFindListWithOneElementAndFindMessageById() {
        userRepository.save(user);
        containerTopicRepository.save(containerTopic);
        message.setContainer(containerTopic);
        messageRepository.save(message);
        Message getMessage = messageRepository.getReferenceById(message.getId());
        assertAll(
            () -> assertEquals(1, messageRepository.findAll().size()),
            () -> assertNotNull(messageRepository.findById(message.getId())),
            () -> assertEquals(getMessage.getContainer(), containerTopic)
        );
    }

    @Test
    public void emptyRepoFindAllMessagesReturnsEmptyList() {
        List<Message> foundMessages = messageRepository.findAll();
        assertThat(foundMessages.isEmpty()).isTrue();
    }

    @Test
    public void getMessagesByValidUserReturnListOfMessages() {
        userRepository.save(user);
        ApplicationUser user2 = new ApplicationUser();
        user2.setFirstName("Jana");
        user2.setLastName("Doe");
        user2.setEmail("jana.doe@mail.com");
        user2.setPassword("myPassword123");
        user2.setRole(Role.EMPLOYEE);
        userRepository.save(user2);

        Message message2 = Message.MessageBuilder.aMessage()
            .withUser(user)
            .withTimestamp(LocalDateTime.parse("2022-02-01T12:00:00.00"))
            .withContent("Test123")
            .withContainer(null)
            .build();
        Message message3 = Message.MessageBuilder.aMessage()
            .withUser(user2)
            .withTimestamp(LocalDateTime.parse("2022-10-05T15:00:00.00"))
            .withContent("This is a cool message")
            .withContainer(null)
            .build();

        messageRepository.save(message);
        messageRepository.save(message2);
        messageRepository.save(message3);

        List<Message> allMessages = messageRepository.findAll();
        assertThat(allMessages.size()).isEqualTo(3);

        List<Message> messagesByUser = messageRepository.findAllByUser(user);
        assertThat(messagesByUser.size()).isEqualTo(2);

        List<Message> messagesByUser2 = messageRepository.findAllByUser(user2);
        assertThat(messagesByUser2.size()).isEqualTo(1);
    }
}
