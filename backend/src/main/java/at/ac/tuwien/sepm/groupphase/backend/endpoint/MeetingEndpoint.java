package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MeetingMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1/meetings")
public class MeetingEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MeetingService meetingService;
    private final MeetingMapper meetingMapper;

    @Autowired
    public MeetingEndpoint(MeetingService meetingService, MeetingMapper meetingMapper) {
        this.meetingService = meetingService;
        this.meetingMapper = meetingMapper;
    }


    @Secured("ROLE_EMPLOYEE")
    @GetMapping
    @Operation(summary = "Get all meetings", security = @SecurityRequirement(name = "apiKey"))
    public List<MeetingDto> getAll() {
        LOGGER.trace("GET /api/v1/meetings");
        return meetingMapper.meetingToMeetingListDto(meetingService.findAll());
    }

    @Secured("ROLE_EMPLOYEE")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create new meeting", security = @SecurityRequirement(name = "apiKey"))
    public MeetingDto create(@Valid @RequestBody MeetingDto meetingDto) {
        LOGGER.trace("POST /api/v1/meetings, body: {}", meetingDto);
        try {
            return meetingMapper.meetingToMeetingDto(meetingService.createMeeting(meetingMapper.meetingDtoToMeeting(meetingDto)));
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while creating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while creating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while creating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_EMPLOYEE")
    @PutMapping(value = "/{id}")
    @Operation(summary = "Update meeting", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<MeetingDto> update(@PathVariable Long id, @Valid @RequestBody MeetingDto meetingDto) {
        LOGGER.trace("PUT /api/v1/meetings/{}, body: {}", id, meetingDto);
        try {
            final ResponseEntity<MeetingDto> response;
            UpdateMeetingDto updatedMeetingDto = meetingService.updateMeeting(id, meetingMapper.meetingDtoToMeeting(meetingDto));
            if (updatedMeetingDto.getUpdatedExisting()) {
                response = ResponseEntity.ok(meetingMapper.meetingToMeetingDto(updatedMeetingDto.getMeeting()));
            } else {
                // This isn't possible – why? ––> response = ResponseEntity.created(updatedMeetingDto.getMeeting());
                response = ResponseEntity.status(HttpStatus.CREATED).body(meetingMapper.meetingToMeetingDto(updatedMeetingDto.getMeeting()));
            }
            return response;
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while updating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while updating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while updating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get meeting", security = @SecurityRequirement(name = "apiKey"))
    public MeetingDto getOneById(@PathVariable Long id) {
        LOGGER.trace("GET /api/v1/meetings/{}", id);
        return meetingMapper.meetingToMeetingDto(meetingService.getOneById(id));
    }

}
