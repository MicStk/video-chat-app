package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Container;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTranscriptRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@DependsOn("generateUsers")
@Component
public class MeetingDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ContainerTranscriptRepository containerTranscriptRepository;

    public MeetingDataGenerator(MeetingRepository meetingRepository, UserRepository userRepository, ContainerTranscriptRepository containerTranscriptRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.containerTranscriptRepository = containerTranscriptRepository;
    }

    @PostConstruct
    @Bean
    private void generateMeetings() {
        LOGGER.trace("generating meetings");

        if (containerTranscriptRepository.findAll().size() < 1) {
            LocalDateTime startTime = LocalDateTime.parse("2023-01-24T14:00:00.00");
            LocalDateTime endTime = LocalDateTime.parse("2023-01-24T15:30:00.00");

            ContainerTranscript container = new ContainerTranscript();
            container.setAuthor(userRepository.findApplicationUserByEmail("florian@email.com"));
            container.setName("Meeting 2023-01-24 22:40");
            container.setPinned(false);
            container.setTimestamp(startTime);
            container.setEndTime(endTime);
            container.setDescription("Transkription mit tiny.");
            container.setSummary("Etwas ungenau die Übersetzung dafür schnell!");
            container.setProgress(3L);
            container.setTotalSteps(3L);

            LOGGER.debug("saving transcript container {}", container);
            containerTranscriptRepository.save(container);
        }
    }
}
