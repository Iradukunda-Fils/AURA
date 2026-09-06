package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_programs")
public class ProgramEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false, foreignKey = @ForeignKey(name = "fk_program_department"))
    private DepartmentEntity department;

    public ProgramEntity() {}

    public ProgramEntity(String id, String name, DepartmentEntity department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DepartmentEntity getDepartment() { return department; }
    public void setDepartment(DepartmentEntity department) { this.department = department; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramEntity that = (ProgramEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
