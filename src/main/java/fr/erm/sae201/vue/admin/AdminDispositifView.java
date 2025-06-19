package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminDispositifController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Vue pour la gestion des Dispositifs Prévisionnels de Secours (DPS) par l'administrateur.
 * <p>
 * Affiche la liste des DPS sous forme de cartes cliquables et fournit des
 * boutons pour ajouter un nouveau dispositif ou exporter la liste en CSV.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminDispositifView extends BaseView {

    /** Conteneur vertical pour la liste des cartes de DPS. */
    private VBox dpsListContainer;
    /** Bouton pour ajouter un nouveau DPS. */
    private Button addButton;
    /** Bouton pour exporter la liste des DPS au format CSV. */
    private Button exportButton;
    private final CompteUtilisateur compte;
    private final AdminDispositifController controller;

    /**
     * Constructeur de la vue de gestion des dispositifs.
     *
     * @param navigator Le navigateur principal.
     * @param compte    Le compte de l'utilisateur connecté.
     */
    public AdminDispositifView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Dispositifs");
        this.compte = compte;
        this.controller = new AdminDispositifController(this, navigator);
    }

    /**
     * Retourne le compte de l'utilisateur connecté.
     * @return Le compte utilisateur.
     */
    public CompteUtilisateur getCompte() {
        return this.compte;
    }

    /**
     * Crée le contenu central de la vue, composé d'un en-tête et d'une liste défilante de DPS.
     *
     * @return Le nœud racine du contenu.
     */
    @Override
    protected Node createCenterContent() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("admin-content-container");

        HBox header = createHeader();
        ScrollPane scrollPane = new ScrollPane();
        dpsListContainer = new VBox(15);
        dpsListContainer.getStyleClass().add("dps-list-container");

        scrollPane.setContent(dpsListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("admin-scroll-pane");

        mainContainer.getChildren().addAll(header, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return mainContainer;
    }

    /**
     * Crée l'en-tête de la page avec le titre et les boutons d'action.
     *
     * @return Un HBox contenant l'en-tête.
     */
    private HBox createHeader() {
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestion des Dispositifs");
        title.getStyleClass().add("admin-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        exportButton = new Button("Exporter (CSV)");
        exportButton.getStyleClass().add("cancel-button");
        
        addButton = new Button("+");
        addButton.getStyleClass().add("add-button");
        
        headerBox.getChildren().addAll(title, spacer, exportButton, addButton);
        return headerBox;
    }

    /**
     * Ajoute une carte visuelle représentant un DPS à la liste.
     *
     * @param dps Le DPS à afficher.
     */
    public void addDpsCard(DPS dps) {
        HBox cardContainer = new HBox(10);
        cardContainer.getStyleClass().add("dps-card");
        cardContainer.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH);
        Label sportLabel = new Label(dps.getSport().getNom());
        sportLabel.getStyleClass().add("dps-card-title");
        String dateText = dps.getJournee().getDate().format(dateFormatter);
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.substring(1);
        Label dateLabel = new Label(dateText);
        dateLabel.getStyleClass().add("dps-card-subtitle");
        Label siteLabel = new Label("Lieu : " + dps.getSite().getNom());
        siteLabel.getStyleClass().add("dps-card-info");
        int startHour = dps.getHoraireDepart()[0];
        int startMinute = dps.getHoraireDepart()[1];
        int endHour = dps.getHoraireFin()[0];
        int endMinute = dps.getHoraireFin()[1];
        String timeText = String.format("Horaires : %02d:%02d - %02d:%02d", startHour, startMinute, endHour, endMinute);
        Label timeLabel = new Label(timeText);
        timeLabel.getStyleClass().add("dps-card-info");
        infoBox.getChildren().addAll(sportLabel, dateLabel, siteLabel, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editButton = new Button();
        SVGPath pencilIcon = new SVGPath();
        pencilIcon.setContent("M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125");
        pencilIcon.getStyleClass().add("edit-icon");
        editButton.setGraphic(pencilIcon);
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(e -> controller.handleEditDps(dps));

        Button deleteButton = new Button();
        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0");
        trashIcon.getStyleClass().add("delete-icon");

        deleteButton.setGraphic(trashIcon);
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> controller.handleDeleteDps(dps));

        HBox actionButtons = new HBox(10, editButton, deleteButton);
        actionButtons.setAlignment(Pos.CENTER);

        cardContainer.getChildren().addAll(infoBox, spacer, actionButtons);
        dpsListContainer.getChildren().add(cardContainer);
    }

    /**
     * Efface la liste des cartes de DPS actuellement affichées.
     */
    public void clearDpsList() {
        dpsListContainer.getChildren().clear();
    }

    /**
     * Affiche un message lorsque la liste des DPS est vide.
     *
     * @param message Le message à afficher.
     */
    public void showEmptyMessage(String message) {
        Label emptyLabel = new Label(message);
        emptyLabel.getStyleClass().add("empty-list-label");
        dpsListContainer.getChildren().add(emptyLabel);
    }

    /**
     * Définit l'action du bouton d'ajout de DPS.
     *
     * @param eventHandler Le gestionnaire d'événement.
     */
    public void setAddButtonAction(EventHandler<ActionEvent> eventHandler) {
        if (addButton != null) {
            addButton.setOnAction(eventHandler);
        }
    }
    
    /**
     * Définit l'action du bouton d'exportation CSV.
     *
     * @param eventHandler Le gestionnaire d'événement.
     */
    public void setExportButtonAction(EventHandler<ActionEvent> eventHandler) {
        if (exportButton != null) {
            exportButton.setOnAction(eventHandler);
        }
    }
}