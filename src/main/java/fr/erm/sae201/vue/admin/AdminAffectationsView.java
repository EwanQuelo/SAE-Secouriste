package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminAffectationsController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
// --- CORRECTION DES IMPORTS ---
import fr.erm.sae201.metier.service.ModelesAlgorithme.AffectationResultat;
import fr.erm.sae201.metier.service.ModelesAlgorithme.Poste; // <-- IMPORT MANQUANT
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
import java.util.List;

public class AdminAffectationsView extends BaseView {

    private ListView<DPS> dpsListView;
    private VBox rightPanel;
    private Label dpsDetailsLabel;
    private VBox propositionContainer;
    private Button runExhaustiveButton, runGloutonButton, saveChangesButton;
    private ProgressIndicator loadingIndicator;

    public AdminAffectationsView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Affectations");
        new AdminAffectationsController(this, navigator);
    }

    @Override
    protected Node createCenterContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.getStyleClass().add("admin-content-container");

        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.getChildren().add(new Label("Choisir un dispositif :"));
        dpsListView = new ListView<>();
        dpsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DPS item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getSport().getNom() + " - " + item.getJournee().getDate());
                }
            }
        });
        leftPanel.getChildren().add(dpsListView);

        rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setDisable(true);

        dpsDetailsLabel = new Label("Veuillez sélectionner un dispositif dans la liste de gauche.");
        dpsDetailsLabel.getStyleClass().add("admin-subtitle");

        runExhaustiveButton = new Button("Approche Exhaustive");
        runExhaustiveButton.getStyleClass().add("algo-button"); // Ajout d'une classe commune

        runGloutonButton = new Button("Approche Gloutonne");
        runGloutonButton.getStyleClass().addAll("algo-button", "glouton-button"); // Classe commune + classe spécifique

        HBox algoButtons = new HBox(20, runExhaustiveButton, runGloutonButton);
        algoButtons.setAlignment(Pos.CENTER);

        propositionContainer = new VBox(5);

        saveChangesButton = new Button("Valider et Enregistrer");
        saveChangesButton.getStyleClass().add("save-button");
        HBox saveBox = new HBox(saveChangesButton);
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        ScrollPane propositionScrollPane = new ScrollPane(propositionContainer);
        propositionScrollPane.setFitToWidth(true);
        propositionScrollPane.setPrefHeight(200);

        rightPanel.getChildren().addAll(dpsDetailsLabel, algoButtons, new Separator(),
                new Label("Proposition d'affectation :"), propositionScrollPane, saveBox);

        StackPane rightStack = new StackPane(rightPanel);
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        rightStack.getChildren().add(loadingIndicator);

        splitPane.getItems().addAll(leftPanel, rightStack);
        splitPane.setDividerPositions(0.3);

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
        loadingIndicator.setVisible(isLoading);
        rightPanel.setDisable(isLoading);
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