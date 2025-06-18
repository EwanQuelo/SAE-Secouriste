package fr.erm.sae201.vue.user;

import fr.erm.sae201.controleur.user.UserCompetencesController; // Assurez-vous d'avoir ce contrôleur
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

import java.util.Set;

public class UserCompetencesView extends BaseView {

    private FlowPane competencesGrid;
    private VBox mainContainer;

    public UserCompetencesView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Compétences");
        // Le contrôleur est maintenant créé dans MainApp, cette ligne est juste pour le contexte
        new UserCompetencesController(this, compte);
    }

    @Override
    protected Node createCenterContent() {
        mainContainer = new VBox(20);
        mainContainer.getStyleClass().add("competences-view-container");
        mainContainer.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Mes Compétences");
        title.getStyleClass().add("form-title");

        competencesGrid = new FlowPane(25, 25); // Augmentation de l'espacement
        competencesGrid.getStyleClass().add("competences-grid");
        competencesGrid.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(competencesGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll-pane");

        mainContainer.getChildren().addAll(title, scrollPane);

        return mainContainer;
    }

    public void displayCompetences(Set<Competence> competences) {
        competencesGrid.getChildren().clear();

        if (competences == null || competences.isEmpty()) {
            showEmptyMessage();
        } else {
            for (Competence c : competences) {
                competencesGrid.getChildren().add(createCompetenceCard(c));
            }
        }
    }

    private void showEmptyMessage() {
        Label emptyLabel = new Label("Vous ne possédez actuellement aucune compétence enregistrée.");
        emptyLabel.getStyleClass().add("empty-message-label");
        mainContainer.getChildren().remove(1);
        mainContainer.getChildren().add(emptyLabel);
    }

    /**
     * Crée une carte visuelle pour une seule compétence, avec la nouvelle icône.
     * @param competence La compétence à afficher.
     * @return Un Node représentant la carte.
     */
    private Node createCompetenceCard(Competence competence) {
        // L'espacement vertical est augmenté pour correspondre à votre demande
        VBox card = new VBox(25); 
        card.getStyleClass().add("competence-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 180); // Taille de la carte ajustée

        // Création de l'icône composite
        StackPane iconStack = new StackPane();
        iconStack.getStyleClass().add("competence-icon-stack");

        // 1. Le cercle noir de fond
        Circle backgroundCircle = new Circle(30); // Rayon de 30px
        backgroundCircle.getStyleClass().add("competence-icon-circle");

        // 2. La coche (checkmark) bleue
        SVGPath checkmark = new SVGPath();
        checkmark.setContent("M9 12.75L11.25 15L15 9.75"); // Chemin SVG pour la coche
        checkmark.getStyleClass().add("competence-icon-checkmark");

        iconStack.getChildren().addAll(backgroundCircle, checkmark);
        
        Label nameLabel = new Label(competence.getIntitule());
        nameLabel.getStyleClass().add("competence-name");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(iconStack, nameLabel);
        return card;
    }
}