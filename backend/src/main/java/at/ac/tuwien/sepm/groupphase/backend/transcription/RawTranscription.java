package at.ac.tuwien.sepm.groupphase.backend.transcription;

import java.util.Objects;

public class RawTranscription {

    private Double start;
    private Double end;
    private String transcript;

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Double getEnd() {
        return end;
    }

    public void setEnd(Double end) {
        this.end = end;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RawTranscription that = (RawTranscription) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end) && Objects.equals(transcript, that.transcript);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, transcript);
    }

    @Override
    public String toString() {
        return "RawTranscription{"
            + "start=" + start
            + ", end=" + end
            + ", transcript='" + transcript + '\''
            + '}';
    }
}
