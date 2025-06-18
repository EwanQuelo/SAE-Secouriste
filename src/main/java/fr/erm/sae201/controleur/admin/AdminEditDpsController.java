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
import java.util.Optional;

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
        view.setAddSiteAction(e -> addNewSite());
        view.setAddSportAction(e -> addNewSport());

        loadComboBoxData();
        initializeForm();
    }

    private void initializeForm() {
        if (dpsToEdit != null) {
            view.setFormTitle("Modifier le Dispositif");
            view.setDpsData(dpsToEdit);
            view.setDateFieldsEditable(false);
        } else {
            view.setFormTitle("Créer un nouveau Dispositif");
            view.setDateFieldsEditable(true);
        }
    }

    private void loadComboBoxData() {
        Platform.runLater(() -> {
            view.populateSiteComboBox(siteDAO.findAll());
            view.populateSportComboBox(sportDAO.findAll());
        });
    }

    private void saveDps() {
        Site site = view.getSelectedSite();
        Sport sport = view.getSelectedSport();
        LocalDate date = view.getSelectedDate();

        if (site == null || sport == null || date == null || view.getStartHour().isEmpty()
                || view.getEndHour().isEmpty()) {
            NotificationUtils.showError("Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            int startHour = Integer.parseInt(view.getStartHour());
            int startMinute = view.getStartMinute().isEmpty() ? 0 : Integer.parseInt(view.getStartMinute());
            int endHour = Integer.parseInt(view.getEndHour());
            int endMinute = view.getEndMinute().isEmpty() ? 0 : Integer.parseInt(view.getEndMinute());

            journeeDAO.create(new Journee(date));

            int[] horaireDepart = { startHour, startMinute };
            int[] horaireFin = { endHour, endMinute };

            if (dpsToEdit != null) {
                dpsToEdit.setSite(site);
                dpsToEdit.setSport(sport);
                // La date et les horaires ne sont pas modifiés car les champs sont désactivés
                if (dpsDAO.update(dpsToEdit) > 0) {
                    NotificationUtils.showSuccess("Succès", "Dispositif mis à jour.");
                    navigator.showAdminDispositifView(view.getCompte());
                } else {
                    NotificationUtils.showError("Erreur", "La mise à jour a échoué.");
                }
            } else {
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

    private void addNewSite() {
        // Appelle la nouvelle méthode de dialogue qui retourne un objet Site
        Optional<Site> result = view.showCreateSiteDialog();

        // si l'utilisateur a cliqué sur "Créer" et que les données sont valides
        result.ifPresent(newSite -> {
            if (siteDAO.create(newSite) != -1) {
                NotificationUtils.showSuccess("Succès", "Site '" + newSite.getNom() + "' créé.");
                loadComboBoxData(); // Rafraîchit la liste des sites
            } else {
                NotificationUtils.showError("Erreur", "Ce site (ou son code) existe déjà.");
            }
        });
    }

    private void addNewSport() {
        // Appelle la nouvelle méthode de dialogue qui retourne un objet Sport
        Optional<Sport> result = view.showCreateSportDialog();

        result.ifPresent(newSport -> {
            if (sportDAO.create(newSport) != -1) {
                NotificationUtils.showSuccess("Succès", "Sport '" + newSport.getNom() + "' créé.");
                loadComboBoxData(); // Rafraîchit la liste des sports
            } else {
                NotificationUtils.showError("Erreur", "Ce sport (ou son code) existe déjà.");
            }
        });
    }

    private void cancel() {
        navigator.showAdminDispositifView(view.getCompte());
    }
}