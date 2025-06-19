package fr.erm.sae201.vue.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

import fr.erm.sae201.utils.RessourceLoader;

/**
 * La vue de l'interface utilisateur pour l'écran de connexion.
 * Elle présente des champs pour l'e-mail et le mot de passe, des boutons pour
 * la connexion et l'inscription, ainsi qu'un lien pour la réinitialisation du mot de passe.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class LoginView {

    private StackPane rootPane;
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button signupButton;
    private Hyperlink forgotPasswordLink;

    /**
     * Construit la vue de connexion en initialisant ses composants graphiques.
     */
    public LoginView() {
        createView();
    }

    /**
     * Crée et assemble tous les éléments graphiques de la vue.
     */
    private void createView() {
        rootPane = new StackPane();
        rootPane.getStyleClass().add("login-root");

        // --- Fond Commun ---
        ImageView backgroundImageView = new ImageView(RessourceLoader.loadImage("background.png"));
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
        rootPane.getChildren().add(backgroundImageView);
        // --- Fin du Fond Commun ---
        

        GaussianBlur blurEffect = new GaussianBlur(10);
        backgroundImageView.setEffect(blurEffect);
        backgroundImageView.setSmooth(true);

        VBox loginBox = new VBox(20);
        loginBox.getStyleClass().add("login-box");
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxSize(380, 450);

        Label titleLabel = new Label("CONNEXION");
        titleLabel.getStyleClass().add("title-label");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("login-input");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.getStyleClass().add("login-input");

        forgotPasswordLink = new Hyperlink("Mot de passe oublié ?");
        forgotPasswordLink.getStyleClass().add("forgot-password-link");
        VBox.setMargin(forgotPasswordLink, new Insets(-10, 0, 0, 0));

        loginButton = new Button("Connexion");
        loginButton.getStyleClass().add("login-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        signupButton = new Button("Inscription");
        signupButton.getStyleClass().add("signup-button");
        signupButton.setMaxWidth(Double.MAX_VALUE);
        
        loginBox.getChildren().addAll(
                titleLabel,
                emailField,
                passwordField,
                forgotPasswordLink,
                loginButton,
                signupButton
        );
        
        // --- Section Commune (Logo) ---
        ImageView olympicRingsView = new ImageView(RessourceLoader.loadImage("olympic_rings.png"));
        olympicRingsView.setFitHeight(60);
        olympicRingsView.setPreserveRatio(true);

        VBox mainContent = new VBox(50);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(loginBox, olympicRingsView);

        rootPane.getChildren().add(mainContent);
        StackPane.setAlignment(mainContent, Pos.CENTER);
        // --- Fin de la Section Commune ---
    }

    /**
     * Affiche une notification d'information à l'utilisateur.
     * La notification apparaît en bas à droite de l'écran et disparaît après 3 secondes.
     */
    public void sendNotif() {
        Notifications.create()
            .title("Application SECOURS")
            .text("ControlsFX est correctement intégré !")
            .hideAfter(Duration.seconds(3))
            .position(Pos.BOTTOM_RIGHT)
            .showInformation();
            
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
     * Retourne le bouton de connexion.
     * @return Le bouton (Button) de connexion.
     */
    public Button getLoginButton() { return loginButton; }
    
    /**
     * Retourne le bouton d'inscription.
     * @return Le bouton (Button) d'inscription.
     */
    public Button getSignupButton() { return signupButton; }
    
    /**
     * Retourne l'hyperlien pour la réinitialisation du mot de passe.
     * @return L'hyperlien (Hyperlink).
     */
    public Hyperlink getForgotPasswordLink() { return forgotPasswordLink; }
}