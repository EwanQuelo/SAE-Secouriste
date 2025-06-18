package fr.erm.sae201.controleur.user;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.user.SecouristeDashboard;
import javafx.application.Platform;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the SecouristeDashboard view.
 * It fetches and manages the data displayed on the user's main dashboard.
 */
public class SecouristeDashboardController {

    private final SecouristeDashboard view;
    private final CompteUtilisateur compte;
    private final AffectationDAO affectationDAO;

    // We store the daily assignments to pass them to the map when it's ready.
    private List<Affectation> dailyAssignments;

    public SecouristeDashboardController(SecouristeDashboard view, CompteUtilisateur compte) {
        this.view = view;
        this.compte = compte;
        this.affectationDAO = new AffectationDAO();
        this.view.setController(this);
        loadDashboardData();
    }

    /**
     * Fetches assignments for the current day and populates the view.
     */
    private void loadDashboardData() {
        if (compte.getIdSecouriste() == null) {
            view.showError("Erreur: Compte secouriste non trouvé.");
            return;
        }

        // Use the actual current date for a live dashboard.
        final LocalDate today = LocalDate.now();

        // Find the start and end of the current week.
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        // Fetch all assignments for the secouriste for the entire week.
        List<Affectation> weeklyAssignments = affectationDAO.findAffectationsForSecouristeBetweenDates(
            compte.getIdSecouriste(),
            startOfWeek,
            endOfWeek
        );

        // CORRECTION: Filter to get assignments only for the current day.
        this.dailyAssignments = weeklyAssignments.stream()
            .filter(a -> a.getDps().getJournee().getDate().equals(today))
            .collect(Collectors.toList());

        // Update the UI on the JavaFX thread with today's assignments and date.
        Platform.runLater(() -> {
            view.populateSchedule(this.dailyAssignments, today);
            view.setMapFooterText("Vos affectations du jour");
        });
    }

    /**
     * Called by the view when the map is ready. Populates the map with markers for the day.
     */
    public void onMapReady() {
        Platform.runLater(() -> {
            view.clearMapMarkers();

            if (this.dailyAssignments == null || this.dailyAssignments.isEmpty()) {
                // If there are no assignments today, we don't add markers.
                // We could center the map on a default location here if desired.
                return;
            }

            // CORRECTION: Add markers only for the daily assignments.
            for (Affectation affectation : this.dailyAssignments) {
                view.addMarkerToMap(affectation);
            }

            // Center the map on the first assignment of the day.
            Affectation firstAffectation = this.dailyAssignments.get(0);
            double lat = firstAffectation.getDps().getSite().getLatitude();
            double lon = firstAffectation.getDps().getSite().getLongitude();
            view.centerMap(lat, lon, 11); // Zoom in a bit more
        });
    }
}