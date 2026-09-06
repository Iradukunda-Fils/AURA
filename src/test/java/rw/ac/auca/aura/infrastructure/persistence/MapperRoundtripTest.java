package rw.ac.auca.aura.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rw.ac.auca.aura.domain.academic.*;
import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.allocation.AllocationDecision;
import rw.ac.auca.aura.domain.allocation.AllocationRun;
import rw.ac.auca.aura.domain.policy.AllocationPolicy;
import rw.ac.auca.aura.domain.policy.ScoringWeightPolicy;
import rw.ac.auca.aura.domain.resource.Building;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.resource.ResourceStatus;
import rw.ac.auca.aura.domain.resource.ResourceType;
import rw.ac.auca.aura.domain.resource.Site;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.Capability;
import rw.ac.auca.aura.domain.shared.CapabilitySet;

import rw.ac.auca.aura.infrastructure.persistence.entities.*;
import rw.ac.auca.aura.infrastructure.persistence.mappers.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MapperRoundtripTest {

    @Test
    @DisplayName("SiteMapper roundtrip test")
    void testSiteMapper() {
        Site site = new Site("SITE_MAS", "Masoro Main Campus", "KIG_MAS");
        SiteEntity entity = SiteMapper.toEntity(site);
        Site reconstructed = SiteMapper.toDomain(entity);

        assertEquals(site.getId(), reconstructed.getId());
        assertEquals(site.getName(), reconstructed.getName());
        assertEquals(site.getLocationCode(), reconstructed.getLocationCode());
    }

    @Test
    @DisplayName("BuildingMapper roundtrip test")
    void testBuildingMapper() {
        Site site = new Site("SITE_MAS", "Masoro Main Campus", "KIG_MAS");
        SiteEntity siteEntity = SiteMapper.toEntity(site);

        Building building = new Building("BLD_SCI", "Science Complex", site.getId());
        BuildingEntity entity = BuildingMapper.toEntity(building, siteEntity);
        Building reconstructed = BuildingMapper.toDomain(entity);

        assertEquals(building.getId(), reconstructed.getId());
        assertEquals(building.getName(), reconstructed.getName());
        assertEquals(building.getSiteId(), reconstructed.getSiteId());
    }

    @Test
    @DisplayName("ResourceMapper capability set serialization roundtrip test")
    void testResourceMapper() {
        CapabilitySet capSet = CapabilitySet.of(Capability.COMPUTERS, Capability.PROJECTOR, Capability.AIR_CONDITIONING);
        Resource resource = new Resource("ROOM_LAB1", "Computer Lab 1", ResourceType.COMPUTER_LAB, 45, "SITE_MAS", "BLD_SCI", capSet, ResourceStatus.ACTIVE);

        SiteEntity siteEntity = new SiteEntity("SITE_MAS", "Masoro Main Campus", "KIG_MAS");
        BuildingEntity bldEntity = new BuildingEntity("BLD_SCI", "Science Complex", siteEntity);

        ResourceEntity entity = ResourceMapper.toEntity(resource, bldEntity);
        Resource reconstructed = ResourceMapper.toDomain(entity);

        assertEquals(resource.getId(), reconstructed.getId());
        assertEquals(resource.getName(), reconstructed.getName());
        assertEquals(resource.getType(), reconstructed.getType());
        assertEquals(resource.getCapacity(), reconstructed.getCapacity());
        assertTrue(reconstructed.getCapabilities().satisfies(capSet));
    }

    @Test
    @DisplayName("AllocationMapper multi-resource join table roundtrip test")
    void testAllocationMapper() {
        TimeSlot slot = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        Allocation alloc = new Allocation("ALLOC_1", "RUN_1", "ACT_1", Set.of("ROOM_LAB1", "ROOM_LAB2"), slot, "SITE_MAS", LocalDateTime.now(), "AdminUser", Allocation.AllocationStatus.COMMITTED);

        AllocationEntity entity = AllocationMapper.toEntity(alloc);
        Allocation reconstructed = AllocationMapper.toDomain(entity);

        assertEquals(alloc.getId(), reconstructed.getId());
        assertEquals(alloc.getRunId(), reconstructed.getRunId());
        assertEquals(alloc.getActivityId(), reconstructed.getActivityId());
        assertEquals(2, reconstructed.getResourceIds().size());
        assertTrue(reconstructed.getResourceIds().contains("ROOM_LAB1"));
        assertTrue(reconstructed.getResourceIds().contains("ROOM_LAB2"));
        assertEquals(alloc.getStatus(), reconstructed.getStatus());
    }

    @Test
    @DisplayName("AllocationRunMapper and AllocationDecisionMapper roundtrip test")
    void testAllocationRunMapper() {
        TimeSlot slot = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
        AllocationDecision decision = new AllocationDecision("ACT_DB", "ROOM_101", slot, "SITE_MAS", true, 92.5, null, null, List.of("ROOM_102"), "Optimal capacity and location fit");
        AllocationRun run = new AllocationRun("RUN_100", "v1.0", "GreedyPriority-v1", LocalDateTime.now(), "SystemUser", List.of(decision), 92.5, AllocationRun.RunStatus.COMPLETED);

        AllocationRunEntity entity = AllocationRunMapper.toEntity(run);
        AllocationRun reconstructed = AllocationRunMapper.toDomain(entity);

        assertEquals(run.getRunId(), reconstructed.getRunId());
        assertEquals(run.getPolicyVersion(), reconstructed.getPolicyVersion());
        assertEquals(run.getGlobalUtilityScore(), reconstructed.getGlobalUtilityScore());
        assertEquals(1, reconstructed.getDecisions().size());

        AllocationDecision recDecision = reconstructed.getDecisions().get(0);
        assertEquals(decision.getActivityId(), recDecision.getActivityId());
        assertEquals(decision.getSelectedResourceId(), recDecision.getSelectedResourceId());
        assertTrue(recDecision.isFeasible());
        assertEquals(92.5, recDecision.getScore());
    }

    @Test
    @DisplayName("PolicyMapper roundtrip test")
    void testPolicyMapper() {
        AllocationPolicy policy = AllocationPolicy.defaultBaseline();
        PolicyEntity entity = PolicyMapper.toEntity(policy);
        AllocationPolicy reconstructed = PolicyMapper.toDomain(entity);

        assertEquals(policy.getId(), reconstructed.getId());
        assertEquals(policy.getVersion(), reconstructed.getVersion());
        assertTrue(reconstructed.isActive());
        assertEquals(policy.getWeights().getWeightCapacityFit(), reconstructed.getWeights().getWeightCapacityFit(), 0.0001);
    }
}
