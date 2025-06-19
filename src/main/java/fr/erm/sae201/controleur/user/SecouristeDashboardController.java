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
 * Contrôleur pour la vue du tableau de bord du secouriste.
 * <p>
 * Il récupère et gère les données affichées sur le tableau de bord
 * principal de l'utilisateur, notamment son planning du jour et les
 * localisations correspondantes sur une carte.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SecouristeDashboardController {

    /** La vue du tableau de bord associée à ce contrôleur. */
    private final SecouristeDashboard view;

    /** Le compte de l'utilisateur secouriste connecté. */
    private final CompteUtilisateur compte;

    /** Le DAO pour accéder aux données des affectations. */
    private final AffectationDAO affectationDAO;

    /**
     * Stocke les affectations du jour pour les transmettre à la carte
     * une fois que celle-ci est initialisée.
     */
    private List<Affectation> dailyAssignments;

    /**
     * Constructeur du contrôleur du tableau de bord.
     *
     * @param view   La vue à contrôler.
     * @param compte Le compte de l'utilisateur connecté.
     */
    public SecouristeDashboardController(SecouristeDashboard view, CompteUtilisateur compte) {
        this.view = view;
        this.compte = compte;
        this.affectationDAO = new AffectationDAO();
        this.view.setController(this);
        loadDashboardData();
    }

    /**
     * Récupère les affectations pour le jour courant et met à jour la vue.
     * La méthode charge d'abord toutes les affectations de la semaine pour l'utilisateur,
     * puis filtre cette liste pour ne conserver que celles du jour actuel.
     */
    private void loadDashboardData() {
        if (compte.getIdSecouriste() == null) {
            view.showError("Erreur: Compte secouriste non trouvé.");
            return;
        }

        final LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<Affectation> weeklyAssignments = affectationDAO.findAffectationsForSecouristeBetweenDates(
            compte.getIdSecouriste(),
            startOfWeek,
            endOfWeek
        );

        this.dailyAssignments = weeklyAssignments.stream()
            .filter(a -> a.getDps().getJournee().getDate().equals(today))
            .collect(Collectors.toList());

        // S'assure que la mise à jour de l'interface est effectuée sur le thread JavaFX.
        Platform.runLater(() -> {
            view.populateSchedule(this.dailyAssignments, today);
            view.setMapFooterText("Vos affectations du jour");
        });
    }

    /**
     * Appelée par la vue lorsque la carte est prête.
     * Peuple la carte avec les marqueurs géographiques des affectations du jour.
     */
    public void onMapReady() {
        Platform.runLater(() -> {
            view.clearMapMarkers();

            if (this.dailyAssignments == null || this.dailyAssignments.isEmpty()) {
                return;
            }

            for (Affectation affectation : this.dailyAssignments) {
                view.addMarkerToMap(affectation);
            }

            Affectation firstAffectation = this.dailyAssignments.get(0);
            double lat = firstAffectation.getDps().getSite().getLatitude();
            double lon = firstAffectation.getDps().getSite().getLongitude();
            view.centerMap(lat, lon, 11);
        });
    }
}