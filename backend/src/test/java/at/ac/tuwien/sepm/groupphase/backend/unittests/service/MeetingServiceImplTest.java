package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.MeetingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@SpringBootTest
@ActiveProfiles("test")
class MeetingServiceImplTest implements TestData {
    @Autowired
    MeetingServiceImpl meetingService;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    UserRepository userRepository;

    private ApplicationUser user;
    private Meeting meeting;

    MeetingServiceImplTest() {
    }

    @BeforeEach
    public void setUp() {
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
    void createValidMeetingReturnsMeetingWithSetId() throws ValidationException, ConflictException {
        userRepository.save(user);
        Meeting createdMeeting = meetingService.createMeeting(meeting);

        assertThat (createdMeeting).isNotNull();
        assertThat (createdMeeting.getId()).isNotNull();
        assertThat (createdMeeting.getTitle()).isEqualTo(meeting.getTitle());
    }

    @Transactional
    @Test
    void createMeetingWithExistingTitleThrowsValidationException() throws ValidationException, ConflictException {
        userRepository.save(user);
        meetingService.createMeeting(meeting);

        assertThrows(ValidationException.class,
            () -> meetingService.createMeeting(meeting));
    }

    @Transactional
    @Test
    void createMeetingWithoutTitleThrowsValidationException() {
        userRepository.save(user);

        meeting.setTitle(null);

        assertThrows(
            ValidationException.class,
            () -> meetingService.createMeeting(meeting));
    }

    @Transactional
    @Test
    void createMeetingWithNonExistingAuthorThrowsConflictException() {
        user.setId(0L);

        meeting.setAuthor(user);

        assertThrows(
            ConflictException.class,
            () -> meetingService.createMeeting(meeting));
    }

    @Transactional
    @Test
    void updateMeeting() throws ValidationException, ConflictException {
        userRepository.save(user);

        Meeting createdMeeting = meetingService.createMeeting(meeting);
        assertThat (createdMeeting).isNotNull();

        meeting.setId(createdMeeting.getId());
        meeting.setTitle("Changed title");
        meeting.setAuthor(user);
        UpdateMeetingDto updatedMeeting = meetingService.updateMeeting(createdMeeting.getId(), meeting);
        assertThat (updatedMeeting).isNotNull();


    }

    @Transactional
    @Test
    void getOneById() throws ValidationException, ConflictException {
        userRepository.save(user);

        Meeting createdMeeting = meetingService.createMeeting(meeting);
        assertThat (createdMeeting).isNotNull();

        Meeting retrievedMeeting = meetingService.getOneById(createdMeeting.getId());
        assertThat (retrievedMeeting).isNotNull();
        assertThat (retrievedMeeting.getTitle()).isEqualTo(meeting.getTitle());
    }

    @Transactional
    @Test
    void getOneByIdWithNonExistingIdThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> meetingService.getOneById(0L));
    }
}