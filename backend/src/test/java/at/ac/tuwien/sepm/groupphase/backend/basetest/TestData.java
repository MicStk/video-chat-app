package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AudioChunkDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopicDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.type.Role;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String LOGIN_BASE_URI = BASE_URI + "/authentication";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String MEETING_BASE_URI = BASE_URI + "/meetings";
    String TOPIC_BASE_URI = BASE_URI + "/topics";
    String AUDIOCHUNKS_BASE_URI = BASE_URI + "/audiochunks";


    String USER_BASE_URI = BASE_URI + "/users";

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_EMPLOYEE");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_EMPLOYEE");
        }
    };

    static ApplicationUser USER() {
        return new ApplicationUser(
            "Max",
            "Mustermann",
            "max.mustermann@email.com",
            "password",
            Role.EMPLOYEE

        );
    }

    static Meeting MEETING(ApplicationUser user) {
        return new Meeting(
            "Test Meeting 1",
            user,
            "This is the first test meeting.",
            LocalDateTime.parse("2022-11-17T14:00:00.00"),
            null,
            null
        );
    }

    static UserDto USER_DETAIL_DTO(ApplicationUser user) {
        return UserDto.UserDtoBuilder.aUserDto()
            .withId(user.getId())
            .withFirstName("Max")
            .withFirstName("Musermann")
            .withEmail("bla@bla.com")
            .withPassword("p")
            .withRole(Role.EMPLOYEE)
            .build();
    }
    static ContainerTopic CONTAINER_TOPIC(ApplicationUser user) {
        return new ContainerTopic(
            "Topic 1",
            user,
            LocalDateTime.parse("2022-01-01T12:00:00.00"),
            Boolean.FALSE,
            new HashSet<>()
        );
    }    static ContainerTranscript CONTAINER_TRANSCRIPT(ApplicationUser user) {
        ContainerTranscript containerTranscript = new ContainerTranscript();
        containerTranscript.setName("Test Container Transcript Title");
        containerTranscript.setTimestamp(LocalDateTime.parse("2022-11-17T14:00:00.00"));
        containerTranscript.setAuthor(user);
        containerTranscript.setDescription("Very important meeting");
        containerTranscript.setSummary("Summary");
        containerTranscript.setEndTime(LocalDateTime.parse("2022-11-17T14:10:00.00"));
        containerTranscript.setPinned(false);
        containerTranscript.setProgress(10L);
        containerTranscript.setTotalSteps(10L);
        containerTranscript.setMessages(new HashSet<>());


        return containerTranscript;
    }

    static MeetingDto MEETING_DTO(UserDto authorDto) {
        String title = "Important December Meeting";
        String description = "This meeting is very important.";
        LocalDateTime startTime = LocalDateTime.parse("2022-11-17T14:00:00.00");
        LocalDateTime endTime = null;
        String summary = null;

        return MeetingDto.MeetingDtoBuilder.aMeetingDto()
            .withTitle(title)
            .withDescription(description)
            .withAuthor(authorDto)
            .withStartTime(startTime)
            .withEndTime(endTime)
            .withSummary(summary)
            .build();
    }

    static TopicDto TOPIC_DTO(UserDto authorDto) {
        return TopicDto.ContainerTopicDtoBuilder.aContainerTopicDto()
            .withName("Topic 1")
            .withAuthor(authorDto)
            .withTimestamp(LocalDateTime.parse("2022-01-01T12:00:00.00"))
            .withPinned(Boolean.FALSE)
            .build();
    }

    static AudioChunk AUDIO_CHUNK(ApplicationUser user) {
        String inputString = "1234";
        byte[] byteArray = inputString.getBytes();
        String data = Base64.getEncoder().encodeToString(byteArray);

        AudioChunk audioChunk = new AudioChunk();
        audioChunk.setData(data);
        audioChunk.setAuthor(user);
        audioChunk.setTimestamp(LocalDateTime.parse("2022-01-01T12:00:00.00"));

        return audioChunk;
    }

    static AudioChunkDto AUDIO_CHUNK_DTO(UserDto userDto) {
        LocalDateTime timestamp = LocalDateTime.now();
        String inputString = "1234";
        byte[] byteArray = inputString.getBytes();

        return AudioChunkDto.AudioChunkDtoBuilder.anAudioChunkDto()
            .withAuthor(userDto)
            .withTimestamp(timestamp)
            .withData(byteArray)
            .build();
    }

    static Message MESSAGE(ApplicationUser user) {
        LocalDateTime timestamp = LocalDateTime.parse("2022-01-01T12:00:00.00");
        String content = "This is a test message";

        Message message = new Message();
        message.setTimestamp(timestamp);
        message.setContent(content);
        message.setUser(user);
        message.setIsTranscript(false);


        return message;
    }

    static MessageDto MESSAGE_DTO(UserDto userDetailDto) {
        LocalDateTime timestamp = LocalDateTime.parse("2022-01-01T12:00:00.00");
        String content = "This is a test message";

        return MessageDto.SimpleMessageDtoBuilder.aSimpleMessageDto()
            .withTimestamp(timestamp)
            .withContent(content)
            .withUser(userDetailDto)
            .withIsTrancript(false)
            .build();
    }

    static AudioChunk REAL_AUDIO_CHUNK(ApplicationUser user) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get("src/test/java/at/ac/tuwien/sepm/groupphase/backend/unittests/AudioChunkData.txt")));

        AudioChunk audioChunk = new AudioChunk();
        audioChunk.setData(data);
        audioChunk.setAuthor(user);
        audioChunk.setTimestamp(LocalDateTime.parse("2022-11-17T14:01:00.00"));

        return audioChunk;
    }
}
