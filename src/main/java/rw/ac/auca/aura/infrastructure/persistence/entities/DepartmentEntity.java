package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_departments")
public class DepartmentEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String faculty;

    public DepartmentEntity() {}

    public DepartmentEntity(String id, String name, String faculty) {
        this.id = id;
        this.name = name;
        this.faculty = faculty;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentEntity that = (DepartmentEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
