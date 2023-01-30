package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AudioChunkDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import org.mapstruct.Mapper;

@Mapper
public interface AudioChunkMapper {

    AudioChunkDto audioChunkToAudioChunkDto(AudioChunk audioChunk);

    AudioChunk audioChunkDtoToAudioChunk(AudioChunkDto audioChunkDto);
}