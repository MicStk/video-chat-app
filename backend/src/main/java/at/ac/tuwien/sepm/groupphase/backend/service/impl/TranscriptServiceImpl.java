package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTranscriptRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TranscriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class TranscriptServiceImpl implements TranscriptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ContainerTranscriptRepository containerTranscriptRepository;
    private final TranscriptValidator transcriptValidator;
    private final MessageRepository messageRepository;

    public TranscriptServiceImpl(ContainerTranscriptRepository containerTranscriptRepository, TranscriptValidator transcriptValidator, MessageRepository messageRepository) {
        this.containerTranscriptRepository = containerTranscriptRepository;
        this.transcriptValidator = transcriptValidator;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ContainerTranscript> getAll() {
        LOGGER.debug("Get all transcripts");

        return containerTranscriptRepository.findAll();
    }

    @Override
    public ContainerTranscript update(Long id, ContainerTranscript containerTranscript) throws ValidationException, ConflictException {
        LOGGER.debug("Update existing transcript {}", containerTranscript);

        containerTranscript.setId(id);
        transcriptValidator.validateForUpdate(id, containerTranscript);

        return containerTranscriptRepository.save(containerTranscript);
    }

    @Override
    @Transactional
    public void deleteTranscript(Long id) throws NotFoundException {
        LOGGER.debug("Delete transcript with ID {}", id);
        try {
            Optional<ContainerTranscript> transcript = containerTranscriptRepository.findById(id);
            if (transcript.isPresent()) {
                List<Message> messages = messageRepository.findMessagesByContainer(id);
                if (messages != null) {
                    messageRepository.deleteAll(messages);
                }
            }
            containerTranscriptRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ContainerTranscript getById(Long id) {
        LOGGER.debug("Get transcript by id {}", id);

        Optional<ContainerTranscript> transcript = containerTranscriptRepository.findById(id);
        if (transcript.isPresent()) {
            return transcript.get();
        } else {
            throw new NotFoundException(String.format("Could not find transcript with id %s", id));
        }
    }
}
