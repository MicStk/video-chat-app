package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TranscriptDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;


@Mapper
public interface TranscriptMapper {

    @Named("transcript")
    TranscriptDto transcriptToTranscriptDto(ContainerTranscript transcript);

    @IterableMapping(qualifiedByName = "transcript")
    List<TranscriptDto> transcriptToTranscriptListDto(List<ContainerTranscript> transcript);

    ContainerTranscript transcriptDtoToTranscript(TranscriptDto transcriptDto);
}

