package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.allocation.AllocationDecision;
import rw.ac.auca.aura.domain.allocation.AllocationRun;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ScoreBreakdown;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationDecisionEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationRunEntity;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AllocationRunMapper {

    public static AllocationRun toDomain(AllocationRunEntity entity) {
        if (entity == null) return null;

        List<AllocationDecision> domainDecisions = new ArrayList<>();
        if (entity.getDecisions() != null) {
            for (AllocationDecisionEntity decisionEntity : entity.getDecisions()) {
                domainDecisions.add(decisionToDomain(decisionEntity));
            }
        }

        return new AllocationRun(
                entity.getRunId(),
                entity.getPolicyVersion(),
                entity.getStrategyVersion(),
                entity.getExecutedAt(),
                entity.getExecutedBy(),
                domainDecisions,
                entity.getGlobalUtilityScore(),
                entity.getStatus()
        );
    }

    public static AllocationRunEntity toEntity(AllocationRun domain) {
        if (domain == null) return null;

        AllocationRunEntity entity = new AllocationRunEntity(
                domain.getRunId(),
                domain.getPolicyVersion(),
                domain.getStrategyVersion(),
                domain.getExecutedAt(),
                domain.getExecutedBy(),
                domain.getGlobalUtilityScore(),
                domain.getStatus()
        );

        if (domain.getDecisions() != null) {
            for (AllocationDecision decision : domain.getDecisions()) {
                AllocationDecisionEntity decisionEntity = decisionToEntity(decision, entity);
                entity.addDecision(decisionEntity);
            }
        }

        return entity;
    }

    public static AllocationDecision decisionToDomain(AllocationDecisionEntity entity) {
        if (entity == null) return null;

        TimeSlot selectedSlot = null;
        if (entity.getDayOfWeek() != null && entity.getStartTime() != null && entity.getEndTime() != null) {
            selectedSlot = new TimeSlot(
                    DayOfWeek.valueOf(entity.getDayOfWeek().toUpperCase()),
                    entity.getStartTime(),
                    entity.getEndTime()
            );
        }

        ScoreBreakdown scoreBreakdown = parseScoreBreakdownJson(entity.getScoreBreakdownJson(), entity.getScore());
        List<String> rejected = parseStringListJson(entity.getRejectedAlternativesJson());

        return new AllocationDecision(
                entity.getActivityId(),
                entity.getSelectedResourceId(),
                selectedSlot,
                entity.getSelectedSiteId(),
                entity.isFeasible(),
                entity.getScore(),
                scoreBreakdown,
                Collections.emptyList(), // Constraint results diagnostic list
                rejected,
                entity.getHumanExplanation()
        );
    }

    public static AllocationDecisionEntity decisionToEntity(AllocationDecision domain, AllocationRunEntity runEntity) {
        if (domain == null) return null;

        String dayOfWeek = domain.getSelectedTimeSlot() != null ? domain.getSelectedTimeSlot().getDayOfWeek().name() : null;
        java.time.LocalTime start = domain.getSelectedTimeSlot() != null ? domain.getSelectedTimeSlot().getStartTime() : null;
        java.time.LocalTime end = domain.getSelectedTimeSlot() != null ? domain.getSelectedTimeSlot().getEndTime() : null;

        String scoreBreakdownJson = serializeScoreBreakdown(domain.getScoreBreakdown());
        String rejectedJson = serializeStringList(domain.getRejectedAlternatives());

        return new AllocationDecisionEntity(
                runEntity,
                domain.getActivityId(),
                domain.getSelectedResourceId(),
                domain.getSelectedSiteId(),
                dayOfWeek,
                start,
                end,
                domain.isFeasible(),
                domain.getScore(),
                domain.getHumanExplanation(),
                scoreBreakdownJson,
                "[]",
                rejectedJson
        );
    }

    private static ScoreBreakdown parseScoreBreakdownJson(String json, double fallbackTotal) {
        if (json == null || json.trim().isEmpty()) {
            return new ScoreBreakdown(100.0, 100.0, 100.0, 100.0, 100.0, fallbackTotal);
        }
        try {
            // Simple format parsing e.g. {"capacityFit":100.0,"preference":80.0,...}
            double c = extractJsonDouble(json, "capacityFit", 100.0);
            double p = extractJsonDouble(json, "preference", 100.0);
            double u = extractJsonDouble(json, "utilization", 100.0);
            double f = extractJsonDouble(json, "fairness", 100.0);
            double l = extractJsonDouble(json, "location", 100.0);
            double total = extractJsonDouble(json, "total", fallbackTotal);
            return new ScoreBreakdown(c, p, u, f, l, total);
        } catch (Exception e) {
            return new ScoreBreakdown(100.0, 100.0, 100.0, 100.0, 100.0, fallbackTotal);
        }
    }

    private static String serializeScoreBreakdown(ScoreBreakdown breakdown) {
        if (breakdown == null) return "{}";
        return String.format(
                "{\"capacityFit\":%.2f,\"preference\":%.2f,\"utilization\":%.2f,\"fairness\":%.2f,\"location\":%.2f,\"total\":%.2f}",
                breakdown.getCapacityFitScore(),
                breakdown.getPreferenceScore(),
                breakdown.getUtilizationScore(),
                breakdown.getFairnessScore(),
                breakdown.getLocationScore(),
                breakdown.getTotalScore()
        );
    }

    private static double extractJsonDouble(String json, String key, double fallback) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return fallback;
        int start = idx + search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        if (end == -1) end = json.length();
        try {
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static List<String> parseStringListJson(String json) {
        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        String cleaned = json.replace("[", "").replace("]", "").replace("\"", "").trim();
        if (!cleaned.isEmpty()) {
            for (String part : cleaned.split(",")) {
                list.add(part.trim());
            }
        }
        return list;
    }

    private static String serializeStringList(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        return "[" + list.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",")) + "]";
    }
}
