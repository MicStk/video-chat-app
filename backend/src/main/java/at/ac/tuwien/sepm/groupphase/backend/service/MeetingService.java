package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface MeetingService {

    /**
     * Create a new meeting in the system.
     *
     * @param meeting to create
     * @return created meeting
     * @throws ValidationException if input validation fails
     * @throws ConflictException if given data conflicts with system state
     */
    Meeting createMeeting(Meeting meeting) throws ValidationException, ConflictException;

    /**
     * Update an existing meeting.
     *
     * @param id of the meeting to update
     * @param meeting updated meting
     * @return updated meetingDTO
     * @throws ValidationException if input validation fails
     * @throws ConflictException if given data conflicts with system state
     */
    UpdateMeetingDto updateMeeting(Long id, Meeting meeting) throws ValidationException, ConflictException;

    /**
     * Get an existing meeting by its ID.
     *
     * @param id of the meeting to fetch
     * @return the meeting
     * @throws NotFoundException if meeting is not found
     */
    Meeting getOneById(Long id) throws NotFoundException;

    /**
     * Find all meeting entries ordered by published at date (descending).
     *
     * @return ordered list of al meeting entries
     */
    List<Meeting> findAll();
}
