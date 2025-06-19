package fr.erm.sae201.vue.user;

import fr.erm.sae201.controleur.user.UserCompetencesController;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
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

/**
 * La vue permettant à un utilisateur secouriste de consulter la liste des compétences qu'il possède.
 * Les compétences sont affichées sous forme d'une grille de cartes visuelles.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class UserCompetencesView extends BaseView {

    /** Grille flexible pour afficher les cartes de compétence. */
    private FlowPane competencesGrid;
    /** Conteneur principal de la vue. */
    private VBox mainContainer;

    /**
     * Construit la vue des compétences de l'utilisateur.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'utilisateur connecté.
     */
    public UserCompetencesView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Compétences");
        new UserCompetencesController(this, compte);
    }

    /**
     * Crée et retourne le contenu central de la vue.
     * Ce contenu est composé d'un titre et d'une grille défilable pour afficher les cartes de compétence.
     *
     * @return Le nœud (Node) principal du contenu de la vue.
     */
    @Override
    protected Node createCenterContent() {
        mainContainer = new VBox(20);
        mainContainer.getStyleClass().add("competences-view-container");
        mainContainer.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Mes Compétences");
        title.getStyleClass().add("form-title");

        // Utilise un FlowPane pour que les cartes se réorganisent automatiquement.
        // Espacement horizontal et vertical de 25px entre les cartes.
        competencesGrid = new FlowPane(25, 25);
        competencesGrid.getStyleClass().add("competences-grid");
        competencesGrid.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(competencesGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll-pane");

        mainContainer.getChildren().addAll(title, scrollPane);

        return mainContainer;
    }

    /**
     * Affiche les compétences de l'utilisateur dans la grille.
     * Si l'utilisateur n'a aucune compétence, un message approprié est affiché à la place.
     *
     * @param competences L'ensemble des compétences possédées par l'utilisateur.
     */
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

    /**
     * Affiche un message indiquant que l'utilisateur n'a aucune compétence.
     * Ce message remplace la grille des compétences.
     */
    private void showEmptyMessage() {
        Label emptyLabel = new Label("Vous ne possédez actuellement aucune compétence enregistrée.");
        emptyLabel.getStyleClass().add("empty-message-label");
        // Remplace le ScrollPane (à l'index 1) par le message.
        mainContainer.getChildren().remove(1);
        mainContainer.getChildren().add(emptyLabel);
    }

    /**
     * Crée une carte visuelle pour une compétence donnée, incluant une icône et le nom de la compétence.
     *
     * @param competence La compétence à afficher.
     * @return Un nœud (Node) représentant la carte de la compétence.
     */
    private Node createCompetenceCard(Competence competence) {
        VBox card = new VBox(25); 
        card.getStyleClass().add("competence-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 180);

        // Création de l'icône composite à l'aide d'un StackPane.
        StackPane iconStack = new StackPane();
        iconStack.getStyleClass().add("competence-icon-stack");

        // 1. Le cercle de fond.
        Circle backgroundCircle = new Circle(30);
        backgroundCircle.getStyleClass().add("competence-icon-circle");

        // 2. La coche (symbole de validation).
        SVGPath checkmark = new SVGPath();
        checkmark.setContent("M9 12.75L11.25 15L15 9.75");
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