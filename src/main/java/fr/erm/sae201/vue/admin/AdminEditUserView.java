package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminEditUserController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class AdminEditUserView extends BaseView {

    // MODIFICATION : L'email est maintenant un TextField
    private TextField prenomField, nomField, emailField, telField, adresseField;
    private DatePicker dateNaissancePicker;
    private VBox competencesContainer;
    private Button saveButton, cancelButton;
    private final Map<String, CheckBox> competenceCheckBoxes = new HashMap<>();
    private final CompteUtilisateur adminCompte;

    public AdminEditUserView(MainApp navigator, CompteUtilisateur adminCompte, Secouriste secouristeToEdit) {
        super(navigator, adminCompte, "Utilisateurs");
        this.adminCompte = adminCompte;
        new AdminEditUserController(this, navigator, secouristeToEdit);
    }

    @Override
    protected Node createCenterContent() {
        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30)); // Plus de padding
        mainContainer.getStyleClass().add("admin-form-container");
        mainContainer.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Modifier un utilisateur");
        title.getStyleClass().add("admin-title");

        HBox formSplitter = new HBox(40);
        formSplitter.setAlignment(Pos.TOP_CENTER);
        
        Node leftPanel = createUserInfoPanel();
        Node rightPanel = createCompetencesPanel();

        formSplitter.getChildren().addAll(leftPanel, rightPanel);
        
        HBox buttonBar = createButtonBar();
        
        mainContainer.getChildren().addAll(title, formSplitter, buttonBar);
        VBox.setVgrow(formSplitter, Priority.ALWAYS);

        // Enveloppe pour contrôler la largeur
        VBox wrapper = new VBox(mainContainer);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    private Node createUserInfoPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPrefWidth(450);

        grid.add(new Label("Prénom :"), 0, 0);
        prenomField = new TextField();
        grid.add(prenomField, 1, 0);

        grid.add(new Label("Nom :"), 0, 1);
        nomField = new TextField();
        grid.add(nomField, 1, 1);
        
        // MODIFICATION : Utilisation d'un TextField non-éditable
        grid.add(new Label("Email :"), 0, 2);
        emailField = new TextField();
        emailField.setEditable(false);
        emailField.setFocusTraversable(false); // Empêche la sélection par tabulation
        grid.add(emailField, 1, 2);

        grid.add(new Label("Téléphone :"), 0, 3);
        telField = new TextField();
        grid.add(telField, 1, 3);

        grid.add(new Label("Adresse :"), 0, 4);
        adresseField = new TextField();
        grid.add(adresseField, 1, 4);

        grid.add(new Label("Date de naissance :"), 0, 5);
        dateNaissancePicker = new DatePicker();
        grid.add(dateNaissancePicker, 1, 5);
        
        // Application du style
        prenomField.getStyleClass().add("settings-input");
        nomField.getStyleClass().add("settings-input");
        emailField.getStyleClass().add("settings-input");
        telField.getStyleClass().add("settings-input");
        adresseField.getStyleClass().add("settings-input");
        dateNaissancePicker.getStyleClass().add("settings-input");
        
        VBox container = new VBox(20, grid);
        container.getStyleClass().add("settings-section");
        HBox.setHgrow(container, Priority.ALWAYS);
        return container;
    }

    private Node createCompetencesPanel() {
        VBox container = new VBox(15);
        container.getStyleClass().add("settings-section");
        HBox.setHgrow(container, Priority.ALWAYS);

        Label title = new Label("Compétences Possédées");
        title.getStyleClass().add("section-title");

        competencesContainer = new VBox(12);
        
        ScrollPane scrollPane = new ScrollPane(competencesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("content-scroll-pane");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().addAll(title, scrollPane);
        return container;
    }

    private HBox createButtonBar() {
        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(buttonBar, new Insets(20, 0, 0, 0));
        
        cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("cancel-button");

        saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("save-button");

        buttonBar.getChildren().addAll(cancelButton, saveButton);
        return buttonBar;
    }

    // Méthodes pour le contrôleur
    public void setSecouristeData(Secouriste secouriste) {
        prenomField.setText(secouriste.getPrenom());
        nomField.setText(secouriste.getNom());
        emailField.setText(secouriste.getEmail()); // Affectation au TextField
        telField.setText(secouriste.getTel());
        adresseField.setText(secouriste.getAddresse());
        if (secouriste.getDateNaissance() != null) {
            dateNaissancePicker.setValue(secouriste.getDateNaissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    public void populateCompetences(List<Competence> allCompetences, Set<Competence> userCompetences) {
        competencesContainer.getChildren().clear();
        competenceCheckBoxes.clear();
        for (Competence competence : allCompetences) {
            CheckBox cb = new CheckBox(competence.getIntitule());
            cb.getStyleClass().add("competence-checkbox");
            cb.setSelected(userCompetences.contains(competence));
            competencesContainer.getChildren().add(cb);
            competenceCheckBoxes.put(competence.getIntitule(), cb);
        }
    }
    
    public CompteUtilisateur getCompte() { return this.adminCompte; }
    public String getPrenom() { return prenomField.getText(); }
    public String getNom() { return nomField.getText(); }
    public String getTel() { return telField.getText(); }
    public String getAdresse() { return adresseField.getText(); }
    public java.time.LocalDate getDateNaissance() { return dateNaissancePicker.getValue(); }
    public Map<String, CheckBox> getCompetenceCheckBoxes() { return competenceCheckBoxes; }

    public void setSaveButtonAction(EventHandler<ActionEvent> event) {
        saveButton.setOnAction(event);
    }
    public void setCancelButtonAction(EventHandler<ActionEvent> event) {
        cancelButton.setOnAction(event);
    }
}