package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageDto {

    private Long id;

    private LocalDateTime timestamp;

    private String content;

    private UserDto user;

    private boolean isTranscript;

    private ContainerTopic container;

    private LocalDateTime editedAt;

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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public boolean getIsTranscript() {
        return isTranscript;
    }

    public void setIsTranscript(boolean transcript) {
        isTranscript = transcript;
    }

    public ContainerTopic getContainer() {
        return container;
    }

    public void setContainer(ContainerTopic container) {
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
        if (!(o instanceof MessageDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(timestamp, that.timestamp)
            && Objects.equals(content, that.content)
            && Objects.equals(user, that.user)
            && Objects.equals(isTranscript, that.isTranscript);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, content, user);
    }

    @Override
    public String toString() {
        return "MessageDto{"
            + "id=" + id
            + ", timestamp=" + timestamp
            + ", content='" + content + '\''
            + ", user=" + user
            + ", isTranscript =" + isTranscript
            + '}';
    }

    public static final class SimpleMessageDtoBuilder {
        private Long id;
        private LocalDateTime timestamp;
        private String content;
        private UserDto user;
        private ContainerTopic container;
        private LocalDateTime editedAt;
        private boolean isTranscript;

        private SimpleMessageDtoBuilder() {
        }

        public static SimpleMessageDtoBuilder aSimpleMessageDto() {
            return new SimpleMessageDtoBuilder();
        }

        public SimpleMessageDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SimpleMessageDtoBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SimpleMessageDtoBuilder withIsTrancript(boolean isTranscript) {
            this.isTranscript = isTranscript;
            return this;
        }

        public SimpleMessageDtoBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public SimpleMessageDtoBuilder withUser(UserDto user) {
            this.user = user;
            return this;
        }

        public SimpleMessageDtoBuilder withContainer(ContainerTopic container) {
            this.container = container;
            return this;
        }

        private SimpleMessageDtoBuilder withEditedAt(LocalDateTime editedAt) {
            this.editedAt = editedAt;
            return this;
        }

        public MessageDto build() {
            MessageDto messageDto = new MessageDto();
            messageDto.setId(id);
            messageDto.setTimestamp(timestamp);
            messageDto.setContent(content);
            messageDto.setUser(user);
            messageDto.setContainer(container);
            messageDto.setEditedAt(editedAt);
            messageDto.setIsTranscript(isTranscript);
            return messageDto;
        }
    }
}