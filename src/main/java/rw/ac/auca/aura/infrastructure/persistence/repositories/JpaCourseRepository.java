package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.academic.Course;
import rw.ac.auca.aura.domain.repository.CourseRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.ProgramEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.CourseMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaCourseRepository extends GenericDao<CourseEntity, String> implements CourseRepository {

    public JpaCourseRepository() {
        super(CourseEntity.class);
    }

    @Override
    public Optional<Course> findById(String id) {
        return findEntityById(id).map(CourseMapper::toDomain);
    }

    @Override
    public Optional<Course> findByCode(String code) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<CourseEntity> list = em.createQuery(
                    "SELECT c FROM CourseEntity c WHERE c.code = :code", CourseEntity.class)
                    .setParameter("code", code)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(CourseMapper.toDomain(list.get(0)));
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public List<Course> findAll() {
        return findAllEntities().stream()
                .map(CourseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findByProgramId(String programId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<CourseEntity> list = em.createQuery(
                    "SELECT c FROM CourseEntity c WHERE c.program.id = :progId", CourseEntity.class)
                    .setParameter("progId", programId)
                    .getResultList();
            return list.stream().map(CourseMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(Course course) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            ProgramEntity progRef = course.getProgramId() != null ? em.find(ProgramEntity.class, course.getProgramId()) : null;
            CourseEntity entity = CourseMapper.toEntity(course, progRef);
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
