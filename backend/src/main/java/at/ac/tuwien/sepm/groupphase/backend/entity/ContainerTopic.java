package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@DiscriminatorValue("TOPIC")
public class ContainerTopic extends Container  {

    public ContainerTopic() {}

    public ContainerTopic(String name, ApplicationUser author, LocalDateTime timestamp, Boolean pinned, Set<Message> messages) {
        this.name      = name;
        this.author = author;
        this.timestamp = timestamp;
        this.pinned    = pinned;
        this.messages  = messages;
    }


    public ApplicationUser getAuthor() {
        return author;
    }

    public void setAuthor(ApplicationUser author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContainerTopic that = (ContainerTopic) o;
        return Objects.equals(id, that.id)
            && Objects.equals(name, that.name)
            && Objects.equals(author, that.author)
            && Objects.equals(timestamp, that.timestamp)
            && Objects.equals(pinned, that.pinned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author, timestamp, pinned);
    }

    @Override
    public String toString() {
        return "ContainerTopic{"
             + "id=" + id
             + ", name='" + name + '\''
             + ", author=" + author
             + ", timestamp=" + timestamp
             + ", pinned=" + pinned
             + '}';
    }
}
