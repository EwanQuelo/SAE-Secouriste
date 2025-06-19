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

/**
 * Contrôleur pour la vue qui affiche et gère la liste des secouristes.
 * 
 * Cette classe gère la logique de la pagination, de la recherche, ainsi que
 * les actions de suppression et de modification d'un secouriste. Elle interagit
 * avec le service SecouristeMngt pour récupérer les données et les met à jour
 * dans la vue correspondante.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminUtilisateursController {

    /** La vue gérée par ce contrôleur. */
    private final AdminUtilisateursView view;

    /** Le service métier pour la gestion des secouristes. */
    private final SecouristeMngt secouristeMngt;

    /** Le navigateur principal de l'application pour changer de vue. */
    private final MainApp navigator;

    /** La page actuellement affichée dans la liste des utilisateurs. */
    private int currentPage = 1;

    /** Le nombre de secouristes à afficher par page. */
    private final int itemsPerPage = 8;

    /** Le nombre total de pages, calculé en fonction du nombre total d'éléments. */
    private int totalPages;

    /** La requête de recherche actuellement appliquée par l'utilisateur. */
    private String currentSearchQuery = "";

    /**
     * Constructeur du contrôleur de la liste des utilisateurs.
     * Initialise les dépendances, lie les écouteurs d'événements (recherche)
     * et les actions des boutons, puis affiche la première page de résultats.
     *
     * @param view      La vue à contrôler.
     * @param navigator Le navigateur principal de l'application.
     */
    public AdminUtilisateursController(AdminUtilisateursView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.secouristeMngt = new SecouristeMngt();

        this.view.getSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
        });

        this.view.setAddUserButtonAction(e -> navigator.showAdminCreateUserView(view.getCompte()));

        displayPage(1);
    }

    /**
     * Met à jour la requête de recherche et réinitialise l'affichage à la première page
     * pour refléter les nouveaux résultats.
     *
     * @param query La nouvelle chaîne de caractères à rechercher.
     */
    private void search(String query) {
        currentSearchQuery = query;
        displayPage(1);
    }

    /**
     * Calcule le nombre total de pages, récupère la liste des secouristes pour
     * la page demandée, et met à jour la vue avec les nouvelles données et
     * les informations de pagination.
     *
     * @param pageNumber Le numéro de la page à afficher.
     */
    public void displayPage(int pageNumber) {
        if (pageNumber < 1) pageNumber = 1;

        int totalItems = secouristeMngt.getTotalSecouristesCount(currentSearchQuery);
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        if (this.totalPages == 0) this.totalPages = 1;

        if (pageNumber > totalPages) pageNumber = totalPages;
        this.currentPage = pageNumber;

        view.setUserCount(totalItems);

        // Assure que la mise à jour de l'interface graphique est effectuée sur le thread de l'application JavaFX.
        Platform.runLater(() -> {
            List<Secouriste> secouristes = secouristeMngt.getSecouristesByPage(currentSearchQuery, currentPage, itemsPerPage);
            view.displayUsers(secouristes);
            view.updatePagination(currentPage, totalPages);
        });
    }

    /**
     * Affiche la page suivante si elle existe.
     */
    public void nextPage() {
        if (currentPage < totalPages) {
            displayPage(currentPage + 1);
        }
    }

    /**
     * Affiche la page précédente si elle existe.
     */
    public void previousPage() {
        if (currentPage > 1) {
            displayPage(currentPage - 1);
        }
    }

    /**
     * Gère la suppression d'un secouriste.
     * Affiche une boîte de dialogue de confirmation avant d'appeler le service
     * pour supprimer l'utilisateur et son compte associé.
     *
     * @param secouriste Le secouriste à supprimer.
     */
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
     * Lance la navigation vers la vue d'édition pour un secouriste donné.
     *
     * @param secouriste Le secouriste à modifier.
     */
    public void editSecouriste(Secouriste secouriste) {
        if (secouriste != null) {
            navigator.showAdminEditUserView(view.getCompte(), secouriste);
        }
    }
}