package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.JourneeDAO;
import fr.erm.sae201.dao.SiteDAO;
import fr.erm.sae201.dao.SportDAO;
import fr.erm.sae201.metier.persistence.*;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminCreateDpsView;
import javafx.application.Platform;
import java.time.LocalDate;
import java.util.List;

public class AdminCreateDpsController {

    private final AdminCreateDpsView view;
    private final MainApp navigator;
    private final DPSDAO dpsDAO;
    private final SiteDAO siteDAO;
    private final SportDAO sportDAO;
    private final JourneeDAO journeeDAO; // Assurez-vous que ce DAO existe

    public AdminCreateDpsController(AdminCreateDpsView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsDAO = new DPSDAO();
        this.siteDAO = new SiteDAO();
        this.sportDAO = new SportDAO();
        this.journeeDAO = new JourneeDAO(); // Assurez-vous que ce DAO existe

        view.setSaveButtonAction(e -> saveDps());
        view.setCancelButtonAction(e -> cancel());

        loadComboBoxData();
    }

    private void loadComboBoxData() {
        Platform.runLater(() -> {
            List<Site> sites = siteDAO.findAll();
            view.populateSiteComboBox(sites);

            List<Sport> sports = sportDAO.findAll();
            view.populateSportComboBox(sports);
        });
    }

    private void saveDps() {
        Site site = view.getSelectedSite();
        Sport sport = view.getSelectedSport();
        LocalDate date = view.getSelectedDate();
        
        if (site == null || sport == null || date == null) {
            NotificationUtils.showError("Champs manquants", "Veuillez sélectionner un site, un sport et une date.");
            return;
        }

        try {
            int startHour = Integer.parseInt(view.getStartHour());
            int startMinute = Integer.parseInt(view.getStartMinute());
            int endHour = Integer.parseInt(view.getEndHour());
            int endMinute = Integer.parseInt(view.getEndMinute());

            // On s'assure que la journée existe dans la BDD avant de l'utiliser
            journeeDAO.create(new Journee(date));

            int[] horaireDepart = {startHour, startMinute};
            int[] horaireFin = {endHour, endMinute};
            
            // L'ID sera généré par la BDD, on peut passer 0
            DPS newDps = new DPS(0, horaireDepart, horaireFin, site, new Journee(date), sport);
            
            if (dpsDAO.create(newDps) != -1) {
                NotificationUtils.showSuccess("Succès", "Le dispositif a été créé avec succès.");
                navigator.showAdminDispositifView(view.getCompte());
            } else {
                NotificationUtils.showError("Erreur", "La création du dispositif a échoué.");
            }

        } catch (NumberFormatException e) {
            NotificationUtils.showError("Format invalide", "Les heures et minutes doivent être des nombres.");
        } catch (IllegalArgumentException e) {
            NotificationUtils.showError("Données invalides", e.getMessage());
        }
    }

    private void cancel() {
        navigator.showAdminDispositifView(view.getCompte());
    }
}