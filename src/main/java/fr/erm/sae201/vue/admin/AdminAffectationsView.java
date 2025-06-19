package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminAffectationsController;
import fr.erm.sae201.metier.graphe.modele.AffectationResultat;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Vue pour l'interface de gestion des affectations des secouristes par un administrateur.
 * 
 * Elle affiche une liste de Dispositifs Prévisionnels de Secours (DPS), permet de
 * lancer des algorithmes d'affectation (exhaustif et glouton) et de visualiser
 * les propositions résultantes avant de les enregistrer.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.2
 */
public class AdminAffectationsView extends BaseView {

    /** Liste affichant les DPS sélectionnables. */
    private ListView<DPS> dpsListView;

    /** Panneau droit affichant les détails et actions pour le DPS sélectionné. */
    private VBox rightPanel;

    /** Label affichant les détails du DPS choisi. */
    private Label dpsDetailsLabel;

    /** Conteneur pour afficher les lignes de la proposition d'affectation. */
    private VBox propositionContainer;

    /** Bouton pour lancer l'algorithme exhaustif. */
    private Button runExhaustiveButton;

    /** Bouton pour lancer l'algorithme glouton. */
    private Button runGloutonButton;

    /** Bouton pour enregistrer les modifications. */
    private Button saveChangesButton;

    /** Vue pour l'animation de chargement (GIF). */
    private ImageView loadingGifView;

    /**
     * Constructeur de la vue des affectations.
     * Initialise la vue et crée une instance de son contrôleur associé.
     *
     * @param navigator Le navigateur principal de l'application.
     * @param compte    Le compte de l'utilisateur administrateur connecté.
     */
    public AdminAffectationsView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Affectations");
        new AdminAffectationsController(this, navigator);
    }

    /**
     * Crée et configure le contenu principal de la vue.
     * Cette méthode assemble les principaux composants de l'interface : un SplitPane
     * contenant la liste des DPS à gauche et le panneau de détails et d'actions à droite.
     *
     * @return Le nœud racine (SplitPane) du contenu central.
     */
    @Override
    protected Node createCenterContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.getStyleClass().add("affectations-view-container");

        VBox leftPanel = new VBox(10);
        leftPanel.getStyleClass().add("affectations-left-panel");
        leftPanel.setPadding(new Insets(10));
        leftPanel.getChildren().add(new Label("Choisir un dispositif :"));

        dpsListView = new ListView<>();
        dpsListView.setCellFactory(param -> new ListCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
            @Override
            protected void updateItem(DPS item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String formattedDate = item.getJournee().getDate().format(formatter);
                    setText(item.getSport().getNom() + " - " + formattedDate);
                }
            }
        });
        leftPanel.getChildren().add(dpsListView);

        rightPanel = new VBox(20);
        rightPanel.getStyleClass().add("affectations-right-panel");
        rightPanel.setPadding(new Insets(20));
        rightPanel.setDisable(true);

        dpsDetailsLabel = new Label("Veuillez sélectionner un dispositif pour commencer.");
        dpsDetailsLabel.getStyleClass().addAll("admin-subtitle", "placeholder-text");

        runExhaustiveButton = new Button("Approche Exhaustive");
        runExhaustiveButton.getStyleClass().addAll("algo-button", "exhaustive-button");

        runGloutonButton = new Button("Approche Gloutonne");
        runGloutonButton.getStyleClass().addAll("algo-button", "glouton-button");

        HBox algoButtons = new HBox(20, runExhaustiveButton, runGloutonButton);
        algoButtons.setAlignment(Pos.CENTER);

        propositionContainer = new VBox(5);
        propositionContainer.getStyleClass().add("affectations-proposition-container");

        ScrollPane propositionScrollPane = new ScrollPane(propositionContainer);
        propositionScrollPane.getStyleClass().add("affectations-proposition-scroll-pane");
        propositionScrollPane.setFitToWidth(true);
        propositionScrollPane.setPrefHeight(250);

        saveChangesButton = new Button("Valider et Enregistrer");
        saveChangesButton.getStyleClass().add("save-button");
        HBox saveBox = new HBox(saveChangesButton);
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        rightPanel.getChildren().addAll(dpsDetailsLabel, algoButtons, new Separator(),
                new Label("Proposition d'affectation :"), propositionScrollPane, saveBox);

        StackPane rightStack = new StackPane(rightPanel);

        Image loadingImage = new Image(getClass().getResourceAsStream("/images/loading1.gif"));
        loadingGifView = new ImageView(loadingImage);
        loadingGifView.setFitWidth(80);
        loadingGifView.setFitHeight(80);
        loadingGifView.setPreserveRatio(true);
        loadingGifView.setVisible(false);

        rightStack.getChildren().add(loadingGifView);

        splitPane.getItems().addAll(leftPanel, rightStack);
        splitPane.setDividerPositions(0.35);

        return splitPane;
    }

    /**
     * Peuple la liste des DPS avec les données fournies.
     *
     * @param dpsList La liste des DPS à afficher.
     */
    public void populateDpsList(List<DPS> dpsList) {
        dpsListView.getItems().setAll(dpsList);
    }

    /**
     * Définit l'action à exécuter lorsqu'un DPS est sélectionné dans la liste.
     *
     * @param listener L'écouteur d'événements à attacher.
     */
    public void setOnDpsSelected(ChangeListener<DPS> listener) {
        dpsListView.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    /**
     * Affiche les détails du DPS sélectionné dans le panneau de droite.
     *
     * @param dps Le DPS sélectionné.
     */
    public void displayDpsDetails(DPS dps) {
        rightPanel.setDisable(false);
        dpsDetailsLabel.setText("Détails pour : " + dps.getSport().getNom());
    }

    /**
     * Efface la proposition d'affectation actuellement affichée.
     */
    public void clearProposition() {
        propositionContainer.getChildren().clear();
    }

    /**
     * Affiche la nouvelle proposition d'affectation dans la zone dédiée.
     *
     * @param proposition La liste des résultats d'affectation à afficher.
     */
    public void displayProposition(List<AffectationResultat> proposition) {
        clearProposition();
        if (proposition.isEmpty()) {
            propositionContainer.getChildren().add(new Label("Aucune affectation possible trouvée."));
            return;
        }
        for (AffectationResultat res : proposition) {
            String text = String.format("Poste [%s] → %s %s",
                    res.getPoste().getCompetenceRequise().getIntitule(),
                    res.getSecouriste().getPrenom(),
                    res.getSecouriste().getNom());
            propositionContainer.getChildren().add(new Label(text));
        }
    }

    /**
     * Active ou désactive le panneau de droite.
     *
     * @param disabled `true` pour désactiver, `false` pour activer.
     */
    public void setRightPanelDisabled(boolean disabled) {
        if (rightPanel != null) {
            rightPanel.setDisable(disabled);
        }
    }

    /**
     * Affiche ou masque l'indicateur de chargement.
     * Affiche une animation et ajuste l'opacité du panneau de droite pour
     * indiquer une opération en cours.
     *
     * @param isLoading `true` pour afficher le chargement, `false` pour le masquer.
     */
    public void showLoading(boolean isLoading) {
        if (loadingGifView != null) {
            loadingGifView.setVisible(isLoading);
        }
        
        if (rightPanel != null) {
            rightPanel.setOpacity(isLoading ? 0.5 : 1.0);
            rightPanel.setDisable(isLoading);
        }
    }

    /**
     * Active ou désactive les boutons de lancement des algorithmes.
     *
     * @param disabled `true` pour désactiver, `false` pour activer.
     */
    public void setAlgoButtonsDisabled(boolean disabled) {
        runExhaustiveButton.setDisable(disabled);
        runGloutonButton.setDisable(disabled);
    }

    /**
     * Définit l'action du bouton pour l'approche exhaustive.
     *
     * @param handler Le gestionnaire d'événement pour l'action du bouton.
     */
    public void setRunExhaustiveAction(EventHandler<ActionEvent> handler) {
        runExhaustiveButton.setOnAction(handler);
    }

    /**
     * Définit l'action du bouton pour l'approche gloutonne.
     *
     * @param handler Le gestionnaire d'événement pour l'action du bouton.
     */
    public void setRunGreedyAction(EventHandler<ActionEvent> handler) {
        runGloutonButton.setOnAction(handler);
    }

    /**
     * Définit l'action du bouton de sauvegarde.
     *
     * @param handler Le gestionnaire d'événement pour l'action du bouton.
     */
    public void setSaveChangesAction(EventHandler<ActionEvent> handler) {
        saveChangesButton.setOnAction(handler);
    }
}