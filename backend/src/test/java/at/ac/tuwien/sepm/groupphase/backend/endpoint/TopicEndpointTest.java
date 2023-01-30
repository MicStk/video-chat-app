package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopicDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTopicRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TopicEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContainerTopicRepository containerTopicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private ContainerTopic containerTopic;

    private ApplicationUser user;

    @BeforeEach
    void setUp() {
        containerTopicRepository.deleteAll();
        this.user = TestData.USER();
        userRepository.save(user);
        this.containerTopic = TestData.CONTAINER_TOPIC(user);
    }

    @AfterEach
    void tearDown() {
        containerTopicRepository.deleteAll();
    }

    @Transactional
    @Test
    void createTopicReturnsTopic() throws Exception {
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        TopicDto topicDto  = TestData.TOPIC_DTO(authorDto);
        String requestBody = objectMapper.writeValueAsString(topicDto);

        MvcResult mvcResult = this.mockMvc.perform(post(TOPIC_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status()
                .isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        TopicDto returnedTopic           = objectMapper.readValue(
            response.getContentAsString(),
            TopicDto.class
        );

        assertThat(returnedTopic.getName()).isEqualTo(topicDto.getName());
        assertThat(returnedTopic.getTimestamp()).isEqualTo(topicDto.getTimestamp());
        assertThat(returnedTopic.getPinned()).isEqualTo(topicDto.getPinned());
        assertThat(returnedTopic.getMessages()).isEqualTo(topicDto.getMessages());
    }

    @Transactional
    @Test
    void createTopicWithoutNameReturnsUnprocessableEntity() throws Exception {
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        TopicDto topicDto = TestData.TOPIC_DTO(authorDto);

        topicDto.setName(null);

        String requestBody = objectMapper.writeValueAsString(topicDto);

        MvcResult mvcResult = this.mockMvc.perform(post(TOPIC_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status()
                .isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getErrorMessage().equals(
            "Validation of topic for creation failed – bad input.. No name given."
        ));
    }

    @Transactional
    @Test
    void updateTopicReturnsUpdatedTopic() throws Exception {
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        TopicDto topicDto  = TestData.TOPIC_DTO(authorDto);

        containerTopic = containerTopicRepository.save(containerTopic);

        topicDto.setId(containerTopic.getId());
        topicDto.setName("Topic 2");

        String requestBody = objectMapper.writeValueAsString(topicDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TOPIC_BASE_URI + '/' + containerTopic.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status()
                .isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        TopicDto returnedTopic           = objectMapper.readValue(
            response.getContentAsString(),
            TopicDto.class
        );

        assertThat(returnedTopic.getName()).isEqualTo("Topic 2");
    }

    @Transactional
    @Test
    void updateTopicPinAsEmployeeReturnsUnprocessableEntity() throws Exception {
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        TopicDto topicDto  = TestData.TOPIC_DTO(authorDto);

        containerTopic = containerTopicRepository.save(containerTopic);

        topicDto.setId(containerTopic.getId());
        topicDto.setPinned(true);

        String requestBody = objectMapper.writeValueAsString(topicDto);

    }

    @Transactional
    @Test
    void updateTopicPinAsAdminReturnsUpdatedTopic() throws Exception {
        userRepository.save(user);
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        TopicDto topicDto  = TestData.TOPIC_DTO(authorDto);

        containerTopic = containerTopicRepository.save(containerTopic);

        topicDto.setId(containerTopic.getId());
        topicDto.setPinned(true);

        String requestBody = objectMapper.writeValueAsString(topicDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TOPIC_BASE_URI + '/' + containerTopic.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status()
                .isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        TopicDto returnedTopic           = objectMapper.readValue(
            response.getContentAsString(),
            TopicDto.class
        );

        assertThat(returnedTopic.getPinned()).isEqualTo(true);
    }

    @Transactional
    @Test
    void updateTopicWithoutNameReturnsUnprocessableEntity() throws Exception {
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        TopicDto topicDto  = TestData.TOPIC_DTO(authorDto);

        containerTopic = containerTopicRepository.save(containerTopic);

        topicDto.setId(containerTopic.getId());
        topicDto.setName(null);

        String requestBody = objectMapper.writeValueAsString(topicDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TOPIC_BASE_URI + '/' + containerTopic.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status()
                .isUnprocessableEntity())
            .andReturn();

    }

    @Transactional
    @Test
    void getTopicByIdReturnsTopic() throws Exception {
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        TopicDto topicDto  = TestData.TOPIC_DTO(authorDto);

        containerTopic = containerTopicRepository.save(containerTopic);

        MvcResult mvcResult = this.mockMvc.perform(get(TOPIC_BASE_URI + '/' + containerTopic.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status()
                .isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        TopicDto returnedTopic           = objectMapper.readValue(
            response.getContentAsString(),
            TopicDto.class
        );

        assertThat(returnedTopic.getName()).isEqualTo(topicDto.getName());
        assertThat(returnedTopic.getTimestamp()).isEqualTo(topicDto.getTimestamp());
        assertThat(returnedTopic.getPinned()).isEqualTo(topicDto.getPinned());
        assertThat(returnedTopic.getMessages()).isEqualTo(topicDto.getMessages());
    }
}
