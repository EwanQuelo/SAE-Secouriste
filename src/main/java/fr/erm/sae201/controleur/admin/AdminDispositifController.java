package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminDispositifView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

import fr.erm.sae201.utils.NotificationUtils;

public class AdminDispositifController {

    private final AdminDispositifView view;
    private final MainApp navigator;
    private final DPSDAO dpsDAO;

    public AdminDispositifController(AdminDispositifView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsDAO = new DPSDAO();

        // Lie le clic du bouton "+" à la méthode handleAddDps
        this.view.setAddButtonAction(event -> handleAddDps());

        // Charge la liste des dispositifs au démarrage
        loadDispositifs();
    }

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
        // On active la navigation vers la vue d'édition
        navigator.showEditDpsView(view.getCompte(), dps);
    }
    

    public void handleDeleteDps(DPS dps) {
        // 1. Demander confirmation à l'utilisateur
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le dispositif : " + dps.getSport().getNom() + " ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();

        // 2. Si l'utilisateur clique sur "OK"
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 3. Appeler le DAO pour supprimer
            int deleteResult = dpsDAO.delete(dps);

            if (deleteResult > 0) {
                NotificationUtils.showSuccess("Suppression réussie", "Le dispositif a été supprimé.");
                // 4. Rafraîchir la liste
                loadDispositifs();
            } else {
                NotificationUtils.showError("Erreur", "La suppression du dispositif a échoué.");
            }
        }
    }

    
    private void handleAddDps() {
    System.out.println("Action : L'administrateur veut ajouter un nouveau DPS.");
    // On active la navigation vers la vue de création
    navigator.showCreateDpsView(view.getCompte());
}
}