package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class TranscriptDto {
    private Long id;
    private String name;
    private UserDto author;
    private String description;
    private LocalDateTime timestamp;
    private LocalDateTime endTime;
    private Boolean pinned;
    private String summary;
    private Long progress;
    private Long totalSteps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getProgress() {
        return progress;
    }

    public void setProgress(Long progress) {
        this.progress = progress;
    }

    public Long getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Long totalSteps) {
        this.totalSteps = totalSteps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TranscriptDto that = (TranscriptDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(author, that.author) && Objects.equals(description, that.description)
            && Objects.equals(timestamp, that.timestamp) && Objects.equals(endTime, that.endTime) && Objects.equals(pinned, that.pinned) && Objects.equals(summary, that.summary)
            && progress.equals(that.progress) && totalSteps.equals(that.totalSteps);
    }

    @Override
    public String toString() {
        return "TranscriptDto{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", author=" + author
            + ", description='" + description + '\''
            + ", timestamp=" + timestamp
            + ", endTime=" + endTime
            + ", pinned=" + pinned
            + ", summary='" + summary + '\''
            + ", progress=" + progress
            + ", totalSteps=" + totalSteps
            + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author, description, timestamp, endTime, pinned, summary, progress, totalSteps);
    }

    public static final class TranscriptDtoBuilder {
        private Long id;
        private String name;
        private UserDto author;
        private String description;
        private LocalDateTime timestamp;
        private LocalDateTime endTime;
        private Boolean pinned;
        private String summary;
        private Long progress;
        private Long totalSteps;

        private TranscriptDtoBuilder() {
        }

        public static TranscriptDto.TranscriptDtoBuilder aTranscriptDto() {
            return new TranscriptDto.TranscriptDtoBuilder();
        }

        public TranscriptDto.TranscriptDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withAuthor(UserDto author) {
            this.author = author;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withPinned(Boolean pinned) {
            this.pinned = pinned;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withProgress(Long progress) {
            this.progress = progress;
            return this;
        }

        public TranscriptDto.TranscriptDtoBuilder withTotalSteps(Long totalSteps) {
            this.totalSteps = totalSteps;
            return this;
        }

        public TranscriptDto build() {
            TranscriptDto transcriptDto = new TranscriptDto();
            transcriptDto.setId(id);
            transcriptDto.setName(name);
            transcriptDto.setAuthor(author);
            transcriptDto.setDescription(description);
            transcriptDto.setTimestamp(timestamp);
            transcriptDto.setEndTime(endTime);
            transcriptDto.setPinned(pinned);
            transcriptDto.setSummary(summary);
            transcriptDto.setProgress(progress);
            transcriptDto.setProgress(totalSteps);
            return transcriptDto;
        }
    }
}