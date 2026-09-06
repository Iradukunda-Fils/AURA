package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import rw.ac.auca.aura.domain.academic.ActivityState;
import rw.ac.auca.aura.domain.academic.ActivityType;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "aura_academic_activities")
public class AcademicActivityEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false, foreignKey = @ForeignKey(name = "fk_activity_offering"))
    private CourseOfferingEntity offering;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 30)
    private ActivityType activityType;

    @Column(name = "student_count", nullable = false)
    private int studentCount;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "required_capabilities_json", length = 2000)
    private String requiredCapabilitiesJson;

    @Column(name = "preferred_site_id", nullable = false, length = 50)
    private String preferredSiteId;

    @Column(name = "preferred_day_of_week", nullable = false, length = 20)
    private String preferredDayOfWeek;

    @Column(name = "preferred_start_time", nullable = false)
    private LocalTime preferredStartTime;

    @Column(name = "preferred_end_time", nullable = false)
    private LocalTime preferredEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityState state;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aura_activity_lecturers",
        joinColumns = @JoinColumn(name = "activity_id", foreignKey = @ForeignKey(name = "fk_activity_lecturer_activity")),
        inverseJoinColumns = @JoinColumn(name = "lecturer_id", foreignKey = @ForeignKey(name = "fk_activity_lecturer_lecturer"))
    )
    private Set<LecturerEntity> lecturers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aura_activity_cohorts",
        joinColumns = @JoinColumn(name = "activity_id", foreignKey = @ForeignKey(name = "fk_activity_cohort_activity")),
        inverseJoinColumns = @JoinColumn(name = "cohort_id", foreignKey = @ForeignKey(name = "fk_activity_cohort_cohort"))
    )
    private Set<StudentCohortEntity> cohorts = new HashSet<>();

    public AcademicActivityEntity() {}

    public AcademicActivityEntity(String id, CourseOfferingEntity offering, String title, ActivityType activityType, int studentCount, int durationMinutes, String requiredCapabilitiesJson, String preferredSiteId, String preferredDayOfWeek, LocalTime preferredStartTime, LocalTime preferredEndTime, ActivityState state) {
        this.id = id;
        this.offering = offering;
        this.title = title;
        this.activityType = activityType;
        this.studentCount = studentCount;
        this.durationMinutes = durationMinutes;
        this.requiredCapabilitiesJson = requiredCapabilitiesJson;
        this.preferredSiteId = preferredSiteId;
        this.preferredDayOfWeek = preferredDayOfWeek;
        this.preferredStartTime = preferredStartTime;
        this.preferredEndTime = preferredEndTime;
        this.state = state;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CourseOfferingEntity getOffering() { return offering; }
    public void setOffering(CourseOfferingEntity offering) { this.offering = offering; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getRequiredCapabilitiesJson() { return requiredCapabilitiesJson; }
    public void setRequiredCapabilitiesJson(String requiredCapabilitiesJson) { this.requiredCapabilitiesJson = requiredCapabilitiesJson; }

    public String getPreferredSiteId() { return preferredSiteId; }
    public void setPreferredSiteId(String preferredSiteId) { this.preferredSiteId = preferredSiteId; }

    public String getPreferredDayOfWeek() { return preferredDayOfWeek; }
    public void setPreferredDayOfWeek(String preferredDayOfWeek) { this.preferredDayOfWeek = preferredDayOfWeek; }

    public LocalTime getPreferredStartTime() { return preferredStartTime; }
    public void setPreferredStartTime(LocalTime preferredStartTime) { this.preferredStartTime = preferredStartTime; }

    public LocalTime getPreferredEndTime() { return preferredEndTime; }
    public void setPreferredEndTime(LocalTime preferredEndTime) { this.preferredEndTime = preferredEndTime; }

    public ActivityState getState() { return state; }
    public void setState(ActivityState state) { this.state = state; }

    public Set<LecturerEntity> getLecturers() { return lecturers; }
    public void setLecturers(Set<LecturerEntity> lecturers) { this.lecturers = lecturers; }

    public Set<StudentCohortEntity> getCohorts() { return cohorts; }
    public void setCohorts(Set<StudentCohortEntity> cohorts) { this.cohorts = cohorts; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicActivityEntity that = (AcademicActivityEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
