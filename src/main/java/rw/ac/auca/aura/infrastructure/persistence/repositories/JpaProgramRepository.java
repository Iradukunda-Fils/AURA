package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.academic.Program;
import rw.ac.auca.aura.domain.repository.ProgramRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.DepartmentEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.ProgramEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.ProgramMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaProgramRepository extends GenericDao<ProgramEntity, String> implements ProgramRepository {

    public JpaProgramRepository() {
        super(ProgramEntity.class);
    }

    @Override
    public Optional<Program> findById(String id) {
        return findEntityById(id).map(ProgramMapper::toDomain);
    }

    @Override
    public List<Program> findAll() {
        return findAllEntities().stream()
                .map(ProgramMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Program> findByDepartmentId(String departmentId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<ProgramEntity> list = em.createQuery(
                    "SELECT p FROM ProgramEntity p WHERE p.department.id = :deptId", ProgramEntity.class)
                    .setParameter("deptId", departmentId)
                    .getResultList();
            return list.stream().map(ProgramMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(Program program) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            DepartmentEntity deptRef = program.getDepartmentId() != null ? em.find(DepartmentEntity.class, program.getDepartmentId()) : null;
            ProgramEntity entity = ProgramMapper.toEntity(program, deptRef);
            saveEntity(entity);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void delete(String id) {
        deleteEntity(id);
    }
}
