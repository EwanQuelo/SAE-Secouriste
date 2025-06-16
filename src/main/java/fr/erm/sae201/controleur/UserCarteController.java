package fr.erm.sae201.controleur;

import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Site;
import fr.erm.sae201.metier.service.AffectationMngt;
import fr.erm.sae201.vue.user.UserCarteView; // Import de la vue pour la méthode de couleur
import javafx.application.Platform;

import java.util.List;
import java.util.Locale;

/**
 * Controller for the "Carte" view.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET (and AI assistant)
 * @version 2.1
 */
public class UserCarteController {

    private final UserCarteView view;
    private final CompteUtilisateur compte;
    private final AffectationMngt affectationMngt;
    private List<Affectation> userAffectations;

    public UserCarteController(UserCarteView view, CompteUtilisateur compte) {
        this.view = view;
        this.compte = compte;
        this.affectationMngt = new AffectationMngt();
        this.view.setController(this);
        preloadData();
    }

    private void preloadData() {
        if (compte.getIdSecouriste() != null) {
            this.userAffectations = affectationMngt.getAssignmentsForSecouriste(compte.getIdSecouriste());
        }
    }

    public void onMapPageReady() {
        System.out.println("CONTROLLER: onMapPageReady() called by View. Loading and displaying affectations.");
        loadAndDisplayAffectations();
    }

    private void loadAndDisplayAffectations() {
        Platform.runLater(() -> {
            view.clearAffectationsList();
            view.clearAllMarkersFromMap();

            if (userAffectations == null || userAffectations.isEmpty()) {
                view.showNoAffectationsMessage();
                return;
            }

            for (Affectation affectation : userAffectations) {
                view.addAffectationToList(affectation);
                addMarkerForAssignment(affectation);
            }

            if (!userAffectations.isEmpty()) {
                Affectation first = userAffectations.get(0);
                Site site = first.getDps().getSite();
                view.centerMapOn(site.getLatitude(), site.getLongitude(), 13);
            }
        });
    }

    private void addMarkerForAssignment(Affectation affectation) {
        DPS dps = affectation.getDps();
        Site site = dps.getSite();
        String markerId = "affectation_" + dps.getId();
        String popupHtml = String.format("<b>%s</b><br>%s",
                dps.getSport().getNom().replace("'", "\\'"),
                site.getNom().replace("'", "\\'")
        );
        String iconHtml = generateMarkerIconHtml(dps);
        String script = String.format(Locale.US, "addOrUpdateMarker('%s', %f, %f, '%s', '%s');",
            markerId,
            site.getLatitude(),
            site.getLongitude(),
            popupHtml,
            iconHtml
        );
        view.executeMapScript(script);
    }

    /**
     * Generates custom HTML for a map marker.
     * **FIXED**: The '%' in '50%' is now escaped as '50%%'.
     * **IMPROVED**: Uses the static method from UserCarteView to get the color.
     *
     * @param dps The DPS object.
     * @return A string of HTML for a colored circle marker.
     */
    private String generateMarkerIconHtml(DPS dps) {
        // IMPROVED: Get the color from the centralized method in the View
        String color = UserCarteView.getDpsColor(dps);

        // FIXED: Escaped the '%' character to '%%'
        String html = String.format(
            "<div style='background-color:%s; color:white; border-radius:50%%; width:24px; height:24px; text-align:center; line-height:24px; font-weight:bold; border: 2px solid white; box-shadow: 0 0 5px rgba(0,0,0,0.5);'>%d</div>",
            color,
            dps.getId()
        );
        return html.replace("'", "\\'");
    }
}