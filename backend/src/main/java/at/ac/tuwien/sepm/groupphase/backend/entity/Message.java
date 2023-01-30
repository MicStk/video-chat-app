package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "timestamp")
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    @Type(type = "true_false")
    private boolean isTranscript;

    @ManyToOne
    private ApplicationUser user;

    @ManyToOne
    private Container container;

    @Column(nullable = true, name = "editedAt")
    private LocalDateTime editedAt;

    public Message() {
    }

    public boolean getIsTranscript() {
        return isTranscript;
    }

    public void setIsTranscript(boolean transcript) {
        isTranscript = transcript;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message message)) {
            return false;
        }
        return Objects.equals(id, message.id)
            && Objects.equals(timestamp, message.timestamp)
            && Objects.equals(content, message.content)
            && Objects.equals(user, message.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, content, user);
    }

    @Override
    public String toString() {
        return "Message{"
            + "id=" + id
            + ", timestamp=" + timestamp
            + ", content='" + content + '\''
            + ", user=" + user
            + '}';
    }

    public static final class MessageBuilder {
        private Long id;
        private LocalDateTime timestamp;
        private String content;
        private ApplicationUser user;
        private Container container;
        private LocalDateTime editedAt;
        private boolean isTranscript;

        private MessageBuilder() {
        }

        public static MessageBuilder aMessage() {
            return new MessageBuilder();
        }

        public MessageBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public MessageBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MessageBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public MessageBuilder withUser(ApplicationUser user) {
            this.user = user;
            return this;
        }

        public MessageBuilder withContainer(Container container) {
            this.container = container;
            return this;
        }

        public MessageBuilder withIsTranscript(boolean isTranscript) {
            this.isTranscript = isTranscript;
            return this;
        }

        public MessageBuilder withEditedAt(LocalDateTime editedAt) {
            this.editedAt = editedAt;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.setId(id);
            message.setTimestamp(timestamp);
            message.setContent(content);
            message.setUser(user);
            message.setContainer(container);
            message.setEditedAt(editedAt);
            message.setIsTranscript(isTranscript);
            return message;
        }
    }
}