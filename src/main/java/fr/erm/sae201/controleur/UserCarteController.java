package fr.erm.sae201.controleur;

import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Site;
import fr.erm.sae201.metier.service.AffectationMngt;
import fr.erm.sae201.vue.user.UserCarteView;
import javafx.application.Platform;

import java.util.List;
import java.util.Locale;

/**
 * Contrôleur pour la vue "Carte".
 * <p>
 * Cette classe charge les affectations de l'utilisateur et les affiche
 * sur une carte géographique. Elle gère la création des marqueurs personnalisés
 * et la communication avec la vue pour l'affichage et le centrage.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 2.1
 */
public class UserCarteController {

    /** La vue de la carte associée à ce contrôleur. */
    private final UserCarteView view;

    /** Le compte de l'utilisateur connecté. */
    private final CompteUtilisateur compte;

    /** Le service métier pour la gestion des affectations. */
    private final AffectationMngt affectationMngt;

    /** La liste des affectations de l'utilisateur, pré-chargée pour un accès rapide. */
    private List<Affectation> userAffectations;

    /**
     * Constructeur du contrôleur de la carte.
     *
     * @param view   La vue à contrôler.
     * @param compte Le compte de l'utilisateur connecté.
     */
    public UserCarteController(UserCarteView view, CompteUtilisateur compte) {
        this.view = view;
        this.compte = compte;
        this.affectationMngt = new AffectationMngt();
        this.view.setController(this);
        preloadData();
    }

    /**
     * Pré-charge les affectations de l'utilisateur depuis la base de données
     * pour les rendre disponibles immédiatement lorsque la vue est prête.
     */
    private void preloadData() {
        if (compte.getIdSecouriste() != null) {
            this.userAffectations = affectationMngt.getAssignmentsForSecouriste(compte.getIdSecouriste());
        }
    }

    /**
     * Méthode appelée par la vue lorsque la page de la carte est entièrement chargée.
     * Déclenche le chargement et l'affichage des affectations sur la carte.
     */
    public void onMapPageReady() {
        System.out.println("CONTROLLER: onMapPageReady() called by View. Loading and displaying affectations.");
        loadAndDisplayAffectations();
    }

    /**
     * Efface les listes et marqueurs existants, puis charge et affiche
     * les nouvelles affectations sur la carte et dans la liste textuelle.
     */
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

    /**
     * Construit et exécute le script JavaScript pour ajouter un marqueur
     * sur la carte pour une affectation donnée.
     *
     * @param affectation L'affectation pour laquelle créer un marqueur.
     */
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
     * Génère le code HTML pour une icône de marqueur personnalisée.
     * L'icône est un cercle coloré contenant l'identifiant du DPS.
     * La couleur est déterminée par une méthode centralisée dans la vue.
     *
     * @param dps Le DPS concerné.
     * @return Une chaîne de caractères HTML représentant l'icône du marqueur.
     */
    private String generateMarkerIconHtml(DPS dps) {
        String color = UserCarteView.getDpsColor(dps);

        String html = String.format(
            "<div style='background-color:%s; color:white; border-radius:50%%; width:24px; height:24px; text-align:center; line-height:24px; font-weight:bold; border: 2px solid white; box-shadow: 0 0 5px rgba(0,0,0,0.5);'>%d</div>",
            color,
            dps.getId()
        );
        return html.replace("'", "\\'");
    }
}