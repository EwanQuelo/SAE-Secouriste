package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.service.SecouristeMngt;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminUtilisateursView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

public class AdminUtilisateursController {

    private final AdminUtilisateursView view;
    private final SecouristeMngt secouristeMngt;
    private final MainApp navigator; // AJOUT : Pour la navigation
    
    private int currentPage = 1;
    private final int itemsPerPage = 8;
    private int totalPages;
    private String currentSearchQuery = "";

    // MODIFICATION : Le constructeur accepte le navigator
    public AdminUtilisateursController(AdminUtilisateursView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator; // AJOUT
        this.secouristeMngt = new SecouristeMngt();
        
        this.view.getSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
        });

        displayPage(1);
    }

    private void search(String query) {
        currentSearchQuery = query;
        displayPage(1);
    }

    public void displayPage(int pageNumber) {
        if (pageNumber < 1) pageNumber = 1;
        
        int totalItems = secouristeMngt.getTotalSecouristesCount(currentSearchQuery);
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        if (this.totalPages == 0) this.totalPages = 1;
        
        if (pageNumber > totalPages) pageNumber = totalPages;
        this.currentPage = pageNumber;
        
        view.setUserCount(totalItems);
        
        Platform.runLater(() -> {
            List<Secouriste> secouristes = secouristeMngt.getSecouristesByPage(currentSearchQuery, currentPage, itemsPerPage);
            view.displayUsers(secouristes);
            view.updatePagination(currentPage, totalPages);
        });
    }
    
    public void nextPage() {
        if (currentPage < totalPages) {
            displayPage(currentPage + 1);
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            displayPage(currentPage - 1);
        }
    }

    public void deleteSecouriste(Secouriste secouriste) {
        if (secouriste == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer " + secouriste.getPrenom() + " " + secouriste.getNom() + " ?");
        alert.setContentText("Cette action est irréversible et supprimera également le compte utilisateur associé.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = secouristeMngt.deleteSecouriste(secouriste.getId());
            if (success) {
                NotificationUtils.showSuccess("Suppression réussie", "Le secouriste a été supprimé.");
                displayPage(currentPage);
            } else {
                NotificationUtils.showError("Échec de la suppression", "Une erreur est survenue.");
            }
        }
    }
    
    /**
     * AJOUT : Lance la navigation vers la vue d'édition pour un secouriste donné.
     * @param secouriste Le secouriste à modifier.
     */
    public void editSecouriste(Secouriste secouriste) {
        if (secouriste != null) {
            navigator.showAdminEditUserView(view.getCompte(), secouriste);
        }
    }
}