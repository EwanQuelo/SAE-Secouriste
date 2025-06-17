package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.JourneeDAO;
import fr.erm.sae201.dao.SiteDAO;
import fr.erm.sae201.dao.SportDAO;
import fr.erm.sae201.metier.persistence.*;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminEditDpsView;
import javafx.application.Platform;
import java.time.LocalDate;
import java.util.List;

public class AdminEditDpsController {

    private final AdminEditDpsView view;
    private final MainApp navigator;
    private final DPS dpsToEdit;
    private final DPSDAO dpsDAO;
    private final SiteDAO siteDAO;
    private final SportDAO sportDAO;
    private final JourneeDAO journeeDAO;

    public AdminEditDpsController(AdminEditDpsView view, MainApp navigator, DPS dpsToEdit) {
        this.view = view;
        this.navigator = navigator;
        this.dpsToEdit = dpsToEdit;
        this.dpsDAO = new DPSDAO();
        this.siteDAO = new SiteDAO();
        this.sportDAO = new SportDAO();
        this.journeeDAO = new JourneeDAO();

        view.setSaveButtonAction(e -> saveDps());
        view.setCancelButtonAction(e -> cancel());

        loadComboBoxData();
        initializeForm();
    }

    private void initializeForm() {
        if (dpsToEdit != null) {
            // --- Mode ÉDITION ---
            view.setFormTitle("Modifier le Dispositif");
            view.setDpsData(dpsToEdit);


            view.setDateFieldsEditable(false);

        } else {
            // --- Mode CRÉATION ---
            view.setFormTitle("Créer un nouveau Dispositif");


            view.setDateFieldsEditable(true);
        }
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

            journeeDAO.create(new Journee(date));

            int[] horaireDepart = { startHour, startMinute };
            int[] horaireFin = { endHour, endMinute };

            if (dpsToEdit != null) {
                // Mise à jour
                dpsToEdit.setSite(site);
                dpsToEdit.setSport(sport);
                dpsToEdit.setJournee(new Journee(date));
                dpsToEdit.setHoraireDepart(horaireDepart);
                dpsToEdit.setHoraireFin(horaireFin);

                if (dpsDAO.update(dpsToEdit) > 0) {
                    NotificationUtils.showSuccess("Succès", "Dispositif mis à jour.");
                    navigator.showAdminDispositifView(view.getCompte());
                } else {
                    NotificationUtils.showError("Erreur", "La mise à jour a échoué.");
                }
            } else {
                // Création
                DPS newDps = new DPS(0, horaireDepart, horaireFin, site, new Journee(date), sport);
                if (dpsDAO.create(newDps) != -1) {
                    NotificationUtils.showSuccess("Succès", "Dispositif créé.");
                    navigator.showAdminDispositifView(view.getCompte());
                } else {
                    NotificationUtils.showError("Erreur", "La création a échoué.");
                }
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