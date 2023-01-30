package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TopicDto {

    private Long id;
    private String name;

    private UserDto author;
    private LocalDateTime timestamp;
    private Boolean pinned;
    private Set<MessageDto> messages = new HashSet<>();

    public TopicDto() {}

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

    public Set<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageDto> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!super.equals(o)) {
            return false;
        }

        TopicDto that = (TopicDto) o;
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
        return "ContainerTopicDto{"
             + "id=" + id
             + ", name='" + name + '\''
             + ", author=" + author
             + ", timestamp=" + timestamp
             + ", pinned=" + pinned
             + '}';
    }

    public static final class ContainerTopicDtoBuilder {

        private Long id;
        private String name;

        private UserDto author;
        private LocalDateTime timestamp;
        private Boolean pinned;

        private ContainerTopicDtoBuilder() {}

        public static ContainerTopicDtoBuilder aContainerTopicDto() {
            return new ContainerTopicDtoBuilder();
        }

        public ContainerTopicDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ContainerTopicDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ContainerTopicDtoBuilder withAuthor(UserDto author) {
            this.author = author;
            return this;
        }

        public ContainerTopicDtoBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ContainerTopicDtoBuilder withPinned(Boolean pinned) {
            this.pinned = pinned;
            return this;
        }

        public TopicDto build() {
            TopicDto topicDto = new TopicDto();
            topicDto.setId(id);
            topicDto.setName(name);
            topicDto.setAuthor(author);
            topicDto.setTimestamp(timestamp);
            topicDto.setPinned(pinned);
            return topicDto;
        }
    }
}
