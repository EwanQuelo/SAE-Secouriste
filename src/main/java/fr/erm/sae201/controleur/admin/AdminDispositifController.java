package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.ExportService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminDispositifView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour l'interface de gestion des Dispositifs Prévisionnels de Secours (DPS).
 * 
 * Cette classe gère les opérations CRUD (Création, Lecture, Mise à jour, Suppression)
 * pour les DPS ainsi que l'exportation de la liste des dispositifs au format CSV.
 * Elle fait le lien entre la vue et les services/DAO correspondants.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminDispositifController {

    /** La vue gérée par ce contrôleur. */
    private final AdminDispositifView view;

    /** Le navigateur principal de l'application pour changer de vue. */
    private final MainApp navigator;

    /** Le DAO pour l'accès aux données des DPS. */
    private final DPSDAO dpsDAO;

    /** Le service pour gérer l'exportation des données en format CSV. */
    private final ExportService exportService;

    /**
     * Constructeur du contrôleur des dispositifs.
     * Initialise les dépendances et lie les actions des boutons (ajout, export)
     * de la vue aux méthodes de ce contrôleur.
     *
     * @param view La vue associée à ce contrôleur.
     * @param navigator Le navigateur principal de l'application.
     */
    public AdminDispositifController(AdminDispositifView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsDAO = new DPSDAO();
        this.exportService = new ExportService();

        this.view.setAddButtonAction(event -> handleAddDps());
        this.view.setExportButtonAction(event -> handleExportToCsv());

        loadDispositifs();
    }

    /**
     * Gère l'exportation de la liste de tous les DPS au format CSV.
     * Récupère les données formatées depuis le service d'export, puis ouvre une
     * boîte de dialogue permettant à l'utilisateur de choisir l'emplacement de sauvegarde du fichier.
     */
    private void handleExportToCsv() {
        String csvData = exportService.exportDpsToCsvString();

        // Vérifie si des données existent au-delà de la ligne d'en-tête.
        if (csvData.split("\n").length <= 1) {
            NotificationUtils.showWarning("Exportation annulée", "Il n'y a aucun dispositif à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer l'export CSV");
        fileChooser.setInitialFileName("export_dispositifs.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));

        File file = fileChooser.showSaveDialog(view.getView().getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(csvData);
                NotificationUtils.showSuccess("Exportation réussie", "Le fichier a été sauvegardé.");
            } catch (IOException e) {
                NotificationUtils.showError("Erreur d'exportation", "Impossible d'écrire dans le fichier.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Charge la liste complète des DPS et met à jour la vue.
     * L'opération est effectuée via Platform.runLater pour s'assurer que les modifications
     * de l'interface graphique se font sur le thread approprié de JavaFX.
     */
    public void loadDispositifs() {
        Platform.runLater(() -> {
            List<DPS> allDps = dpsDAO.findAll();
            view.clearDpsList();

            if (allDps == null || allDps.isEmpty()) {
                view.showEmptyMessage("Aucun dispositif trouvé.");
            } else {
                for (DPS dps : allDps) {
                    view.addDpsCard(dps);
                }
            }
        });
    }

    /**
     * Gère l'action de modification d'un DPS.
     * Ouvre la vue d'édition pour le dispositif sélectionné.
     *
     * @param dps Le DPS à modifier.
     */
    public void handleEditDps(DPS dps) {
        navigator.showEditDpsView(view.getCompte(), dps);
    }

    /**
     * Gère la suppression d'un DPS.
     * Affiche une boîte de dialogue de confirmation avant d'effectuer la suppression via le DAO.
     *
     * @param dps Le DPS à supprimer.
     */
    public void handleDeleteDps(DPS dps) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le dispositif : " + dps.getSport().getNom() + " ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (dpsDAO.delete(dps) > 0) {
                NotificationUtils.showSuccess("Suppression réussie", "Le dispositif a été supprimé.");
                loadDispositifs();
            } else {
                NotificationUtils.showError("Erreur", "La suppression du dispositif a échoué.");
            }
        }
    }

    /**
     * Gère l'action d'ajout d'un nouveau DPS.
     * Ouvre la vue de création de dispositif.
     */
    private void handleAddDps() {
        navigator.showCreateDpsView(view.getCompte());
    }
}