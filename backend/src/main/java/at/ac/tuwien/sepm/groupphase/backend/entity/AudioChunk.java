package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class AudioChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ApplicationUser author;

    @Column(nullable = false, name = "timestamp")
    private LocalDateTime timestamp;

    @Lob
    @Column(nullable = false, name = "data", length = 10000)
    private String data;

    @Column(nullable = true, name = "savedtofileat")
    private LocalDateTime savedToFileAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getAuthor() {
        return author;
    }

    public void setAuthor(ApplicationUser author) {
        this.author = author;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocalDateTime getSavedToFileAt() {
        return savedToFileAt;
    }

    public void setSavedToFileAt(LocalDateTime transcribed) {
        this.savedToFileAt = transcribed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioChunk that = (AudioChunk) o;
        return id.equals(that.id) && author.equals(that.author) && timestamp.equals(that.timestamp) && data.equals(that.data) && savedToFileAt.equals(that.savedToFileAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, timestamp, data, savedToFileAt);
    }

    @Override
    public String toString() {
        return "AudioChunk{"
            + "id=" + id
            + ", author=" + author
            + ", timestamp=" + timestamp
            + ", data='" + data + '\''
            + ", savedToFileAt= " + savedToFileAt
            + '}';
    }
}
