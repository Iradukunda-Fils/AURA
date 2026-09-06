package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.Program;
import rw.ac.auca.aura.infrastructure.persistence.entities.DepartmentEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.ProgramEntity;

public class ProgramMapper {

    public static Program toDomain(ProgramEntity entity) {
        if (entity == null) return null;
        String deptId = entity.getDepartment() != null ? entity.getDepartment().getId() : null;
        return new Program(entity.getId(), entity.getName(), deptId);
    }

    public static ProgramEntity toEntity(Program domain, DepartmentEntity deptEntity) {
        if (domain == null) return null;
        return new ProgramEntity(domain.getId(), domain.getName(), deptEntity);
    }
}
