package rw.ac.auca.aura.domain.resource;

/**
 * Categorization of physical allocatable resources.
 */
public enum ResourceType {
    CLASSROOM("Standard Classroom"),
    COMPUTER_LAB("Computer Laboratory"),
    LAB("Science / Practical Laboratory"),
    LECTURE_HALL("Large Amphitheater / Lecture Hall"),
    EQUIPMENT("Specialized Equipment Unit");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
