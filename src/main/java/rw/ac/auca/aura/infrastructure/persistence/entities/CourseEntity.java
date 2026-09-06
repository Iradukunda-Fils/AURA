package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_courses", uniqueConstraints = {
    @UniqueConstraint(name = "uk_course_code", columnNames = {"code"})
})
public class CourseEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(name = "credit_hours", nullable = false)
    private int creditHours;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_program"))
    private ProgramEntity program;

    public CourseEntity() {}

    public CourseEntity(String id, String code, String title, int creditHours, ProgramEntity program) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.creditHours = creditHours;
        this.program = program;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }

    public ProgramEntity getProgram() { return program; }
    public void setProgram(ProgramEntity program) { this.program = program; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseEntity that = (CourseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
