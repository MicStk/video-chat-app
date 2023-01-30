package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTopicRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class TopicValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ContainerTopicRepository containerTopicRepository;
    private final MessageRepository messageRepository;
    private final HttpServletRequest request;
    private final UserRepository userRepository;

    public TopicValidator(ContainerTopicRepository containerTopicRepository, MessageRepository messageRepository, HttpServletRequest request,
                          UserRepository userRepository) {
        this.containerTopicRepository = containerTopicRepository;
        this.messageRepository        = messageRepository;
        this.request                  = request;
        this.userRepository = userRepository;
    }

    public void validateForCreate(ContainerTopic containerTopic) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", containerTopic);

        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors   = new ArrayList<>();

        if (containerTopic.getId() != null) {
            validationErrors.add("ID must not be given.");
        }

        if (containerTopic.getName() != null) {
            if (containerTopic.getName().isBlank()) {
                validationErrors.add("Name must not be blank.");
            }
        } else {
            validationErrors.add("No name given.");
        }

        if (containerTopic.getAuthor() != null) {
            if (!userRepository.existsById(containerTopic.getAuthor().getId())) {
                conflictErrors.add("Given author does not exist.");
            }
        } else {
            validationErrors.add("No author given.");
        }

        if (containerTopic.getTimestamp() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (containerTopic.getTimestamp().isAfter(now)) {
                validationErrors.add("Timestamp must not be in the future.");
            }
        } else {
            validationErrors.add("No timestamp given.");
        }

        if (containerTopic.getPinned() == null) {
            validationErrors.add("Not whether it is pinned given.");
        }

        if (containerTopic.getMessages() != null && !containerTopic.getMessages().isEmpty()) {
            for (Message message : containerTopic.getMessages()) {
                if (!messageRepository.existsById(message.getId())) {
                    conflictErrors.add("No Message with the id " + message.getId() + " exists.");
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of topic for creation failed – bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of topic for creation failed – conflict with system state.", conflictErrors);
        }
    }

    public void validateForUpdate(Long id, ContainerTopic containerTopic) throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({})", containerTopic);

        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors   = new ArrayList<>();

        if (id != null && !containerTopicRepository.existsById(containerTopic.getId())) {
            conflictErrors.add("No ContainerTopic with that id exists.");
        }

        if (containerTopic.getId() != null && !Objects.equals(containerTopic.getId(), id)) {
            validationErrors.add("ID must not be given, or be equal to the id parameter.");
        }

        if (containerTopic.getName() != null) {
            if (containerTopic.getName().isBlank()) {
                validationErrors.add("Name must not be blank.");
            }
        } else {
            validationErrors.add("No name given.");
        }

        if (containerTopic.getAuthor() != null) {
            if (!userRepository.existsById(containerTopic.getAuthor().getId())) {
                conflictErrors.add("Given author does not exist.");
            }
        } else {
            validationErrors.add("No author given.");
        }

        if (containerTopic.getTimestamp() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (containerTopic.getTimestamp().isAfter(now)) {
                validationErrors.add("Timestamp must not be in the future.");
            }
        } else {
            validationErrors.add("No timestamp given.");
        }

        if (containerTopic.getPinned() == null) {
            validationErrors.add("Not whether it is pinned given.");
        }

        if (containerTopic.getMessages() != null && !containerTopic.getMessages().isEmpty()) {
            for (Message message : containerTopic.getMessages()) {
                if (!messageRepository.existsById(message.getId())) {
                    conflictErrors.add("No Message with the id " + message.getId() + " exists.");
                }
            }
        }

        if (id != null && containerTopicRepository.existsById(containerTopic.getId())) {
            Optional<ContainerTopic> tmp = containerTopicRepository.findById(id);
            if (tmp.isPresent()) {
                if (tmp.get().getPinned() != containerTopic.getPinned()) {
                    if (!request.isUserInRole("ROLE_ADMIN")) {
                        validationErrors.add("The parameter pinned can only be changed by the admin.");
                    }
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of topic for creation failed – bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of topic for creation failed – conflict with system state.", conflictErrors);
        }
    }
}
