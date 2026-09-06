package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "aura_system_metadata")
public class SystemMetadataEntity {

    @Id
    @Column(name = "metadata_key", nullable = false, length = 100)
    private String key;

    @Column(name = "metadata_value", nullable = false, length = 500)
    private String value;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SystemMetadataEntity() {}

    public SystemMetadataEntity(String key, String value, LocalDateTime updatedAt) {
        this.key = key;
        this.value = value;
        this.updatedAt = updatedAt;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemMetadataEntity that = (SystemMetadataEntity) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
