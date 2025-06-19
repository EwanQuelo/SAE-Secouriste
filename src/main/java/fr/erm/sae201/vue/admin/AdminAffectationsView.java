package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminAffectationsController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;

import fr.erm.sae201.metier.service.ModelesAlgorithme.AffectationResultat;

import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AdminAffectationsView extends BaseView {

    private ListView<DPS> dpsListView;
    private VBox rightPanel;
    private Label dpsDetailsLabel;
    private VBox propositionContainer;
    private Button runExhaustiveButton, runGloutonButton, saveChangesButton;
    private ImageView loadingGifView;

    public AdminAffectationsView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Affectations");
        new AdminAffectationsController(this, navigator);
    }

    @Override
    protected Node createCenterContent() {
        // --- CONTENEUR PRINCIPAL ---
        SplitPane splitPane = new SplitPane();
        // NOUVEAU : On utilise une classe spécifique pour cette vue
        splitPane.getStyleClass().add("affectations-view-container");

        // --- PANNEAU DE GAUCHE ---
        VBox leftPanel = new VBox(10);
        leftPanel.getStyleClass().add("affectations-left-panel");
        // Le padding est maintenant géré par le CSS, mais on peut le garder pour la
        // structure
        leftPanel.setPadding(new Insets(10));
        leftPanel.getChildren().add(new Label("Choisir un dispositif :"));

        dpsListView = new ListView<>();
        // Pas besoin de classe de style ici, elle est ciblée par son parent

        dpsListView.setCellFactory(param -> new ListCell<>() {

            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);

            @Override
            protected void updateItem(DPS item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Utilisation du formateur pour afficher la date.
                    String formattedDate = item.getJournee().getDate().format(formatter);
                    setText(item.getSport().getNom() + " - " + formattedDate);
                }
            }
        });
        leftPanel.getChildren().add(dpsListView);

        // --- PANNEAU DE DROITE ---
        rightPanel = new VBox(20);
        rightPanel.getStyleClass().add("affectations-right-panel");
        rightPanel.setPadding(new Insets(20));
        rightPanel.setDisable(true);

        dpsDetailsLabel = new Label("Veuillez sélectionner un dispositif pour commencer.");
        // Ajout d'une classe pour le texte placeholder
        dpsDetailsLabel.getStyleClass().addAll("admin-subtitle", "placeholder-text");

        // --- BOUTONS D'ALGORITHMES ---
        runExhaustiveButton = new Button("Approche Exhaustive");
        // NOUVEAU : Classes de style pour les boutons
        runExhaustiveButton.getStyleClass().addAll("algo-button", "exhaustive-button");

        runGloutonButton = new Button("Approche Gloutonne");
        runGloutonButton.getStyleClass().addAll("algo-button", "glouton-button");

        HBox algoButtons = new HBox(20, runExhaustiveButton, runGloutonButton);
        algoButtons.setAlignment(Pos.CENTER);

        // --- CONTENEUR DE LA PROPOSITION ---
        propositionContainer = new VBox(5);
        propositionContainer.getStyleClass().add("affectations-proposition-container");

        // NOUVEAU : ScrollPane avec sa classe de style
        ScrollPane propositionScrollPane = new ScrollPane(propositionContainer);
        propositionScrollPane.getStyleClass().add("affectations-proposition-scroll-pane");
        propositionScrollPane.setFitToWidth(true);
        // La hauteur est maintenant gérée par le CSS, mais on peut garder une
        // prefHeight
        propositionScrollPane.setPrefHeight(250);

        // --- BOUTON DE SAUVEGARDE ---
        saveChangesButton = new Button("Valider et Enregistrer");
        saveChangesButton.getStyleClass().add("save-button");
        HBox saveBox = new HBox(saveChangesButton);
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        rightPanel.getChildren().addAll(dpsDetailsLabel, algoButtons, new Separator(),
                new Label("Proposition d'affectation :"), propositionScrollPane, saveBox);

        // --- GESTION DU CHARGEMENT ---
        StackPane rightStack = new StackPane(rightPanel);

        // Crée une instance de l'image à partir de votre fichier GIF
        // Assurez-vous que le chemin est correct.
        Image loadingImage = new Image(getClass().getResourceAsStream("/images/loading1.gif"));

        // Crée un ImageView pour afficher l'image
        loadingGifView = new ImageView(loadingImage);

        // Optionnel : redimensionner le GIF s'il est trop grand
        loadingGifView.setFitWidth(80);
        loadingGifView.setFitHeight(80);
        loadingGifView.setPreserveRatio(true);

        loadingGifView.setVisible(false); // Caché par défaut

        // Ajoute le GIF au StackPane, il sera centré par défaut.
        rightStack.getChildren().add(loadingGifView);

        splitPane.getItems().addAll(leftPanel, rightStack);
        splitPane.setDividerPositions(0.35);

        return splitPane;
    }

    // --- Méthodes pour le Contrôleur ---

    public void populateDpsList(List<DPS> dpsList) {
        dpsListView.getItems().setAll(dpsList);
    }

    public void setOnDpsSelected(ChangeListener<DPS> listener) {
        dpsListView.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public void displayDpsDetails(DPS dps) {
        rightPanel.setDisable(false);
        dpsDetailsLabel.setText("Détails pour : " + dps.getSport().getNom());
    }

    public void clearProposition() {
        propositionContainer.getChildren().clear();
    }

    public void displayProposition(List<AffectationResultat> proposition) {
        clearProposition();
        if (proposition.isEmpty()) {
            propositionContainer.getChildren().add(new Label("Aucune affectation possible trouvée."));
            return;
        }
        for (AffectationResultat res : proposition) {
            String text = String.format("Poste [%s] → %s %s",
                    res.poste().competenceRequise().getIntitule(),
                    res.secouriste().getPrenom(),
                    res.secouriste().getNom());
            propositionContainer.getChildren().add(new Label(text));
        }
    }

    public void setRightPanelDisabled(boolean disabled) {
        if (rightPanel != null) {
            rightPanel.setDisable(disabled);
        }
    }

    public void showLoading(boolean isLoading) {
    if (loadingGifView != null) {
        loadingGifView.setVisible(isLoading);
    }
    
    if (rightPanel != null) {
        rightPanel.setOpacity(isLoading ? 0.5 : 1.0);
        rightPanel.setDisable(isLoading);
    }
}

    public void setAlgoButtonsDisabled(boolean disabled) {
        runExhaustiveButton.setDisable(disabled);
        runGloutonButton.setDisable(disabled);
    }

    public void setRunExhaustiveAction(EventHandler<ActionEvent> handler) {
        runExhaustiveButton.setOnAction(handler);
    }

    public void setRunGreedyAction(EventHandler<ActionEvent> handler) {
        runGloutonButton.setOnAction(handler);
    }

    public void setSaveChangesAction(EventHandler<ActionEvent> handler) {
        saveChangesButton.setOnAction(handler);
    }
}