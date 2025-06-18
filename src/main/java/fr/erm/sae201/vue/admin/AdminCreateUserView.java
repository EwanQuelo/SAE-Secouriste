package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminCreateUserController;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminCreateUserView extends BaseView {

    private TextField prenomField, nomField, emailField, telField, adresseField;
    private PasswordField passwordField;
    private DatePicker dateNaissancePicker;
    private VBox competencesContainer;
    private Button saveButton, cancelButton;
    private final Map<Competence, CheckBox> competenceCheckBoxes = new HashMap<>();
    private final CompteUtilisateur adminCompte;

    public AdminCreateUserView(MainApp navigator, CompteUtilisateur adminCompte) {
        super(navigator, adminCompte, "Utilisateurs");
        this.adminCompte = adminCompte;
        new AdminCreateUserController(this, navigator);
    }

    @Override
    protected Node createCenterContent() {
        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        // On réutilise la même classe CSS que la vue de modification
        mainContainer.getStyleClass().add("admin-form-container"); 
        mainContainer.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Créer un nouvel utilisateur");
        title.getStyleClass().add("admin-title");

        HBox formSplitter = new HBox(40);
        formSplitter.setAlignment(Pos.TOP_CENTER);
        
        Node leftPanel = createUserInfoPanel();
        Node rightPanel = createCompetencesPanel();

        formSplitter.getChildren().addAll(leftPanel, rightPanel);
        
        HBox buttonBar = createButtonBar();
        
        mainContainer.getChildren().addAll(title, formSplitter, buttonBar);
        VBox.setVgrow(formSplitter, Priority.ALWAYS);

        VBox wrapper = new VBox(mainContainer);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    private Node createUserInfoPanel() {
        VBox container = new VBox(20);
        container.getStyleClass().add("settings-section");

        Label sectionTitle = new Label("Informations Personnelles");
        sectionTitle.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);

        grid.add(new Label("Prénom :"), 0, 0);
        prenomField = new TextField();
        grid.add(prenomField, 1, 0);

        grid.add(new Label("Nom :"), 0, 1);
        nomField = new TextField();
        grid.add(nomField, 1, 1);
        
        grid.add(new Label("Email :"), 0, 2);
        emailField = new TextField();
        grid.add(emailField, 1, 2);

        grid.add(new Label("Mot de passe :"), 0, 3);
        passwordField = new PasswordField();
        grid.add(passwordField, 1, 3);

        grid.add(new Label("Téléphone :"), 0, 4);
        telField = new TextField();
        grid.add(telField, 1, 4);

        grid.add(new Label("Adresse :"), 0, 5);
        adresseField = new TextField();
        grid.add(adresseField, 1, 5);

        grid.add(new Label("Date de naissance :"), 0, 6);
        dateNaissancePicker = new DatePicker();
        grid.add(dateNaissancePicker, 1, 6);
        
        // Appliquer le style à tous les champs
        prenomField.getStyleClass().add("settings-input");
        nomField.getStyleClass().add("settings-input");
        emailField.getStyleClass().add("settings-input");
        passwordField.getStyleClass().add("settings-input");
        telField.getStyleClass().add("settings-input");
        adresseField.getStyleClass().add("settings-input");
        dateNaissancePicker.getStyleClass().add("settings-input");
        
        container.getChildren().addAll(sectionTitle, grid);
        HBox.setHgrow(container, Priority.ALWAYS);
        return container;
    }

    private Node createCompetencesPanel() {
        VBox container = new VBox(15);
        container.getStyleClass().add("settings-section");

        Label title = new Label("Compétences (Optionnel)");
        title.getStyleClass().add("section-title");

        competencesContainer = new VBox(12);
        
        ScrollPane scrollPane = new ScrollPane(competencesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().addAll(title, scrollPane);
        HBox.setHgrow(container, Priority.ALWAYS);
        return container;
    }

    private HBox createButtonBar() {
        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(buttonBar, new Insets(20, 0, 0, 0));
        
        cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("cancel-button");

        saveButton = new Button("Créer l'utilisateur");
        saveButton.getStyleClass().add("save-button");

        buttonBar.getChildren().addAll(cancelButton, saveButton);
        return buttonBar;
    }

    // Méthodes pour le contrôleur
    public void populateCompetences(List<Competence> allCompetences) {
        competencesContainer.getChildren().clear();
        competenceCheckBoxes.clear();
        for (Competence competence : allCompetences) {
            CheckBox cb = new CheckBox(competence.getIntitule());
            cb.getStyleClass().add("competence-checkbox");
            
            cb.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                updateCompetenceDependencies();
            });
            
            competencesContainer.getChildren().add(cb);
            competenceCheckBoxes.put(competence, cb);
        }
    }

    private void updateCompetenceDependencies() {
        Set<Competence> allRequiredPrerequisites = new HashSet<>();
        for (Map.Entry<Competence, CheckBox> entry : competenceCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                allRequiredPrerequisites.addAll(entry.getKey().getPrerequisites());
            }
        }

        for (Map.Entry<Competence, CheckBox> entry : competenceCheckBoxes.entrySet()) {
            Competence competence = entry.getKey();
            CheckBox checkBox = entry.getValue();

            if (allRequiredPrerequisites.contains(competence)) {
                checkBox.setSelected(true);
                checkBox.setDisable(true);
            } else {
                checkBox.setDisable(false);
            }
        }
    }
    
    public CompteUtilisateur getCompte() { return this.adminCompte; }
    public String getPrenom() { return prenomField.getText(); }
    public String getNom() { return nomField.getText(); }
    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public String getTel() { return telField.getText(); }
    public String getAdresse() { return adresseField.getText(); }
    public LocalDate getDateNaissance() { return dateNaissancePicker.getValue(); }
    public Map<Competence, CheckBox> getCompetenceCheckBoxes() {
        return competenceCheckBoxes;
    }
    
    public void setSaveButtonAction(EventHandler<ActionEvent> event) { saveButton.setOnAction(event); }
    public void setCancelButtonAction(EventHandler<ActionEvent> event) { cancelButton.setOnAction(event); }
}