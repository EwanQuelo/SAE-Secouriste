package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminParametresController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * La vue des paramètres pour le compte administrateur.
 * Cette vue permet à l'administrateur de visualiser son adresse e-mail, de modifier son mot de passe
 * et de se déconnecter de l'application.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminParametresView extends BaseView {

    private PasswordField newPasswordField, confirmPasswordField;
    private Button savePasswordButton, logoutButton;
    private Label emailLabel;

    /**
     * Construit la vue des paramètres de l'administrateur.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'administrateur connecté.
     * @param authService Le service d'authentification pour gérer les opérations liées au compte.
     */
    public AdminParametresView(MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        // Le nom de la vue "Accueil" est utilisé pour la gestion du style "actif" dans la barre de navigation.
        super(navigator, compte, "Accueil");
        new AdminParametresController(this, navigator, compte, authService);
    }

    /**
     * Crée et retourne le contenu central de la vue.
     *
     * @return Le nœud (Node) contenant les sections de paramètres.
     */
    @Override
    protected Node createCenterContent() {
        VBox container = new VBox(40);
        container.getStyleClass().add("form-container");
        container.setAlignment(Pos.CENTER);

        Text scenetitle = new Text("Paramètres Administrateur");
        scenetitle.getStyleClass().add("form-title");

        VBox emailInfoBox = createEmailInfoSection();
        VBox passwordBox = createPasswordSection();

        logoutButton = new Button("Déconnexion");
        logoutButton.getStyleClass().add("logout-button");
        VBox.setMargin(logoutButton, new Insets(10, 0, 0, 0));

        container.getChildren().addAll(scenetitle, emailInfoBox, passwordBox, logoutButton);
        return container;
    }

    /**
     * Crée la section affichant l'adresse e-mail du compte administrateur.
     *
     * @return Un VBox contenant les éléments d'information du compte.
     */
    private VBox createEmailInfoSection() {
        VBox sectionContainer = new VBox(5);
        sectionContainer.getStyleClass().add("settings-section");

        HBox emailLine = new HBox(10);
        emailLine.setAlignment(Pos.CENTER_LEFT);

        Label emailTitle = new Label("Compte Administrateur:");
        emailTitle.getStyleClass().add("section-title");

        emailLabel = new Label();
        emailLabel.getStyleClass().add("info-text-main");

        emailLine.getChildren().addAll(emailTitle, emailLabel);
        sectionContainer.getChildren().add(emailLine);
        return sectionContainer;
    }

    /**
     * Crée la section permettant à l'administrateur de modifier son mot de passe.
     *
     * @return Un VBox contenant les champs de mot de passe et le bouton de sauvegarde.
     */
    private VBox createPasswordSection() {
        VBox box = new VBox(10);
        box.getStyleClass().add("settings-section");
        box.setAlignment(Pos.CENTER_LEFT);

        Label passwordTitle = new Label("Modifier le mot de passe");
        passwordTitle.getStyleClass().add("section-title");

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        newPasswordField.getStyleClass().add("settings-input");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le nouveau mot de passe");
        confirmPasswordField.getStyleClass().add("settings-input");

        savePasswordButton = new Button("Enregistrer le mot de passe");
        savePasswordButton.getStyleClass().add("save-button");

        HBox buttonContainer = new HBox(savePasswordButton);
        buttonContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonContainer, new Insets(10, 0, 0, 0));

        box.getChildren().addAll(passwordTitle, newPasswordField, confirmPasswordField, buttonContainer);
        return box;
    }

    /**
     * Définit l'e-mail à afficher dans le libellé d'information.
     *
     * @param email L'adresse e-mail de l'administrateur.
     */
    public void setEmail(String email) {
        emailLabel.setText(email);
    }

    /**
     * Récupère le nouveau mot de passe saisi par l'utilisateur.
     *
     * @return Le nouveau mot de passe.
     */
    public String getNewPassword() {
        return newPasswordField.getText();
    }

    /**
     * Récupère le mot de passe de confirmation saisi.
     *
     * @return Le mot de passe de confirmation.
     */
    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }

    /**
     * Associe une action au bouton d'enregistrement du mot de passe.
     *
     * @param handler Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setSavePasswordButtonAction(EventHandler<ActionEvent> handler) {
        savePasswordButton.setOnAction(handler);
    }

    /**
     * Associe une action au bouton de déconnexion.
     *
     * @param handler Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setLogoutButtonAction(EventHandler<ActionEvent> handler) {
        logoutButton.setOnAction(handler);
    }
}