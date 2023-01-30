package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class MessageMappingTest implements TestData {

    @Autowired
    private SimpleMessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    private final ApplicationUser user = TestData.USER();

    private final Message message = TestData.MESSAGE(user);

    @Test
    public void givenNothing_whenMapDetailedMessageDtoToEntity_thenEntityHasAllProperties() {
        MessageDto detailedMessageDto = messageMapper.messageToSimpleMessageDto(message);
        assertAll(
            () -> assertEquals(message.getId(), detailedMessageDto.getId()),
            () -> assertEquals(message.getTimestamp(), detailedMessageDto.getTimestamp()),
            () -> assertEquals(message.getContent(), detailedMessageDto.getContent()),
            () -> assertEquals(message.getUser().getId(), detailedMessageDto.getUser().getId()),
            () -> assertEquals(message.getContainer(), detailedMessageDto.getContainer())
        );
    }

    @Test
    public void givenNothing_whenMapListWithTwoMessageEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message);

        List<MessageDto> messageDtos = messageMapper.messageToSimpleMessageDto(messages);
        assertEquals(2, messageDtos.size());
        MessageDto messageDto = messageDtos.get(0);
        assertAll(
            () -> assertEquals(message.getId(), messageDto.getId()),
            () -> assertEquals(message.getTimestamp(), messageDto.getTimestamp()),
            () -> assertEquals(message.getUser().getId(), messageDto.getUser().getId()),
            () -> assertEquals(message.getContainer(), messageDto.getContainer())
        );
    }


}
