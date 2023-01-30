package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AudioChunkRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AudioChunkService;
import at.ac.tuwien.sepm.groupphase.backend.transcription.Transcriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Base64;
import java.util.Optional;

@Service
public class AudioChunkServiceImpl implements AudioChunkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AudioChunkValidator audioChunkValidator;
    private final AudioChunkRepository audioChunkRepository;
    private final Transcriptor transcriptor;

    public AudioChunkServiceImpl(AudioChunkValidator audioChunkValidator, AudioChunkRepository audioChunkRepository, Transcriptor transcriptor) {
        this.audioChunkValidator = audioChunkValidator;
        this.audioChunkRepository = audioChunkRepository;
        this.transcriptor = transcriptor;
    }

    @Override
    public AudioChunk createAudioChunk(AudioChunk audioChunk) throws ValidationException, ConflictException {
        LOGGER.debug("Create new audio chunk {}", audioChunk);
        audioChunkValidator.validateForCreate(audioChunk);
        return audioChunkRepository.save(audioChunk);
    }

    @Override
    public AudioChunk getOneById(Long id) throws NotFoundException {
        LOGGER.debug("Get audio chunk with ID {}", id);
        Optional<AudioChunk> result = audioChunkRepository.findById(id);
        if (result.isEmpty()) {
            throw new NotFoundException("Audio chunk with id " + id + " does not exist!");
        }
        return result.get();
    }

    @Override
    public void deleteAudioChunk(Long id) throws NotFoundException {
        LOGGER.debug("Delete audio chunk with ID {}", id);
        try {
            audioChunkRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] getAudioData(Long id) throws NotFoundException {
        LOGGER.debug("Get audio byte data from base64 with ID {}", id);
        Optional<AudioChunk> chunk = audioChunkRepository.findById(id);
        if (chunk.isPresent()) {
            return Base64.getDecoder().decode(chunk.get().getData());
        } else {
            throw new NotFoundException("Audio chunk not found!");
        }
    }
}
