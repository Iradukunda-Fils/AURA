package rw.ac.auca.aura.domain;

import org.junit.jupiter.api.Test;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TimeSlotTest {

    @Test
    public void testValidTimeSlotCreation() {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(10, 0);

        TimeSlot slot = new TimeSlot(DayOfWeek.MONDAY, start, end);
        assertEquals(120, slot.getDurationInMinutes());
    }

    @Test
    public void testInvalidTimeSlotThrowsException() {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(8, 0);

        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(DayOfWeek.MONDAY, start, end));
    }

    @Test
    public void testOverlaps() {
        LocalTime t0800 = LocalTime.of(8, 0);
        LocalTime t1000 = LocalTime.of(10, 0);
        LocalTime t0900 = LocalTime.of(9, 0);
        LocalTime t1100 = LocalTime.of(11, 0);

        TimeSlot slotA = new TimeSlot(DayOfWeek.MONDAY, t0800, t1000);
        TimeSlot slotB = new TimeSlot(DayOfWeek.MONDAY, t0900, t1100);
        TimeSlot slotC = new TimeSlot(DayOfWeek.MONDAY, t1000, t1100);

        assertTrue(slotA.overlaps(slotB));
        assertFalse(slotA.overlaps(slotC), "Adjacent timeslots touching at boundary should not overlap");
    }
}
