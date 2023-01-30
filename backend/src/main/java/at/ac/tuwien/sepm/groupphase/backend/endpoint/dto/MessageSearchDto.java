package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageSearchDto {

    private String text;
    private String author;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toTime;

    public MessageSearchDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalDateTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalDateTime toTime) {
        this.toTime = toTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MessageSearchDto that)) {
            return false;
        }

        return Objects.equals(text, that.text)
            && Objects.equals(author, that.author)
            && Objects.equals(fromTime, that.fromTime)
            && Objects.equals(toTime, that.toTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, author, fromTime, toTime);
    }

    @Override
    public String toString() {
        return "MessageSearchDto{"
            + "text='" + text + '\''
            + ", author=" + author
            + ", fromTime=" + fromTime
            + ", toTime=" + toTime
            + '}';
    }

    public static final class MessageSearchDtoBuilder {

        private String text;
        private String author;
        private LocalDateTime fromTime;
        private LocalDateTime toTime;

        private MessageSearchDtoBuilder() {
        }

        public static MessageSearchDtoBuilder aSimpleMessageDto() {
            return new MessageSearchDtoBuilder();
        }

        public MessageSearchDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public MessageSearchDtoBuilder withAuthorId(String author) {
            this.author = author;
            return this;
        }

        public MessageSearchDtoBuilder withFromTime(LocalDateTime fromTime) {
            this.fromTime = fromTime;
            return this;
        }

        public MessageSearchDtoBuilder withToTime(LocalDateTime toTime) {
            this.toTime = toTime;
            return this;
        }

        public MessageSearchDto build() {
            MessageSearchDto messageSearchDto = new MessageSearchDto();
            messageSearchDto.setText(text);
            messageSearchDto.setAuthor(author);
            messageSearchDto.setFromTime(fromTime);
            messageSearchDto.setToTime(toTime);
            return messageSearchDto;
        }
    }
}