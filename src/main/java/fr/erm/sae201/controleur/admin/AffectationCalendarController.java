package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.AffectationMngt;
import fr.erm.sae201.metier.service.DPSMngt;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AffectationCalendarView;
import javafx.application.Platform;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AffectationCalendarController {

    // A record to hold all necessary info for one event in the calendar
    public record DpsCalendarEvent(DPS dps, int assignedCount, int requiredCount) {}

    private final AffectationCalendarView view;
    private final MainApp navigator;
    private final DPSMngt dpsMngt;
    private final AffectationMngt affectationMngt;

    private LocalDate currentWeekStart;

    public AffectationCalendarController(AffectationCalendarView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsMngt = new DPSMngt();
        this.affectationMngt = new AffectationMngt();
        // Start on the current week
        this.currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);

        // Link view buttons to controller actions
        this.view.setNextWeekAction(e -> changeWeek(1));
        this.view.setPrevWeekAction(e -> changeWeek(-1));
        this.view.setTodayAction(e -> goToToday());

        // Initial data load
        loadWeekData();
    }

    public void changeWeek(int weeksToAdd) {
        this.currentWeekStart = this.currentWeekStart.plusWeeks(weeksToAdd);
        loadWeekData();
    }

    public void goToToday() {
        this.currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        loadWeekData();
    }

    private void loadWeekData() {
        // Prepare the view for new data
        view.prepareForData(currentWeekStart);

        new Thread(() -> {
            LocalDate weekEnd = currentWeekStart.plusDays(6);
            List<DPS> dpsList = dpsMngt.getAllDpsBetweenDates(currentWeekStart, weekEnd);

            Map<LocalDate, List<DpsCalendarEvent>> calendarData = new HashMap<>();

            for (DPS dps : dpsList) {
                if (dps == null) continue;

                int assignedCount = affectationMngt.getAssignmentCountForDps(dps.getId());
                int requiredCount = dps.getCompetencesRequises().values().stream().mapToInt(Integer::intValue).sum();

                DpsCalendarEvent event = new DpsCalendarEvent(dps, assignedCount, requiredCount);

                calendarData.computeIfAbsent(dps.getJournee().getDate(), k -> new ArrayList<>()).add(event);
            }

            // Update the UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                view.populateCalendar(calendarData);
            });
        }).start();
    }
}