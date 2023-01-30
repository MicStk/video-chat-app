package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class MeetingValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public MeetingValidator(MeetingRepository meetingRepository, UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
    }

    public void validateForCreate(Meeting meeting) throws ValidationException, ConflictException {
        LOGGER.trace("validateforCreate({})", meeting);

        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors = new ArrayList<>();

        if (meeting.getId() != null) {
            validationErrors.add("ID must not be given.");
        }

        if (meeting.getTitle() != null) {
            if (meeting.getTitle().isBlank()) {
                validationErrors.add("Meeting title must not be blank.");
            }
            if (meeting.getTitle().length() > 255) {
                validationErrors.add("Meeting title can only be 255 characters long.");
            }
        } else {
            validationErrors.add("No meeting title given.");
        }

        if (meeting.getAuthor() != null) {
            if (!userRepository.existsById(meeting.getAuthor().getId())) {
                conflictErrors.add("Author does not exist.");
            }
        } else {
            validationErrors.add("Meeting author must be given.");
        }

        if (meeting.getDescription() != null) {
            if (meeting.getDescription().isBlank()) {
                validationErrors.add("Meeting description was given, but is blank.");
            }
            if (meeting.getDescription().length() > 255) {
                validationErrors.add("Meeting description can only be 255 characters long.");
            }
        }

        if (meeting.getStartTime() == null) {
            validationErrors.add("No meeting start time given.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (meeting.getStartTime().isAfter(now)) {
            validationErrors.add("Meeting start must not be in the future.");
        }

        if (meeting.getEndTime() != null) {
            if (meeting.getStartTime().isAfter(meeting.getEndTime()) || meeting.getStartTime().isEqual(meeting.getEndTime())) {
                validationErrors.add("Meeting end must be after meeting start.");
            }
            if (meeting.getEndTime().isAfter(now)) {
                validationErrors.add("Meeting end must not be in the future.");
            }
        }

        if (meeting.getSummary() != null) {
            if (meeting.getEndTime() == null) {
                validationErrors.add("Summary must only be given if meeting has already ended.");
            }
            if (meeting.getSummary().isBlank()) {
                validationErrors.add("Meeting summary was given, but is blank.");
            }
            if (meeting.getSummary().length() > 1000) {
                validationErrors.add("Meeting summary can only be 1000 characters long.");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of meeting for updating failed – bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of meeting for updating failed – conflict with system state.", conflictErrors);
        }
    }

    public void validateForUpdate(Long id, Meeting meeting) throws ValidationException, ConflictException {
        LOGGER.trace("validateforUpdate({})", meeting);
        final List<String> validationErrors = new ArrayList<>();
        final List<String> conflictErrors = new ArrayList<>();

        if (id == null) {
            validationErrors.add("No ID given in URI.");
        } else {
            Optional<Meeting> oldMeeting;
            oldMeeting = meetingRepository.findById(id);

            if (oldMeeting.isEmpty()) {
                conflictErrors.add("Meeting to be updated does not exist.");
            } else {
                if (meeting.getId() != null && !Objects.equals(id, meeting.getId())) {
                    validationErrors.add("ID was given in body, but IDs in URI and body do not match.");
                }
                if (meeting.getAuthor() != null) {
                    if (!userRepository.existsById(meeting.getAuthor().getId())) {
                        conflictErrors.add("Author does not exist.");
                    }
                } else {
                    validationErrors.add("Meeting author must be given.");
                }
                if (meeting.getDescription() != null) {
                    if (meeting.getDescription().isBlank()) {
                        validationErrors.add("Meeting description was given, but is blank.");
                    }
                    if (meeting.getDescription().length() > 255) {
                        validationErrors.add("Meeting description can only be 255 characters long.");
                    }
                }
                if (!(meeting.getStartTime().isEqual(oldMeeting.get().getStartTime()))) {
                    conflictErrors.add("Meeting start time cannot be changed retroactively.");
                }

                LocalDateTime now = LocalDateTime.now();

                if (meeting.getEndTime() == null && oldMeeting.get().getEndTime() != null) {
                    conflictErrors.add("Already finished meeting must not be reopened.");
                }
                if (meeting.getEndTime() != null && oldMeeting.get().getEndTime() == null) {
                    if (meeting.getStartTime().isAfter(meeting.getEndTime()) || meeting.getStartTime().isEqual(meeting.getEndTime())) {
                        validationErrors.add("Meeting end must be after meeting start.");
                    }
                    if (meeting.getEndTime().isAfter(now)) {
                        validationErrors.add("Meeting end must not be in the future.");
                    }
                }
                if (meeting.getEndTime() != null && oldMeeting.get().getEndTime() != null && !(meeting.getEndTime().isEqual(oldMeeting.get().getEndTime()))) {
                    conflictErrors.add("Meeting end time cannot be changed retroactively.");
                }

                if (meeting.getSummary() != null) {
                    if (meeting.getEndTime() == null) {
                        validationErrors.add("Summary must only be given if meeting has already ended.");
                    }
                    if (meeting.getSummary().isBlank()) {
                        validationErrors.add("Meeting summary was given, but is blank.");
                    }
                    if (meeting.getSummary().length() > 1000) {
                        validationErrors.add("Meeting summary can only be 1000 characters long.");
                    }
                }

                if (meeting.getTitle() != null) {
                    if (meeting.getTitle().isBlank()) {
                        validationErrors.add("Meeting title must not be blank.");
                    }
                    if (meeting.getTitle().length() > 255) {
                        validationErrors.add("Meeting title can only be 255 characters long.");
                    }
                } else {
                    validationErrors.add("No meeting title given.");
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of meeting for updating failed – bad input.", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of meeting for updating failed – conflict with system state.", conflictErrors);
        }
    }
}
