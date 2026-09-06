package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.academic.Lecturer;
import rw.ac.auca.aura.domain.repository.LecturerRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.DepartmentEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.LecturerEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.LecturerMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaLecturerRepository extends GenericDao<LecturerEntity, String> implements LecturerRepository {

    public JpaLecturerRepository() {
        super(LecturerEntity.class);
    }

    @Override
    public Optional<Lecturer> findById(String id) {
        return findEntityById(id).map(LecturerMapper::toDomain);
    }

    @Override
    public Optional<Lecturer> findByStaffNumber(String staffNumber) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<LecturerEntity> list = em.createQuery(
                    "SELECT l FROM LecturerEntity l WHERE l.staffNumber = :staffNumber", LecturerEntity.class)
                    .setParameter("staffNumber", staffNumber)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(LecturerMapper.toDomain(list.get(0)));
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public List<Lecturer> findAll() {
        return findAllEntities().stream()
                .map(LecturerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lecturer> findByDepartmentId(String departmentId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<LecturerEntity> list = em.createQuery(
                    "SELECT l FROM LecturerEntity l WHERE l.department.id = :deptId", LecturerEntity.class)
                    .setParameter("deptId", departmentId)
                    .getResultList();
            return list.stream().map(LecturerMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(Lecturer lecturer) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            DepartmentEntity deptRef = lecturer.getDepartmentId() != null ? em.find(DepartmentEntity.class, lecturer.getDepartmentId()) : null;
            LecturerEntity entity = LecturerMapper.toEntity(lecturer, deptRef);
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
