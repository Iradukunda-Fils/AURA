package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.academic.Student;
import rw.ac.auca.aura.domain.repository.StudentRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.StudentMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaStudentRepository extends GenericDao<StudentEntity, String> implements StudentRepository {

    public JpaStudentRepository() {
        super(StudentEntity.class);
    }

    @Override
    public Optional<Student> findById(String id) {
        return findEntityById(id).map(StudentMapper::toDomain);
    }

    @Override
    public Optional<Student> findByStudentNumber(String studentNumber) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<StudentEntity> list = em.createQuery(
                    "SELECT s FROM StudentEntity s WHERE s.studentNumber = :sNum", StudentEntity.class)
                    .setParameter("sNum", studentNumber)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(StudentMapper.toDomain(list.get(0)));
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public List<Student> findAll() {
        return findAllEntities().stream()
                .map(StudentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findByCohortId(String cohortId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<StudentEntity> list = em.createQuery(
                    "SELECT s FROM StudentEntity s WHERE s.cohort.id = :cohortId", StudentEntity.class)
                    .setParameter("cohortId", cohortId)
                    .getResultList();
            return list.stream().map(StudentMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(Student student) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            StudentCohortEntity cohortRef = student.getCohortId() != null ? em.find(StudentCohortEntity.class, student.getCohortId()) : null;
            StudentEntity entity = StudentMapper.toEntity(student, cohortRef);
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
