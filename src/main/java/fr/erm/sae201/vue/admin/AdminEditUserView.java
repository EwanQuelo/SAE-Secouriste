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
import java.util.HashSet;

/**
 * La vue permettant à un administrateur de modifier les informations d'un utilisateur (secouriste).
 * Elle présente un formulaire pour les détails personnels (nom, contact, etc.) et un panneau
 * pour l'attribution et la gestion des compétences de l'utilisateur, avec une logique de
 * gestion des prérequis.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminEditUserView extends BaseView {

    private TextField prenomField, nomField, emailField, telField, adresseField;
    private DatePicker dateNaissancePicker;
    private VBox competencesContainer;
    private Button saveButton, cancelButton;
    private final Map<Competence, CheckBox> competenceCheckBoxes = new HashMap<>();
    private final CompteUtilisateur adminCompte;
    

    /**
     * Constructeur de la vue d'édition d'utilisateur.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param adminCompte Le compte de l'administrateur connecté.
     * @param secouristeToEdit Le secouriste dont le profil est à modifier.
     */
    public AdminEditUserView(MainApp navigator, CompteUtilisateur adminCompte, Secouriste secouristeToEdit) {
        super(navigator, adminCompte, "Utilisateurs");
        this.adminCompte = adminCompte;
        new AdminEditUserController(this, navigator, secouristeToEdit);
    }

    /**
     * Crée et retourne le contenu central de la vue, composé des panneaux d'informations
     * et de compétences.
     *
     * @return Le nœud (Node) racine du contenu central.
     */
    @Override
    protected Node createCenterContent() {
        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
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

        VBox wrapper = new VBox(mainContainer);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    /**
     * Crée le panneau de gauche contenant les champs d'informations personnelles de l'utilisateur.
     *
     * @return Le nœud (Node) du panneau d'informations.
     */
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
        
        grid.add(new Label("Email :"), 0, 2);
        emailField = new TextField();
        emailField.setEditable(false); // L'email n'est pas modifiable, car il sert d'identifiant unique.
        emailField.setFocusTraversable(false);
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

    /**
     * Crée le panneau de droite contenant la liste des compétences à cocher.
     *
     * @return Le nœud (Node) du panneau des compétences.
     */
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

    /**
     * Crée la barre de boutons "Enregistrer" et "Annuler".
     *
     * @return Le conteneur (HBox) avec les boutons d'action.
     */
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

    /**
     * Pré-remplit les champs du formulaire avec les données d'un secouriste.
     *
     * @param secouriste Le secouriste dont les informations doivent être affichées.
     */
    public void setSecouristeData(Secouriste secouriste) {
        prenomField.setText(secouriste.getPrenom());
        nomField.setText(secouriste.getNom());
        emailField.setText(secouriste.getEmail());
        telField.setText(secouriste.getTel());
        adresseField.setText(secouriste.getAddresse());
        if (secouriste.getDateNaissance() != null) {
            dateNaissancePicker.setValue(secouriste.getDateNaissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    /**
     * Crée et affiche la liste des compétences sous forme de cases à cocher,
     * en cochant celles que l'utilisateur possède déjà.
     *
     * @param allCompetences La liste de toutes les compétences disponibles.
     * @param userCompetences L'ensemble des compétences que le secouriste possède.
     */
    public void populateCompetences(List<Competence> allCompetences, Set<Competence> userCompetences) {
        competencesContainer.getChildren().clear();
        competenceCheckBoxes.clear();

        for (Competence competence : allCompetences) {
            CheckBox cb = new CheckBox(competence.getIntitule());
            cb.getStyleClass().add("competence-checkbox");
            
            if (userCompetences.contains(competence)) {
                cb.setSelected(true);
            }
            
            cb.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                updateCompetenceDependencies();
            });
            
            competencesContainer.getChildren().add(cb);
            competenceCheckBoxes.put(competence, cb);
        }

        // Appelle la méthode une fois à l'initialisation pour définir l'état des dépendances.
        updateCompetenceDependencies();
    }

    /**
     * Met à jour l'état des cases à cocher en fonction des dépendances entre compétences.
     * Si une compétence sélectionnée requiert un prérequis, la case du prérequis est
     * automatiquement cochée et désactivée pour empêcher sa désélection.
     */
    private void updateCompetenceDependencies() {
        // Étape 1 : Récupérer l'ensemble de tous les prérequis nécessaires pour les compétences actuellement sélectionnées.
        Set<Competence> allRequiredPrerequisites = new HashSet<>();
        for (Map.Entry<Competence, CheckBox> entry : competenceCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                allRequiredPrerequisites.addAll(entry.getKey().getPrerequisites());
            }
        }

        // Étape 2 : Parcourir toutes les cases à cocher pour appliquer les règles de dépendance.
        for (Map.Entry<Competence, CheckBox> entry : competenceCheckBoxes.entrySet()) {
            Competence competence = entry.getKey();
            CheckBox checkBox = entry.getValue();

            // Si cette compétence est un prérequis pour une autre compétence déjà cochée...
            if (allRequiredPrerequisites.contains(competence)) {
                checkBox.setSelected(true); // ...elle doit être cochée...
                checkBox.setDisable(true);   // ...et on la verrouille pour ne pas pouvoir la décocher.
            } else {
                checkBox.setDisable(false); // ...sinon, elle reste modifiable par l'utilisateur.
            }
        }
    }


    /**
     * Retourne le compte de l'administrateur connecté.
     * @return Le compte utilisateur de l'administrateur.
     */
    public CompteUtilisateur getCompte() { return this.adminCompte; }
    
    /**
     * Retourne le prénom saisi dans le champ de texte.
     * @return Le prénom de l'utilisateur.
     */
    public String getPrenom() { return prenomField.getText(); }
    
    /**
     * Retourne le nom saisi dans le champ de texte.
     * @return Le nom de l'utilisateur.
     */
    public String getNom() { return nomField.getText(); }
    
    /**
     * Retourne le numéro de téléphone saisi dans le champ de texte.
     * @return Le numéro de téléphone de l'utilisateur.
     */
    public String getTel() { return telField.getText(); }
    
    /**
     * Retourne l'adresse saisie dans le champ de texte.
     * @return L'adresse de l'utilisateur.
     */
    public String getAdresse() { return adresseField.getText(); }
    
    /**
     * Retourne la date de naissance sélectionnée.
     * @return La date de naissance de l'utilisateur.
     */
    public java.time.LocalDate getDateNaissance() { return dateNaissancePicker.getValue(); }
    
    /**
     * Retourne la map associant chaque compétence à sa case à cocher.
     * @return Une map de compétences et de leurs CheckBox associées.
     */
    public Map<Competence, CheckBox> getCompetenceCheckBoxes() {
        return competenceCheckBoxes;
    }
    
    /**
     * Associe une action au bouton d'enregistrement.
     * @param event Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setSaveButtonAction(EventHandler<ActionEvent> event) {
        saveButton.setOnAction(event);
    }
    
    /**
     * Associe une action au bouton d'annulation.
     * @param event Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setCancelButtonAction(EventHandler<ActionEvent> event) {
        cancelButton.setOnAction(event);
    }
}