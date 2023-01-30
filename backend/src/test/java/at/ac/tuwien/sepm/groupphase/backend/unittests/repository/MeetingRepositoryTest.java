package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
class MeetingRepositoryTest implements TestData {
    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    UserRepository userRepository;
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

    @Test
    void findOpenMeetingsReturnsListWithOpenMeetings() {
        userRepository.save(user);
        meetingRepository.save(meeting);

        List<Meeting> meetingList = meetingRepository.findOpenMeetings();

        assertThat (meetingList.size()).isGreaterThan(0);
        assertThat (meetingList.get(0).getTitle()).isEqualTo(meeting.getTitle());
    }

    @Test
    void findOpenMeetingsReturnsEmptyListWithOpenMeetings() {

        List<Meeting> meetingList = meetingRepository.findOpenMeetings();

        assertThat (meetingList.size()).isEqualTo(0);

    }

    @Test
    void findMeetingByTitleReturnsMeetingWithTitle() {
        userRepository.save(user);
        meetingRepository.save(meeting);

        Meeting retrievedMeeting = meetingRepository.findMeetingByTitle(meeting.getTitle());

        assertThat (retrievedMeeting.getTitle()).isEqualTo(meeting.getTitle());
    }

    @Test
    void findMeetingByTitleReturnsNothing() {

        Meeting retrievedMeeting = meetingRepository.findMeetingByTitle(meeting.getTitle());

        assertThat (retrievedMeeting).isNull();
    }
}