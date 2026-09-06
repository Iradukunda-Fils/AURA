package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.Department;
import rw.ac.auca.aura.infrastructure.persistence.entities.DepartmentEntity;

public class DepartmentMapper {

    public static Department toDomain(DepartmentEntity entity) {
        if (entity == null) return null;
        return new Department(entity.getId(), entity.getName(), entity.getFaculty());
    }

    public static DepartmentEntity toEntity(Department domain) {
        if (domain == null) return null;
        return new DepartmentEntity(domain.getId(), domain.getName(), domain.getCode());
    }
}
