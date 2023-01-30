package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "containers")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, length = 100)
    protected String name;

    @ManyToOne
    protected ApplicationUser author;

    @Column(nullable = false)
    protected LocalDateTime timestamp;

    @Column(nullable = false)
    protected Boolean pinned;

    @OneToMany(targetEntity = Message.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    protected Set<Message> messages;

    public Container() {}

    public Container(String name, ApplicationUser author, LocalDateTime timestamp, Boolean pinned) {
        this.name      = name;
        this.author = author;
        this.timestamp = timestamp;
        this.pinned    = pinned;
    }

    public Container(String name, ApplicationUser author, LocalDateTime timestamp, Boolean pinned, Set<Message> messages) {
        this.name      = name;
        this.author = author;
        this.timestamp = timestamp;
        this.pinned    = pinned;
        this.messages  = messages;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
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

        Container that = (Container) o;
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
        return "Container {"
            + "id=" + id
            + ", name='" + name + '\''
            + ", author=" + author
            + ", timestamp=" + timestamp
            + ", pinned=" + pinned
            + '}';
    }
}
