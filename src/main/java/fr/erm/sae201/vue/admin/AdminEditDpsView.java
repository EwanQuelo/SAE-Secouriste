package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminEditDpsController;
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
import java.util.List;
import java.util.Optional;

public class AdminEditDpsView extends BaseView {

    private final CompteUtilisateur compte;
    private ComboBox<Site> siteComboBox;
    private ComboBox<Sport> sportComboBox;
    private DatePicker datePicker;
    private TextField startHourField, startMinuteField, endHourField, endMinuteField;
    private Button saveButton, cancelButton;
    private Label titleLabel;
    private Button addSiteButton, addSportButton;

    public AdminEditDpsView(MainApp navigator, CompteUtilisateur compte, DPS dpsToEdit) {
        super(navigator, compte, "Dispositifs");
        this.compte = compte;
        new AdminEditDpsController(this, navigator, dpsToEdit);
    }

    @Override
    protected Node createCenterContent() {
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(25));
        formContainer.getStyleClass().add("admin-form-container");

        titleLabel = new Label();
        titleLabel.getStyleClass().add("admin-title");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER_LEFT);

        // Ligne 1: Site
        formGrid.add(new Label("Site de l'épreuve :"), 0, 0);
        siteComboBox = new ComboBox<>();
        siteComboBox.setPrefWidth(300);
        addSiteButton = new Button("+");
        addSiteButton.getStyleClass().add("add-button-small");
        HBox siteBox = new HBox(10, siteComboBox, addSiteButton);
        siteBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(siteBox, 1, 0);

        // Ligne 2: Sport
        formGrid.add(new Label("Sport concerné :"), 0, 1);
        sportComboBox = new ComboBox<>();
        sportComboBox.setPrefWidth(300);
        addSportButton = new Button("+");
        addSportButton.getStyleClass().add("add-button-small");
        HBox sportBox = new HBox(10, sportComboBox, addSportButton);
        sportBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(sportBox, 1, 1);

        // Ligne 3: Date
        formGrid.add(new Label("Date :"), 0, 2);
        datePicker = new DatePicker();
        formGrid.add(datePicker, 1, 2);

        // Ligne 4: Heure de début
        formGrid.add(new Label("Heure de début (HH:MM) :"), 0, 3);
        HBox startTimeBox = new HBox(5);
        startTimeBox.setAlignment(Pos.CENTER_LEFT);
        startHourField = new TextField();
        startHourField.setPromptText("HH");
        startMinuteField = new TextField();
        startMinuteField.setPromptText("MM");
        startTimeBox.getChildren().addAll(startHourField, new Label(":"), startMinuteField);
        formGrid.add(startTimeBox, 1, 3);

        // Ligne 5: Heure de fin
        formGrid.add(new Label("Heure de fin (HH:MM) :"), 0, 4);
        HBox endTimeBox = new HBox(5);
        endTimeBox.setAlignment(Pos.CENTER_LEFT);
        endHourField = new TextField();
        endHourField.setPromptText("HH");
        endMinuteField = new TextField();
        endMinuteField.setPromptText("MM");
        endTimeBox.getChildren().addAll(endHourField, new Label(":"), endMinuteField);
        formGrid.add(endTimeBox, 1, 4);

        // Ligne 6: Boutons d'action
        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("save-button");
        cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("cancel-button");
        buttonBar.getChildren().addAll(cancelButton, saveButton);
        formGrid.add(buttonBar, 1, 5);

        formContainer.getChildren().addAll(titleLabel, formGrid);
        return formContainer;
    }

    // --- Méthodes pour que le contrôleur manipule la vue ---

    public CompteUtilisateur getCompte() {
        return this.compte;
    }

    public void setDateFieldsEditable(boolean editable) {
        datePicker.setDisable(!editable);
        startHourField.setDisable(!editable);
        startMinuteField.setDisable(!editable);
        endHourField.setDisable(!editable);
        endMinuteField.setDisable(!editable);

        if (!editable) {
            datePicker.setStyle("-fx-opacity: 0.7;");
            startHourField.getParent().setStyle("-fx-opacity: 0.7;");
            endHourField.getParent().setStyle("-fx-opacity: 0.7;");
        }
    }

    public Optional<Site> showCreateSiteDialog() {
        // Création de la boîte de dialogue personnalisée
        Dialog<Site> dialog = new Dialog<>();
        dialog.setTitle("Créer un nouveau Site");
        dialog.setHeaderText("Veuillez remplir les informations pour le nouveau site.");

        // Ajout des boutons "Créer" et "Annuler"
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Création du formulaire à l'intérieur de la boîte de dialogue
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

        // Convertit le résultat du dialogue en un objet Site
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    return new Site(
                            codeField.getText(),
                            nomField.getText(),
                            Float.parseFloat(lonField.getText()),
                            Float.parseFloat(latField.getText()));
                } catch (Exception e) {
                    // En cas d'erreur de parsing ou de validation, on retourne null
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue personnalisée pour créer un nouveau Sport.
     * 
     * @return Un Optional contenant un objet Sport si l'utilisateur valide, sinon
     *         un Optional vide.
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

    public void populateSiteComboBox(List<Site> sites) {
        Site selected = siteComboBox.getValue();
        siteComboBox.setItems(FXCollections.observableArrayList(sites));
        if (selected != null) {
            siteComboBox.setValue(selected);
        }
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

    public void populateSportComboBox(List<Sport> sports) {
        Sport selected = sportComboBox.getValue();
        sportComboBox.setItems(FXCollections.observableArrayList(sports));
        if (selected != null) {
            sportComboBox.setValue(selected);
        }
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

    public void setFormTitle(String title) {
        titleLabel.setText(title);
    }

    public void setDpsData(DPS dps) {
        siteComboBox.setValue(dps.getSite());
        sportComboBox.setValue(dps.getSport());
        datePicker.setValue(dps.getJournee().getDate());
        startHourField.setText(String.format("%02d", dps.getHoraireDepart()[0]));
        startMinuteField.setText(String.format("%02d", dps.getHoraireDepart()[1]));
        endHourField.setText(String.format("%02d", dps.getHoraireFin()[0]));
        endMinuteField.setText(String.format("%02d", dps.getHoraireFin()[1]));
    }

    public Site getSelectedSite() {
        return siteComboBox.getValue();
    }

    public Sport getSelectedSport() {
        return sportComboBox.getValue();
    }

    public LocalDate getSelectedDate() {
        return datePicker.getValue();
    }

    public String getStartHour() {
        return startHourField.getText();
    }

    public String getStartMinute() {
        return startMinuteField.getText();
    }

    public String getEndHour() {
        return endHourField.getText();
    }

    public String getEndMinute() {
        return endMinuteField.getText();
    }

    public void setSaveButtonAction(EventHandler<ActionEvent> event) {
        saveButton.setOnAction(event);
    }

    public void setCancelButtonAction(EventHandler<ActionEvent> event) {
        cancelButton.setOnAction(event);
    }

    public void setAddSiteAction(EventHandler<ActionEvent> event) {
        addSiteButton.setOnAction(event);
    }

    public void setAddSportAction(EventHandler<ActionEvent> event) {
        addSportButton.setOnAction(event);
    }
}