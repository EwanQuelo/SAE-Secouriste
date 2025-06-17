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

public class AdminEditDpsView extends BaseView {

    private final CompteUtilisateur compte;
    private ComboBox<Site> siteComboBox;
    private ComboBox<Sport> sportComboBox;
    private DatePicker datePicker;
    private TextField startHourField, startMinuteField;
    private TextField endHourField, endMinuteField;
    private Button saveButton, cancelButton;
    private Label titleLabel;

    public AdminEditDpsView(MainApp navigator, CompteUtilisateur compte, DPS dpsToEdit) {
        super(navigator, compte, "Dispositifs");
        this.compte = compte;
        new AdminEditDpsController(this, navigator, dpsToEdit);
    }

    public void setDateFieldsEditable(boolean editable) {
        datePicker.setDisable(!editable);
        startHourField.setDisable(!editable);
        startMinuteField.setDisable(!editable);
        endHourField.setDisable(!editable);
        endMinuteField.setDisable(!editable);
        
        // On peut aussi changer légèrement le style pour indiquer qu'ils sont non modifiables
        if (!editable) {
            datePicker.setStyle("-fx-opacity: 0.7;");
            startHourField.getParent().setStyle("-fx-opacity: 0.7;");
            endHourField.getParent().setStyle("-fx-opacity: 0.7;");
        }
    }

    public CompteUtilisateur getCompte() {
        return this.compte;
    }

    @Override
    protected Node createCenterContent() {
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(25));
        formContainer.getStyleClass().add("admin-form-container");

        titleLabel = new Label(); // Le titre sera défini par le contrôleur
        titleLabel.getStyleClass().add("admin-title");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);

        formGrid.add(new Label("Site de l'épreuve :"), 0, 0);
        siteComboBox = new ComboBox<>();
        siteComboBox.setPrefWidth(350); // Donner une largeur fixe
        siteComboBox.getStyleClass().add("admin-combo-box");
        formGrid.add(siteComboBox, 1, 0);

        // Ligne 2: Choix du sport
        formGrid.add(new Label("Sport concerné :"), 0, 1);
        sportComboBox = new ComboBox<>();
        sportComboBox.setPrefWidth(350); // Donner une largeur fixe
        sportComboBox.getStyleClass().add("admin-combo-box");
        formGrid.add(sportComboBox, 1, 1);

        formGrid.add(new Label("Date :"), 0, 2);
        datePicker = new DatePicker();
        formGrid.add(datePicker, 1, 2);

        formGrid.add(new Label("Heure de début (HH:MM) :"), 0, 3);
        HBox startTimeBox = new HBox(5);
        startHourField = new TextField();
        startHourField.setPromptText("HH");
        startMinuteField = new TextField();
        startMinuteField.setPromptText("MM");
        startTimeBox.getChildren().addAll(startHourField, new Label(":"), startMinuteField);
        formGrid.add(startTimeBox, 1, 3);

        formGrid.add(new Label("Heure de fin (HH:MM) :"), 0, 4);
        HBox endTimeBox = new HBox(5);
        endHourField = new TextField();
        endHourField.setPromptText("HH");
        endMinuteField = new TextField();
        endMinuteField.setPromptText("MM");
        endTimeBox.getChildren().addAll(endHourField, new Label(":"), endMinuteField);
        formGrid.add(endTimeBox, 1, 4);

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

    public void populateSiteComboBox(List<Site> sites) {
        siteComboBox.setItems(FXCollections.observableArrayList(sites));
        // Affiche le nom du site dans la liste déroulante
        siteComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Site item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
        siteComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Site item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
    }

    public void populateSportComboBox(List<Sport> sports) {
        sportComboBox.setItems(FXCollections.observableArrayList(sports));
        // Affiche le nom du sport dans la liste déroulante
        sportComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Sport item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
        sportComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Sport item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
    }

    public void setFormTitle(String title) {
        titleLabel.setText(title);
    }

    // Pré-remplit le formulaire avec les données d'un DPS existant
    public void setDpsData(DPS dps) {
        siteComboBox.setValue(dps.getSite());
        sportComboBox.setValue(dps.getSport());
        datePicker.setValue(dps.getJournee().getDate());
        startHourField.setText(String.valueOf(dps.getHoraireDepart()[0]));
        startMinuteField.setText(String.valueOf(dps.getHoraireDepart()[1]));
        endHourField.setText(String.valueOf(dps.getHoraireFin()[0]));
        endMinuteField.setText(String.valueOf(dps.getHoraireFin()[1]));
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
}