package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private ApplicationUser user;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        this.user = TestData.USER();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenCreateUser_thenFindListWithUser() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setFirstName("Max");
        userDto.setLastName("Mustermann");
        userDto.setEmail("max.mustermann@email.com");
        userDto.setPassword("password");
        userDto.setRole(Role.EMPLOYEE);

        String body = objectMapper.writeValueAsString(userDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        UserDto user = objectMapper.readValue(response.getContentAsString(),
            UserDto.class);

        assertEquals("Max", user.getFirstName());
        assertEquals("Mustermann", user.getLastName());
        assertEquals("max.mustermann@email.com", user.getEmail());
        assertEquals(Role.EMPLOYEE, user.getRole());

    }

    @Test
    public void createDuplicateUserReturnsValidationError() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setFirstName("Max");
        userDto.setLastName("Mustermann");
        userDto.setEmail("max.mustermann@email.com");
        userDto.setPassword("password");
        userDto.setRole(Role.EMPLOYEE);

        userRepository.save(this.user);

        String body = objectMapper.writeValueAsString(userDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();
        Exception response = mvcResult.getResolvedException();

        assertEquals(
            "409 CONFLICT \"Validation of meeting for updating failed – conflict with system state.. Conflicts: User email already exists. Contact admin if you need to reset your password..\"; nested exception is at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException: Validation of meeting for updating failed – conflict with system state.. Conflicts: User email already exists. Contact admin if you need to reset your password..",
            response.getMessage());
    }

    @Test
    public void givenNotAdmin_whenCreateUser_thenReturn403() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setFirstName("Max");
        userDto.setLastName("Mustermann");
        userDto.setEmail("max.mustermann@email.com");
        userDto.setPassword("password");
        userDto.setRole(Role.EMPLOYEE);

        String body = objectMapper.writeValueAsString(userDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}
