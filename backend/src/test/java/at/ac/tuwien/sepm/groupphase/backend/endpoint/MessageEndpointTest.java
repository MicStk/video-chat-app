package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTranscriptRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MessageEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ContainerTranscriptRepository containerTranscriptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private ApplicationUser user;
    private ContainerTranscript containerTranscript;
    private Message message;

    @BeforeEach
    public void beforeEach() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
        this.user = TestData.USER();
        this.containerTranscript = TestData.CONTAINER_TRANSCRIPT(this.user);
        this.message = TestData.MESSAGE(this.user);
    }

    @AfterEach
    void tearDown() {

        messageRepository.deleteAll();
        containerTranscriptRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createReturnsMessage() throws Exception {
        userRepository.save(user);

        UserDto userDetailDto = userMapper.applicationUserToUserDto(user);
        MessageDto messageDto = TestData.MESSAGE_DTO(userDetailDto);
        String requestBody = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        MessageDto returnedMessageDto = objectMapper.readValue(response.getContentAsString(), MessageDto.class);

        assertThat(returnedMessageDto.getTimestamp().equals(messageDto.getTimestamp()));
        assertThat(returnedMessageDto.getUser().equals(messageDto.getUser()));
        assertThat(returnedMessageDto.getContent().equals(messageDto.getContent()));
    }

    @Test
    void createMessageWithInvalidAuthorThrowsException() throws Exception {
        userRepository.save(user);

        UserDto userDto = userMapper.applicationUserToUserDto(user);
        userDto.setId(-1L);
        MessageDto messageDto = TestData.MESSAGE_DTO(userDto);

        String requestBody = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(Objects.equals(response.getErrorMessage(), "Validation of message for creating failed - conflict with system state.. Conflicts: Author does not exist.."));
    }

    @Test
    void createMessageWithInvalidContentThrowsException() throws Exception {
        userRepository.save(user);
        UserDto userDto = userMapper.applicationUserToUserDto(user);
        MessageDto messageDto = TestData.MESSAGE_DTO(userDto);
        messageDto.setContent("     ");

        String requestBody = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(Objects.equals(response.getErrorMessage(), "Validation of message for creating failed - bad input.. Failed Validations: Content of message can not only contain white spaces.."));
    }

    @Test
    public void getAllMessagesByAGivenContainer_ReturnsAllMessagesInContainer() throws Exception {
        userRepository.save(user);
        ContainerTranscript container = containerTranscriptRepository.save(containerTranscript);
        this.message.setContainer(container);
        messageRepository.save(message);

        int containerId = 1;

        byte[] body = this.mockMvc.perform(get(MESSAGE_BASE_URI)
                .param("containerId", String.valueOf(containerId))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<MessageDto> messagesResult = objectMapper.readerFor(MessageDto.class).<MessageDto>readValues(body).readAll();

        assertThat(messagesResult).isNotNull();
        assertThat(messagesResult.size() > 0);

    }

    @Test
    public void findMessageWithValidId() throws Exception {
        userRepository.save(user);
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/{id}", message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        MessageDto responseMessageDto = objectMapper.readValue(response.getContentAsString(), MessageDto.class);
        assertEquals(message, messageMapper.simpleMassageDtoToMessage(responseMessageDto));
    }

    @Test
    public void givenOneMessage_whenFindByNonExistingId_then404() throws Exception {
        userRepository.save(user);
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/{id}", -1L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void searchMessageTextReturnsFoundMessages() throws Exception {
        userRepository.save(user);
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/search?text=test")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assertThat(response.getContentAsString()).contains("test");
    }

    @Test
    public void searchMessageReturnsFoundMessages() throws Exception {
        userRepository.save(user);
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/search?fromTime=2021-01-01T12:00:00.00Z")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertThat(response.getContentAsString())
            .contains("test");
    }

}
