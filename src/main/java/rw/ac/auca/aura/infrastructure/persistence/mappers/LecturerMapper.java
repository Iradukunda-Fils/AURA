package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.Lecturer;
import rw.ac.auca.aura.infrastructure.persistence.entities.DepartmentEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.LecturerEntity;

public class LecturerMapper {

    public static Lecturer toDomain(LecturerEntity entity) {
        if (entity == null) return null;
        String deptId = entity.getDepartment() != null ? entity.getDepartment().getId() : null;
        return new Lecturer(entity.getId(), entity.getStaffNumber(), entity.getFullName(), entity.getEmail(), deptId);
    }

    public static LecturerEntity toEntity(Lecturer domain, DepartmentEntity deptEntity) {
        if (domain == null) return null;
        return new LecturerEntity(domain.getId(), domain.getStaffNumber(), domain.getName(), domain.getEmail(), deptEntity);
    }
}
