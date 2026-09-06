package rw.ac.auca.aura.domain.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Value Object encapsulating a set of capabilities with subset matching semantics.
 */
public final class CapabilitySet implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Set<Capability> capabilities;

    public CapabilitySet(Set<Capability> capabilities) {
        if (capabilities == null || capabilities.isEmpty()) {
            this.capabilities = Collections.emptySet();
        } else {
            this.capabilities = Collections.unmodifiableSet(EnumSet.copyOf(capabilities));
        }
    }

    public static CapabilitySet empty() {
        return new CapabilitySet(Collections.emptySet());
    }

    public static CapabilitySet of(Capability... caps) {
        if (caps == null || caps.length == 0) {
            return empty();
        }
        EnumSet<Capability> set = EnumSet.noneOf(Capability.class);
        Collections.addAll(set, caps);
        return new CapabilitySet(set);
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    /**
     * Checks if this CapabilitySet satisfies all required capabilities in the required set.
     * Hard constraint rule: RequiredCapabilities <= ResourceCapabilities
     */
    public boolean satisfies(CapabilitySet required) {
        if (required == null || required.capabilities.isEmpty()) {
            return true;
        }
        return this.capabilities.containsAll(required.capabilities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CapabilitySet that = (CapabilitySet) o;
        return Objects.equals(capabilities, that.capabilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capabilities);
    }

    @Override
    public String toString() {
        return capabilities.toString();
    }
}
