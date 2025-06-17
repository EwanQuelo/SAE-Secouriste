package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminDispositifController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AdminDispositifView extends BaseView {

    private VBox dpsListContainer;
    private Button addButton;
    private final CompteUtilisateur compte;

    public AdminDispositifView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Dispositifs");
        this.compte = compte;

        // Le constructeur de BaseView appelle createCenterContent(),
        // ce qui crée les composants.
        // Maintenant que les composants existent, on peut créer le contrôleur.
        new AdminDispositifController(this, navigator); 
    }
    
    public CompteUtilisateur getCompte() {
        return this.compte;
    }

    @Override
    protected Node createCenterContent() {
        // Le conteneur principal pour tout le contenu central
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(25));
        mainContainer.getStyleClass().add("admin-content-container");

        // L'en-tête avec le titre et le bouton "+"
        HBox header = createHeader();

        // Une zone de défilement pour la liste des fiches
        ScrollPane scrollPane = new ScrollPane();
        dpsListContainer = new VBox(15);
        dpsListContainer.setPadding(new Insets(10));
        dpsListContainer.getStyleClass().add("dps-list-container");
        
        scrollPane.setContent(dpsListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("admin-scroll-pane");

        // Assemblage final de la vue
        mainContainer.getChildren().addAll(header, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // Le scrollpane prend toute la hauteur dispo

        return mainContainer;
    }

    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Gestion des Dispositifs");
        title.getStyleClass().add("admin-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addButton = new Button("+");
        addButton.getStyleClass().add("add-button");

        headerBox.getChildren().addAll(title, spacer, addButton);
        return headerBox;
    }

    // --- Méthodes publiques pour que le Contrôleur puisse manipuler la Vue ---

    public void clearDpsList() {
        dpsListContainer.getChildren().clear();
    }

    public void showEmptyMessage(String message) {
        Label emptyLabel = new Label(message);
        emptyLabel.getStyleClass().add("empty-list-label");
        dpsListContainer.getChildren().add(emptyLabel);
    }

    public void addDpsCard(DPS dps) {
        VBox card = new VBox(5);
        card.getStyleClass().add("dps-card");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH);
        
        // Titre de la fiche : le nom du sport
        Label sportLabel = new Label(dps.getSport().getNom());
        sportLabel.getStyleClass().add("dps-card-title");
        
        // Sous-titre : la date formatée
        String dateText = dps.getJournee().getDate().format(dateFormatter);
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.substring(1);
        Label dateLabel = new Label(dateText);
        dateLabel.getStyleClass().add("dps-card-subtitle");

        // Infos supplémentaires : le lieu et les horaires
        Label siteLabel = new Label("Lieu : " + dps.getSite().getNom());
        siteLabel.getStyleClass().add("dps-card-info");
        
        int startHour = dps.getHoraireDepart()[0];
        int startMinute = dps.getHoraireDepart()[1];
        int endHour = dps.getHoraireFin()[0];
        int endMinute = dps.getHoraireFin()[1];
        String timeText = String.format("Horaires : %02d:%02d - %02d:%02d", startHour, startMinute, endHour, endMinute);
        Label timeLabel = new Label(timeText);
        timeLabel.getStyleClass().add("dps-card-info");
        
        card.getChildren().addAll(sportLabel, dateLabel, siteLabel, timeLabel);
        dpsListContainer.getChildren().add(card);
    }

    public void setAddButtonAction(EventHandler<ActionEvent> eventHandler) {
        if (addButton != null) {
            addButton.setOnAction(eventHandler);
        }
    }
}