package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface TranscriptService {

    /**
     * Get an existing transcript by its ID.
     *
     * @param id of the transcript to fetch
     * @return the transcript
     * @throws NotFoundException if transcript is not found
     */
    ContainerTranscript getById(Long id) throws NotFoundException;

    /**
     * Find all transcript entries ordered by published at date (descending).
     *
     * @return ordered list of al transcript entries
     */
    List<ContainerTranscript> getAll();

    /**
     * Update an existing transcript.
     *
     * @param id of the transcript to update
     * @param containerTranscript to update
     * @return updated containerTranscript
     */
    ContainerTranscript update(Long id, ContainerTranscript containerTranscript) throws ValidationException, ConflictException;

    /**
     * Delete an existing transcript by its ID.
     *
     * @param id of the transcript to delete
     * @throws NotFoundException if transcript is not found
     */
    void deleteTranscript(Long id);
}
