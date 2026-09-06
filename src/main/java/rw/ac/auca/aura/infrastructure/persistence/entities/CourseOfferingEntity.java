package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "aura_course_offerings", uniqueConstraints = {
    @UniqueConstraint(name = "uk_offering_course_period", columnNames = {"course_id", "academic_year", "semester", "section"})
})
public class CourseOfferingEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_offering_course"))
    private CourseEntity course;

    @Column(name = "academic_year", nullable = false)
    private int academicYear;

    @Column(nullable = false, length = 20)
    private String semester;

    @Column(nullable = false, length = 50)
    private String section;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aura_offering_lecturers",
        joinColumns = @JoinColumn(name = "offering_id", foreignKey = @ForeignKey(name = "fk_offering_lecturer_offering")),
        inverseJoinColumns = @JoinColumn(name = "lecturer_id", foreignKey = @ForeignKey(name = "fk_offering_lecturer_lecturer"))
    )
    private Set<LecturerEntity> lecturers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aura_offering_cohorts",
        joinColumns = @JoinColumn(name = "offering_id", foreignKey = @ForeignKey(name = "fk_offering_cohort_offering")),
        inverseJoinColumns = @JoinColumn(name = "cohort_id", foreignKey = @ForeignKey(name = "fk_offering_cohort_cohort"))
    )
    private Set<StudentCohortEntity> cohorts = new HashSet<>();

    public CourseOfferingEntity() {}

    public CourseOfferingEntity(String id, CourseEntity course, int academicYear, String semester, String section) {
        this.id = id;
        this.course = course;
        this.academicYear = academicYear;
        this.semester = semester;
        this.section = section;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CourseEntity getCourse() { return course; }
    public void setCourse(CourseEntity course) { this.course = course; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Set<LecturerEntity> getLecturers() { return lecturers; }
    public void setLecturers(Set<LecturerEntity> lecturers) { this.lecturers = lecturers; }

    public Set<StudentCohortEntity> getCohorts() { return cohorts; }
    public void setCohorts(Set<StudentCohortEntity> cohorts) { this.cohorts = cohorts; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseOfferingEntity that = (CourseOfferingEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
