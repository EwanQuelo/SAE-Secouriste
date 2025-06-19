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

/**
 * Contrôleur pour la vue de modification des informations d'un secouriste.
 * 
 * Cette classe charge les données du secouriste à éditer, y compris ses
 * compétences
 * actuelles, et permet à l'administrateur de les mettre à jour. La sauvegarde
 * est gérée par le service SecouristeMngt qui assure la cohérence des données.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminEditUserController {

    /** La vue de modification d'utilisateur. */
    private final AdminEditUserView view;

    /** Le navigateur principal de l'application. */
    private final MainApp navigator;

    /** Le secouriste dont les informations sont en cours de modification. */
    private final Secouriste secouristeToEdit;

    /** Le service métier pour la gestion de la logique des secouristes. */
    private final SecouristeMngt secouristeMngt;

    /** Le DAO pour accéder aux données des compétences. */
    private final CompetenceDAO competenceDAO;

    /**
     * Constructeur du contrôleur de modification d'utilisateur.
     *
     * @param view             La vue à contrôler.
     * @param navigator        Le navigateur principal.
     * @param secouristeToEdit Le secouriste à modifier.
     */
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

    /**
     * Charge les données du secouriste et la liste de toutes les compétences
     * de manière asynchrone pour peupler la vue.
     */
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
     * Gère la sauvegarde des modifications apportées au secouriste.
     * Récupère les données du formulaire, met à jour l'objet Secouriste,
     * puis délègue la mise à jour en base de données au service SecouristeMngt.
     */
    private void saveChanges() {
        try {
            secouristeToEdit.setPrenom(view.getPrenom());
            secouristeToEdit.setNom(view.getNom());
            secouristeToEdit.setTel(view.getTel());
            secouristeToEdit.setAddresse(view.getAdresse());
            if (view.getDateNaissance() != null) {
                secouristeToEdit.setDateNaissance(
                        Date.from(view.getDateNaissance().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        } catch (IllegalArgumentException e) {
            NotificationUtils.showError("Données invalides", e.getMessage());
            return;
        }

        // Récupère l'ensemble des compétences cochées dans la vue.
        Set<Competence> selectedCompetences = new HashSet<>();
        for (Map.Entry<Competence, CheckBox> entry : view.getCompetenceCheckBoxes().entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedCompetences.add(entry.getKey());
            }
        }

        boolean success = secouristeMngt.updateSecouristeInfoAndCompetences(secouristeToEdit, selectedCompetences);

        if (success) {
            NotificationUtils.showSuccess("Mise à jour réussie",
                    "Les informations du secouriste ont été mises à jour.");
            navigator.showAdminUtilisateursView(view.getCompte());
        } else {
            NotificationUtils.showError("Échec",
                    "La mise à jour a échoué. Consultez les journaux pour plus de détails.");
        }
    }

    /**
     * Annule les modifications et retourne à la liste des utilisateurs.
     */
    private void cancel() {
        navigator.showAdminUtilisateursView(view.getCompte());
    }
}