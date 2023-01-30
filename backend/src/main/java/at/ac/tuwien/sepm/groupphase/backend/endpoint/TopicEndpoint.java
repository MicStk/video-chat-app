package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopicDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TopicMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/topics")
public class TopicEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TopicMapper topicMapper;
    private final TopicService topicService;

    @Autowired
    public TopicEndpoint(TopicService topicService, TopicMapper topicMapper) {
        this.topicService = topicService;
        this.topicMapper = topicMapper;
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping
    @Operation(summary = "Get all topics", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public List<TopicDto> getAll() {
        LOGGER.trace("GET /api/v1/topics");

        return topicMapper.topicToTopicDto(
            topicService.getAll()
        );
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get a topic by id", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public TopicDto getById(@PathVariable Long id) {
        LOGGER.trace("GET /api/v1/topics/{}", id);

        return topicMapper.topicToTopicDto(
            topicService.getById(id)
        );
    }

    @Secured("ROLE_EMPLOYEE")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add a new topic", security = @SecurityRequirement(name = "apiKey"))
    public TopicDto create(@Valid @RequestBody TopicDto topicDto) {
        LOGGER.trace("POST /api/v1/topics name: {}", topicDto.getName());

        try {
            return topicMapper.topicToTopicDto(
                topicService.create(
                    topicMapper.topicDtoToTopic(topicDto)
                )
            );
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while creating topic. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while creating topic. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while creating topic. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_EMPLOYEE")
    @PutMapping(value = "/{id}")
    @Operation(summary = "Change an existing topic", security = @SecurityRequirement(name = "apiKey"))
    public TopicDto update(@PathVariable Long id, @Valid @RequestBody TopicDto topicDto) {
        LOGGER.trace("PUT /api/v1/topics/{}, body: {}", id, topicDto);
        try {
            return topicMapper.topicToTopicDto(
                topicService.update(
                    id, topicMapper.topicDtoToTopic(topicDto)
                )
            );
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while updating topic. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while updating topic. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while updating topic. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete topic", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.trace("DELETE /api/v1/topics/{}", id);
        topicService.deleteTopic(id);
    }
}
