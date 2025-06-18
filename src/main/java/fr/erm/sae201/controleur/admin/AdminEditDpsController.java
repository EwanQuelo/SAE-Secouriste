package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.SiteDAO;
import fr.erm.sae201.dao.SportDAO;
import fr.erm.sae201.metier.persistence.*;
import fr.erm.sae201.metier.service.DPSMngt; // NOUVEAU : Import du service
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminEditDpsView;
import javafx.application.Platform;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminEditDpsController {

    private final AdminEditDpsView view;
    private final MainApp navigator;
    private final DPS dpsToEdit;
    private final SiteDAO siteDAO;
    private final SportDAO sportDAO;
    
    // NOUVEAU : Le contrôleur utilise le service DPSMngt
    private final DPSMngt dpsMngt;

    // SUPPRIMÉ : Le contrôleur n'a plus besoin du JourneeDAO
    // private final JourneeDAO journeeDAO;

    public AdminEditDpsController(AdminEditDpsView view, MainApp navigator, DPS dpsToEdit) {
        this.view = view;
        this.navigator = navigator;
        this.dpsToEdit = dpsToEdit;
        this.siteDAO = new SiteDAO();
        this.sportDAO = new SportDAO();
        
        // NOUVEAU : Instanciation du service
        this.dpsMngt = new DPSMngt();
        
        // SUPPRIMÉ : this.journeeDAO = new JourneeDAO();

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
        new Thread(() -> {
            List<Site> sites = siteDAO.findAll();
            List<Sport> sports = sportDAO.findAll();
            Platform.runLater(() -> {
                view.populateSiteComboBox(sites);
                view.populateSportComboBox(sports);
            });
        }).start();
    }

    /**
     * MODIFIÉ : Utilise le service DPSMngt pour la création et la mise à jour.
     */
    private void saveDps() {
        Site site = view.getSelectedSite();
        Sport sport = view.getSelectedSport();
        LocalDate date = view.getSelectedDate();

        if (site == null || sport == null || date == null || view.getStartHour().isEmpty() || view.getEndHour().isEmpty()) {
            NotificationUtils.showError("Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            int startHour = Integer.parseInt(view.getStartHour());
            int startMinute = view.getStartMinute().isEmpty() ? 0 : Integer.parseInt(view.getStartMinute());
            int endHour = Integer.parseInt(view.getEndHour());
            int endMinute = view.getEndMinute().isEmpty() ? 0 : Integer.parseInt(view.getEndMinute());
            
            int[] horaireDepart = { startHour, startMinute };
            int[] horaireFin = { endHour, endMinute };

            if (dpsToEdit != null) { // Mode mise à jour
                dpsToEdit.setSite(site);
                dpsToEdit.setSport(sport);
                // La date et les horaires ne sont pas modifiables ici
                if (dpsMngt.updateDps(dpsToEdit)) {
                    NotificationUtils.showSuccess("Succès", "Dispositif mis à jour.");
                    navigator.showAdminDispositifView(view.getCompte());
                } else {
                    NotificationUtils.showError("Erreur", "La mise à jour a échoué.");
                }
            } else { // Mode création
                DPS newDps = new DPS(0, horaireDepart, horaireFin, site, new Journee(date), sport);
                
                // Le contrôleur appelle la méthode de service unique
                if (dpsMngt.createDps(newDps) != -1) {
                    NotificationUtils.showSuccess("Succès", "Dispositif créé.");
                    navigator.showAdminDispositifView(view.getCompte());
                } else {
                    NotificationUtils.showError("Erreur", "La création a échoué.");
                }
            }
        } catch (NumberFormatException e) {
            NotificationUtils.showError("Format invalide", "Les heures et minutes doivent être des nombres.");
        } catch (IllegalArgumentException | SQLException e) {
            NotificationUtils.showError("Données invalides", e.getMessage());
        }
    }

    // Le reste des méthodes du contrôleur (addNewSite, addNewSport, cancel) ne change pas.
    private void addNewSite() {
        Optional<Site> result = view.showCreateSiteDialog();
        result.ifPresent(newSite -> {
            if (siteDAO.create(newSite) != -1) {
                NotificationUtils.showSuccess("Succès", "Site '" + newSite.getNom() + "' créé.");
                loadComboBoxData();
            } else {
                NotificationUtils.showError("Erreur", "Ce site (ou son code) existe déjà.");
            }
        });
    }

    private void addNewSport() {
        Optional<Sport> result = view.showCreateSportDialog();
        result.ifPresent(newSport -> {
            if (sportDAO.create(newSport) != -1) {
                NotificationUtils.showSuccess("Succès", "Sport '" + newSport.getNom() + "' créé.");
                loadComboBoxData();
            } else {
                NotificationUtils.showError("Erreur", "Ce sport (ou son code) existe déjà.");
            }
        });
    }

    private void cancel() {
        navigator.showAdminDispositifView(view.getCompte());
    }
}