package fr.erm.sae201.vue.auth;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import fr.erm.sae201.utils.RessourceLoader;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * La vue de l'interface utilisateur pour l'écran d'inscription.
 * Elle fournit un formulaire complet pour la création d'un nouveau compte,
 * avec des champs pour le prénom, le nom, la date de naissance, l'e-mail et le mot de passe.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SignupView {

    private StackPane rootPane;
    private TextField emailField;
    private PasswordField passwordField;
    private TextField firstNameField;
    private TextField lastNameField;
    private Button signupButton;
    private Hyperlink loginLink;
    private ComboBox<Integer> dayComboBox;
    private ComboBox<Integer> monthComboBox;
    private ComboBox<Integer> yearComboBox;

    /**
     * Construit la vue d'inscription en initialisant ses composants graphiques.
     */
    public SignupView() {
        createView();
    }

    /**
     * Crée et assemble tous les éléments graphiques de la vue.
     */
    private void createView() {
        rootPane = new StackPane();
        rootPane.getStyleClass().add("login-root");

        ImageView backgroundImageView = new ImageView(RessourceLoader.loadImage("background.png"));
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
        rootPane.getChildren().add(backgroundImageView);

        GaussianBlur blurEffect = new GaussianBlur(10);
        backgroundImageView.setEffect(blurEffect);
        backgroundImageView.setSmooth(true);
        
        VBox formBox = new VBox(15);
        formBox.getStyleClass().add("login-box");
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxSize(420, 600);

        Label titleLabel = new Label("INSCRIPTION");
        titleLabel.getStyleClass().add("title-label");

        firstNameField = new TextField();
        firstNameField.setPromptText("Prénom");
        firstNameField.getStyleClass().add("login-input");

        lastNameField = new TextField();
        lastNameField.setPromptText("Nom");
        lastNameField.getStyleClass().add("login-input");

        Label dobLabel = new Label("Date de naissance");
        dobLabel.getStyleClass().add("form-label");

        dayComboBox = new ComboBox<>();
        dayComboBox.setPromptText("Jour");
        dayComboBox.getStyleClass().add("login-input");
        for (int i = 1; i <= 31; i++) {
            dayComboBox.getItems().add(i);
        }

        monthComboBox = new ComboBox<>();
        monthComboBox.setPromptText("Mois");
        monthComboBox.getStyleClass().add("login-input");
        for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }

        yearComboBox = new ComboBox<>();
        yearComboBox.setPromptText("Année");
        yearComboBox.getStyleClass().add("login-input");
        int currentYear = LocalDate.now().getYear();
        // Le secouriste doit avoir au moins 18 ans pour s'inscrire.
        for (int i = currentYear - 18; i >= currentYear - 100; i--) {
            yearComboBox.getItems().add(i);
        }

        HBox dobBox = new HBox(10, dayComboBox, monthComboBox, yearComboBox);
        dobBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(dayComboBox, Priority.ALWAYS);
        HBox.setHgrow(monthComboBox, Priority.ALWAYS);
        HBox.setHgrow(yearComboBox, Priority.ALWAYS);

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("login-input");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.getStyleClass().add("login-input");

        signupButton = new Button("S'inscrire");
        signupButton.getStyleClass().add("login-button");
        signupButton.setMaxWidth(Double.MAX_VALUE);

        loginLink = new Hyperlink("Déjà un compte ? Connectez-vous");
        loginLink.getStyleClass().add("forgot-password-link");

        formBox.getChildren().addAll(
                titleLabel,
                firstNameField,
                lastNameField,
                dobLabel,
                dobBox,
                emailField,
                passwordField,
                signupButton,
                loginLink
        );

        ImageView olympicRingsView = new ImageView(RessourceLoader.loadImage("olympic_rings.png"));
        olympicRingsView.setFitHeight(60);
        olympicRingsView.setPreserveRatio(true);

        VBox mainContent = new VBox(40);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(formBox, olympicRingsView);

        rootPane.getChildren().add(mainContent);
        StackPane.setAlignment(mainContent, Pos.CENTER);
    }

    /**
     * Retourne le conteneur racine de cette vue.
     * @return Le StackPane racine.
     */
    public StackPane getView() { return rootPane; }

    /**
     * Récupère l'adresse e-mail saisie par l'utilisateur.
     * @return L'adresse e-mail.
     */
    public String getEmail() { return emailField.getText(); }

    /**
     * Récupère le mot de passe saisi par l'utilisateur.
     * @return Le mot de passe.
     */
    public String getPassword() { return passwordField.getText(); }

    /**
     * Récupère le prénom saisi par l'utilisateur.
     * @return Le prénom.
     */
    public String getFirstName() { return firstNameField.getText(); }

    /**
     * Récupère le nom de famille saisi par l'utilisateur.
     * @return Le nom de famille.
     */
    public String getLastName() { return lastNameField.getText(); }

    /**
     * Retourne le bouton d'inscription.
     * @return Le bouton (Button) d'inscription.
     */
    public Button getSignupButton() { return signupButton; }

    /**
     * Retourne l'hyperlien pour revenir à la page de connexion.
     * @return L'hyperlien (Hyperlink).
     */
    public Hyperlink getLoginLink() { return loginLink; }
    
    /**
     * Construit un objet LocalDate à partir des valeurs sélectionnées dans les ComboBox.
     * Retourne null si un des champs n'est pas rempli ou si la date est invalide
     * (par exemple, 30 février).
     *
     * @return Un objet LocalDate représentant la date de naissance, ou null en cas d'erreur.
     */
    public LocalDate getDateOfBirth() {
        Integer day = dayComboBox.getValue();
        Integer month = monthComboBox.getValue();
        Integer year = yearComboBox.getValue();

        if (day == null || month == null || year == null) {
            return null;
        }

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            // Cette exception est levée pour les dates invalides comme le 30 février.
            System.err.println("Invalid date selected: " + e.getMessage());
            return null;
        }
    }
}