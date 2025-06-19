package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminCompetencesController;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Vue pour la gestion des compétences par l'administrateur.
 * 
 * Affiche les compétences sous forme de cartes, permet leur ajout, modification
 * et suppression via des boîtes de dialogue interactives.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminCompetencesView extends BaseView {

    /** Conteneur principal qui affiche les cartes de compétence. */
    private FlowPane competencesContainer;
    /** Bouton pour ajouter une nouvelle compétence. */
    private Button addButton;
    private final AdminCompetencesController controller;

    /**
     * Constructeur de la vue de gestion des compétences.
     *
     * @param navigator Le navigateur principal.
     * @param compte    Le compte de l'utilisateur connecté.
     */
    public AdminCompetencesView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Compétences");
        this.controller = new AdminCompetencesController(this, navigator);
    }

    /**
     * Crée et retourne le contenu central de la vue.
     *
     * @return Le nœud racine du contenu.
     */
    @Override
    protected Node createCenterContent() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("admin-competences-container");

        HBox header = createHeader();

        competencesContainer = new FlowPane(20, 20);
        competencesContainer.setPadding(new Insets(10));
        
        ScrollPane scrollPane = new ScrollPane(competencesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("admin-scroll-pane");

        mainContainer.getChildren().addAll(header, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return mainContainer;
    }

    /**
     * Crée l'en-tête de la page avec le titre et le bouton d'ajout.
     *
     * @return Un HBox contenant l'en-tête.
     */
    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestion des Compétences");
        title.getStyleClass().add("admin-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        addButton = new Button("+");
        addButton.getStyleClass().add("add-button");
        headerBox.getChildren().addAll(title, spacer, addButton);
        return headerBox;
    }

    /**
     * Ajoute une carte visuelle représentant une compétence à l'interface.
     *
     * @param competence    La compétence à afficher.
     * @param deleteHandler Le gestionnaire d'événement pour l'action de suppression.
     * @param editHandler   Le gestionnaire d'événement pour l'action de modification.
     */
    public void addCompetenceCard(Competence competence, Consumer<Competence> deleteHandler, Consumer<Competence> editHandler) {
        VBox card = new VBox(10);
        card.getStyleClass().add("competence-card");
        card.setPrefWidth(250);
        card.setMinWidth(250);

        Label title = new Label(competence.getIntitule());
        title.getStyleClass().add("competence-card-title");
        
        Button editButton = new Button();
        SVGPath editIcon = new SVGPath();
        editIcon.setContent("M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125");
        editIcon.getStyleClass().add("edit-icon");
        editButton.setGraphic(editIcon);
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(e -> editHandler.accept(competence));

        Button deleteButton = new Button();
        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0");
        trashIcon.getStyleClass().add("delete-icon");
        deleteButton.setGraphic(trashIcon);
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteHandler.accept(competence));

        HBox titleBar = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().addAll(title, spacer, editButton, deleteButton);
        
        Label prereqLabel = new Label("Prérequis :");
        prereqLabel.getStyleClass().add("prerequisites-list-label");
        
        FlowPane prerequisitesPane = new FlowPane(5, 5);
        if (competence.getPrerequisites().isEmpty()) {
            Label noneLabel = new Label("Aucun");
            noneLabel.getStyleClass().add("prerequisite-none");
            prerequisitesPane.getChildren().add(noneLabel);
        } else {
            for (Competence prereq : competence.getPrerequisites()) {
                Label tag = new Label(prereq.getIntitule());
                tag.getStyleClass().add("prerequisite-tag");
                prerequisitesPane.getChildren().add(tag);
            }
        }
        
        card.getChildren().addAll(titleBar, prereqLabel, prerequisitesPane);
        competencesContainer.getChildren().add(card);
    }    
    
    /**
     * Affiche la boîte de dialogue pour l'ajout d'une compétence.
     * Permet de saisir un nom et de sélectionner des prérequis via une liste de cases à cocher.
     *
     * @param existingCompetences Toutes les compétences existantes à proposer comme prérequis.
     * @return Un Optional contenant une paire (nom, liste de prérequis) si l'utilisateur valide.
     */
    public Optional<Pair<String, List<Competence>>> showAddCompetenceDialog(List<Competence> existingCompetences) {
        Dialog<Pair<String, List<Competence>>> dialog = new Dialog<>();
        dialog.setTitle("Créer une nouvelle Compétence");
        dialog.setHeaderText("Entrez le nom de la nouvelle compétence et cochez ses prérequis.");
        
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField intituleField = new TextField();
        intituleField.setPromptText("Ex: PSE2");
        
        ListView<Competence> prerequisitesListView = new ListView<>();
        prerequisitesListView.setItems(FXCollections.observableArrayList(existingCompetences));
        prerequisitesListView.setPrefHeight(200);

        Set<Competence> selectedPrerequisites = new HashSet<>();

        prerequisitesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Competence item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(item.getIntitule());
                    checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            selectedPrerequisites.add(item);
                        } else {
                            selectedPrerequisites.remove(item);
                        }
                    });
                    setGraphic(checkBox);
                }
            }
        });

        grid.add(new Label("Nom de la compétence:"), 0, 0);
        grid.add(intituleField, 1, 0);
        grid.add(new Label("Prérequis (optionnel):"), 0, 1);
        grid.add(prerequisitesListView, 1, 1);
        
        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        intituleField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String newIntitule = intituleField.getText();
                return new Pair<>(newIntitule, new ArrayList<>(selectedPrerequisites));
            }
            return null;
        });

        return dialog.showAndWait();
    }
    
    /**
     * Affiche la boîte de dialogue pour la modification des prérequis d'une compétence.
     *
     * @param competenceToEdit La compétence à modifier.
     * @param allCompetences   Toutes les compétences disponibles pour le choix des prérequis.
     * @return Un Optional contenant la nouvelle liste de prérequis si l'utilisateur valide.
     */
    public Optional<List<Competence>> showEditCompetenceDialog(Competence competenceToEdit, List<Competence> allCompetences) {
        Dialog<List<Competence>> dialog = new Dialog<>();
        dialog.setTitle("Modifier les Prérequis");
        dialog.setHeaderText("Modifier les prérequis pour : " + competenceToEdit.getIntitule());
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        List<Competence> availablePrerequisites = allCompetences.stream()
                .filter(c -> !c.equals(competenceToEdit))
                .toList();

        ListView<Competence> prerequisitesListView = new ListView<>();
        prerequisitesListView.setItems(FXCollections.observableArrayList(availablePrerequisites));
        prerequisitesListView.setPrefHeight(240);
        
        Set<Competence> selectedPrerequisites = new HashSet<>(competenceToEdit.getPrerequisites());

        prerequisitesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Competence item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(item.getIntitule());
                    checkBox.setSelected(selectedPrerequisites.contains(item));
                    
                    checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            selectedPrerequisites.add(item);
                        } else {
                            selectedPrerequisites.remove(item);
                        }
                    });
                    setGraphic(checkBox);
                }
            }
        });

        dialog.getDialogPane().setContent(prerequisitesListView);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new ArrayList<>(selectedPrerequisites);
            }
            return null;
        });

        return dialog.showAndWait();
    }
    
    /**
     * Efface toutes les cartes de compétence de l'affichage.
     */
    public void clearCompetencesList() {
        competencesContainer.getChildren().clear();
    }

    /**
     * Affiche un message lorsque la liste des compétences est vide.
     *
     * @param message Le message à afficher.
     */
    public void showEmptyMessage(String message) {
        Label emptyLabel = new Label(message);
        emptyLabel.getStyleClass().add("empty-list-label");
        competencesContainer.getChildren().add(emptyLabel);
    }
    
    /**
     * Définit l'action à exécuter lorsque le bouton d'ajout est cliqué.
     *
     * @param eventHandler Le gestionnaire d'événement pour l'action du bouton.
     */
    public void setAddButtonAction(EventHandler<ActionEvent> eventHandler) {
        if (addButton != null) {
            addButton.setOnAction(eventHandler);
        }
    }
}