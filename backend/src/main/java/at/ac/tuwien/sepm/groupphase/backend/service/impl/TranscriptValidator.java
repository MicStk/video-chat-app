package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTranscriptRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TranscriptValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ContainerTranscriptRepository containerTranscriptRepository;
    private final MessageRepository messageRepository;
    private final HttpServletRequest request;

    public TranscriptValidator(ContainerTranscriptRepository containerTranscriptRepository, MessageRepository messageRepository, HttpServletRequest request) {
        this.containerTranscriptRepository = containerTranscriptRepository;
        this.messageRepository        = messageRepository;
        this.request                  = request;
    }

    public void validateForUpdate(Long id, ContainerTranscript containerTranscript) throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({})", containerTranscript);

        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors   = new ArrayList<>();

        if (id != null && containerTranscriptRepository.existsById(containerTranscript.getId())) {
            Optional<ContainerTranscript> tmp = containerTranscriptRepository.findById(id);
            if (tmp.isPresent()) {
                if (tmp.get().getPinned() != containerTranscript.getPinned()) {
                    if (!request.isUserInRole("ROLE_ADMIN")) {
                        validationErrors.add("The parameter pinned can only be changed by the admin.");
                    }
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of transcript for updating failed – bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of transcript for updating failed – conflict with system state.", conflictErrors);
        }
    }
}