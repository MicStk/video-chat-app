package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AudioChunkDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.repository.AudioChunkRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.MeetingService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.AudioChunkServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AudioChunkEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AudioChunkRepository audioChunkRepository;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private AudioChunkServiceImpl audioChunkService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private ApplicationUser user;
    private Meeting meeting;

    @BeforeEach
    void setUp() {
        audioChunkRepository.deleteAll();
        userRepository.deleteAll();
        meetingRepository.deleteAll();
        this.user = TestData.USER();
        this.meeting = TestData.MEETING(user);
    }

    @AfterEach
    void tearDown() {
        audioChunkRepository.deleteAll();
        meetingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Transactional
    @Test
    void createReturnsAudioChunk() throws Exception {
        userRepository.save(user);
        meetingService.createMeeting(meeting);

        UserDto userDto = userMapper.applicationUserToUserDto(user);

        AudioChunkDto audioChunkDto = TestData.AUDIO_CHUNK_DTO(userDto);

        String requestBody = objectMapper.writeValueAsString(audioChunkDto);

        MvcResult mvcResult = this.mockMvc.perform(post(AUDIOCHUNKS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        AudioChunkDto returnedAudioChunkDto = objectMapper.readValue(response.getContentAsString(), AudioChunkDto.class);

        assertThat(returnedAudioChunkDto.getAuthor().equals(audioChunkDto.getAuthor()));
        assertThat(returnedAudioChunkDto.getTimestamp().equals(audioChunkDto.getTimestamp()));
        assertThat(returnedAudioChunkDto.getData().equals(audioChunkDto.getData()));

    }

    @Test
    void createWithInvalidAuthorIdThrowsException() throws Exception {
        userRepository.save(user);
        meetingService.createMeeting(meeting);

        UserDto userDto = userMapper.applicationUserToUserDto(user);

        userDto.setId(0L);
        AudioChunkDto audioChunkDto = TestData.AUDIO_CHUNK_DTO(userDto);

        String requestBody = objectMapper.writeValueAsString(audioChunkDto);

        MvcResult mvcResult = this.mockMvc.perform(post(AUDIOCHUNKS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(Objects.equals(response.getErrorMessage(), "Validation of audio chunk for updating failed – conflict with system state.. Conflicts: The author was not found.."));


    }

    @Test
    void deleteAudioChunkByIdReturnsStatusOk() throws Exception {
        userRepository.save(user);
        meetingService.createMeeting(meeting);
        AudioChunk createdAudioChunk = audioChunkService.createAudioChunk(TestData.AUDIO_CHUNK(user));

    }

    @Test
    void deleteAudioChunkByInvalidIdReturnsStatusXX() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(AUDIOCHUNKS_BASE_URI + "/" + "0")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isNotFound())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(Objects.equals(response.getErrorMessage(), "No class at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk entity with id 0 exists!"));


    }

    @Test
    void getAudioChunkByIdReturnsAudioChunkDto() throws Exception {
        userRepository.save(user);
        Meeting createdMeeting = meetingService.createMeeting(meeting);

        MvcResult updateMvcResult = this.mockMvc.perform(
                get(MEETING_BASE_URI + "/" + createdMeeting.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = updateMvcResult.getResponse();

        Meeting returnedMeeting = objectMapper.readValue(response.getContentAsString(), Meeting.class);

        assertThat(returnedMeeting.getTitle()).isEqualTo(meeting.getTitle());
    }

    @Test
    void getAudioChunkByInvalidIdReturnsNotFoundException() throws Exception {

        MvcResult updateMvcResult = this.mockMvc.perform(
                get(MEETING_BASE_URI + "/" + "0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound())
            .andReturn();

        MockHttpServletResponse response = updateMvcResult.getResponse();

        assertThat(response.getContentAsString().equals("Meeting with ID 0 does not exist!"));
    }

}