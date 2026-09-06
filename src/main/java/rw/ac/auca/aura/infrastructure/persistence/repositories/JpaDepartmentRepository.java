package rw.ac.auca.aura.infrastructure.persistence.repositories;

import rw.ac.auca.aura.domain.academic.Department;
import rw.ac.auca.aura.domain.repository.DepartmentRepository;
import rw.ac.auca.aura.infrastructure.persistence.entities.DepartmentEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.DepartmentMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaDepartmentRepository extends GenericDao<DepartmentEntity, String> implements DepartmentRepository {

    public JpaDepartmentRepository() {
        super(DepartmentEntity.class);
    }

    @Override
    public Optional<Department> findById(String id) {
        return findEntityById(id).map(DepartmentMapper::toDomain);
    }

    @Override
    public List<Department> findAll() {
        return findAllEntities().stream()
                .map(DepartmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Department department) {
        DepartmentEntity entity = DepartmentMapper.toEntity(department);
        saveEntity(entity);
    }

    @Override
    public void delete(String id) {
        deleteEntity(id);
    }
}
