package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class AudioChunkDto {

    private Long id;

    private UserDto author;

    private LocalDateTime timestamp;

    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioChunkDto that = (AudioChunkDto) o;
        return Objects.equals(id, that.id) && Objects.equals(author, that.author) && Objects.equals(timestamp, that.timestamp) && Objects.equals(data, that.data) /*&& Objects.equals(transcribed, that.transcribed)*/;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, timestamp, data);
    }

    @Override
    public String toString() {
        return "AudioChunkDto{"
            + "id=" + id
            + ", author=" + author
            + ", timestamp=" + timestamp
            + ", data='" + data + '\''
            + '}';
    }

    public static final class AudioChunkDtoBuilder {

        private Long id;

        private UserDto author;

        private LocalDateTime timestamp;

        private byte[] data;

        private AudioChunkDtoBuilder() {
        }

        public static AudioChunkDto.AudioChunkDtoBuilder anAudioChunkDto() {
            return new AudioChunkDto.AudioChunkDtoBuilder();
        }

        public AudioChunkDto.AudioChunkDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AudioChunkDto.AudioChunkDtoBuilder withAuthor(UserDto author) {
            this.author = author;
            return this;
        }

        public AudioChunkDto.AudioChunkDtoBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AudioChunkDto.AudioChunkDtoBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public AudioChunkDto build() {
            AudioChunkDto audioChunkDto = new AudioChunkDto();
            audioChunkDto.setId(id);
            audioChunkDto.setAuthor(author);
            audioChunkDto.setTimestamp(timestamp);
            audioChunkDto.setData(String.valueOf(data));
            return audioChunkDto;
        }
    }
}