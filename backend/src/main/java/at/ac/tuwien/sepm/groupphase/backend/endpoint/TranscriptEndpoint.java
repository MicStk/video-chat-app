package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TranscriptDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TranscriptMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.TranscriptService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/transcripts")
public class TranscriptEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TranscriptMapper transcriptMapper;
    private final TranscriptService transcriptService;

    @Autowired
    public TranscriptEndpoint(TranscriptService transcriptService, TranscriptMapper transcriptMapper) {
        this.transcriptService = transcriptService;
        this.transcriptMapper = transcriptMapper;
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping
    @Operation(summary = "Get all transcripts", security = @SecurityRequirement(name = "apiKey"))
    public List<TranscriptDto> getAll() {
        LOGGER.trace("GET /api/v1/transcripts");

        return transcriptMapper.transcriptToTranscriptListDto(transcriptService.getAll()
        );
    }

    @Secured("ROLE_EMPLOYEE")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get a transcript by id", security = @SecurityRequirement(name = "apiKey"))
    public TranscriptDto getById(@PathVariable Long id) {
        LOGGER.trace("GET /api/v1/transcript/{}", id);

        return transcriptMapper.transcriptToTranscriptDto(
            transcriptService.getById(id)
        );
    }

    @Secured("ROLE_EMPLOYEE")
    @PutMapping(value = "/{id}")
    @Operation(summary = "Change an existing transcript", security = @SecurityRequirement(name = "apiKey"))
    public TranscriptDto update(@PathVariable Long id, @Valid @RequestBody TranscriptDto transcriptDto) {
        LOGGER.trace("PUT /api/v1/transcript/{}, body: {}", id, transcriptDto);
        try {
            return transcriptMapper.transcriptToTranscriptDto(
                transcriptService.update(
                    id, transcriptMapper.transcriptDtoToTranscript(transcriptDto)
                )
            );
        } catch (NotFoundException e) {
            LOGGER.error("Not found error while updating transcript. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            LOGGER.error("Validation error while updating transcript. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while updating transcript. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete transcript", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.trace("DELETE /api/v1/transcript/{}", id);
        transcriptService.deleteTranscript(id);
    }

}
