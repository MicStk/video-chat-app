package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Named("simpleMessage")
    MessageDto messageToSimpleMessageDto(Message message);

    /**
     * This is necessary since the MessageDto misses the text property and the collection mapper can't handle
     * missing fields.
     **/
    @IterableMapping(qualifiedByName = "simpleMessage")
    List<MessageDto> messageToSimpleMessageDto(List<Message> message);

    Message simpleMassageDtoToMessage(MessageDto messageDto);
}

