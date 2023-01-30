package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTopicRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class TopicDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ContainerTopicRepository topicRepository;
    private final UserRepository userRepository;

    public TopicDataGenerator(ContainerTopicRepository topicRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    @Bean
    private void generateTopic() {
        if (topicRepository.findAll().size() > 0) {
            LOGGER.debug("topic already generated");
        } else {
            LocalDateTime startTime;
            ContainerTopic topic;

            LOGGER.debug("generating {} topic entries", 1);
            startTime = LocalDateTime.parse("2022-12-01T14:01:00.00");
            topic = new ContainerTopic("General", userRepository.findById(2L).get(), startTime, true, null);
            LOGGER.debug("saving topic {}", topic);
            topicRepository.save(topic);

            LOGGER.debug("generating {} topic entries", 1);
            startTime = LocalDateTime.parse("2022-12-01T14:01:00.00");
            topic = new ContainerTopic("IR1", userRepository.findById(3L).get(), startTime, true, null);
            LOGGER.debug("saving topic {}", topic);
            topicRepository.save(topic);

            LOGGER.debug("generating {} topic entries", 1);
            startTime = LocalDateTime.parse("2022-12-24T19:01:00.00");
            topic = new ContainerTopic("MR2", userRepository.findById(3L).get(), startTime, true, null);
            LOGGER.debug("saving topic {}", topic);
            topicRepository.save(topic);

            LOGGER.debug("generating {} topic entries", 1);
            startTime = LocalDateTime.parse("2023-01-19T17:01:00.00");
            topic = new ContainerTopic("IR2", userRepository.findById(3L).get(), startTime, true, null);
            LOGGER.debug("saving topic {}", topic);
            topicRepository.save(topic);

            LOGGER.debug("generating {} topic entries", 1);
            startTime = LocalDateTime.parse("2023-01-25T17:01:00.00");
            topic = new ContainerTopic("MR3", userRepository.findById(3L).get(), startTime, true, null);
            LOGGER.debug("saving topic {}", topic);
            topicRepository.save(topic);
        }
    }
}
