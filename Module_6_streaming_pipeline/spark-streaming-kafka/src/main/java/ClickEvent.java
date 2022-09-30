import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;


public class ClickEvent {
    //using java.util.Date for better readability
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss:SSS")
    private Date timestamp;
    private String page;
    private long userId;
    private long duration;

    public ClickEvent() {
    }

    public ClickEvent(final Date timestamp, final String page, final long userId, long duration) {
        this.timestamp = timestamp;
        this.page = page;
        this.userId = userId;
        this.duration = duration;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPage() {
        return page;
    }

    public void setPage(final String page) {
        this.page = page;
    }

    public long getUserId() { return userId; }

    public void setUserId(long userId) { this.userId = userId; }

    public long getDuration() { return duration; }

    public void setDuration(long duration) { this.duration = duration; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ClickEvent that = (ClickEvent) o;
        return Objects.equals(timestamp, that.timestamp) && Objects.equals(page, that.page) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, page, userId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClickEvent{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", page='").append(page).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append('}');
        return sb.toString();
    }



}
