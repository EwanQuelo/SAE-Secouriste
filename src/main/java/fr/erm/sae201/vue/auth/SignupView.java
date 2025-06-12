package fr.erm.sae201.vue.auth;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import fr.erm.sae201.utils.RessourceLoader;

public class SignupView {

    private StackPane rootPane;
    private TextField emailField;
    private PasswordField passwordField;
    private TextField firstNameField;
    private TextField lastNameField;
    private Button signupButton;
    private Hyperlink loginLink;

    public SignupView() {
        createView();
    }

    private void createView() {
        rootPane = new StackPane();
        rootPane.getStyleClass().add("login-root");

        ImageView backgroundImageView = new ImageView(RessourceLoader.loadImage("background.png"));
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
        rootPane.getChildren().add(backgroundImageView);

        VBox formBox = new VBox(20);
        formBox.getStyleClass().add("login-box");
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxSize(380, 550); // Boîte un peu plus grande

        Label titleLabel = new Label("INSCRIPTION");
        titleLabel.getStyleClass().add("title-label");

        firstNameField = new TextField();
        firstNameField.setPromptText("Prénom");
        firstNameField.getStyleClass().add("login-input");

        lastNameField = new TextField();
        lastNameField.setPromptText("Nom de famille");
        lastNameField.getStyleClass().add("login-input");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("login-input");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.getStyleClass().add("login-input");

        signupButton = new Button("Inscription");
        signupButton.getStyleClass().add("login-button"); // Style principal
        signupButton.setMaxWidth(Double.MAX_VALUE);
        
        loginLink = new Hyperlink("Déjà un compte ? Connectez-vous");
        loginLink.getStyleClass().add("forgot-password-link");

        formBox.getChildren().addAll(
                titleLabel,
                firstNameField,
                lastNameField,
                emailField,
                passwordField,
                signupButton,
                loginLink
        );

        ImageView olympicRingsView = new ImageView(RessourceLoader.loadImage("olympic_rings.png"));
        olympicRingsView.setFitHeight(60);
        olympicRingsView.setPreserveRatio(true);

        VBox mainContent = new VBox(50);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(formBox, olympicRingsView);

        rootPane.getChildren().add(mainContent);
        StackPane.setAlignment(mainContent, Pos.CENTER);
    }

    // Getters pour le contrôleur
    public StackPane getView() { return rootPane; }
    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public String getFirstName() { return firstNameField.getText(); }
    public String getLastName() { return lastNameField.getText(); }
    public Button getSignupButton() { return signupButton; }
    public Hyperlink getLoginLink() { return loginLink; }
}