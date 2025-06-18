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
import java.util.stream.Collectors; // NOUVEAU : Import pour le stream

public class AdminEditUserController {

    private final AdminEditUserView view;
    private final MainApp navigator;
    private final Secouriste secouristeToEdit;
    private final SecouristeMngt secouristeMngt;
    private final CompetenceDAO competenceDAO;

    public AdminEditUserController(AdminEditUserView view, MainApp navigator, Secouriste secouristeToEdit) {
        this.view = view;
        this.navigator = navigator;
        this.secouristeToEdit = secouristeToEdit;
        this.secouristeMngt = new SecouristeMngt();
        this.competenceDAO = new CompetenceDAO();

        view.setSaveButtonAction(e -> saveChanges());
        view.setCancelButtonAction(e -> cancel());

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            List<Competence> allCompetences = competenceDAO.findAll();
            Set<Competence> userCompetences = secouristeToEdit.getCompetences();

            Platform.runLater(() -> {
                view.setSecouristeData(secouristeToEdit);
                view.populateCompetences(allCompetences, userCompetences);
            });
        }).start();
    }

    /**
     * MODIFIÉ : La logique est maintenant déléguée au service SecouristeMngt.
     */
    private void saveChanges() {
        // 1. Mettre à jour l'objet secouriste avec les données du formulaire
        try {
            secouristeToEdit.setPrenom(view.getPrenom());
            secouristeToEdit.setNom(view.getNom());
            secouristeToEdit.setTel(view.getTel());
            secouristeToEdit.setAddresse(view.getAdresse());
            if (view.getDateNaissance() != null) {
                secouristeToEdit.setDateNaissance(Date.from(view.getDateNaissance().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        } catch (IllegalArgumentException e) {
            NotificationUtils.showError("Données invalides", e.getMessage());
            return;
        }

        // 2. Récupérer l'ensemble final des compétences souhaitées depuis la vue
        Set<Competence> selectedCompetences = view.getCompetenceCheckBoxes().entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // 3. Appeler le service avec les informations à jour et le nouvel ensemble de compétences
        boolean success = secouristeMngt.updateSecouristeInfoAndCompetences(secouristeToEdit, selectedCompetences);
        
        if (success) {
            NotificationUtils.showSuccess("Mise à jour réussie", "Les informations du secouriste ont été mises à jour.");
            navigator.showAdminUtilisateursView(view.getCompte());
        } else {
            NotificationUtils.showError("Échec", "La mise à jour a échoué. Consultez les logs pour plus de détails.");
        }
    }

    private void cancel() {
        navigator.showAdminUtilisateursView(view.getCompte());
    }
}