package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AudioChunkDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AudioChunkMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.AudioChunkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/audiochunks")
public class AudioChunkEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AudioChunkService audioChunkService;
    private final AudioChunkMapper audioChunkMapper;


    @Autowired
    public AudioChunkEndpoint(AudioChunkService audioChunkService, AudioChunkMapper audioChunkMapper) {
        this.audioChunkService = audioChunkService;
        this.audioChunkMapper = audioChunkMapper;
    }

    @Secured("ROLE_EMPLOYEE")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create new audio chunk", security = @SecurityRequirement(name = "apiKey"))
    public AudioChunkDto create(@Valid @RequestBody AudioChunkDto audioChunkDto) {
        try {
            LOGGER.trace("POST /api/v1/audiochunks, body: {}", audioChunkDto.getAuthor());
            return audioChunkMapper.audioChunkToAudioChunkDto(audioChunkService.createAudioChunk(audioChunkMapper.audioChunkDtoToAudioChunk(audioChunkDto)));
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while creating audio chunk. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while creating audio chunk. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while creating audio chunk. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get audio chunk", security = @SecurityRequirement(name = "apiKey"))
    public AudioChunkDto getOneById(@PathVariable Long id) {
        LOGGER.trace("GET /api/v1/audiochunks/{}", id);
        return audioChunkMapper.audioChunkToAudioChunkDto(audioChunkService.getOneById(id));
    }

    @Secured("ROLE_EMPLOYEE")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete audio chunk", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.trace("DELETE /api/v1/audiochunks/{}", id);
        audioChunkService.deleteAudioChunk(id);
    }
}
