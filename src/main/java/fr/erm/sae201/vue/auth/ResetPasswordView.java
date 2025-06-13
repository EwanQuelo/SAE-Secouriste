package fr.erm.sae201.vue.auth;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment; // Import TextAlignment
import fr.erm.sae201.utils.RessourceLoader;

public class ResetPasswordView {

    private StackPane rootPane;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button validateButton;
    private Hyperlink loginLink;
    private Hyperlink signupLink;

    public ResetPasswordView() {
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
        formBox.setMaxSize(380, 450);

        Label titleLabel = new Label("RÉINITIALISER LE MOT DE PASSE");
        titleLabel.getStyleClass().add("title-label");
        
        // MODIFIED: Ensure the title wraps and is centered if it's too long
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        newPasswordField.getStyleClass().add("login-input");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");
        confirmPasswordField.getStyleClass().add("login-input");

        validateButton = new Button("Valider");
        validateButton.getStyleClass().add("login-button");
        validateButton.setMaxWidth(Double.MAX_VALUE);

        loginLink = new Hyperlink("Connexion");
        loginLink.getStyleClass().add("forgot-password-link");
        signupLink = new Hyperlink("Inscription");
        signupLink.getStyleClass().add("forgot-password-link");

        HBox linksBox = new HBox(20, loginLink, signupLink);
        linksBox.setAlignment(Pos.CENTER);
        
        formBox.getChildren().addAll(
                titleLabel,
                newPasswordField,
                confirmPasswordField,
                validateButton,
                linksBox
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
    
    // Getters remain unchanged
    public StackPane getView() { return rootPane; }
    public String getNewPassword() { return newPasswordField.getText(); }
    public String getConfirmPassword() { return confirmPasswordField.getText(); }
    public Button getValidateButton() { return validateButton; }
    public Hyperlink getLoginLink() { return loginLink; }
    public Hyperlink getSignupLink() { return signupLink; }
}