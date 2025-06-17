package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.service.SecouristeMngt;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminEditUserView;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminEditUserController {

    private final AdminEditUserView view;
    private final MainApp navigator;
    private final Secouriste secouristeToEdit;
    private final SecouristeMngt secouristeMngt;
    private final CompetenceDAO competenceDAO;
    private final Set<Competence> initialCompetences;

    public AdminEditUserController(AdminEditUserView view, MainApp navigator, Secouriste secouristeToEdit) {
        this.view = view;
        this.navigator = navigator;
        this.secouristeToEdit = secouristeToEdit;
        this.secouristeMngt = new SecouristeMngt();
        this.competenceDAO = new CompetenceDAO();
        
        // Stocke l'état initial des compétences de l'utilisateur pour comparaison
        this.initialCompetences = new HashSet<>(secouristeToEdit.getCompetences());

        // Attacher les écouteurs d'événements
        view.setSaveButtonAction(e -> saveChanges());
        view.setCancelButtonAction(e -> cancel());
        
        // Charger les données dans la vue
        loadData();
    }

    private void loadData() {
        Platform.runLater(() -> {
            // 1. Remplir les informations personnelles de l'utilisateur
            view.setSecouristeData(secouristeToEdit);
            
            // 2. Charger toutes les compétences disponibles
            List<Competence> allCompetences = competenceDAO.findAll();
            
            // 3. Remplir la liste des compétences dans la vue, en cochant celles que l'utilisateur possède
            view.populateCompetences(allCompetences, initialCompetences);
        });
    }

    private void saveChanges() {
        // 1. Mettre à jour les informations personnelles
        try {
            secouristeToEdit.setPrenom(view.getPrenom());
            secouristeToEdit.setNom(view.getNom());
            secouristeToEdit.setTel(view.getTel());
            secouristeToEdit.setAddresse(view.getAdresse());
            if (view.getDateNaissance() != null) {
                secouristeToEdit.setDateNaissance(Date.from(view.getDateNaissance().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            // Sauvegarder les informations mises à jour du secouriste dans la BDD
            secouristeMngt.update(secouristeToEdit);
            
        } catch (IllegalArgumentException e) {
            NotificationUtils.showError("Données invalides", e.getMessage());
            return;
        }

        // 2. Mettre à jour les compétences
        Map<String, CheckBox> checkBoxes = view.getCompetenceCheckBoxes();
        
        for(Map.Entry<String, CheckBox> entry : checkBoxes.entrySet()) {
            String intitule = entry.getKey();
            boolean isSelected = entry.getValue().isSelected();
            boolean wasSelected = initialCompetences.stream().anyMatch(c -> c.getIntitule().equals(intitule));

            if (isSelected && !wasSelected) {
                // La compétence a été ajoutée
                secouristeMngt.addCompetenceToSecouriste(secouristeToEdit.getId(), intitule);
            } else if (!isSelected && wasSelected) {
                // La compétence a été supprimée
                secouristeMngt.removeCompetenceFromSecouriste(secouristeToEdit.getId(), intitule);
            }
        }
        
        NotificationUtils.showSuccess("Mise à jour réussie", "Les informations du secouriste ont été mises à jour.");
        navigator.showAdminUtilisateursView(view.getCompte()); // Retour à la liste des utilisateurs
    }
    
    private void cancel() {
        navigator.showAdminUtilisateursView(view.getCompte()); // Retour sans sauvegarder
    }
}