package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;

import java.util.Objects;

public class UpdateMeetingDto {

    private Meeting meeting;

    private Boolean updatedExisting;

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Boolean getUpdatedExisting() {
        return updatedExisting;
    }

    public void setUpdatedExisting(Boolean updatedExisting) {
        this.updatedExisting = updatedExisting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateMeetingDto that = (UpdateMeetingDto) o;
        return meeting.equals(that.meeting) && updatedExisting.equals(that.updatedExisting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meeting, updatedExisting);
    }

    @Override
    public String toString() {
        return "UpdateMeetingDto{"
            + "meeting=" + meeting
            + ", updatedExisting=" + updatedExisting
            + '}';
    }

    public static final class UpdateMeetingDtoBuilder {
        private Meeting meeting;
        private Boolean updatedExisting;

        private UpdateMeetingDtoBuilder() {
        }

        public static UpdateMeetingDtoBuilder anUpdateMeetingDto() {
            return new UpdateMeetingDtoBuilder();
        }

        public UpdateMeetingDtoBuilder withMeeting(Meeting meeting) {
            this.meeting = meeting;
            return this;
        }

        public UpdateMeetingDtoBuilder withUpdatedExisting(Boolean updatedExisting) {
            this.updatedExisting = updatedExisting;
            return this;
        }

        public UpdateMeetingDto build() {
            UpdateMeetingDto updateMeetingDto = new UpdateMeetingDto();
            updateMeetingDto.setMeeting(meeting);
            updateMeetingDto.setUpdatedExisting(updatedExisting);
            return updateMeetingDto;
        }
    }
}