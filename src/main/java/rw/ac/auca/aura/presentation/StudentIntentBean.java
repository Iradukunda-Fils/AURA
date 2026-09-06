package rw.ac.auca.aura.presentation;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.resource.Resource;

import java.io.Serializable;
import java.util.List;

@Named("studentBean")
@SessionScoped
public class StudentIntentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AdminAllocationBean adminBean;

    private String searchQuery = "";
    private String selectedCampusFilter = "ALL";

    public List<Allocation> getFilteredAllocations() {
        if (adminBean == null) {
            return List.of();
        }

        List<Allocation> all = adminBean.getCommittedAllocations();
        if (all == null) return List.of();

        return all.stream()
                .filter(alloc -> {
                    boolean matchesQuery = searchQuery == null || searchQuery.isBlank() ||
                            alloc.getActivityId().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            alloc.getResourceIds().toString().toLowerCase().contains(searchQuery.toLowerCase());

                    boolean matchesCampus = "ALL".equalsIgnoreCase(selectedCampusFilter) ||
                            alloc.getSiteId().toUpperCase().contains(selectedCampusFilter.toUpperCase());

                    return matchesQuery && matchesCampus;
                })
                .toList();
    }

    public List<Resource> getAvailableRooms() {
        if (adminBean == null) return List.of();
        return adminBean.getAllocationService().getRegisteredResources();
    }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public String getSelectedCampusFilter() { return selectedCampusFilter; }
    public void setSelectedCampusFilter(String selectedCampusFilter) { this.selectedCampusFilter = selectedCampusFilter; }
}
