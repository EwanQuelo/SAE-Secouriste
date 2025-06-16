package fr.erm.sae201.vue.auth;

import fr.erm.sae201.utils.RessourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Represents the user interface for the "Forgot Password" screen.
 */
public class ForgotPasswordView {

    private StackPane rootPane;
    private TextField emailField;
    private TextField codeField;
    private Button sendCodeButton;
    private Hyperlink loginLink; // ADDED: Hyperlink to go back to login

    public ForgotPasswordView() {
        createView();
    }

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

        VBox formBox = new VBox(20);
        formBox.getStyleClass().add("login-box");
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxSize(420, 450);

        Label titleLabel = new Label("MOT DE PASSE OUBLIÉ");
        titleLabel.getStyleClass().add("title-label");
        // MODIFIED: Allow text to wrap to a new line if it's too long
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("login-input");

        sendCodeButton = new Button("Envoyer le code");
        sendCodeButton.getStyleClass().add("login-button");
        sendCodeButton.setMaxWidth(Double.MAX_VALUE);
        
        codeField = new TextField();
        codeField.setPromptText("Code reçu par email");
        codeField.getStyleClass().add("login-input");
        codeField.setVisible(false);
        codeField.setManaged(false);

        // ADDED: Create and style the login link
        loginLink = new Hyperlink("Retour à la connexion");
        loginLink.getStyleClass().add("forgot-password-link");
        VBox.setMargin(loginLink, new Insets(10, 0, 0, 0)); // Add some top margin

        formBox.getChildren().addAll(
                titleLabel,
                emailField,
                sendCodeButton,
                codeField,
                loginLink // ADDED: Add the link to the form
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
    
    public void showCodeField() {
        codeField.setVisible(true);
        codeField.setManaged(true);
    }

    // Getters for the controller
    public StackPane getView() { return rootPane; }
    public String getEmail() { return emailField.getText(); }
    public String getCode() { return codeField.getText(); }
    public Button getSendCodeButton() { return sendCodeButton; }
    public Hyperlink getLoginLink() { return loginLink; } // ADDED: Getter for the new link
}