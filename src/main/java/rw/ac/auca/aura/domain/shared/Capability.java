package rw.ac.auca.aura.domain.shared;

/**
 * Value Object / Enum representing technical and pedagogical capabilities required by academic activities
 * or provided by physical institutional resources.
 */
public enum Capability {
    COMPUTERS("Desktop Computer Lab Setup"),
    PROJECTOR("HD Video Projector / Screen"),
    SMARTBOARD("Interactive Smartboard"),
    NETWORK_LAB("Specialized Networking Hardware / Switches"),
    AIR_CONDITIONING("Air Conditioning"),
    SPECIALIZED_EQUIPMENT("Specialized Laboratory Equipment"),
    AUDIO_SYSTEM("Surround Audio System"),
    WHITEBOARD("Standard Magnetic Whiteboard");

    private final String description;

    Capability(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
