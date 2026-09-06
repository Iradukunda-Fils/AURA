package rw.ac.auca.aura.domain.shared;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object representing the result of evaluating a hard or soft constraint specification.
 */
public final class ConstraintResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String constraintName;
    private final boolean passed;
    private final String detailMessage;

    public ConstraintResult(String constraintName, boolean passed, String detailMessage) {
        this.constraintName = Objects.requireNonNull(constraintName, "Constraint name cannot be null");
        this.passed = passed;
        this.detailMessage = detailMessage != null ? detailMessage : "";
    }

    public static ConstraintResult pass(String constraintName, String message) {
        return new ConstraintResult(constraintName, true, message);
    }

    public static ConstraintResult fail(String constraintName, String failureReason) {
        return new ConstraintResult(constraintName, false, failureReason);
    }

    public String getConstraintName() {
        return constraintName;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintResult that = (ConstraintResult) o;
        return passed == that.passed &&
                Objects.equals(constraintName, that.constraintName) &&
                Objects.equals(detailMessage, that.detailMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constraintName, passed, detailMessage);
    }

    @Override
    public String toString() {
        return (passed ? "[PASS] " : "[FAIL] ") + constraintName + ": " + detailMessage;
    }
}
