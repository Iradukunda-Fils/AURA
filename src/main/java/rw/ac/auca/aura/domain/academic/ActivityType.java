package rw.ac.auca.aura.domain.academic;

/**
 * Categorization of academic activities.
 */
public enum ActivityType {
    LECTURE("Standard Lecture"),
    LABORATORY("Computer / Science Laboratory"),
    PRACTICAL("Practical Hands-on Session"),
    SEMINAR("Interactive Seminar"),
    EXAMINATION("Institutional Examination");

    private final String displayName;

    ActivityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
