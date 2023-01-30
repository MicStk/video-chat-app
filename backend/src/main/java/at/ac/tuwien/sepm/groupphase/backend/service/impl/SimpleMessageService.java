package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleMessageService implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageRepository messageRepository;

    private final SimpleMessageValidator validator;

    public SimpleMessageService(MessageRepository messageRepository, SimpleMessageValidator validator) {
        this.messageRepository = messageRepository;
        this.validator = validator;
    }

    @Override
    public List<Message> findAll(Long containerId) {
        LOGGER.debug("Find all messages");
        if (containerId == null) {
            return messageRepository.findAllByOrderByTimestampDesc();
        } else {
            return messageRepository.findMessagesByContainer(containerId);
        }
    }

    @Override
    public Message findOne(Long id) {
        LOGGER.debug("Find message with id {}", id);
        Optional<Message> message = messageRepository.findById(id);
        if (message.isPresent()) {
            return message.get();
        } else {
            throw new NotFoundException(String.format("Could not find message with id %s", id));
        }
    }

    @Override
    public List<Message> search(MessageSearchDto searchParams) {
        LOGGER.debug("Find all messages matching {}", searchParams);
        if (searchParams == null) {
            return messageRepository.findAllByOrderByTimestampDesc();
        } else {
            return messageRepository.searchMessagesByCriteria(
                searchParams.getText(),
                searchParams.getAuthor(),
                searchParams.getFromTime(),
                searchParams.getToTime()
            );
        }
    }

    @Override
    public Message publishMessage(Message message) throws ValidationException, ConflictException {
        LOGGER.debug("Publish new message {}", message);
        //message.setTimestamp(LocalDateTime.now());
        validator.validateForCreate(message);
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long id) {
        LOGGER.debug("Delete message with ID {}", id);
        try {
            messageRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Message update(Long id, Message message) throws ValidationException, ConflictException {
        LOGGER.debug("Update existing message {}", message);

        message.setId(id);
        validator.validateForUpdate(id, message);

        message.setEditedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

}
