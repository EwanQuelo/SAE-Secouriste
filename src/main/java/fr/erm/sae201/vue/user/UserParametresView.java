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

/**
 * La vue des paramètres pour l'utilisateur secouriste.
 * Elle est organisée en deux colonnes : l'une pour la modification des informations personnelles
 * (nom, adresse, etc.) et l'autre pour les paramètres de sécurité (changement de mot de passe,
 * consultation de l'e-mail). Un bouton de déconnexion est également présent.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class UserParametresView extends BaseView {

    private TextField prenomField, nomField, emailField, telField, adresseField;
    private DatePicker dateNaissancePicker;
    private PasswordField newPasswordField, confirmPasswordField;
    private Button saveInfoButton, savePasswordButton, logoutButton;
    private final UserParametresController controller;

    /**
     * Construit la vue des paramètres de l'utilisateur.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'utilisateur connecté.
     * @param authService Le service d'authentification pour gérer les opérations liées au compte.
     */
    public UserParametresView(MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        super(navigator, compte, "Paramètres");
        this.controller = new UserParametresController(this, navigator, compte, authService);
    }

    /**
     * Crée et retourne le contenu central de la vue, structuré en deux colonnes.
     *
     * @return Le nœud (Node) contenant l'ensemble des panneaux de paramètres.
     */
    @Override
    protected Node createCenterContent() {
        VBox container = new VBox(30);
        container.getStyleClass().add("user-settings-view");
        container.setAlignment(Pos.TOP_CENTER);

        Text scenetitle = new Text("Paramètres du compte");
        scenetitle.getStyleClass().add("form-title");

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

    /**
     * Crée la section de gauche pour les informations personnelles.
     *
     * @return Un VBox contenant le formulaire des informations personnelles.
     */
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

    /**
     * Crée la section de droite pour les paramètres de sécurité.
     *
     * @return Un VBox contenant le formulaire de sécurité.
     */
    private VBox createSecuritySection() {
        VBox box = new VBox(20);
        box.getStyleClass().add("settings-section");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label title = new Label("Sécurité");
        title.getStyleClass().add("section-title");

        emailField = new TextField();
        // L'email sert d'identifiant unique et ne doit pas être modifiable par l'utilisateur.
        emailField.setEditable(false);
        emailField.setFocusTraversable(false);
        emailField.getStyleClass().addAll("settings-input", "disabled-input");
        
        VBox emailBox = new VBox(5, new Label("Adresse e-mail"), emailField);
        
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

    /**
     * Pré-remplit les champs du formulaire avec les données d'un secouriste.
     *
     * @param secouriste L'objet Secouriste dont les données doivent être affichées.
     */
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

    /**
     * Retourne le prénom saisi.
     * @return Le prénom.
     */
    public String getPrenom() { return prenomField.getText(); }
    
    /**
     * Retourne le nom saisi.
     * @return Le nom.
     */
    public String getNom() { return nomField.getText(); }
    
    /**
     * Retourne le numéro de téléphone saisi.
     * @return Le numéro de téléphone.
     */
    public String getTel() { return telField.getText(); }
    
    /**
     * Retourne l'adresse saisie.
     * @return L'adresse.
     */
    public String getAdresse() { return adresseField.getText(); }
    
    /**
     * Retourne la date de naissance sélectionnée.
     * @return La date de naissance.
     */
    public java.time.LocalDate getDateNaissance() { return dateNaissancePicker.getValue(); }
    
    /**
     * Retourne le nouveau mot de passe saisi.
     * @return Le nouveau mot de passe.
     */
    public String getNewPassword() { return newPasswordField.getText(); }
    
    /**
     * Retourne le mot de passe de confirmation saisi.
     * @return Le mot de passe de confirmation.
     */
    public String getConfirmPassword() { return confirmPasswordField.getText(); }

    /**
     * Associe une action au bouton de sauvegarde des informations personnelles.
     * @param handler Le gestionnaire d'événement.
     */
    public void setSaveInfoButtonAction(EventHandler<ActionEvent> handler) { saveInfoButton.setOnAction(handler); }
    
    /**
     * Associe une action au bouton de sauvegarde du mot de passe.
     * @param handler Le gestionnaire d'événement.
     */
    public void setSavePasswordButtonAction(EventHandler<ActionEvent> handler) { savePasswordButton.setOnAction(handler); }
    
    /**
     * Associe une action au bouton de déconnexion.
     * @param handler Le gestionnaire d'événement.
     */
    public void setLogoutButtonAction(EventHandler<ActionEvent> handler) { logoutButton.setOnAction(handler); }
}