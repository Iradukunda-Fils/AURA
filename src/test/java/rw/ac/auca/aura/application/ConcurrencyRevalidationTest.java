package rw.ac.auca.aura.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rw.ac.auca.aura.application.allocation.AllocationApplicationService;
import rw.ac.auca.aura.domain.allocation.AllocationRun;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrencyRevalidationTest {

    private AllocationApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AllocationApplicationService();
    }

    @Test
    @DisplayName("commitRun verifies hard constraints and successfully commits initial simulation pass")
    void testSuccessfulCommitRun() {
        AllocationRun run = service.executeSimulationRun("RUN_TEST_001", "TestUser");
        assertNotNull(run);
        assertFalse(run.getDecisions().isEmpty());

        boolean committed = service.commitRun("RUN_TEST_001", "AdminUser");
        assertTrue(committed);
        assertFalse(service.getActiveAllocations().isEmpty());
    }

    @Test
    @DisplayName("commitRun detects stale proposals when resources become inactive or conflicted")
    void testConcurrencyRevalidationFailsForStaleRun() {
        AllocationRun run1 = service.executeSimulationRun("RUN_SIM_1", "User1");
        boolean committed1 = service.commitRun("RUN_SIM_1", "User1");
        assertTrue(committed1);

        int initialCommittedSize = service.getActiveAllocations().size();

        // Attempting to commit a second duplicate simulation pass for the same activities
        AllocationRun run2 = service.executeSimulationRun("RUN_SIM_2", "User2");
        boolean committed2 = service.commitRun("RUN_SIM_2", "User2");

        // The second commit should reject stale allocations that conflict with active committed allocations
        assertEquals(initialCommittedSize, service.getActiveAllocations().size());
    }
}
