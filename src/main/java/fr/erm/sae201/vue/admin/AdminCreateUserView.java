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

/**
 * Vue pour la création d'un nouvel utilisateur (secouriste) par un administrateur.
 * 
 * Cette vue présente un formulaire divisé en deux sections : les informations
 * personnelles et la sélection des compétences. Elle gère également la logique
 * de dépendance entre les compétences cochées (un prérequis est automatiquement
 * coché si une compétence supérieure est sélectionnée).
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminCreateUserView extends BaseView {

    private TextField prenomField, nomField, emailField, telField, adresseField;
    private PasswordField passwordField;
    private DatePicker dateNaissancePicker;
    /** Conteneur pour les cases à cocher des compétences. */
    private VBox competencesContainer;
    private Button saveButton, cancelButton;
    /** Map liant chaque compétence à sa case à cocher pour un accès facile. */
    private final Map<Competence, CheckBox> competenceCheckBoxes = new HashMap<>();
    private final CompteUtilisateur adminCompte;

    /**
     * Constructeur de la vue de création d'utilisateur.
     *
     * @param navigator   Le navigateur principal.
     * @param adminCompte Le compte de l'administrateur connecté.
     */
    public AdminCreateUserView(MainApp navigator, CompteUtilisateur adminCompte) {
        super(navigator, adminCompte, "Utilisateurs");
        this.adminCompte = adminCompte;
        new AdminCreateUserController(this, navigator);
    }

    /**
     * Crée le contenu central de la vue, qui est un formulaire de création.
     *
     * @return Le nœud racine du contenu.
     */
    @Override
    protected Node createCenterContent() {
        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
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

    /**
     * Crée le panneau contenant les champs d'informations personnelles de l'utilisateur.
     *
     * @return Un nœud contenant le formulaire des informations personnelles.
     */
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

    /**
     * Crée le panneau contenant la liste des compétences à assigner.
     *
     * @return Un nœud contenant la sélection des compétences.
     */
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

    /**
     * Crée la barre de boutons contenant "Annuler" et "Créer l'utilisateur".
     *
     * @return Un HBox contenant les boutons d'action.
     */
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

    /**
     * Peuple la liste des compétences avec des cases à cocher.
     *
     * @param allCompetences La liste de toutes les compétences disponibles.
     */
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

    /**
     * Met à jour l'état des cases à cocher en fonction des dépendances.
     * Si une compétence est sélectionnée, tous ses prérequis sont automatiquement
     * sélectionnés et désactivés pour empêcher l'utilisateur de les décocher.
     */
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
    
    /**
     * Retourne le compte de l'administrateur actuellement connecté.
     * @return Le compte administrateur.
     */
    public CompteUtilisateur getCompte() { return this.adminCompte; }
    /**
     * Retourne le prénom saisi dans le champ de texte.
     * @return Le prénom.
     */
    public String getPrenom() { return prenomField.getText(); }
    /**
     * Retourne le nom saisi dans le champ de texte.
     * @return Le nom.
     */
    public String getNom() { return nomField.getText(); }
    /**
     * Retourne l'email saisi dans le champ de texte.
     * @return L'email.
     */
    public String getEmail() { return emailField.getText(); }
    /**
     * Retourne le mot de passe saisi dans le champ de texte.
     * @return Le mot de passe.
     */
    public String getPassword() { return passwordField.getText(); }
    /**
     * Retourne le téléphone saisi dans le champ de texte.
     * @return Le téléphone.
     */
    public String getTel() { return telField.getText(); }
    /**
     * Retourne l'adresse saisie dans le champ de texte.
     * @return L'adresse.
     */
    public String getAdresse() { return adresseField.getText(); }
    /**
     * Retourne la date de naissance sélectionnée.
     * @return La date de naissance.
     */
    public LocalDate getDateNaissance() { return dateNaissancePicker.getValue(); }
    /**
     * Retourne la map des compétences et de leurs cases à cocher associées.
     * @return La map des compétences.
     */
    public Map<Competence, CheckBox> getCompetenceCheckBoxes() {
        return competenceCheckBoxes;
    }
    
    /**
     * Définit l'action pour le bouton de sauvegarde.
     * @param event Le gestionnaire d'événement.
     */
    public void setSaveButtonAction(EventHandler<ActionEvent> event) { saveButton.setOnAction(event); }
    /**
     * Définit l'action pour le bouton d'annulation.
     * @param event Le gestionnaire d'événement.
     */
    public void setCancelButtonAction(EventHandler<ActionEvent> event) { cancelButton.setOnAction(event); }
}