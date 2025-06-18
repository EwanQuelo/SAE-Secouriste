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

public class AdminCompetencesController {

    private final AdminCompetencesView view;
    private final MainApp navigator;
    private final CompetenceDAO competenceDAO;
    private final CompetenceMngt competenceService;

    public AdminCompetencesController(AdminCompetencesView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.competenceDAO = new CompetenceDAO();
        // NOUVEAU : Instanciation du service
        this.competenceService = new CompetenceMngt();
        this.view.setAddButtonAction(event -> handleAddCompetence());
        loadCompetences();
    }

    public void loadCompetences() {
        // MODIFIÉ : Utilisation d'un thread pour ne pas geler l'UI
        new Thread(() -> {
            List<Competence> allCompetences = competenceDAO.findAll();
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

    // MODIFIÉ : La logique métier est déléguée au service
    private void handleAddCompetence() {
        Optional<Pair<String, List<Competence>>> result = view.showAddCompetenceDialog(competenceDAO.findAll());

        result.ifPresent(pair -> {
            String newIntitule = pair.getKey();
            List<Competence> prerequisites = pair.getValue();

            try {
                // Le contrôleur se contente d'appeler le service
                competenceService.createCompetence(newIntitule, prerequisites);
                NotificationUtils.showSuccess("Succès", "La compétence '" + newIntitule + "' a été créée.");
                loadCompetences(); // On rafraîchit la vue

            } catch (CompetenceMngt.CycleDetectedException e) {
                NotificationUtils.showError("Création impossible", e.getMessage());
            } catch (SQLException e) {
                NotificationUtils.showError("Erreur BDD", e.getMessage());
            }
        });
    }

    // MODIFIÉ : La logique métier est déléguée au service
    public void handleEditCompetence(Competence competenceToEdit) {
        List<Competence> allCompetences = competenceDAO.findAll();
        Optional<List<Competence>> result = view.showEditCompetenceDialog(competenceToEdit, allCompetences);

        result.ifPresent(newPrerequisitesList -> {
            try {
                // Le contrôleur se contente d'appeler le service
                competenceService.updatePrerequisites(competenceToEdit, newPrerequisitesList);
                NotificationUtils.showSuccess("Succès", "Les prérequis pour '" + competenceToEdit.getIntitule() + "' ont été mis à jour.");
                loadCompetences();

            } catch (CompetenceMngt.CycleDetectedException e) {
                NotificationUtils.showError("Modification impossible", e.getMessage());
            }
        });
    }

    // handleDeleteCompetence() ne change pas car la logique est simple (un seul appel DAO)
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