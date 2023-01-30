package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.type.Role;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser user;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        this.user = TestData.USER();
    }

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void whenCreateUser_thenReturnUser() throws ValidationException, ConflictException {

        ApplicationUser createdUser = userService.createUser(user);

        assertEquals("Max", createdUser.getFirstName());
        assertEquals("Mustermann", createdUser.getLastName());
        assertEquals("max.mustermann@email.com", createdUser.getEmail());
        assertNotEquals("password", createdUser.getPassword());
        assertEquals(Role.EMPLOYEE, createdUser.getRole());

    }

    @Test
    public void loginExistingUserWithCorrectPasswordReturnsJWTToken() throws ValidationException, ConflictException {

        ApplicationUser createdUser = userService.createUser(user);

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail(createdUser.getEmail());
        loginDto.setPassword("password");

        String token = userService.login(loginDto);

        String[] pieces = token.split("\\.");
        String b64payload = pieces[1];
        String jsonString = new String(Base64.decodeBase64(b64payload), StandardCharsets.UTF_8);

        assertThat(jsonString)
            .contains("mustermann")
            .contains("ROLE_EMPLOYEE");
    }

    @Test
    public void loginExistingUserWithIncorrectPasswordThrowsBadCredentialsException() throws ValidationException, ConflictException {

        ApplicationUser createdUser = userService.createUser(user);

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail(createdUser.getEmail());
        loginDto.setPassword("WRONG_PASSWORD");

        Exception exception = assertThrows(BadCredentialsException.class,
            () -> userService.login(loginDto));

        String expectedMessage = "Username or password is incorrect or account is locked";
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    public void createUserWithInvalidEmailAddress() {
        user.setEmail("wrongemail.com");

        ValidationException thrown = assertThrows(ValidationException.class,
            () -> userService.createUser(user)
        );

        assertEquals("Validation of user for creating a new user failed. Failed Validations: Email is set but not well formatted.", thrown.getMessage());
    }

    @Test
    public void createUserWithInvalidFirstNameAndLastName() {
        user.setFirstName("  ");
        user.setLastName("      ");
        ValidationException thrown = assertThrows(ValidationException.class,
            () -> userService.createUser(user)
        );

        assertEquals("Validation of user for creating a new user failed. Failed Validations: firstName of given user is empty, lastName of given user is empty.", thrown.getMessage());
    }

}
