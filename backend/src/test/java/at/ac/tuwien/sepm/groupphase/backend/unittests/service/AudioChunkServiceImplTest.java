package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AudioChunkRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.AudioChunkServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.MeetingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@SpringBootTest
@ActiveProfiles("test")
public class AudioChunkServiceImplTest {

    @Autowired
    MeetingServiceImpl meetingService;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    AudioChunkRepository audioChunkRepository;

    @Autowired
    AudioChunkServiceImpl audioChunkService;

    @Autowired
    UserRepository userRepository;

    private ApplicationUser user;
    private Meeting meeting;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        meetingRepository.deleteAll();
        audioChunkRepository.deleteAll();
        this.user = TestData.USER();
        this.meeting = TestData.MEETING(user);
    }

    @AfterEach
    void tearDown() {
        audioChunkRepository.deleteAll();
        meetingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createAudioChunk() throws ValidationException, ConflictException {
        userRepository.save(user);
        meetingService.createMeeting(meeting);
        AudioChunk audioChunk = TestData.AUDIO_CHUNK(user);

        AudioChunk createdAudioChunk = audioChunkService.createAudioChunk(audioChunk);

        assertThat (createdAudioChunk.getId()).isNotNull();
        assertThat (createdAudioChunk.getAuthor()).isEqualTo(audioChunk.getAuthor());
        assertThat (createdAudioChunk.getTimestamp()).isEqualTo(audioChunk.getTimestamp());
        assertThat (createdAudioChunk.getData()).isEqualTo(audioChunk.getData());
    }

    @Test
    void createAudioChunkWihtoutMeetingThrowsValidationException() {
        userRepository.save(user);
        AudioChunk audioChunk = TestData.AUDIO_CHUNK(user);

        assertThrows(ConflictException.class,
            () -> audioChunkService.createAudioChunk(audioChunk));
    }

    @Test
    void getOneByIdReturnsAudioChunkWithId() throws ValidationException, ConflictException {
        userRepository.save(user);
        meetingService.createMeeting(meeting);
        AudioChunk audioChunk = TestData.AUDIO_CHUNK(user);

        AudioChunk createdAudioChunk = audioChunkService.createAudioChunk(audioChunk);

        AudioChunk retrievedAudioChunk = audioChunkService.getOneById(createdAudioChunk.getId());

        assertThat (retrievedAudioChunk.getId()).isEqualTo(createdAudioChunk.getId());
        assertThat (retrievedAudioChunk.getAuthor().getId()).isEqualTo(createdAudioChunk.getAuthor().getId());
        assertThat (retrievedAudioChunk.getTimestamp().getHour()).isEqualTo(createdAudioChunk.getTimestamp().getHour());
        assertThat (retrievedAudioChunk.getTimestamp().getMinute()).isEqualTo(createdAudioChunk.getTimestamp().getMinute());
        assertThat (retrievedAudioChunk.getTimestamp().getSecond()).isEqualTo(createdAudioChunk.getTimestamp().getSecond());
        assertThat (retrievedAudioChunk.getData()).isEqualTo(createdAudioChunk.getData());
    }

    @Test
    void getOneByInvalidIdThrowsNotFoundException() {

        assertThrows(NotFoundException.class,
            () -> audioChunkService.getOneById(0L));
    }

    @Test
    void deleteAudioChunkRemovesAudioChunkFromDatastorage() throws ValidationException, ConflictException {
        userRepository.save(user);
        meetingService.createMeeting(meeting);
        AudioChunk audioChunk = TestData.AUDIO_CHUNK(user);

        AudioChunk createdAudioChunk = audioChunkService.createAudioChunk(audioChunk);

        audioChunkService.deleteAudioChunk(createdAudioChunk.getId());

        assertThrows(NotFoundException.class,
            () -> audioChunkService.getOneById(createdAudioChunk.getId()));

    }

    @Test
    void deleteAudioChunkWithInvalidIdThrowsNotFoundException() {
        assertThrows(NotFoundException.class,
            () -> audioChunkService.deleteAudioChunk(0L));
    }

    @Test
    void getAudioDataReturnsByteArray() throws ValidationException, ConflictException {
        userRepository.save(user);
        meetingService.createMeeting(meeting);
        AudioChunk audioChunk = TestData.AUDIO_CHUNK(user);

        AudioChunk createdAudioChunk = audioChunkService.createAudioChunk(audioChunk);

        byte[] retrievedAudioChunk = audioChunkService.getAudioData(createdAudioChunk.getId());

        assertThat(retrievedAudioChunk).isNotNull();

    }
}