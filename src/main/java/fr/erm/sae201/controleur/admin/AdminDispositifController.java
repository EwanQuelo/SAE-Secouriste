// Dans le fichier src/main/java/fr/erm/sae201/controleur/admin/AdminDispositifController.java

package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.ExportService; // NOUVEL IMPORT
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminDispositifView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser; // NOUVEL IMPORT

import java.io.File; // NOUVEL IMPORT
import java.io.IOException; // NOUVEL IMPORT
import java.io.PrintWriter; // NOUVEL IMPORT
import java.util.List;
import java.util.Optional;

public class AdminDispositifController {

    private final AdminDispositifView view;
    private final MainApp navigator;
    private final DPSDAO dpsDAO;
    private final ExportService exportService; // NOUVEAU

    public AdminDispositifController(AdminDispositifView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsDAO = new DPSDAO();
        this.exportService = new ExportService(); // NOUVEAU

        this.view.setAddButtonAction(event -> handleAddDps());
        this.view.setExportButtonAction(event -> handleExportToCsv()); // NOUVEAU

        loadDispositifs();
    }
    
    // NOUVELLE MÉTHODE pour gérer l'exportation
    private void handleExportToCsv() {
        String csvData = exportService.exportDpsToCsvString();

        // Vérifie s'il y a des données à exporter (au-delà de l'en-tête)
        if (csvData.split("\n").length <= 1) {
            NotificationUtils.showWarning("Exportation annulée", "Il n'y a aucun dispositif à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer l'export CSV");
        fileChooser.setInitialFileName("export_dispositifs.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));

        // Affiche la boîte de dialogue de sauvegarde
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

    // Les autres méthodes du contrôleur restent inchangées
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

    public void handleEditDps(DPS dps) {
        System.out.println("Action : L'administrateur veut modifier le DPS ID: " + dps.getId());
        navigator.showEditDpsView(view.getCompte(), dps);
    }
    
    public void handleDeleteDps(DPS dps) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le dispositif : " + dps.getSport().getNom() + " ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int deleteResult = dpsDAO.delete(dps);
            if (deleteResult > 0) {
                NotificationUtils.showSuccess("Suppression réussie", "Le dispositif a été supprimé.");
                loadDispositifs();
            } else {
                NotificationUtils.showError("Erreur", "La suppression du dispositif a échoué.");
            }
        }
    }
    
    private void handleAddDps() {
        System.out.println("Action : L'administrateur veut ajouter un nouveau DPS.");
        navigator.showCreateDpsView(view.getCompte());
    }
}