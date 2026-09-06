package rw.ac.auca.aura.application.allocation;

import rw.ac.auca.aura.domain.academic.*;
import rw.ac.auca.aura.domain.allocation.*;
import rw.ac.auca.aura.domain.constraint.SpecificationContext;
import rw.ac.auca.aura.domain.policy.AllocationPolicy;
import rw.ac.auca.aura.domain.policy.ScoringWeightPolicy;
import rw.ac.auca.aura.domain.resource.*;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.Capability;
import rw.ac.auca.aura.domain.shared.CapabilitySet;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application Service for managing allocation optimization runs, explainability audits,
 * and lifecycle transitions of allocations (Simulation vs Execution / Commitment).
 */
public class AllocationApplicationService {

    private final Map<String, AllocationRun> runHistory = new ConcurrentHashMap<>();
    private final List<Allocation> activeAllocations = new ArrayList<>();
    private final List<Resource> registeredResources = new ArrayList<>();
    private final List<AcademicActivity> pendingActivities = new ArrayList<>();
    private final List<TimeSlot> availableTimeSlots = new ArrayList<>();
    
    private ScoringWeightPolicy activeWeightPolicy = ScoringWeightPolicy.defaultPolicy();

    public AllocationApplicationService() {
        seedSampleAucaData();
    }

    /**
     * Pre-populates realistic AUCA campus data (Masoro & Gishushu), Computer Labs, Science Labs,
     * Lecture Halls, Lecturers, Cohorts, and Academic Activities.
     */
    public synchronized void seedSampleAucaData() {
        registeredResources.clear();
        pendingActivities.clear();
        activeAllocations.clear();
        runHistory.clear();
        availableTimeSlots.clear();

        // 1. Sites
        Site masoro = new Site("SITE_MAS", "Masoro Main Campus", "Kigali - Masoro");
        Site gishushu = new Site("SITE_GIS", "Gishushu Campus", "Kigali - Gishushu");

        // 2. Buildings
        Building scienceBuilding = new Building("BLD_SCI", "Science & Tech Complex", masoro.getId());
        Building adminBuilding = new Building("BLD_ADM", "Administration Building", masoro.getId());
        Building theologyBuilding = new Building("BLD_THEO", "Theology & Arts Center", gishushu.getId());
        Building bizBuilding = new Building("BLD_BIZ", "Business School Block", masoro.getId());

        // 3. Capabilities
        CapabilitySet labCap = CapabilitySet.of(Capability.COMPUTERS, Capability.PROJECTOR, Capability.AIR_CONDITIONING);
        CapabilitySet hwCap = CapabilitySet.of(Capability.COMPUTERS, Capability.NETWORK_LAB, Capability.PROJECTOR);
        CapabilitySet lectureCap = CapabilitySet.of(Capability.PROJECTOR, Capability.AUDIO_SYSTEM, Capability.WHITEBOARD);
        CapabilitySet standardCap = CapabilitySet.of(Capability.PROJECTOR, Capability.WHITEBOARD);
        CapabilitySet examCap = CapabilitySet.of(Capability.WHITEBOARD, Capability.AIR_CONDITIONING);

        // 4. Resources (Rooms)
        Resource lab1 = new Resource("ROOM_LAB1", "Computer Lab 1", ResourceType.COMPUTER_LAB, 45, masoro.getId(), scienceBuilding.getId(), labCap, ResourceStatus.ACTIVE);
        Resource lab2 = new Resource("ROOM_LAB2", "Computer Lab 2", ResourceType.COMPUTER_LAB, 50, masoro.getId(), scienceBuilding.getId(), labCap, ResourceStatus.ACTIVE);
        Resource lab3 = new Resource("ROOM_LAB3", "Network & Hardware Lab", ResourceType.LAB, 35, masoro.getId(), scienceBuilding.getId(), hwCap, ResourceStatus.ACTIVE);
        Resource hallA = new Resource("ROOM_HALL_A", "Auditorium A", ResourceType.LECTURE_HALL, 180, masoro.getId(), adminBuilding.getId(), lectureCap, ResourceStatus.ACTIVE);
        Resource hallB = new Resource("ROOM_HALL_B", "Auditorium B", ResourceType.LECTURE_HALL, 120, gishushu.getId(), theologyBuilding.getId(), lectureCap, ResourceStatus.ACTIVE);
        Resource room101 = new Resource("ROOM_101", "Lecture Room 101", ResourceType.CLASSROOM, 65, masoro.getId(), scienceBuilding.getId(), standardCap, ResourceStatus.ACTIVE);
        Resource room102 = new Resource("ROOM_102", "Lecture Room 102", ResourceType.CLASSROOM, 60, masoro.getId(), scienceBuilding.getId(), standardCap, ResourceStatus.ACTIVE);
        Resource room103 = new Resource("ROOM_103", "Seminar Room 103", ResourceType.CLASSROOM, 30, masoro.getId(), scienceBuilding.getId(), standardCap, ResourceStatus.ACTIVE);
        Resource room201 = new Resource("ROOM_201", "Gishushu Room 201", ResourceType.CLASSROOM, 50, gishushu.getId(), theologyBuilding.getId(), standardCap, ResourceStatus.ACTIVE);
        Resource room202 = new Resource("ROOM_202", "Gishushu Room 202", ResourceType.CLASSROOM, 45, gishushu.getId(), theologyBuilding.getId(), examCap, ResourceStatus.MAINTENANCE);

        registeredResources.add(lab1);
        registeredResources.add(lab2);
        registeredResources.add(lab3);
        registeredResources.add(hallA);
        registeredResources.add(hallB);
        registeredResources.add(room101);
        registeredResources.add(room102);
        registeredResources.add(room103);
        registeredResources.add(room201);
        registeredResources.add(room202);

        // 5. Academic Entities
        Department csDept = new Department("DEPT_CS", "Computer Science & IT", "Faculty of Information Technology");
        Department bizDept = new Department("DEPT_BIZ", "Business Administration", "Faculty of Business");

        Program sseProg = new Program("PROG_SSE", "Software Engineering", csDept.getId());
        Program itProg = new Program("PROG_IT", "Information Technology", csDept.getId());
        Program bisProg = new Program("PROG_BIS", "Business Information Systems", bizDept.getId());

        Lecturer drEric = new Lecturer("LEC_001", "ST_001", "Dr. Eric Niyomugabo", "eric.n@auca.ac.rw", csDept.getId());
        Lecturer profJean = new Lecturer("LEC_002", "ST_002", "Prof. Jean Pierre", "jean.p@auca.ac.rw", csDept.getId());
        Lecturer msAlice = new Lecturer("LEC_003", "ST_003", "Ms. Alice Umutoni", "alice.u@auca.ac.rw", csDept.getId());
        Lecturer drClem = new Lecturer("LEC_004", "ST_004", "Dr. Clementine Mukamana", "clementine.m@auca.ac.rw", csDept.getId());
        Lecturer profEmma = new Lecturer("LEC_005", "ST_005", "Prof. Emmanuel Habimana", "emmanuel.h@auca.ac.rw", bizDept.getId());

        StudentCohort cohortBsse3 = new StudentCohort("COHORT_2026_BSSE3", "BSSE Year 3 - Sec A", sseProg.getId(), 2026, 42);
        StudentCohort cohortBsse4 = new StudentCohort("COHORT_2026_BSSE4", "BSSE Year 4 - Sec A", sseProg.getId(), 2026, 48);
        StudentCohort cohortBsit2 = new StudentCohort("COHORT_2026_BSIT2", "BSIT Year 2 - Sec B", itProg.getId(), 2026, 55);
        StudentCohort cohortBis3 = new StudentCohort("COHORT_2026_BSBIS3", "BSBIS Year 3 - Sec A", bisProg.getId(), 2026, 38);

        Course webTech = new Course("CRS_WEB", "INSY 321", "Web Technology & Cloud Systems", 4, sseProg.getId());
        Course dbSys = new Course("CRS_DB", "INSY 224", "Database Management Systems", 4, sseProg.getId());
        Course advJava = new Course("CRS_JAVA", "INSY 411", "Advanced Enterprise Java", 4, sseProg.getId());
        Course netSec = new Course("CRS_NET", "NETW 312", "Computer Networks & Security", 4, itProg.getId());
        Course ooad = new Course("CRS_OOAD", "INSY 315", "Object-Oriented Analysis & Design", 3, sseProg.getId());
        Course mis = new Course("CRS_MIS", "ACCT 211", "Management Information Systems", 3, bisProg.getId());

        CourseOffering webTechOff = new CourseOffering("OFF_WEB", webTech.getId(), "2026-S2", "Sec A", Set.of(drEric.getId()), Set.of(cohortBsse3.getId()));
        CourseOffering dbSysOff = new CourseOffering("OFF_DB", dbSys.getId(), "2026-S2", "Sec A", Set.of(profJean.getId()), Set.of(cohortBsse3.getId()));
        CourseOffering advJavaOff = new CourseOffering("OFF_JAVA", advJava.getId(), "2026-S2", "Sec A", Set.of(msAlice.getId()), Set.of(cohortBsse4.getId()));
        CourseOffering netSecOff = new CourseOffering("OFF_NET", netSec.getId(), "2026-S2", "Sec B", Set.of(drClem.getId()), Set.of(cohortBsit2.getId()));
        CourseOffering ooadOff = new CourseOffering("OFF_OOAD", ooad.getId(), "2026-S2", "Sec A", Set.of(drEric.getId()), Set.of(cohortBsse3.getId()));
        CourseOffering misOff = new CourseOffering("OFF_MIS", mis.getId(), "2026-S2", "Sec A", Set.of(profEmma.getId()), Set.of(cohortBis3.getId()));

        // 6. Time Slots (Mon-Thu 08:00-10:00, 10:00-12:00, 14:00-16:00, 16:00-18:00)
        TimeSlot mon1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        TimeSlot mon2 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
        TimeSlot mon3 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(16, 0));
        TimeSlot tue1 = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        TimeSlot tue2 = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
        TimeSlot wed3 = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(16, 0));
        TimeSlot thu1 = new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));

        availableTimeSlots.add(mon1);
        availableTimeSlots.add(mon2);
        availableTimeSlots.add(mon3);
        availableTimeSlots.add(tue1);
        availableTimeSlots.add(tue2);
        availableTimeSlots.add(wed3);
        availableTimeSlots.add(thu1);

        // 7. Academic Activities
        AcademicActivity actWebLab = new AcademicActivity("ACT_WEB_LAB", webTechOff.getId(), "Web Tech Lab Session", ActivityType.LABORATORY, Set.of(drEric.getId()), Set.of(cohortBsse3.getId()), 42, 120, labCap, masoro.getId(), mon1, ActivityState.SUBMITTED);
        AcademicActivity actDbLec = new AcademicActivity("ACT_DB_LEC", dbSysOff.getId(), "Database Systems Lecture", ActivityType.LECTURE, Set.of(profJean.getId()), Set.of(cohortBsse3.getId()), 42, 120, lectureCap, masoro.getId(), mon1, ActivityState.SUBMITTED);
        AcademicActivity actJavaLec = new AcademicActivity("ACT_JAVA_LEC", advJavaOff.getId(), "Advanced Java Enterprise", ActivityType.LECTURE, Set.of(msAlice.getId()), Set.of(cohortBsse4.getId()), 48, 120, lectureCap, masoro.getId(), mon2, ActivityState.SUBMITTED);
        AcademicActivity actNetLab = new AcademicActivity("ACT_NET_LAB", netSecOff.getId(), "Networks & Hardware Lab", ActivityType.LABORATORY, Set.of(drClem.getId()), Set.of(cohortBsit2.getId()), 35, 120, hwCap, masoro.getId(), wed3, ActivityState.SUBMITTED);
        AcademicActivity actOoadLec = new AcademicActivity("ACT_OOAD_LEC", ooadOff.getId(), "OOAD System Design", ActivityType.LECTURE, Set.of(drEric.getId()), Set.of(cohortBsse3.getId()), 42, 120, standardCap, masoro.getId(), thu1, ActivityState.SUBMITTED);
        AcademicActivity actMisLec = new AcademicActivity("ACT_MIS_LEC", misOff.getId(), "Management Info Systems", ActivityType.LECTURE, Set.of(profEmma.getId()), Set.of(cohortBis3.getId()), 38, 120, standardCap, gishushu.getId(), tue2, ActivityState.SUBMITTED);
        AcademicActivity actWebExam = new AcademicActivity("ACT_WEB_EXAM", webTechOff.getId(), "Web Tech Midterm Exam", ActivityType.EXAMINATION, Set.of(drEric.getId()), Set.of(cohortBsse3.getId()), 42, 120, examCap, gishushu.getId(), mon3, ActivityState.SUBMITTED);

        pendingActivities.add(actWebLab);
        pendingActivities.add(actDbLec);
        pendingActivities.add(actJavaLec);
        pendingActivities.add(actNetLab);
        pendingActivities.add(actOoadLec);
        pendingActivities.add(actMisLec);
        pendingActivities.add(actWebExam);
    }

    public synchronized ScoringWeightPolicy getActiveWeightPolicy() {
        return activeWeightPolicy;
    }

    public synchronized void updateWeightPolicy(ScoringWeightPolicy policy) {
        this.activeWeightPolicy = Objects.requireNonNull(policy, "Policy cannot be null");
    }

    public synchronized void registerResource(Resource resource) {
        registeredResources.add(resource);
    }

    public synchronized void registerActivity(AcademicActivity activity) {
        pendingActivities.add(activity);
    }

    public synchronized List<Resource> getRegisteredResources() {
        return Collections.unmodifiableList(registeredResources);
    }

    public synchronized List<AcademicActivity> getPendingActivities() {
        return Collections.unmodifiableList(pendingActivities);
    }

    public synchronized List<Allocation> getActiveAllocations() {
        return Collections.unmodifiableList(activeAllocations);
    }

    /**
     * Executes a new simulation allocation run using the GreedyPriorityStrategy.
     */
    public synchronized AllocationRun executeSimulationRun(String runId, String triggeredBy) {
        GreedyPriorityStrategy strategy = new GreedyPriorityStrategy();

        List<AcademicActivity> unallocated = pendingActivities.stream()
                .filter(a -> a.getState() == ActivityState.SUBMITTED || a.getState() == ActivityState.DRAFT)
                .toList();

        SpecificationContext baseContext = new SpecificationContext(activeAllocations, pendingActivities);
        AllocationPolicy policy = new AllocationPolicy("POL_001", "v1.0", "AUCA Active Allocation Policy", activeWeightPolicy, true);

        AllocationRun run = strategy.executeAllocationRun(
                runId,
                unallocated,
                registeredResources,
                availableTimeSlots,
                baseContext,
                policy,
                triggeredBy
        );

        runHistory.put(runId, run);
        return run;
    }

    /**
     * Commits an allocation run with MANDATORY concurrency revalidation:
     * Re-evaluates all hard constraints, resource active status, and timeslot overlaps
     * against live active allocations before transferring proposed allocations into committed state.
     */
    public synchronized boolean commitRun(String runId, String approvedBy) {
        AllocationRun run = runHistory.get(runId);
        if (run == null) {
            return false;
        }

        List<Allocation> newlyCommitted = new ArrayList<>();

        for (AllocationDecision decision : run.getDecisions()) {
            if (!decision.isFeasible()) {
                continue;
            }

            // 1. Re-verify resource existence and ACTIVE status
            Resource targetResource = registeredResources.stream()
                    .filter(r -> r.getId().equals(decision.getSelectedResourceId()))
                    .findFirst()
                    .orElse(null);

            if (targetResource == null || targetResource.getStatus() != ResourceStatus.ACTIVE) {
                // Stale proposal: resource is no longer active or available
                continue;
            }

            // 2. Re-verify timeslot conflict against live committed allocations (concurrency check)
            boolean conflictDetected = activeAllocations.stream().anyMatch(existing ->
                    existing.getResourceIds().contains(decision.getSelectedResourceId()) &&
                    existing.getTimeSlot() != null &&
                    existing.getTimeSlot().overlaps(decision.getSelectedTimeSlot())
            );

            if (conflictDetected) {
                // Stale proposal: another run committed a conflicting allocation at this timeslot
                continue;
            }

            // 3. Re-verify capacity constraint
            AcademicActivity activity = pendingActivities.stream()
                    .filter(a -> a.getId().equals(decision.getActivityId()))
                    .findFirst()
                    .orElse(null);

            if (activity == null || targetResource.getCapacity() < activity.getStudentCount()) {
                continue;
            }

            // Revalidation passed: create committed allocation
            Allocation committedAllocation = new Allocation(
                    "ALLOC_" + decision.getActivityId(),
                    runId,
                    decision.getActivityId(),
                    Set.of(decision.getSelectedResourceId()),
                    decision.getSelectedTimeSlot(),
                    decision.getSelectedSiteId(),
                    LocalDateTime.now(),
                    approvedBy,
                    Allocation.AllocationStatus.COMMITTED
            );

            newlyCommitted.add(committedAllocation);
            activity.transitionTo(ActivityState.ALLOCATED);
        }

        if (newlyCommitted.isEmpty() && !run.getDecisions().isEmpty()) {
            return false;
        }

        activeAllocations.addAll(newlyCommitted);
        return true;
    }

    public synchronized AllocationRun getRun(String runId) {
        return runHistory.get(runId);
    }

    public synchronized List<AllocationRun> getAllRuns() {
        return runHistory.values().stream()
                .sorted(Comparator.comparing(AllocationRun::getExecutedAt).reversed())
                .toList();
    }
}
