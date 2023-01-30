package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
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
import java.util.Optional;

@Component
public class SimpleMessageValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final HttpServletRequest request;

    public SimpleMessageValidator(UserRepository userRepository, HttpServletRequest request, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.request = request;
    }

    public void validateForCreate(Message message) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", message);

        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors = new ArrayList<>();

        if (message.getId() != null) {
            validationErrors.add("ID must not be given.");
        }

        if (message.getContent() == null) {
            validationErrors.add("Content of message must not be empty");
        } else {
            if (message.getContent().trim().isEmpty()) {
                validationErrors.add("Content of message can not only contain white spaces.");
            }
        }

        if (message.getTimestamp() == null) {
            validationErrors.add("No timestamp of message given.");
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (message.getTimestamp().isAfter(now)) {
                validationErrors.add("Timestamp of message must not be in the future.");
            }
        }

        if (message.getUser() == null) {
            validationErrors.add("Creator of the message must be given.");
        } else {
            if (!userRepository.existsById(message.getUser().getId())) {
                conflictErrors.add("Author does not exist.");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of message for creating failed - bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of message for creating failed - conflict with system state.", conflictErrors);
        }

    }

    public void validateForUpdate(Long id, Message message) throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({})", message);

        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors = new ArrayList<>();

        if (id == null) {
            validationErrors.add("ID must not be empty");
        } else {
            if (id == 0) {
                validationErrors.add("ID must not be 0.");
            }
        }

        if (message.getContent() == null) {
            validationErrors.add("Content of message must not be empty");
        } else {
            if (message.getContent().trim().isEmpty()) {
                validationErrors.add("Content of message can not only contain white spaces.");
            }
        }

        if (message.getTimestamp() == null) {
            validationErrors.add("No timestamp of message given.");
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (message.getTimestamp().isAfter(now)) {
                validationErrors.add("Timestamp of message must not be in the future.");
            }
        }

        if (message.getUser() == null) {
            validationErrors.add("Creator of the message must be given.");
        }

        if (messageRepository.findById(id).isPresent()) {
            Optional<Message> msg = messageRepository.findById(id);
            if (msg.isPresent()) {
                if (!msg.get().getTimestamp().equals(message.getTimestamp())) {
                    conflictErrors.add("The timestamp of a message can not be changed.");
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of message for creating failed - bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of message for creating failed - conflict with system state.", conflictErrors);
        }
    }
}
