package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface TopicService {

    /**
     * Get all topics.
     *
     * @return all topics
     */
    List<ContainerTopic> getAll();

    /**
     * Get a topic by id.
     *
     * @param id of the topic
     * @return the topic
     */
    ContainerTopic getById(Long id);

    /**
     * Create a new topic.
     *
     * @param containerTopic to create
     * @return the created containerTopic
     */
    ContainerTopic create(ContainerTopic containerTopic) throws ValidationException, ConflictException;

    /**
     * Update an existing topic.
     *
     * @param id of the topic to update
     * @param containerTopic to update
     * @return updated containerTopic
     */
    ContainerTopic update(Long id, ContainerTopic containerTopic) throws ValidationException, ConflictException;

    /**
     * Delete an existing topic by its ID.
     *
     * @param id of the topic to delete
     * @throws NotFoundException if topic is not found
     */
    void deleteTopic(Long id);
}
