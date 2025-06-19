package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminEditDpsController;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Site;
import fr.erm.sae201.metier.persistence.Sport;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * La vue pour la création ou la modification d'un Dispositif Prévisionnel de Secours (DPS).
 * Elle présente un formulaire pour saisir les informations du DPS (site, sport, date, horaires)
 * ainsi qu'une section pour définir les besoins en compétences (nombre de secouristes par compétence).
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminEditDpsView extends BaseView {

    private final CompteUtilisateur compte;
    private ComboBox<Site> siteComboBox;
    private ComboBox<Sport> sportComboBox;
    private DatePicker datePicker;
    private TextField startHourField, startMinuteField, endHourField, endMinuteField;
    private Button saveButton, cancelButton;
    private Label titleLabel;
    private Button addSiteButton, addSportButton;

    private VBox requirementsContainer;

    /**
     * Constructeur de la vue d'édition de DPS.
     * Initialise les composants de la vue et lie celle-ci à son contrôleur.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'utilisateur actuellement connecté.
     * @param dpsToEdit Le DPS à modifier. Si null, la vue est en mode création.
     */
    public AdminEditDpsView(MainApp navigator, CompteUtilisateur compte, DPS dpsToEdit) {
        super(navigator, compte, "Dispositifs");
        this.compte = compte;
        new AdminEditDpsController(this, navigator, dpsToEdit);
    }

    /**
     * Crée et retourne le contenu central de la vue.
     *
     * @return Le nœud (Node) contenant le formulaire principal et la section des compétences.
     */
    @Override
    protected Node createCenterContent() {
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(25));
        formContainer.getStyleClass().add("admin-form-container");

        titleLabel = new Label();
        titleLabel.getStyleClass().add("admin-title");

        HBox mainSplit = new HBox(40);
        mainSplit.setAlignment(Pos.TOP_LEFT);

        Node mainForm = createMainForm();
        Node requirementsForm = createRequirementsForm();

        mainSplit.getChildren().addAll(mainForm, requirementsForm);

        HBox buttonBar = createButtonBar();

        formContainer.getChildren().addAll(titleLabel, mainSplit, buttonBar);
        return formContainer;
    }

    /**
     * Crée la partie principale du formulaire pour les détails du DPS.
     *
     * @return Le nœud (Node) contenant les champs pour le site, le sport, la date et les horaires.
     */
    private Node createMainForm() {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER_LEFT);

        formGrid.add(new Label("Site de l'épreuve :"), 0, 0);
        siteComboBox = new ComboBox<>();
        siteComboBox.setPrefWidth(300);
        addSiteButton = new Button("+");
        addSiteButton.getStyleClass().add("add-button-small");
        HBox siteBox = new HBox(10, siteComboBox, addSiteButton);
        siteBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(siteBox, 1, 0);

        formGrid.add(new Label("Sport concerné :"), 0, 1);
        sportComboBox = new ComboBox<>();
        sportComboBox.setPrefWidth(300);
        addSportButton = new Button("+");
        addSportButton.getStyleClass().add("add-button-small");
        HBox sportBox = new HBox(10, sportComboBox, addSportButton);
        sportBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(sportBox, 1, 1);

        formGrid.add(new Label("Date :"), 0, 2);
        datePicker = new DatePicker();
        formGrid.add(datePicker, 1, 2);

        formGrid.add(new Label("Heure de début (HH:MM) :"), 0, 3);
        HBox startTimeBox = new HBox(5);
        startTimeBox.setAlignment(Pos.CENTER_LEFT);
        startHourField = new TextField();
        startHourField.setPromptText("HH");
        startMinuteField = new TextField();
        startMinuteField.setPromptText("MM");
        startTimeBox.getChildren().addAll(startHourField, new Label(":"), startMinuteField);
        formGrid.add(startTimeBox, 1, 3);

        formGrid.add(new Label("Heure de fin (HH:MM) :"), 0, 4);
        HBox endTimeBox = new HBox(5);
        endTimeBox.setAlignment(Pos.CENTER_LEFT);
        endHourField = new TextField();
        endHourField.setPromptText("HH");
        endMinuteField = new TextField();
        endMinuteField.setPromptText("MM");
        endTimeBox.getChildren().addAll(endHourField, new Label(":"), endMinuteField);
        formGrid.add(endTimeBox, 1, 4);

        return formGrid;
    }

    /**
     * Crée la section du formulaire dédiée à la définition des besoins en compétences.
     *
     * @return Le nœud (Node) contenant la liste des contrôles pour chaque compétence.
     */
    private Node createRequirementsForm() {
        VBox container = new VBox(15);
        HBox.setHgrow(container, Priority.ALWAYS);
        container.getStyleClass().add("requirements-panel");

        Label reqTitle = new Label("Besoins en Compétences");
        reqTitle.getStyleClass().add("section-title");

        requirementsContainer = new VBox(10);

        ScrollPane scrollPane = new ScrollPane(requirementsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        scrollPane.getStyleClass().add("content-scroll-pane");

        container.getChildren().addAll(reqTitle, scrollPane);
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
        saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("save-button");
        cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("cancel-button");
        buttonBar.getChildren().addAll(cancelButton, saveButton);
        return buttonBar;
    }

    /**
     * Peuple la liste des besoins en compétences dans l'interface.
     *
     * @param allCompetences La liste de toutes les compétences disponibles.
     * @param existingRequirements Une map des compétences déjà requises et leur nombre pour le DPS en cours d'édition.
     */
    public void populateRequirements(List<Competence> allCompetences, Map<Competence, Integer> existingRequirements) {
        requirementsContainer.getChildren().clear();
        for (Competence competence : allCompetences) {
            // Utilise getOrDefault pour fournir une valeur de 0 si la compétence n'est pas encore requise.
            int initialValue = existingRequirements.getOrDefault(competence, 0);
            CompetenceRequirementControl control = new CompetenceRequirementControl(competence, initialValue);
            requirementsContainer.getChildren().add(control);
        }
    }

    /**
     * Récupère les besoins en compétences saisis par l'utilisateur.
     *
     * @return Une map associant chaque compétence au nombre de secouristes requis.
     */
    public Map<Competence, Integer> getCompetenceRequirements() {
        Map<Competence, Integer> requirements = new HashMap<>();
        for (Node node : requirementsContainer.getChildren()) {
            if (node instanceof CompetenceRequirementControl control) {
                requirements.put(control.getCompetence(), control.getRequiredNumber());
            }
        }
        return requirements;
    }

    /**
     * Retourne le compte de l'utilisateur connecté.
     *
     * @return Le compte utilisateur.
     */
    public CompteUtilisateur getCompte() {
        return this.compte;
    }

    /**
     * Affiche une boîte de dialogue pour créer un nouveau site.
     *
     * @return Un Optional contenant le nouveau Site s'il a été créé, sinon un Optional vide.
     */
    public Optional<Site> showCreateSiteDialog() {
        Dialog<Site> dialog = new Dialog<>();
        dialog.setTitle("Créer un nouveau Site");
        dialog.setHeaderText("Veuillez remplir les informations pour le nouveau site.");
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField codeField = new TextField();
        codeField.setPromptText("Ex: CRCHV");
        TextField nomField = new TextField();
        nomField.setPromptText("Ex: Courchevel");
        TextField lonField = new TextField();
        lonField.setPromptText("Ex: 6.6335");
        TextField latField = new TextField();
        latField.setPromptText("Ex: 45.4153");
        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(nomField, 1, 1);
        grid.add(new Label("Longitude:"), 0, 2);
        grid.add(lonField, 1, 2);
        grid.add(new Label("Latitude:"), 0, 3);
        grid.add(latField, 1, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    return new Site(codeField.getText(), nomField.getText(), Float.parseFloat(lonField.getText()),
                            Float.parseFloat(latField.getText()));
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });
        return dialog.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue pour créer un nouveau sport.
     *
     * @return Un Optional contenant le nouveau Sport s'il a été créé, sinon un Optional vide.
     */
    public Optional<Sport> showCreateSportDialog() {
        Dialog<Sport> dialog = new Dialog<>();
        dialog.setTitle("Créer un nouveau Sport");
        dialog.setHeaderText("Veuillez remplir les informations pour le nouveau sport.");
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField codeField = new TextField();
        codeField.setPromptText("Ex: SKI-DH");
        TextField nomField = new TextField();
        nomField.setPromptText("Ex: Ski Alpin - Descente");
        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(nomField, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Sport(codeField.getText(), nomField.getText());
            }
            return null;
        });
        return dialog.showAndWait();
    }

    /**
     * Remplit la liste déroulante des sites avec les données fournies.
     *
     * @param sites La liste des sites à afficher.
     */
    public void populateSiteComboBox(List<Site> sites) {
        Site selected = siteComboBox.getValue();
        siteComboBox.setItems(FXCollections.observableArrayList(sites));
        if (selected != null)
            siteComboBox.setValue(selected);
        siteComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Site item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        siteComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Site item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
    }

    /**
     * Remplit la liste déroulante des sports avec les données fournies.
     *
     * @param sports La liste des sports à afficher.
     */
    public void populateSportComboBox(List<Sport> sports) {
        Sport selected = sportComboBox.getValue();
        sportComboBox.setItems(FXCollections.observableArrayList(sports));
        if (selected != null)
            sportComboBox.setValue(selected);
        sportComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Sport item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        sportComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Sport item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
    }

    /**
     * Définit le titre du formulaire.
     *
     * @param title Le titre à afficher.
     */
    public void setFormTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Pré-remplit les champs du formulaire avec les données d'un DPS existant.
     *
     * @param dps Le DPS dont les données sont utilisées pour remplir le formulaire.
     */
    public void setDpsData(DPS dps) {
        siteComboBox.setValue(dps.getSite());
        sportComboBox.setValue(dps.getSport());
        datePicker.setValue(dps.getJournee().getDate());
        startHourField.setText(String.format("%02d", dps.getHoraireDepart()[0]));
        startMinuteField.setText(String.format("%02d", dps.getHoraireDepart()[1]));
        endHourField.setText(String.format("%02d", dps.getHoraireFin()[0]));
        endMinuteField.setText(String.format("%02d", dps.getHoraireFin()[1]));
    }

    /**
     * Récupère le site sélectionné dans la liste déroulante.
     *
     * @return Le site sélectionné.
     */
    public Site getSelectedSite() {
        return siteComboBox.getValue();
    }

    /**
     * Récupère le sport sélectionné dans la liste déroulante.
     *
     * @return Le sport sélectionné.
     */
    public Sport getSelectedSport() {
        return sportComboBox.getValue();
    }

    /**
     * Récupère la date sélectionnée dans le sélecteur de date.
     *
     * @return La date sélectionnée.
     */
    public LocalDate getSelectedDate() {
        return datePicker.getValue();
    }

    /**
     * Récupère l'heure de début saisie.
     *
     * @return La chaîne de caractères de l'heure de début.
     */
    public String getStartHour() {
        return startHourField.getText();
    }

    /**
     * Récupère les minutes de début saisies.
     *
     * @return La chaîne de caractères des minutes de début.
     */
    public String getStartMinute() {
        return startMinuteField.getText();
    }

    /**
     * Récupère l'heure de fin saisie.
     *
     * @return La chaîne de caractères de l'heure de fin.
     */
    public String getEndHour() {
        return endHourField.getText();
    }

    /**
     * Récupère les minutes de fin saisies.
     *
     * @return La chaîne de caractères des minutes de fin.
     */
    public String getEndMinute() {
        return endMinuteField.getText();
    }

    /**
     * Associe une action au bouton d'enregistrement.
     *
     * @param event Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setSaveButtonAction(EventHandler<ActionEvent> event) {
        saveButton.setOnAction(event);
    }

    /**
     * Associe une action au bouton d'annulation.
     *
     * @param event Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setCancelButtonAction(EventHandler<ActionEvent> event) {
        cancelButton.setOnAction(event);
    }

    /**
     * Associe une action au bouton d'ajout de site.
     *
     * @param event Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setAddSiteAction(EventHandler<ActionEvent> event) {
        addSiteButton.setOnAction(event);
    }

    /**
     * Associe une action au bouton d'ajout de sport.
     *
     * @param event Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setAddSportAction(EventHandler<ActionEvent> event) {
        addSportButton.setOnAction(event);
    }
}