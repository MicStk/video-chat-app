package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@DiscriminatorValue("TRANSCRIPT")
public class ContainerTranscript extends Container  {

    @Column(name = "description")
    private String description;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "summary")
    private String summary;

    @Column(nullable = false, name = "progress")
    private Long progress;

    @Column(name = "total_steps", nullable = false)
    private Long totalSteps;

    public ContainerTranscript() {}

    public ContainerTranscript(String name, ApplicationUser author, LocalDateTime timestamp, Boolean pinned, String description, LocalDateTime endTime, String summary, Set<Message> messages) {
        this.name      = name;
        this.author = author;
        this.timestamp = timestamp;
        this.pinned    = pinned;
        this.description = description;
        this.endTime = endTime;
        this.summary = summary;
        this.messages = messages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getProgress() {
        return progress;
    }

    public void setProgress(Long progress) {
        this.progress = progress;
    }

    public ApplicationUser getAuthor() {
        return author;
    }

    public void setAuthor(ApplicationUser author) {
        this.author = author;
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
        if (!super.equals(o)) {
            return false;
        }
        ContainerTranscript that = (ContainerTranscript) o;
        return Objects.equals(description, that.description) && endTime.equals(that.endTime) && Objects.equals(summary, that.summary) && Objects.equals(messages, that.messages)
            && Objects.equals(author, that.author) && Objects.equals(progress, that.progress) && Objects.equals(totalSteps, that.totalSteps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, endTime, summary, messages, author, progress, totalSteps);
    }

    @Override
    public String toString() {
        return "ContainerTranscript{"
             + "description='" + description + '\''
             + ", endTime=" + endTime
             + ", summary='" + summary + '\''
             + ", messages=" + messages
             + ", id=" + id
             + ", name='" + name + '\''
             + ", author=" + author
             + ", timestamp=" + timestamp
             + ", pinned=" + pinned
             + ", progress=" + progress
             + ", totalSteps=" + totalSteps
             + '}';
    }
}
