package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class AudioChunkValidator {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MeetingRepository meetingRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateForCreate(AudioChunk audioChunk) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", audioChunk);

        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors = new ArrayList<>();

        if (audioChunk.getId() != null) {
            validationErrors.add("ID must not be given.");
        }

        if (userRepository.findById(audioChunk.getAuthor().getId()).isEmpty()) {
            conflictErrors.add("The author was not found.");
        }

        if (meetingRepository.findOpenMeetings().isEmpty()) {
            conflictErrors.add("There is no currently running meeting.");
        }

        if (audioChunk.getAuthor() != null) {
            if (!userRepository.existsById(audioChunk.getAuthor().getId())) {
                conflictErrors.add("Author does not exist.");
            }
        } else {
            validationErrors.add("Meeting author must be given.");
        }

        List<Meeting> openMeeting = meetingRepository.findOpenMeetings();

        if (openMeeting.isEmpty()) {
            conflictErrors.add("There is no currently running meeting.");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of audio chunk for updating failed – bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of audio chunk for updating failed – conflict with system state.", conflictErrors);
        }
    }
}
