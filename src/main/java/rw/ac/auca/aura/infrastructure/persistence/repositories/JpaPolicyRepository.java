package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.policy.AllocationPolicy;
import rw.ac.auca.aura.domain.repository.PolicyRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.PolicyEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.PolicyMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaPolicyRepository extends GenericDao<PolicyEntity, String> implements PolicyRepository {

    public JpaPolicyRepository() {
        super(PolicyEntity.class);
    }

    @Override
    public Optional<AllocationPolicy> findById(String id) {
        return findEntityById(id).map(PolicyMapper::toDomain);
    }

    @Override
    public Optional<AllocationPolicy> findActivePolicy() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<PolicyEntity> list = em.createQuery(
                    "SELECT p FROM PolicyEntity p WHERE p.active = true", PolicyEntity.class)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(PolicyMapper.toDomain(list.get(0)));
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public List<AllocationPolicy> findAll() {
        return findAllEntities().stream()
                .map(PolicyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(AllocationPolicy policy) {
        PolicyEntity entity = PolicyMapper.toEntity(policy);
        saveEntity(entity);
    }
}
