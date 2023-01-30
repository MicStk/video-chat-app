package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class MeetingDto {

    private Long id;
    private String title;
    private UserDto author;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String summary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeetingDto that = (MeetingDto) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(author, that.author)
            && Objects.equals(description, that.description) && Objects.equals(startTime, that.startTime)
            && Objects.equals(endTime, that.endTime) && Objects.equals(summary, that.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, description, startTime, endTime, summary);
    }

    @Override
    public String toString() {
        return "MeetingDto{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", author=" + author
            + ", description='" + description + '\''
            + ", startTime=" + startTime
            + ", endTime=" + endTime
            + ", summary='" + summary + '\''
            + '}';
    }

    public static final class MeetingDtoBuilder {
        private Long id;
        private String title;
        private UserDto author;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String summary;

        private MeetingDtoBuilder() {
        }

        public static MeetingDto.MeetingDtoBuilder aMeetingDto() {
            return new MeetingDto.MeetingDtoBuilder();
        }

        public MeetingDto.MeetingDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public MeetingDto.MeetingDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public MeetingDto.MeetingDtoBuilder withAuthor(UserDto author) {
            this.author = author;
            return this;
        }

        public MeetingDto.MeetingDtoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public MeetingDto.MeetingDtoBuilder withStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public MeetingDto.MeetingDtoBuilder withEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public MeetingDto.MeetingDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public MeetingDto build() {
            MeetingDto meetingDto = new MeetingDto();
            meetingDto.setId(id);
            meetingDto.setTitle(title);
            meetingDto.setAuthor(author);
            meetingDto.setDescription(description);
            meetingDto.setStartTime(startTime);
            meetingDto.setEndTime(endTime);
            meetingDto.setSummary(summary);
            return meetingDto;
        }
    }
}