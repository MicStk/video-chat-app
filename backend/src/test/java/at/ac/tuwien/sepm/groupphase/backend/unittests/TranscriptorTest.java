package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AudioChunkRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTranscriptRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AudioChunkService;
import at.ac.tuwien.sepm.groupphase.backend.service.MeetingService;
import at.ac.tuwien.sepm.groupphase.backend.transcription.Transcriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TranscriptorTest implements TestData {

    @Autowired
    AudioChunkService audioChunkService;
    @Autowired
    MeetingService meetingService;
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    AudioChunkRepository audioChunkRepository;
    @Autowired
    ContainerTranscriptRepository containerTranscriptRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    Transcriptor transcriptor;

    @Test
    public void AudioChunkProcessorStartsWhenAudioProcessorWatchdogIsInitialized() throws ValidationException, ConflictException, IOException, InterruptedException {
        ApplicationUser user = TestData.USER();
        userRepository.save(user);
        Meeting meeting = TestData.MEETING(user);

        meeting.setStartTime(LocalDateTime.parse("2022-11-17T14:00:00.00"));

        Meeting createdMeeting = this.meetingService.createMeeting(meeting);

        AudioChunk audioChunk = TestData.REAL_AUDIO_CHUNK(user);
        audioChunkService.createAudioChunk(audioChunk);

        meeting.setEndTime(LocalDateTime.parse("2022-11-17T14:05:00.00"));
        this.meetingService.updateMeeting(createdMeeting.getId(), meeting);

        assertThat(transcriptor.isAlive()).isTrue();

        // ** commented out for gitlab pipeline **
        //Thread.sleep(12000);
        //assertThat(messageRepository.findAll().size()).isGreaterThan(0);

        transcriptor.interrupt();
        audioChunkRepository.delete(audioChunk);
        meetingRepository.delete(meeting);
        messageRepository.deleteAll();
        containerTranscriptRepository.deleteAll();
        userRepository.delete(user);
    }
}
