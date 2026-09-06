package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.repository.AcademicActivityRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.AcademicActivityEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseOfferingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.LecturerEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.AcademicActivityMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaAcademicActivityRepository extends GenericDao<AcademicActivityEntity, String> implements AcademicActivityRepository {

    public JpaAcademicActivityRepository() {
        super(AcademicActivityEntity.class);
    }

    @Override
    public Optional<AcademicActivity> findById(String id) {
        return findEntityById(id).map(AcademicActivityMapper::toDomain);
    }

    @Override
    public List<AcademicActivity> findAll() {
        return findAllEntities().stream()
                .map(AcademicActivityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicActivity> findByOfferingId(String offeringId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<AcademicActivityEntity> list = em.createQuery(
                    "SELECT a FROM AcademicActivityEntity a WHERE a.offering.id = :offeringId", AcademicActivityEntity.class)
                    .setParameter("offeringId", offeringId)
                    .getResultList();
            return list.stream().map(AcademicActivityMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(AcademicActivity activity) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            CourseOfferingEntity offeringRef = activity.getOfferingId() != null ? em.find(CourseOfferingEntity.class, activity.getOfferingId()) : null;

            Set<LecturerEntity> lecturers = new HashSet<>();
            if (activity.getLecturerIds() != null) {
                for (String lecId : activity.getLecturerIds()) {
                    LecturerEntity lec = em.find(LecturerEntity.class, lecId);
                    if (lec != null) lecturers.add(lec);
                }
            }

            Set<StudentCohortEntity> cohorts = new HashSet<>();
            if (activity.getCohortIds() != null) {
                for (String cohortId : activity.getCohortIds()) {
                    StudentCohortEntity cohort = em.find(StudentCohortEntity.class, cohortId);
                    if (cohort != null) cohorts.add(cohort);
                }
            }

            AcademicActivityEntity entity = AcademicActivityMapper.toEntity(activity, offeringRef, lecturers, cohorts);
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
