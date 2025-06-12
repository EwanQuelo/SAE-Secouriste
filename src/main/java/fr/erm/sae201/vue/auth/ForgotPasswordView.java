package fr.erm.sae201.vue.auth;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import fr.erm.sae201.utils.RessourceLoader;

public class ForgotPasswordView {

    private StackPane rootPane;
    private TextField emailField;
    private TextField codeField;
    private Button sendCodeButton;

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

        VBox formBox = new VBox(20);
        formBox.getStyleClass().add("login-box");
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxSize(380, 400);

        Label titleLabel = new Label("MOT DE PASSE OUBLIÉ");
        titleLabel.getStyleClass().add("title-label");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("login-input");

        sendCodeButton = new Button("Envoyer le code");
        sendCodeButton.getStyleClass().add("login-button");
        sendCodeButton.setMaxWidth(Double.MAX_VALUE);
        
        codeField = new TextField();
        codeField.setPromptText("Code reçu par email");
        codeField.getStyleClass().add("login-input");
        // Optionnel : cacher le champ de code au début
        codeField.setVisible(false);
        codeField.setManaged(false);


        formBox.getChildren().addAll(
                titleLabel,
                emailField,
                sendCodeButton,
                codeField
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

    // Getters pour le contrôleur
    public StackPane getView() { return rootPane; }
    public String getEmail() { return emailField.getText(); }
    public String getCode() { return codeField.getText(); }
    public Button getSendCodeButton() { return sendCodeButton; }
}