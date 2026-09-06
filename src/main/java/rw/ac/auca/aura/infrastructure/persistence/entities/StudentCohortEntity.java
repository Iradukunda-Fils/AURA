package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_student_cohorts")
public class StudentCohortEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cohort_program"))
    private ProgramEntity program;

    @Column(name = "academic_year", nullable = false)
    private int academicYear;

    @Column(name = "student_count", nullable = false)
    private int studentCount;

    public StudentCohortEntity() {}

    public StudentCohortEntity(String id, String name, ProgramEntity program, int academicYear, int studentCount) {
        this.id = id;
        this.name = name;
        this.program = program;
        this.academicYear = academicYear;
        this.studentCount = studentCount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ProgramEntity getProgram() { return program; }
    public void setProgram(ProgramEntity program) { this.program = program; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentCohortEntity that = (StudentCohortEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
