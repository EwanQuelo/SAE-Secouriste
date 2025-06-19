package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.service.CompetenceMngt;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminCompetencesView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour l'interface de gestion des compétences par l'administrateur.
 * 
 * Cette classe gère les interactions de l'utilisateur avec la vue des compétences.
 * Elle coordonne les appels au service métier (pour la logique de création/modification)
 * et au DAO (pour l'accès aux données), puis met à jour l'interface en conséquence.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminCompetencesController {

    /** La vue associée à ce contrôleur. */
    private final AdminCompetencesView view;

    /** Le navigateur principal de l'application. */
    private final MainApp navigator;

    /** Le DAO pour l'accès direct aux données des compétences. */
    private final CompetenceDAO competenceDAO;

    /** Le service métier pour la gestion des règles logiques des compétences. */
    private final CompetenceMngt competenceService;

    /**
     * Constructeur du contrôleur des compétences.
     * Initialise les composants et lie l'action du bouton d'ajout à sa méthode de gestion.
     *
     * @param view La vue associée à ce contrôleur.
     * @param navigator Le navigateur principal de l'application.
     */
    public AdminCompetencesController(AdminCompetencesView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.competenceDAO = new CompetenceDAO();
        this.competenceService = new CompetenceMngt();
        this.view.setAddButtonAction(event -> handleAddCompetence());
        loadCompetences();
    }

    /**
     * Charge la liste des compétences depuis la base de données et les affiche dans la vue.
     * L'opération d'accès aux données est effectuée dans un thread séparé
     * pour ne pas bloquer l'interface utilisateur pendant le chargement.
     */
    public void loadCompetences() {
        new Thread(() -> {
            List<Competence> allCompetences = competenceDAO.findAll();
            // L'interface graphique ne peut être modifiée que depuis le thread principal de JavaFX.
            // Platform.runLater s'assure que le code de mise à jour de la vue est exécuté correctement.
            Platform.runLater(() -> {
                view.clearCompetencesList();
                if (allCompetences.isEmpty()) {
                    view.showEmptyMessage("Aucune compétence n'est enregistrée.");
                } else {
                    for (Competence c : allCompetences) {
                        view.addCompetenceCard(c, this::handleDeleteCompetence, this::handleEditCompetence);
                    }
                }
            });
        }).start();
    }

    /**
     * Gère le processus de création d'une nouvelle compétence.
     * Ouvre une boîte de dialogue, récupère les informations saisies,
     * puis délègue la création au service métier qui applique les règles de validation (ex: détection de cycle).
     */
    private void handleAddCompetence() {
        Optional<Pair<String, List<Competence>>> result = view.showAddCompetenceDialog(competenceDAO.findAll());

        result.ifPresent(pair -> {
            String newIntitule = pair.getKey();
            List<Competence> prerequisites = pair.getValue();

            try {
                competenceService.createCompetence(newIntitule, prerequisites);
                NotificationUtils.showSuccess("Succès", "La compétence '" + newIntitule + "' a été créée.");
                loadCompetences(); // Rafraîchit la vue pour afficher la nouvelle compétence

            } catch (CompetenceMngt.CycleDetectedException e) {
                NotificationUtils.showError("Création impossible", e.getMessage());
            } catch (SQLException e) {
                NotificationUtils.showError("Erreur BDD", e.getMessage());
            }
        });
    }

    /**
     * Gère la modification des prérequis d'une compétence existante.
     * Ouvre une boîte de dialogue, puis délègue la mise à jour au service métier
     * pour s'assurer qu'aucune dépendance cyclique n'est créée.
     *
     * @param competenceToEdit La compétence à modifier.
     */
    public void handleEditCompetence(Competence competenceToEdit) {
        List<Competence> allCompetences = competenceDAO.findAll();
        Optional<List<Competence>> result = view.showEditCompetenceDialog(competenceToEdit, allCompetences);

        result.ifPresent(newPrerequisitesList -> {
            try {
                competenceService.updatePrerequisites(competenceToEdit, newPrerequisitesList);
                NotificationUtils.showSuccess("Succès", "Les prérequis pour '" + competenceToEdit.getIntitule() + "' ont été mis à jour.");
                loadCompetences();

            } catch (CompetenceMngt.CycleDetectedException e) {
                NotificationUtils.showError("Modification impossible", e.getMessage());
            }
        });
    }

    /**
     * Gère la suppression d'une compétence.
     * Affiche une alerte de confirmation avant de procéder à la suppression définitive.
     *
     * @param competence La compétence à supprimer.
     */
    public void handleDeleteCompetence(Competence competence) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la compétence '" + competence.getIntitule() + "' ?");
        confirmation.setContentText("Cette action est irréversible et la supprimera de tous les secouristes et prérequis.");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (competenceDAO.delete(competence) > 0) {
                NotificationUtils.showSuccess("Suppression réussie", "La compétence a été supprimée.");
                loadCompetences();
            } else {
                NotificationUtils.showError("Erreur", "La suppression a échoué.");
            }
        }
    }
}