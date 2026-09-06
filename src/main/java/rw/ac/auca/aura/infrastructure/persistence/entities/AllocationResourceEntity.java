package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "aura_allocation_resources")
public class AllocationResourceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "allocation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_alloc_res_allocation"))
    private AllocationEntity allocation;

    @Column(name = "resource_id", nullable = false, length = 100)
    private String resourceId;

    public AllocationResourceEntity() {}

    public AllocationResourceEntity(AllocationEntity allocation, String resourceId) {
        this.allocation = allocation;
        this.resourceId = resourceId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AllocationEntity getAllocation() { return allocation; }
    public void setAllocation(AllocationEntity allocation) { this.allocation = allocation; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationResourceEntity that = (AllocationResourceEntity) o;
        String thisAllocId = (allocation != null) ? allocation.getId() : null;
        String thatAllocId = (that.allocation != null) ? that.allocation.getId() : null;
        return Objects.equals(resourceId, that.resourceId) && Objects.equals(thisAllocId, thatAllocId);
    }

    @Override
    public int hashCode() {
        String allocId = (allocation != null) ? allocation.getId() : null;
        return Objects.hash(resourceId, allocId);
    }
}
