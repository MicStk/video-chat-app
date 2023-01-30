package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class MeetingServiceImpl implements MeetingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MeetingRepository meetingRepository;

    private final MeetingValidator meetingValidator;

    public MeetingServiceImpl(MeetingRepository meetingRepository, MeetingValidator meetingValidator) {
        this.meetingRepository = meetingRepository;
        this.meetingValidator = meetingValidator;
    }

    @Override
    public Meeting createMeeting(Meeting meeting) throws ValidationException, ConflictException {
        LOGGER.debug("Create new meeting {}", meeting);
        meetingValidator.validateForCreate(meeting);
        return meetingRepository.save(meeting);
    }

    @Override
    public UpdateMeetingDto updateMeeting(Long id, Meeting meeting) throws ValidationException, ConflictException {
        LOGGER.debug("Update meeting with id {} with data {}", id, meeting);
        meetingValidator.validateForUpdate(id, meeting);
        meeting.setId(id);
        Boolean alreadyExisted = meetingRepository.existsById(id);
        return UpdateMeetingDto.UpdateMeetingDtoBuilder.anUpdateMeetingDto()
            .withMeeting(meetingRepository.save(meeting))
            .withUpdatedExisting(alreadyExisted)
            .build();
    }

    @Override
    public Meeting getOneById(Long id) throws NotFoundException {
        LOGGER.debug("Get meeting with id {}", id);
        Optional<Meeting> result = meetingRepository.findById(id);
        if (result.isEmpty()) {
            throw new NotFoundException("Meeting with ID " + id + " does not exist!");
        }
        return result.get();
    }

    @Override
    public List<Meeting> findAll() {
        LOGGER.debug("Find all meetings");
        return meetingRepository.findAll();
    }
}
