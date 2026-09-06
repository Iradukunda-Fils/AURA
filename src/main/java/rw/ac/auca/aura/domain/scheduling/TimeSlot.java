package rw.ac.auca.aura.domain.scheduling;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Value Object representing an immutable academic time interval.
 * Enforces valid time ranges and provides overlap detection.
 */
public final class TimeSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimeSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Day of week cannot be null.");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null.");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time (" + endTime + ") must be strictly after start time (" + startTime + ").");
        }
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Determines whether this time slot overlaps with another time slot.
     * Overlap occurs when the time slots fall on the same day and their intervals intersect:
     * (this.start < other.end) AND (other.start < this.end)
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null) {
            return false;
        }
        if (this.dayOfWeek != other.dayOfWeek) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return dayOfWeek == timeSlot.dayOfWeek &&
                Objects.equals(startTime, timeSlot.startTime) &&
                Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + " - " + endTime;
    }
}
