package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.academic.CourseOffering;
import rw.ac.auca.aura.domain.repository.CourseOfferingRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseOfferingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.LecturerEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.CourseOfferingMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaCourseOfferingRepository extends GenericDao<CourseOfferingEntity, String> implements CourseOfferingRepository {

    public JpaCourseOfferingRepository() {
        super(CourseOfferingEntity.class);
    }

    @Override
    public Optional<CourseOffering> findById(String id) {
        return findEntityById(id).map(CourseOfferingMapper::toDomain);
    }

    @Override
    public List<CourseOffering> findAll() {
        return findAllEntities().stream()
                .map(CourseOfferingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseOffering> findByCourseId(String courseId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<CourseOfferingEntity> list = em.createQuery(
                    "SELECT co FROM CourseOfferingEntity co WHERE co.course.id = :courseId", CourseOfferingEntity.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
            return list.stream().map(CourseOfferingMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(CourseOffering offering) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            CourseEntity courseRef = offering.getCourseId() != null ? em.find(CourseEntity.class, offering.getCourseId()) : null;

            Set<LecturerEntity> lecturers = new HashSet<>();
            if (offering.getLecturerIds() != null) {
                for (String lecId : offering.getLecturerIds()) {
                    LecturerEntity lec = em.find(LecturerEntity.class, lecId);
                    if (lec != null) lecturers.add(lec);
                }
            }

            Set<StudentCohortEntity> cohorts = new HashSet<>();
            if (offering.getCohortIds() != null) {
                for (String cohortId : offering.getCohortIds()) {
                    StudentCohortEntity cohort = em.find(StudentCohortEntity.class, cohortId);
                    if (cohort != null) cohorts.add(cohort);
                }
            }

            CourseOfferingEntity entity = CourseOfferingMapper.toEntity(offering, courseRef, lecturers, cohorts);
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
