package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "title")
    private String title;

    @ManyToOne
    private ApplicationUser author;

    @Column(name = "description")
    private String description;

    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "summary", length = 1000)
    private String summary;

    public Meeting() {
    }

    public Meeting(String title, ApplicationUser author, String description, LocalDateTime startTime, LocalDateTime endTime, String summary) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.summary = summary;
    }

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

    public ApplicationUser getAuthor() {
        return author;
    }

    public void setAuthor(ApplicationUser author) {
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
        Meeting meeting = (Meeting) o;
        return id.equals(meeting.id) && title.equals(meeting.title) && author.equals(meeting.author)
            && Objects.equals(description, meeting.description) && startTime.equals(meeting.startTime) && Objects.equals(endTime, meeting.endTime)
            && Objects.equals(summary, meeting.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, description, startTime, endTime, summary);
    }

    @Override
    public String toString() {
        return "Meeting{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", author=" + author
            + ", description='" + description + '\''
            + ", startTime=" + startTime
            + ", endTime=" + endTime
            + ", summary='" + summary + '\''
            + '}';
    }
}
