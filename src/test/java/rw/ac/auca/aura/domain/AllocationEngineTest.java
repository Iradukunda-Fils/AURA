package rw.ac.auca.aura.domain;

import org.junit.jupiter.api.Test;
import rw.ac.auca.aura.application.allocation.AllocationApplicationService;
import rw.ac.auca.aura.domain.allocation.AllocationDecision;
import rw.ac.auca.aura.domain.allocation.AllocationRun;

import static org.junit.jupiter.api.Assertions.*;

public class AllocationEngineTest {

    @Test
    public void testFullSimulationRunWithSeedData() {
        AllocationApplicationService service = new AllocationApplicationService();
        
        AllocationRun run = service.executeSimulationRun("RUN_TEST_001", "UnitTestRunner");

        assertNotNull(run);
        assertEquals("RUN_TEST_001", run.getRunId());
        assertFalse(run.getDecisions().isEmpty(), "Decisions should not be empty for sample AUCA seed data");

        long feasibleDecisionsCount = run.getDecisions().stream().filter(AllocationDecision::isFeasible).count();
        assertTrue(feasibleDecisionsCount > 0);

        // Verify committing the run
        boolean committed = service.commitRun("RUN_TEST_001", "TestAdmin");
        assertTrue(committed);
        assertEquals(feasibleDecisionsCount, service.getActiveAllocations().size());
    }
}
