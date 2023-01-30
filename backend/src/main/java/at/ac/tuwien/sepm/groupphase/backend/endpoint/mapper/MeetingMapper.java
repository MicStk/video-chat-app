package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MeetingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface MeetingMapper {

    @Named("meeting")
    MeetingDto meetingToMeetingDto(Meeting meeting);

    @IterableMapping(qualifiedByName = "meeting")
    List<MeetingDto> meetingToMeetingListDto(List<Meeting> meetings);

    Meeting meetingDtoToMeeting(MeetingDto meetingDto);
}
