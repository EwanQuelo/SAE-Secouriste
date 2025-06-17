package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminDispositifView;
import javafx.application.Platform;
import java.util.List;

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

    /**
     * Récupère les DPS depuis le DAO et demande à la vue de les afficher.
     * Utilise Platform.runLater pour s'assurer que l'interface est prête.
     */
    public void loadDispositifs() {
        Platform.runLater(() -> {
            List<DPS> allDps = dpsDAO.findAll();
            view.clearDpsList();

            if (allDps == null || allDps.isEmpty()) {
                view.showEmptyMessage("Aucun dispositif n'a été créé pour le moment.");
            } else {
                for (DPS dps : allDps) {
                    view.addDpsCard(dps);
                }
            }
        });
    }

    /**
     * Gère l'action du clic sur le bouton "+" pour créer un nouveau DPS.
     */
    private void handleAddDps() {
        navigator.showCreateDpsView(view.getCompte());
    }
}