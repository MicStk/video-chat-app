package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopicDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface TopicMapper {

    @Named("topic")
    TopicDto topicToTopicDto(ContainerTopic containerTopic);

    @IterableMapping(qualifiedByName = "topic")
    List<TopicDto> topicToTopicDto(List<ContainerTopic> containerTopic);

    ContainerTopic topicDtoToTopic(TopicDto topicDto);
}
