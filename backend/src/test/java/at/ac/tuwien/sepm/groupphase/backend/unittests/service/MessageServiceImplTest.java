package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceImplTest implements TestData {
    @Autowired
    private SimpleMessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser user;
    private Message message;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
        this.user = TestData.USER();
        this.message = TestData.MESSAGE(user);
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createValidMessageAndReturnValidMessage() throws ValidationException, ConflictException {
        userRepository.save(user);
        Message createdMessage = messageService.publishMessage(message);

        assertThat (createdMessage).isNotNull();
        assertThat (createdMessage.getId()).isNotNull();
        assertThat (createdMessage.getUser()).isEqualTo(message.getUser());
        assertThat (createdMessage.getTimestamp()).isEqualTo(message.getTimestamp());
        assertThat (createdMessage.getContent()).isEqualTo(message.getContent());
    }

    @Test
    void createMessageWithoutUserThrowsValidationException() {
        userRepository.save(user);
        message.setUser(null);

        assertThrows(
            ValidationException.class, () -> messageService.publishMessage(message)
        );
    }

    @Test
    void createMessageWithInvalidUserThrowsConflictException() {
        userRepository.save(user);
        message.getUser().setId(-1L);

        assertThrows(
            ConflictException.class, () -> messageService.publishMessage(message)
        );
    }

    @Test
    void getMessageByValidId() throws ValidationException, ConflictException {
        userRepository.save(user);
        Message createdMessage = messageService.publishMessage(message);
        assertThat (createdMessage).isNotNull();
        assertThat (createdMessage.getId()).isNotNull();

        Message receivedMessage = messageService.findOne(createdMessage.getId());
        assertThat (receivedMessage).isNotNull();
        assertThat (receivedMessage.getId()).isEqualTo(createdMessage.getId());
        assertThat (receivedMessage.getTimestamp()).isEqualTo(createdMessage.getTimestamp());
        assertThat (receivedMessage.getContent()).isEqualTo(createdMessage.getContent());
    }

    @Test
    void findOneByIdWithNonExistingIdThrowsNotFoundException() {
        assertThrows(
            NotFoundException.class, () -> messageService.findOne(-1L)
        );
    }

    @Test
    void getAllMessagesReturnsAllMessages() throws ValidationException, ConflictException {
        userRepository.save(user);
        Message message2 = TestData.MESSAGE(user);
        message2.setContent("This is a second test message");
        message2.setTimestamp(LocalDateTime.now());
        Message createdMessage1 = messageService.publishMessage(message);
        Message createdMessage2 = messageService.publishMessage(message2);

        List<Message> messages = messageService.findAll(null);

        assertThat (messages.size()).isEqualTo( 2);
        assertThat (messages.get(1).getId()).isEqualTo(createdMessage1.getId());
        assertThat (messages.get(1).getContent()).isEqualTo(createdMessage1.getContent());

        assertThat (messages.get(0).getId()).isEqualTo(createdMessage2.getId());
        assertThat (messages.get(0).getContent()).isEqualTo(createdMessage2.getContent());
    }

    @Test
    void searchMessageReturnsMessage() throws ValidationException, ConflictException {
        userRepository.save(user);
        messageService.publishMessage(message);

        MessageSearchDto searchDto = new MessageSearchDto();
        searchDto.setText("test");

        List<Message> messages = messageService.search(searchDto);

        assertThat (messages.size()).isGreaterThan(0);
        assertThat (messages.get(0).getContent()).contains("test");

    }

    @Test
    void searchMessageWithDateFromReturnsMessage() throws ValidationException, ConflictException {
        userRepository.save(user);
        messageService.publishMessage(message);

        MessageSearchDto searchDto = new MessageSearchDto();
        searchDto.setFromTime(LocalDateTime.parse("2021-01-01T12:00:00.00"));

        List<Message> messages = messageService.search(searchDto);

        assertThat (messages.size()).isGreaterThan(0);
        assertThat (messages.get(0).getContent()).contains("test");
    }

    @Transactional
    @Test
    void deleteMessageByIdDeletesMessage() {
        Message msg = messageRepository.save(message);
        messageService.deleteMessage(msg.getId());

        assertThrows(NotFoundException.class,
            () -> messageService.findOne(message.getId()));
    }
}
