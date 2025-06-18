package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminParametresController; // Le nouveau contrôleur
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

// Remplacez le contenu de votre AdminParametresView.java par ceci
public class AdminParametresView extends BaseView {

    private PasswordField newPasswordField, confirmPasswordField;
    private Button savePasswordButton, logoutButton;
    private Label emailLabel; // Pour afficher l'email

    public AdminParametresView(MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        // Le titre de la vue dans la navbar reste "Accueil" ou ce que vous préférez
        super(navigator, compte, "Accueil");
        // On crée le contrôleur spécifique à l'admin
        new AdminParametresController(this, navigator, compte, authService);
    }

    @Override
    protected Node createCenterContent() {
        // On utilise la classe CSS originale pour garder le même style
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

    private VBox createEmailInfoSection() {
        VBox sectionContainer = new VBox(5);
        sectionContainer.getStyleClass().add("settings-section");

        HBox emailLine = new HBox(10);
        emailLine.setAlignment(Pos.CENTER_LEFT);

        Label emailTitle = new Label("Compte Administrateur:");
        emailTitle.getStyleClass().add("section-title");

        emailLabel = new Label(); // Le contrôleur remplira ceci
        emailLabel.getStyleClass().add("info-text-main");

        emailLine.getChildren().addAll(emailTitle, emailLabel);
        sectionContainer.getChildren().add(emailLine);
        return sectionContainer;
    }

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

    // Getters et Setters pour le contrôleur
    public void setEmail(String email) {
        emailLabel.setText(email);
    }

    public String getNewPassword() {
        return newPasswordField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }

    public void setSavePasswordButtonAction(EventHandler<ActionEvent> handler) {
        savePasswordButton.setOnAction(handler);
    }

    public void setLogoutButtonAction(EventHandler<ActionEvent> handler) {
        logoutButton.setOnAction(handler);
    }
}