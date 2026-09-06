package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.academic.StudentCohort;
import rw.ac.auca.aura.domain.repository.StudentCohortRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.ProgramEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.StudentCohortMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaStudentCohortRepository extends GenericDao<StudentCohortEntity, String> implements StudentCohortRepository {

    public JpaStudentCohortRepository() {
        super(StudentCohortEntity.class);
    }

    @Override
    public Optional<StudentCohort> findById(String id) {
        return findEntityById(id).map(StudentCohortMapper::toDomain);
    }

    @Override
    public List<StudentCohort> findAll() {
        return findAllEntities().stream()
                .map(StudentCohortMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentCohort> findByProgramId(String programId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<StudentCohortEntity> list = em.createQuery(
                    "SELECT sc FROM StudentCohortEntity sc WHERE sc.program.id = :progId", StudentCohortEntity.class)
                    .setParameter("progId", programId)
                    .getResultList();
            return list.stream().map(StudentCohortMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(StudentCohort cohort) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            ProgramEntity progRef = cohort.getProgramId() != null ? em.find(ProgramEntity.class, cohort.getProgramId()) : null;
            StudentCohortEntity entity = StudentCohortMapper.toEntity(cohort, progRef);
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
