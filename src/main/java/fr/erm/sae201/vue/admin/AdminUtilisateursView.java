package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminUtilisateursController;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.event.EventHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La vue pour la gestion de la liste des utilisateurs secouristes.
 * Elle affiche une liste paginée et filtrable des secouristes, et permet à l'administrateur
 * de lancer des opérations d'ajout, de modification ou de suppression sur ces derniers.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminUtilisateursView extends BaseView {

    private AdminUtilisateursController controller;
    private VBox userListContainer;
    private Label userCountLabel;
    private HBox paginationContainer;
    private TextField searchField;
    private MainApp navigator;
    private final CompteUtilisateur compte;
    private Button addUserButton;

    private final Map<String, Color> competenceColorMap = new HashMap<>();
    private final List<Color> colorPalette = Arrays.asList(
            Color.web("#4F46E5"), Color.web("#D97706"), Color.web("#059669"),
            Color.web("#DB2777"), Color.web("#6D28D9"), Color.web("#0891B2"),
            Color.web("#BE185D"), Color.web("#4D7C0F")
    );
    private int nextColorIndex = 0;

    /**
     * Constructeur de la vue de gestion des utilisateurs.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'administrateur connecté.
     */
    public AdminUtilisateursView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Utilisateurs");
        this.navigator = navigator;
        this.controller = new AdminUtilisateursController(this, this.navigator);
        this.compte = compte;
    }

    /**
     * Retourne le compte de l'utilisateur connecté.
     *
     * @return Le compte utilisateur.
     */
    public CompteUtilisateur getCompte() {
        return this.compte;
    }

    /**
     * Retourne le champ de recherche.
     *
     * @return Le TextField utilisé pour la recherche.
     */
    public TextField getSearchField() {
        return searchField;
    }

    /**
     * Crée et retourne le contenu principal de la vue.
     * Ce contenu inclut un en-tête avec des contrôles, une liste déroulante pour les utilisateurs,
     * et un pied de page pour la pagination.
     *
     * @return Le nœud (Node) racine du contenu.
     */
    @Override
    protected Node createCenterContent() {
        VBox mainContainer = new VBox(15);
        mainContainer.getStyleClass().add("admin-user-management-panel");

        Node header = createHeader();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("admin-user-scroll-pane");
        userListContainer = new VBox();
        scrollPane.setContent(userListContainer);
        Node footer = createFooter();

        mainContainer.getChildren().addAll(header, scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return mainContainer;
    }

    /**
     * Crée l'en-tête de la vue, qui contient le titre, le compteur d'utilisateurs,
     * le bouton d'ajout et le champ de recherche.
     *
     * @return Le nœud (Node) de l'en-tête.
     */
    private Node createHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));

        Label titleLabel = new Label("Utilisateurs Secouriste");
        titleLabel.getStyleClass().add("admin-user-title");
        userCountLabel = new Label("");
        userCountLabel.getStyleClass().add("user-count-badge");
        
        addUserButton = new Button("+");
        addUserButton.getStyleClass().add("add-button");
        HBox.setMargin(addUserButton, new Insets(0, 0, 0, 10));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        searchField = new TextField();
        searchField.setPromptText("Rechercher par nom, prénom, email...");
        searchField.getStyleClass().add("search-bar");
        searchField.setPrefWidth(300);

        header.getChildren().addAll(titleLabel, userCountLabel, addUserButton, spacer, searchField);
        return header;
    }

    /**
     * Définit l'action à exécuter lors du clic sur le bouton d'ajout d'utilisateur.
     *
     * @param eventHandler Le gestionnaire d'événement.
     */
    public void setAddUserButtonAction(EventHandler<ActionEvent> eventHandler) {
        if (addUserButton != null) {
            addUserButton.setOnAction(eventHandler);
        }
    }

    /**
     * Crée le pied de page de la vue, qui contient le conteneur pour les contrôles de pagination.
     *
     * @return Le nœud (Node) du pied de page.
     */
    private Node createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        paginationContainer = new HBox(5);
        paginationContainer.setAlignment(Pos.CENTER);
        footer.getChildren().add(paginationContainer);
        return footer;
    }

    /**
     * Affiche la liste des secouristes dans la vue.
     * Efface la liste actuelle et la repeuple avec les secouristes fournis.
     * Affiche un message si la liste est vide.
     *
     * @param secouristes La liste des secouristes à afficher.
     */
    public void displayUsers(List<Secouriste> secouristes) {
        userListContainer.getChildren().clear();
        if (secouristes.isEmpty()) {
            Label placeholder = new Label("Aucun secouriste trouvé pour cette page.");
            placeholder.getStyleClass().add("empty-list-label");
            userListContainer.getChildren().add(placeholder);
        } else {
            for (Secouriste secouriste : secouristes) {
                userListContainer.getChildren().add(createUserRowNode(secouriste));
            }
        }
    }

    /**
     * Crée la représentation visuelle d'une ligne pour un secouriste.
     * Cette ligne contient les initiales, le nom/prénom, l'e-mail, des Labels colorées
     * pour les compétences, et les boutons de modification et de suppression.
     *
     * @param secouriste Le secouriste pour lequel créer la ligne.
     * @return Le nœud (Node) représentant la ligne de l'utilisateur.
     */
    private Node createUserRowNode(Secouriste secouriste) {
        HBox row = new HBox();
        row.getStyleClass().add("user-row");
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane pfpContainer = new StackPane();
        pfpContainer.setPrefSize(32, 32);
        String initials = (secouriste.getPrenom().substring(0, 1) + secouriste.getNom().substring(0, 1)).toUpperCase();
        Label initialsLabel = new Label(initials);
        initialsLabel.getStyleClass().add("user-initials");
        Circle circle = new Circle(16);
        circle.getStyleClass().add("user-initials-circle");
        pfpContainer.getChildren().addAll(circle, initialsLabel);

        VBox nameBox = new VBox(-2);
        Label nameLabel = new Label(secouriste.getPrenom() + " " + secouriste.getNom());
        nameLabel.getStyleClass().add("user-name");
        Label handleLabel = new Label("@" + secouriste.getPrenom().toLowerCase());
        handleLabel.getStyleClass().add("user-handle");
        nameBox.getChildren().addAll(nameLabel, handleLabel);
        HBox nameContainer = new HBox(10, pfpContainer, nameBox);
        nameContainer.setAlignment(Pos.CENTER_LEFT);
        nameContainer.setPrefWidth(250);

        Label emailLabel = new Label(secouriste.getEmail());
        emailLabel.getStyleClass().add("user-email");
        emailLabel.setPrefWidth(250);

        HBox competencesBox = new HBox(5);
        competencesBox.setAlignment(Pos.CENTER_LEFT);
        for (Competence comp : secouriste.getCompetences()) {
            Label compLabel = new Label(comp.getIntitule());
            compLabel.getStyleClass().add("competence-tag");
            Color tagColor = getColorForCompetence(comp.getIntitule());
            Color textColor = (tagColor.getBrightness() > 0.6) ? Color.BLACK : Color.WHITE;
            compLabel.setStyle(
                "-fx-background-color: " + toHexString(tagColor) + "; " +
                "-fx-text-fill: " + toHexString(textColor) + ";"
            );
            competencesBox.getChildren().add(compLabel);
        }
        HBox.setHgrow(competencesBox, Priority.ALWAYS);

        Button deleteButton = new Button();
        deleteButton.getStyleClass().add("action-icon-button");
        SVGPath deleteIcon = new SVGPath();
        deleteIcon.setContent("M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0");
        deleteIcon.getStyleClass().add("svg-icon");
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setOnAction(e -> controller.deleteSecouriste(secouriste));

        Button editButton = new Button();
        editButton.getStyleClass().add("action-icon-button");
        SVGPath editIcon = new SVGPath();
        editIcon.setContent("M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125");
        editIcon.getStyleClass().add("svg-icon");
        editButton.setGraphic(editIcon);
        editButton.setOnAction(e -> controller.editSecouriste(secouriste));

        HBox actionsBox = new HBox(5, deleteButton, editButton);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(nameContainer, emailLabel, competencesBox, actionsBox);
        return row;
    }
    
    /**
     * Assigne une couleur à une compétence et la conserve pour les utilisations futures.
     * Ceci garantit qu'une même compétence a toujours la même couleur dans l'interface,
     * en utilisant une palette prédéfinie.
     *
     * @param intitule L'intitulé de la compétence.
     * @return La couleur (Color) associée.
     */
    private Color getColorForCompetence(String intitule) {
        return competenceColorMap.computeIfAbsent(intitule, k -> {
            Color color = colorPalette.get(nextColorIndex);
            nextColorIndex = (nextColorIndex + 1) % colorPalette.size();
            return color;
        });
    }

    /**
     * Convertit un objet Color JavaFX en sa représentation hexadécimale CSS (ex: '#FFFFFF').
     *
     * @param color La couleur à convertir.
     * @return La chaîne de caractères hexadécimale.
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Met à jour les contrôles de pagination en bas de la vue.
     * Affiche les boutons 'Précédent' et 'Suivant', les numéros de page, et gère
     * l'affichage des ellipses '...' pour les listes de pages longues.
     *
     * @param currentPage Le numéro de la page actuelle.
     * @param totalPages Le nombre total de pages.
     */
    public void updatePagination(int currentPage, int totalPages) {
        paginationContainer.getChildren().clear();

        Button prevButton = new Button("← Précédent");
        prevButton.getStyleClass().add("pagination-button");
        prevButton.setDisable(currentPage == 1);
        prevButton.setOnAction(e -> controller.previousPage());
        paginationContainer.getChildren().add(prevButton);

        if (currentPage > 2) {
             paginationContainer.getChildren().add(createPageButton(1));
            if (currentPage > 3) paginationContainer.getChildren().add(new Label("..."));
        }
        if (currentPage > 1) paginationContainer.getChildren().add(createPageButton(currentPage - 1));
        
        Button currentButton = createPageButton(currentPage);
        currentButton.getStyleClass().add("page-number-active");
        paginationContainer.getChildren().add(currentButton);

        if (currentPage < totalPages) paginationContainer.getChildren().add(createPageButton(currentPage + 1));
        if (currentPage < totalPages - 1) {
            if (currentPage < totalPages - 2) paginationContainer.getChildren().add(new Label("..."));
            paginationContainer.getChildren().add(createPageButton(totalPages));
        }

        Button nextButton = new Button("Suivant →");
        nextButton.getStyleClass().add("pagination-button");
        nextButton.setDisable(currentPage == totalPages);
        nextButton.setOnAction(e -> controller.nextPage());
        paginationContainer.getChildren().add(nextButton);
    }

    /**
     * Crée un bouton de pagination pour un numéro de page spécifique.
     *
     * @param pageNumber Le numéro de la page pour le bouton.
     * @return Le bouton (Button) de page.
     */
    private Button createPageButton(int pageNumber) {
        Button pageButton = new Button(String.valueOf(pageNumber));
        pageButton.getStyleClass().add("page-number-button");
        pageButton.setOnAction(e -> controller.displayPage(pageNumber));
        return pageButton;
    }

    /**
     * Met à jour le libellé affichant le nombre total de secouristes.
     *
     * @param count Le nombre total de secouristes.
     */
    public void setUserCount(int count) {
        userCountLabel.setText(count + " secouristes");
    }
}