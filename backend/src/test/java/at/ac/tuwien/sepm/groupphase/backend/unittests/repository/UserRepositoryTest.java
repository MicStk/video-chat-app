package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest implements TestData {

    @Autowired
    private UserRepository userRepository;
    private ApplicationUser user;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        this.user = TestData.USER();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Transactional
    @Test
    public void givenNothing_whenSaveUser_thenFindListWithOneElementAndFindMessageById() {
        ApplicationUser savedUser = userRepository.save(user);

        assertAll(
            () -> assertEquals(1, userRepository.findAll().size()),
            () -> assertNotNull(userRepository.findById(user.getId())),
            () -> savedUser.getFirstName().equals(user.getFirstName()),
            () -> savedUser.getLastName().equals(user.getLastName()),
            () -> savedUser.getEmail().equals(user.getEmail())
        );
    }

    @Transactional
    @Test
    public void findUserPerEmailReturnsApplicationUser() {
        ApplicationUser savedUser = userRepository.save(user);

        ApplicationUser retrievedUser = userRepository.findApplicationUserByEmail(savedUser.getEmail());

        assertAll(
            () -> retrievedUser.getFirstName().equals(savedUser.getFirstName()),
            () -> retrievedUser.getLastName().equals(savedUser.getLastName()),
            () -> retrievedUser.getEmail().equals(savedUser.getEmail())
        );
    }

    @Transactional
    @Test
    public void findUserPerInvalidEmailReturnsNull() {

        ApplicationUser retrievedUser = userRepository.findApplicationUserByEmail("non-existing@email.com");

        assertThat (retrievedUser).isNull();
    }
}
