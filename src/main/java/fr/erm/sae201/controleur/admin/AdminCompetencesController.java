package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.metier.graphe.algorithme.TriTopologique; // NOUVEL IMPORT
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminCompetencesView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

import java.util.HashSet; // NOUVEL IMPORT
import java.util.List;
import java.util.Optional;
import java.util.Set; // NOUVEL IMPORT

public class AdminCompetencesController {

    private final AdminCompetencesView view;
    private final MainApp navigator;
    private final CompetenceDAO competenceDAO;

    // ... (constructeur et loadCompetences() ne changent pas)
    public AdminCompetencesController(AdminCompetencesView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.competenceDAO = new CompetenceDAO();
        this.view.setAddButtonAction(event -> handleAddCompetence());
        loadCompetences();
    }

    public void loadCompetences() {
        Platform.runLater(() -> {
            List<Competence> allCompetences = competenceDAO.findAll();
            view.clearCompetencesList();

            if (allCompetences.isEmpty()) {
                view.showEmptyMessage("Aucune compétence n'est enregistrée.");
            } else {
                for (Competence c : allCompetences) {
                    // On passe les deux handlers
                    view.addCompetenceCard(c, this::handleDeleteCompetence, this::handleEditCompetence);
                }
            }
        });
    }

    private void handleAddCompetence() {
        // La vue nous retourne le nom et les prérequis sélectionnés
        Optional<Pair<String, List<Competence>>> result = view.showAddCompetenceDialog(competenceDAO.findAll());

        result.ifPresent(pair -> {
            String newIntitule = pair.getKey();
            List<Competence> prerequisites = pair.getValue();

            // --- DÉBUT DE LA MODIFICATION : VÉRIFICATION DU CYCLE ---

            // 1. On récupère le graphe actuel des compétences
            Set<Competence> graphToTest = new HashSet<>(competenceDAO.findAll());

            // 2. On simule l'ajout de la nouvelle compétence et de ses prérequis
            Competence newCompetenceNode = new Competence(newIntitule);
            newCompetenceNode.setPrerequisites(new HashSet<>(prerequisites));
            graphToTest.add(newCompetenceNode);

            // 3. On utilise votre classe pour vérifier la présence d'un cycle
            TriTopologique validator = new TriTopologique();
            if (!validator.estAcyclique(graphToTest)) {
                // Si un cycle est détecté, on affiche une erreur et on arrête
                NotificationUtils.showError("Création impossible", "L'ajout de ces prérequis créerait un cycle de dépendances. (Ex: A -> B -> A)");
                return; // On arrête le processus ici
            }

            // --- FIN DE LA MODIFICATION ---

            // Si la vérification passe, on continue la création
            Competence newCompetenceToSave = new Competence(newIntitule);

            if (competenceDAO.create(newCompetenceToSave) > 0) {
                // On ajoute les prérequis un par un
                for (Competence prereq : prerequisites) {
                    competenceDAO.addPrerequisite(newIntitule, prereq.getIntitule());
                }
                NotificationUtils.showSuccess("Succès", "La compétence '" + newIntitule + "' a été créée.");
                loadCompetences(); // On rafraîchit la vue
            } else {
                NotificationUtils.showError("Erreur", "Une compétence avec ce nom existe déjà.");
            }
        });
    }
    
    public void handleEditCompetence(Competence competenceToEdit) {
        // 1. Récupérer toutes les compétences pour les proposer comme prérequis
        List<Competence> allCompetences = competenceDAO.findAll();
        
        // 2. Ouvrir la boîte de dialogue de modification
        Optional<List<Competence>> result = view.showEditCompetenceDialog(competenceToEdit, allCompetences);

        result.ifPresent(newPrerequisitesList -> {
            // 3. Vérifier si la modification crée un cycle (DAG check)
            Set<Competence> graphToTest = new HashSet<>(allCompetences);
            
            // On trouve la compétence à modifier dans notre copie du graphe
            Competence nodeToUpdate = graphToTest.stream()
                .filter(c -> c.equals(competenceToEdit))
                .findFirst().orElse(null);

            if (nodeToUpdate != null) {
                // On met à jour ses prérequis dans la copie pour le test
                nodeToUpdate.setPrerequisites(new HashSet<>(newPrerequisitesList));

                TriTopologique validator = new TriTopologique();
                if (!validator.estAcyclique(graphToTest)) {
                    NotificationUtils.showError("Modification impossible", "Cette modification créerait un cycle de dépendances.");
                    return; // On arrête tout
                }
            }

            // 4. Si le test est réussi, on applique les changements en base de données
            // D'abord, on supprime tous les anciens prérequis
            Set<Competence> oldPrerequisites = competenceToEdit.getPrerequisites();
            for (Competence oldPrereq : oldPrerequisites) {
                competenceDAO.removePrerequisite(competenceToEdit.getIntitule(), oldPrereq.getIntitule());
            }

            // Ensuite, on ajoute les nouveaux
            for (Competence newPrereq : newPrerequisitesList) {
                competenceDAO.addPrerequisite(competenceToEdit.getIntitule(), newPrereq.getIntitule());
            }

            NotificationUtils.showSuccess("Succès", "Les prérequis pour '" + competenceToEdit.getIntitule() + "' ont été mis à jour.");
            loadCompetences(); // Recharger la vue pour afficher les changements
        });
    }

    // handleDeleteCompetence() ne change pas
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