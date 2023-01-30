package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1/messages")
public class MessageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageEndpoint(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping
    @Operation(summary = "Get list of messages without details", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public List<MessageDto> findAll(@RequestParam(required = false) Long containerId) {
        LOGGER.trace("GET /api/v1/messages");
        return messageMapper.messageToSimpleMessageDto(messageService.findAll(containerId));
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific message", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public MessageDto find(@PathVariable Long id) {
        LOGGER.trace("GET /api/v1/messages/{}", id);
        return messageMapper.messageToSimpleMessageDto(messageService.findOne(id));
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping(path = "/search")
    @Operation(summary = "Get list of messages matching the parameters", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public List<MessageDto> search(MessageSearchDto searchParams) {
        LOGGER.trace("GET /api/v1/messages/search");
        return messageMapper.messageToSimpleMessageDto(messageService.search(searchParams));
    }

    @Secured("ROLE_EMPLOYEE")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new message", security = @SecurityRequirement(name = "apiKey"))
    public MessageDto create(@Valid @RequestBody MessageDto messageDto) {
        try {
            LOGGER.trace("POST /api/v1/messages body: {}", messageDto);
            return messageMapper.messageToSimpleMessageDto(
                messageService.publishMessage(messageMapper.simpleMassageDtoToMessage(messageDto)));
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while creating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while creating message. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while creating message. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }

    }

    @Secured("ROLE_EMPLOYEE")
    @PutMapping(value = "/{id}")
    @Transactional
    @Operation(summary = "Change an existing topic", security = @SecurityRequirement(name = "apiKey"))
    public MessageDto update(@PathVariable Long id, @Valid @RequestBody MessageDto messageDto) {
        try {
            LOGGER.trace("PUT /api/v1/messages/{}", id);
            return messageMapper.messageToSimpleMessageDto(
                messageService.update(id, messageMapper.simpleMassageDtoToMessage(messageDto)));
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while updating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while updating message. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while updating message. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }

    }

    @Secured("ROLE_EMPLOYEE")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete transcript", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.trace("DELETE /api/v1/messages/{}", id);
        messageService.deleteMessage(id);
    }
}
