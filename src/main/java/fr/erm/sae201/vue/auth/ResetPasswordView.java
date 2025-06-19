package fr.erm.sae201.vue.auth;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import fr.erm.sae201.utils.RessourceLoader;

/**
 * La vue de l'interface utilisateur pour l'écran de réinitialisation du mot de passe.
 * Cette vue apparaît après que l'utilisateur a validé le code de vérification. Elle lui
 * permet de saisir et de confirmer un nouveau mot de passe.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class ResetPasswordView {

    private StackPane rootPane;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button validateButton;
    private Hyperlink loginLink;
    private Hyperlink signupLink;

    /**
     * Construit la vue de réinitialisation de mot de passe en initialisant ses composants graphiques.
     */
    public ResetPasswordView() {
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

        VBox formBox = new VBox(20);
        formBox.getStyleClass().add("login-box");
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxSize(380, 450);

        Label titleLabel = new Label("RÉINITIALISER LE MOT DE PASSE");
        titleLabel.getStyleClass().add("title-label");
        
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
    
    /**
     * Retourne le conteneur racine de cette vue.
     * @return Le StackPane racine.
     */
    public StackPane getView() { return rootPane; }
    
    /**
     * Récupère le nouveau mot de passe saisi par l'utilisateur.
     * @return Le nouveau mot de passe.
     */
    public String getNewPassword() { return newPasswordField.getText(); }
    
    /**
     * Récupère le mot de passe de confirmation saisi par l'utilisateur.
     * @return Le mot de passe de confirmation.
     */
    public String getConfirmPassword() { return confirmPasswordField.getText(); }
    
    /**
     * Retourne le bouton de validation du nouveau mot de passe.
     * @return Le bouton (Button) "Valider".
     */
    public Button getValidateButton() { return validateButton; }
    
    /**
     * Retourne l'hyperlien pour revenir à la page de connexion.
     * @return L'hyperlien (Hyperlink) "Connexion".
     */
    public Hyperlink getLoginLink() { return loginLink; }
    
    /**
     * Retourne l'hyperlien pour revenir à la page d'inscription.
     * @return L'hyperlien (Hyperlink) "Inscription".
     */
    public Hyperlink getSignupLink() { return signupLink; }
}