package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

public interface AudioChunkService {

    /**
     * Save an audio chunk into the system.
     *
     * @param audioChunk the audio chunk
     * @return the saved audio chunk
     * @throws ValidationException if input validation fails
     * @throws ConflictException if given data conflicts with system state
     */
    AudioChunk createAudioChunk(AudioChunk audioChunk) throws ValidationException, ConflictException;

    /**
     * Get an existing audio chunk by its ID.
     *
     * @param id of the audio chunk to fetch
     * @return the audio chunk
     * @throws NotFoundException if audio chunk is not found
     */
    AudioChunk getOneById(Long id) throws NotFoundException;

    /**
     * Delete an existing audio chunk by its ID.
     *
     * @param id of the audio chunk to delete
     * @throws NotFoundException if audio chunk is not found
     */
    void deleteAudioChunk(Long id) throws NotFoundException;

    /**
     * Get an existing audio chunk by its ID.
     *
     * @param id of the audio chunk to fetch
     *                * @return the audio chunk as byte array
     * @throws NotFoundException if audio chunk is not found
     */
    byte[] getAudioData(Long id) throws NotFoundException;
}
