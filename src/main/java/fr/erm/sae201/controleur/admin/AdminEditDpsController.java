package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.dao.SiteDAO;
import fr.erm.sae201.dao.SportDAO;
import fr.erm.sae201.metier.persistence.*;
import fr.erm.sae201.metier.service.DPSMngt;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminEditDpsView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur pour la vue de création et d'édition d'un Dispositif Prévisionnel de Secours (DPS).
 * <p>
 * Cette classe gère deux modes : la création d'un nouveau DPS ou la modification
 * d'un DPS existant. Elle est responsable de charger les données nécessaires
 * (sites, sports, compétences), de pré-remplir le formulaire en mode édition,
 * et de traiter la sauvegarde des données en interaction avec le service DPSMngt.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminEditDpsController {

    /** La vue d'édition/création de DPS. */
    private final AdminEditDpsView view;

    /** Le navigateur principal de l'application. */
    private final MainApp navigator;

    /** Le DPS à éditer. Est null en mode création. */
    private final DPS dpsToEdit;

    /** Le DAO pour accéder aux données des sites. */
    private final SiteDAO siteDAO;

    /** Le DAO pour accéder aux données des sports. */
    private final SportDAO sportDAO;

    /** Le DAO pour accéder aux données des compétences. */
    private final CompetenceDAO competenceDAO;

    /** Le service métier pour la gestion de la logique des DPS. */
    private final DPSMngt dpsMngt;

    /**
     * Constructeur du contrôleur.
     *
     * @param view      La vue à contrôler.
     * @param navigator Le navigateur principal.
     * @param dpsToEdit Le DPS à éditer. Si null, le contrôleur passe en mode création.
     */
    public AdminEditDpsController(AdminEditDpsView view, MainApp navigator, DPS dpsToEdit) {
        this.view = view;
        this.navigator = navigator;
        this.dpsToEdit = dpsToEdit;
        this.siteDAO = new SiteDAO();
        this.sportDAO = new SportDAO();
        this.competenceDAO = new CompetenceDAO();
        this.dpsMngt = new DPSMngt();

        view.setSaveButtonAction(e -> saveDps());
        view.setCancelButtonAction(e -> cancel());
        view.setAddSiteAction(e -> addNewSite());
        view.setAddSportAction(e -> addNewSport());

        loadInitialData();
        initializeForm();
    }

    /**
     * Initialise le formulaire en définissant le titre et en pré-remplissant
     * les champs si le contrôleur est en mode édition.
     */
    private void initializeForm() {
        if (dpsToEdit != null) {
            view.setFormTitle("Modifier le Dispositif");
            view.setDpsData(dpsToEdit);
        } else {
            view.setFormTitle("Créer un nouveau Dispositif");
        }
    }

    /**
     * Charge les données initiales (sites, sports, compétences) de manière asynchrone
     * et peuple les contrôles de la vue une fois les données récupérées.
     */
    private void loadInitialData() {
        new Thread(() -> {
            List<Site> sites = siteDAO.findAll();
            List<Sport> sports = sportDAO.findAll();
            List<Competence> allCompetences = competenceDAO.findAll();

            final Map<Competence, Integer> requirementsToLoad;
            if (dpsToEdit != null) {
                requirementsToLoad = dpsMngt.getDps(dpsToEdit.getId()).getCompetencesRequises();
            } else {
                requirementsToLoad = Collections.emptyMap();
            }

            Platform.runLater(() -> {
                view.populateSiteComboBox(sites);
                view.populateSportComboBox(sports);
                view.populateRequirements(allCompetences, requirementsToLoad);
            });
        }).start();
    }

    /**
     * Gère la sauvegarde du DPS (création ou mise à jour).
     * Valide les entrées, gère la confirmation de l'utilisateur si des modifications
     * critiques sont apportées, et délègue l'opération à DPSMngt.
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

            Map<Competence, Integer> requirements = view.getCompetenceRequirements();

            if (dpsToEdit != null) { // Mode mise à jour
                int[] newHoraireDepart = {startHour, startMinute};
                int[] newHoraireFin = {endHour, endMinute};
                boolean dateOrTimeChanged = !dpsToEdit.getJournee().getDate().equals(date) ||
                                            !Arrays.equals(dpsToEdit.getHoraireDepart(), newHoraireDepart) ||
                                            !Arrays.equals(dpsToEdit.getHoraireFin(), newHoraireFin);

                // Si la date ou les horaires changent, les affectations existantes deviennent invalides.
                // Il faut avertir l'utilisateur et obtenir sa confirmation avant de les supprimer.
                if (dateOrTimeChanged) {
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Confirmation de Modification");
                    confirmation.setHeaderText("Les informations critiques du DPS ont changé.");
                    confirmation.setContentText("Modifier la date ou les horaires entraînera la suppression de toutes les affectations actuelles pour ce dispositif.\n\nVoulez-vous continuer ?");
                    confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                    Optional<ButtonType> result = confirmation.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        dpsMngt.deleteAllAffectationsForDps(dpsToEdit.getId());
                    } else {
                        return; // L'utilisateur a annulé, on interrompt la sauvegarde.
                    }
                }

                dpsToEdit.setSite(site);
                dpsToEdit.setSport(sport);
                dpsToEdit.setJournee(new Journee(date));
                dpsToEdit.setHoraireDepart(newHoraireDepart);
                dpsToEdit.setHoraireFin(newHoraireFin);

                if (dpsMngt.updateDps(dpsToEdit)) {
                    updateDpsRequirements(dpsToEdit.getId(), requirements);
                    NotificationUtils.showSuccess("Succès", "Dispositif mis à jour.");
                    navigator.showAdminDispositifView(view.getCompte());
                } else {
                    NotificationUtils.showError("Erreur", "La mise à jour a échoué.");
                }

            } else { // Mode création
                int[] horaireDepart = {startHour, startMinute};
                int[] horaireFin = {endHour, endMinute};
                DPS newDps = new DPS(0, horaireDepart, horaireFin, site, new Journee(date), sport);

                long newDpsId = dpsMngt.createDps(newDps);
                if (newDpsId != -1) {
                    updateDpsRequirements(newDpsId, requirements);
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

    /**
     * Met à jour les compétences requises pour un DPS donné.
     * Ajoute ou met à jour les exigences si le nombre est supérieur à zéro,
     * sinon supprime l'exigence.
     *
     * @param dpsId        L'ID du DPS à mettre à jour.
     * @param requirements La carte des compétences et du nombre requis.
     */
    private void updateDpsRequirements(long dpsId, Map<Competence, Integer> requirements) {
        for (Map.Entry<Competence, Integer> entry : requirements.entrySet()) {
            String intitule = entry.getKey().getIntitule();
            int nombre = entry.getValue();
            if (nombre > 0) {
                dpsMngt.setRequiredCompetence(dpsId, intitule, nombre);
            } else {
                dpsMngt.removeRequiredCompetence(dpsId, intitule);
            }
        }
    }

    /**
     * Gère l'ajout rapide d'un nouveau site depuis le formulaire du DPS.
     * Ouvre une boîte de dialogue et, en cas de succès, recharge les données pour
     * rendre le nouveau site immédiatement disponible.
     */
    private void addNewSite() {
        Optional<Site> result = view.showCreateSiteDialog();
        result.ifPresent(newSite -> {
            if (siteDAO.create(newSite) != -1) {
                NotificationUtils.showSuccess("Succès", "Site '" + newSite.getNom() + "' créé.");
                loadInitialData();
            } else {
                NotificationUtils.showError("Erreur", "Ce site (ou son code) existe déjà.");
            }
        });
    }

    /**
     * Gère l'ajout rapide d'un nouveau sport depuis le formulaire du DPS.
     * Ouvre une boîte de dialogue et, en cas de succès, recharge les données pour
     * rendre le nouveau sport immédiatement disponible.
     */
    private void addNewSport() {
        Optional<Sport> result = view.showCreateSportDialog();
        result.ifPresent(newSport -> {
            if (sportDAO.create(newSport) != -1) {
                NotificationUtils.showSuccess("Succès", "Sport '" + newSport.getNom() + "' créé.");
                loadInitialData();
            } else {
                NotificationUtils.showError("Erreur", "Ce sport (ou son code) existe déjà.");
            }
        });
    }

    /**
     * Annule l'opération en cours et retourne à la liste des dispositifs.
     */
    private void cancel() {
        navigator.showAdminDispositifView(view.getCompte());
    }
}