package fr.erm.sae201.vue.user;

import fr.erm.sae201.controleur.user.UserParametresController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.time.ZoneId;

public class UserParametresView extends BaseView {

    private TextField prenomField, nomField, emailField, telField, adresseField;
    private DatePicker dateNaissancePicker;
    private PasswordField newPasswordField, confirmPasswordField;
    private Button saveInfoButton, savePasswordButton, logoutButton;
    private final UserParametresController controller;

    public UserParametresView(MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        super(navigator, compte, "Paramètres");
        this.controller = new UserParametresController(this, navigator, compte, authService);
    }

    @Override
    protected Node createCenterContent() {
        VBox container = new VBox(30);
        // ADD THE UNIQUE CSS CLASS HERE
        container.getStyleClass().add("user-settings-view");
        container.setAlignment(Pos.TOP_CENTER);

        Text scenetitle = new Text("Paramètres du compte");
        scenetitle.getStyleClass().add("form-title");

        // Use an HBox for the two-column layout
        HBox settingsColumns = new HBox(40);
        settingsColumns.setAlignment(Pos.TOP_CENTER);

        VBox personalInfoBox = createPersonalInfoSection();
        VBox securityBox = createSecuritySection();

        settingsColumns.getChildren().addAll(personalInfoBox, securityBox);
        
        logoutButton = new Button("Déconnexion");
        logoutButton.getStyleClass().add("logout-button");
        VBox.setMargin(logoutButton, new Insets(20, 0, 0, 0));

        container.getChildren().addAll(scenetitle, settingsColumns, logoutButton);
        
        return container;
    }

    private VBox createPersonalInfoSection() {
        VBox box = new VBox(20);
        box.getStyleClass().add("settings-section");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label title = new Label("Informations Personnelles");
        title.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        prenomField = new TextField();
        prenomField.setPromptText("Votre prénom");
        prenomField.getStyleClass().add("settings-input");
        
        nomField = new TextField();
        nomField.setPromptText("Votre nom");
        nomField.getStyleClass().add("settings-input");
        
        dateNaissancePicker = new DatePicker();
        dateNaissancePicker.getStyleClass().add("settings-input");
        
        telField = new TextField();
        telField.setPromptText("Numéro de téléphone");
        telField.getStyleClass().add("settings-input");
        
        adresseField = new TextField();
        adresseField.setPromptText("Votre adresse");
        adresseField.getStyleClass().add("settings-input");

        grid.add(new Label("Prénom:"), 0, 0);
        grid.add(prenomField, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(nomField, 1, 1);
        grid.add(new Label("Date de naissance:"), 0, 2);
        grid.add(dateNaissancePicker, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3);
        grid.add(telField, 1, 3);
        grid.add(new Label("Adresse:"), 0, 4);
        grid.add(adresseField, 1, 4);

        saveInfoButton = new Button("Enregistrer les informations");
        saveInfoButton.getStyleClass().add("save-button");
        HBox buttonContainer = new HBox(saveInfoButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        
        box.getChildren().addAll(title, grid, buttonContainer);
        VBox.setMargin(buttonContainer, new Insets(10, 0, 0, 0));
        return box;
    }

    private VBox createSecuritySection() {
        VBox box = new VBox(20);
        box.getStyleClass().add("settings-section");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label title = new Label("Sécurité");
        title.getStyleClass().add("section-title");

        emailField = new TextField();
        emailField.setEditable(false);
        emailField.setFocusTraversable(false);
        emailField.getStyleClass().addAll("settings-input", "disabled-input");
        
        VBox emailBox = new VBox(5, new Label("Adresse e-mail (non modifiable)"), emailField);
        
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        newPasswordField.getStyleClass().add("settings-input");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");
        confirmPasswordField.getStyleClass().add("settings-input");

        VBox passwordBox = new VBox(5, new Label("Changer de mot de passe"), newPasswordField, confirmPasswordField);
        
        savePasswordButton = new Button("Enregistrer le mot de passe");
        savePasswordButton.getStyleClass().add("save-button");
        HBox buttonContainer = new HBox(savePasswordButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        box.getChildren().addAll(title, emailBox, passwordBox, buttonContainer);
        VBox.setMargin(buttonContainer, new Insets(10, 0, 0, 0));
        return box;
    }

    // Getters and Setters remain the same, but for completeness:
    public void setSecouristeData(Secouriste secouriste) {
        prenomField.setText(secouriste.getPrenom());
        nomField.setText(secouriste.getNom());
        telField.setText(secouriste.getTel());
        adresseField.setText(secouriste.getAddresse());
        emailField.setText(secouriste.getEmail());
        if (secouriste.getDateNaissance() != null) {
            dateNaissancePicker.setValue(secouriste.getDateNaissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    public String getPrenom() { return prenomField.getText(); }
    public String getNom() { return nomField.getText(); }
    public String getTel() { return telField.getText(); }
    public String getAdresse() { return adresseField.getText(); }
    public java.time.LocalDate getDateNaissance() { return dateNaissancePicker.getValue(); }
    public String getNewPassword() { return newPasswordField.getText(); }
    public String getConfirmPassword() { return confirmPasswordField.getText(); }

    public void setSaveInfoButtonAction(EventHandler<ActionEvent> handler) { saveInfoButton.setOnAction(handler); }
    public void setSavePasswordButtonAction(EventHandler<ActionEvent> handler) { savePasswordButton.setOnAction(handler); }
    public void setLogoutButtonAction(EventHandler<ActionEvent> handler) { logoutButton.setOnAction(handler); }
}