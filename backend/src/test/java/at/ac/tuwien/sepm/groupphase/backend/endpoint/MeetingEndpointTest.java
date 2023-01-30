package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.MeetingService;
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

import java.util.Objects;

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
public class MeetingEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
        meetingRepository.deleteAll();
        this.user = TestData.USER();
        this.meeting = TestData.MEETING(user);
    }

    @AfterEach
    void tearDown() {
        meetingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Transactional
    @Test
    void createMeetingReturnsMeeting() throws Exception {
        userRepository.save(user);
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        MeetingDto meetingDto = TestData.MEETING_DTO(authorDto);

        String requestBody = objectMapper.writeValueAsString(meetingDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MEETING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        Meeting returnedMeeting = objectMapper.readValue(response.getContentAsString(), Meeting.class);

        assertThat(returnedMeeting.getTitle()).isEqualTo(meetingDto.getTitle());
        assertThat(returnedMeeting.getDescription()).isEqualTo(meetingDto.getDescription());
        assertThat(returnedMeeting.getStartTime()).isEqualTo(meetingDto.getStartTime());
        assertThat(returnedMeeting.getEndTime()).isEqualTo(meetingDto.getEndTime());
        assertThat(returnedMeeting.getSummary()).isEqualTo(meetingDto.getSummary());
    }

    @Transactional
    @Test
    void createMeetingWithoutTitleReturnsUnprocessableEntity() throws Exception {
        userRepository.save(user);
        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        MeetingDto meetingDto = TestData.MEETING_DTO(authorDto);

        meetingDto.setTitle(null);

        String requestBody = objectMapper.writeValueAsString(meetingDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MEETING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(Objects.equals(response.getErrorMessage(), "Validation of meeting for updating failed – bad input.. Failed Validations: No meeting title given.."));
    }

    @Test
    void updateMeetingTitleReturnsUpdatedMeeting() throws Exception {
        userRepository.save(user);
        meeting.setTitle("Initial Title");
        Meeting createdMeeting = meetingService.createMeeting(meeting);

        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        MeetingDto meetingDto = TestData.MEETING_DTO(authorDto);
        meetingDto.setTitle("New updated title");
        meetingDto.setId(createdMeeting.getId());

        String updateRequestBody = objectMapper.writeValueAsString(meetingDto);

        MvcResult updateMvcResult = this.mockMvc.perform(
                put(MEETING_BASE_URI + "/" + createdMeeting.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateRequestBody)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = updateMvcResult.getResponse();

        Meeting returnedMeeting = objectMapper.readValue(response.getContentAsString(), Meeting.class);

        assertThat(returnedMeeting.getTitle()).isEqualTo(meetingDto.getTitle());
    }

    @Test
    void updateMeetingWithoutTitleReturnsUnprocessableEntity() throws Exception {
        userRepository.save(user);
        meeting.setTitle("Initial Title");
        Meeting createdMeeting = meetingService.createMeeting(meeting);

        UserDto authorDto = TestData.USER_DETAIL_DTO(user);
        MeetingDto meetingDto = TestData.MEETING_DTO(authorDto);
        meetingDto.setTitle(null);
        meetingDto.setId(createdMeeting.getId());

        String updateRequestBody = objectMapper.writeValueAsString(meetingDto);

        MvcResult updateMvcResult = this.mockMvc.perform(
                put(MEETING_BASE_URI + "/" + createdMeeting.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateRequestBody)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = updateMvcResult.getResponse();

        assertThat(Objects.equals(response.getErrorMessage(), "Validation of meeting for updating failed – bad input.. Failed Validations: No meeting title given.."));

    }

    @Test
    void getOneByIdReturnsMeetingWithThatId() throws Exception {
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
    void getOneByIdWithInvalidIdReturnsNotFound() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
                get(MEETING_BASE_URI + "/" + "0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString().equals("Meeting with ID 0 does not exist!"));
    }
}