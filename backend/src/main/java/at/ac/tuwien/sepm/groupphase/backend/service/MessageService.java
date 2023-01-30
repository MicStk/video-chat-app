package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface MessageService {

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @param containerId ID of container which contains message
     * @return ordered list of all message entries
     */
    List<Message> findAll(Long containerId);

    /**
     * Find a single message entry by id.
     *
     * @param id the id of the message entry
     * @return the message entry
     */
    Message findOne(Long id);

    /**
     * Find all message entries matching the search parameters.
     *
     * @param messageSearch the search parameters
     * @return ordered list of all matching message entries
     */
    List<Message> search(MessageSearchDto messageSearch);

    /**
     * Publish a single message entry.
     *
     * @param message to publish
     * @return published message entry
     * @throws ValidationException if input validation fails
     * @throws ConflictException if given data conflicts with system state
     */
    Message publishMessage(Message message) throws ValidationException, ConflictException;

    /**
     * Delete an existing message by its ID.
     *
     * @param id of the message to delete
     * @throws NotFoundException if message is not found
     */
    void deleteMessage(Long id);

    /**
     * Update an existing message by its ID.
     *
     * @param id of the message to update
     * @param message to update
     * @throws NotFoundException if message is not found
     * @throws ValidationException if input validation fails
     * @throws ConflictException if given data conflicts with system state
     */
    Message update(Long id, Message message) throws ValidationException, ConflictException;

}
