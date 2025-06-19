// src/main/java/fr/erm/sae201/controleur/admin/AdminVisualiserController.java
package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.AffectationMngt;
import fr.erm.sae201.metier.service.DPSMngt;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminVisualiserView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminVisualiserController {

    private final AdminVisualiserView view;
    private final MainApp navigator;
    private final CompteUtilisateur compte;
    private final DPSMngt dpsMngt;
    private final AffectationMngt affectationMngt;

    private LocalDate currentWeekStart;

    // Record local pour passer des données enrichies à la vue
    public record DpsStatusInfo(DPS dps, int assigned, int required) {}

    public AdminVisualiserController(AdminVisualiserView view, MainApp navigator, CompteUtilisateur compte) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.dpsMngt = new DPSMngt();
        this.affectationMngt = new AffectationMngt();
        this.currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);

        // Bind actions from the view
        this.view.setWeekChangeHandler(weeksToAdd -> {
            currentWeekStart = currentWeekStart.plusWeeks(weeksToAdd);
            refreshView();
        });

        // Initial data load
        refreshView();
    }

    public void refreshView() {
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        List<DPS> allDpsInWeek = dpsMngt.getAllDpsBetweenDates(currentWeekStart, weekEnd);
        
        List<DpsStatusInfo> dpsStatusList = new ArrayList<>();
        for (DPS dps : allDpsInWeek) {
            int assignedCount = affectationMngt.getAssignmentCountForDps(dps.getId());
            int requiredCount = dpsMngt.getTotalPersonnelRequired(dps.getId());
            dpsStatusList.add(new DpsStatusInfo(dps, assignedCount, requiredCount));
        }
        
        // Pass the enriched data to the view to draw
        view.populateCalendar(currentWeekStart, dpsStatusList);
    }
}