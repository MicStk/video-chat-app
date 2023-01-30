package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTopicRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class TopicServiceImpl implements TopicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ContainerTopicRepository containerTopicRepository;
    private final MessageRepository messageRepository;
    private final TopicValidator topicValidator;

    public TopicServiceImpl(ContainerTopicRepository containerTopicRepository, TopicValidator topicValidator, MessageRepository messageRepository) {
        this.topicValidator           = topicValidator;
        this.containerTopicRepository = containerTopicRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ContainerTopic> getAll() {
        LOGGER.debug("Get all topics");

        return containerTopicRepository.findAll();
    }

    @Override
    public ContainerTopic getById(Long id) {
        LOGGER.debug("Get topic by id {}", id);

        Optional<ContainerTopic> topic = containerTopicRepository.findById(id);
        if (topic.isPresent()) {
            return topic.get();
        } else {
            throw new NotFoundException(String.format("Could not find topic with id %s", id));
        }
    }

    @Override
    public ContainerTopic create(ContainerTopic containerTopic) throws ValidationException, ConflictException {
        LOGGER.debug("Create new topic {}", containerTopic);

        topicValidator.validateForCreate(containerTopic);

        return containerTopicRepository.save(containerTopic);
    }

    @Override
    public ContainerTopic update(Long id, ContainerTopic containerTopic) throws ValidationException, ConflictException {
        LOGGER.debug("Update existing topic {}", containerTopic);

        containerTopic.setId(id);
        topicValidator.validateForUpdate(id, containerTopic);

        return containerTopicRepository.save(containerTopic);
    }

    @Override
    @Transactional
    public void deleteTopic(Long id) throws NotFoundException {
        LOGGER.debug("Delete topic with ID {}", id);
        try {
            Optional<ContainerTopic> topic = containerTopicRepository.findById(id);
            if (topic.isPresent()) {
                List<Message> messages = messageRepository.findMessagesByContainer(id);
                if (messages != null) {
                    messageRepository.deleteAll(messages);
                }
            }
            containerTopicRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage(), e);
        }
    }
}
