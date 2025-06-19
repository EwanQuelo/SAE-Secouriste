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
 * La vue de l'interface utilisateur pour l'écran "Mot de passe oublié".
 * Elle fournit un formulaire où l'utilisateur peut saisir son adresse e-mail pour
 * initier le processus de réinitialisation de mot de passe.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class ForgotPasswordView {

    private StackPane rootPane;
    private TextField emailField;
    private TextField codeField;
    private Button sendCodeButton;
    private Hyperlink loginLink;

    /**
     * Construit la vue "Mot de passe oublié" en initialisant ses composants graphiques.
     */
    public ForgotPasswordView() {
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
        formBox.setMaxSize(420, 450);

        Label titleLabel = new Label("MOT DE PASSE OUBLIÉ");
        titleLabel.getStyleClass().add("title-label");
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
        // Le champ de code est initialement caché. Il n'apparaît que sur demande du contrôleur.
        codeField.setVisible(false);
        codeField.setManaged(false);

        loginLink = new Hyperlink("Retour à la connexion");
        loginLink.getStyleClass().add("forgot-password-link");
        VBox.setMargin(loginLink, new Insets(10, 0, 0, 0));

        formBox.getChildren().addAll(
                titleLabel,
                emailField,
                sendCodeButton,
                codeField,
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
    
    /**
     * Rend visible le champ de saisie du code de vérification.
     * Cette méthode est typiquement appelée après que le code a été envoyé à l'utilisateur.
     */
    public void showCodeField() {
        codeField.setVisible(true);
        codeField.setManaged(true);
    }

    /**
     * Retourne le conteneur racine de cette vue.
     * @return Le StackPane racine.
     */
    public StackPane getView() { return rootPane; }

    /**
     * Récupère l'adresse e-mail saisie par l'utilisateur.
     * @return L'adresse e-mail sous forme de chaîne de caractères.
     */
    public String getEmail() { return emailField.getText(); }

    /**
     * Récupère le code de vérification saisi par l'utilisateur.
     * @return Le code de vérification sous forme de chaîne de caractères.
     */
    public String getCode() { return codeField.getText(); }

    /**
     * Retourne le bouton permettant d'envoyer le code de réinitialisation.
     * @return Le bouton (Button) "Envoyer le code".
     */
    public Button getSendCodeButton() { return sendCodeButton; }

    /**
     * Retourne l'hyperlien permettant de revenir à la page de connexion.
     * @return L'hyperlien (Hyperlink) "Retour à la connexion".
     */
    public Hyperlink getLoginLink() { return loginLink; }
}