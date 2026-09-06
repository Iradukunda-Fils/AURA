package rw.ac.auca.aura.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.ac.auca.aura.domain.academic.*;
import rw.ac.auca.aura.domain.constraint.CapacitySpecification;
import rw.ac.auca.aura.domain.resource.Building;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.resource.ResourceStatus;
import rw.ac.auca.aura.domain.resource.ResourceType;
import rw.ac.auca.aura.domain.resource.Site;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.CapabilitySet;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SpecificationTest {

    private AcademicActivity activity;
    private Resource smallRoom;
    private Resource largeRoom;
    private TimeSlot timeSlot;

    @BeforeEach
    public void setup() {
        Site masoro = new Site("S1", "Masoro", "Kigali");
        Building building = new Building("B1", "Science Building", masoro.getId());

        smallRoom = new Resource("R1", "Small Room 101", ResourceType.CLASSROOM, 30, masoro.getId(), building.getId(), CapabilitySet.empty(), ResourceStatus.ACTIVE);
        largeRoom = new Resource("R2", "Large Room 202", ResourceType.CLASSROOM, 60, masoro.getId(), building.getId(), CapabilitySet.empty(), ResourceStatus.ACTIVE);

        Department dept = new Department("D1", "CS", "Faculty of IT");
        Program prog = new Program("P1", "Software Eng", dept.getId());
        StudentCohort cohort = new StudentCohort("C1", "BSSE 2026", prog.getId(), 2026, 50);
        Course course = new Course("INSY321_C", "INSY321", "Web Tech", 4, prog.getId());
        Lecturer lecturer = new Lecturer("L1", "ST01", "Dr. Eric", "eric@auca.ac.rw", dept.getId());
        CourseOffering offering = new CourseOffering("O1", course.getId(), "2026-S2", "SecA", Set.of(lecturer.getId()), Set.of(cohort.getId()));

        timeSlot = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));

        activity = new AcademicActivity("ACT1", offering.getId(), course.getCode() + " - " + course.getName(), ActivityType.LECTURE, Set.of(lecturer.getId()), Set.of(cohort.getId()), 50, 120, CapabilitySet.empty(), masoro.getId(), timeSlot, ActivityState.SUBMITTED);
    }

    @Test
    public void testCapacitySpecificationPassesForLargeRoom() {
        CapacitySpecification spec = new CapacitySpecification();
        ConstraintResult result = spec.evaluate(activity, largeRoom, timeSlot, null);

        assertTrue(result.isPassed());
    }

    @Test
    public void testCapacitySpecificationFailsForSmallRoom() {
        CapacitySpecification spec = new CapacitySpecification();
        ConstraintResult result = spec.evaluate(activity, smallRoom, timeSlot, null);

        assertFalse(result.isPassed());
        assertTrue(result.getDetailMessage().contains("Insufficient capacity"));
    }
}
