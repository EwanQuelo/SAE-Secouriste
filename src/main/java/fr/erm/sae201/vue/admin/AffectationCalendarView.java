package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AffectationCalendarController;
import fr.erm.sae201.controleur.admin.AffectationCalendarController.DpsCalendarEvent;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Vue du calendrier hebdomadaire pour l'administrateur, affichant tous les DPS
 * et leur statut d'affectation.
 */
public class AffectationCalendarView extends BaseView {

    private VBox mainContainer;
    private Button prevWeekButton, nextWeekButton, todayButton;
    private GridPane calendarGrid;
    private List<AnchorPane> dayPanes; // Un panneau par jour pour y positionner les événements

    // Constantes pour la mise en page du calendrier
    private static final int START_HOUR = 7;
    private static final int END_HOUR = 23;
    private static final double HOUR_HEIGHT = 80.0; // Hauteur en pixels pour une heure

    public AffectationCalendarView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Calendrier Admin");
        // La vue instancie son contrôleur, qui gérera la logique.
        new AffectationCalendarController(this, navigator);
    }

    @Override
    protected Node createCenterContent() {
        mainContainer = new VBox(10);
        mainContainer.getStyleClass().add("calendar-container");
        // Le contrôleur se chargera de peupler cette vue.
        return mainContainer;
    }

    // --- Méthodes pour que le Contrôleur puisse interagir avec la Vue ---

    public void setPrevWeekAction(EventHandler<ActionEvent> handler) {
        if (prevWeekButton != null) prevWeekButton.setOnAction(handler);
    }

    public void setNextWeekAction(EventHandler<ActionEvent> handler) {
        if (nextWeekButton != null) nextWeekButton.setOnAction(handler);
    }

    public void setTodayAction(EventHandler<ActionEvent> handler) {
        if (todayButton != null) todayButton.setOnAction(handler);
    }

    /**
     * Prépare la structure de la vue (barre de navigation et grille vide)
     * avant que le contrôleur n'y insère les données.
     * @param weekStart Le premier jour de la semaine à afficher.
     */
    public void prepareForData(LocalDate weekStart) {
        mainContainer.getChildren().clear();

        GridPane weekNavigationBar = createWeekNavigationBar(weekStart);
        calendarGrid = createCalendarGrid();
        
        ScrollPane scrollPane = new ScrollPane(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("calendar-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContainer.getChildren().addAll(weekNavigationBar, scrollPane);
    }

    /**
     * Peuple la grille du calendrier avec les événements DPS fournis par le contrôleur.
     * @param data Une map associant une date à une liste d'événements pour ce jour.
     */
    public void populateCalendar(Map<LocalDate, List<DpsCalendarEvent>> data) {
        // On nettoie seulement les événements précédents, pas toute la grille
        for (AnchorPane pane : dayPanes) {
            pane.getChildren().clear();
        }

        // On parcourt les données reçues du contrôleur
        for (Map.Entry<LocalDate, List<DpsCalendarEvent>> entry : data.entrySet()) {
            LocalDate date = entry.getKey();
            int dayIndex = date.getDayOfWeek().getValue() - 1; // 0=Lundi, 6=Dimanche

            if (dayIndex < 0 || dayIndex >= 7) continue; // Sécurité

            for (DpsCalendarEvent event : entry.getValue()) {
                int startHour = event.dps().getHoraireDepart()[0];
                int startMinute = event.dps().getHoraireDepart()[1];
                int endHour = event.dps().getHoraireFin()[0];
                int endMinute = event.dps().getHoraireFin()[1];

                // On ignore les événements qui commencent avant notre heure de début visible
                if (startHour < START_HOUR) continue;

                // Calcul de la position Y (verticale) en pixels
                double startOffsetInMinutes = (startHour - START_HOUR) * 60 + startMinute;
                double topOffset = startOffsetInMinutes * (HOUR_HEIGHT / 60.0);

                // Calcul de la durée et donc de la hauteur du bloc en pixels
                double durationInMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute);
                double eventNodeHeight = Math.max(25, durationInMinutes * (HOUR_HEIGHT / 60.0)); // Hauteur minimale de 25px

                Node eventNode = createEventNode(event);
                
                // On cible le panneau du jour correspondant
                AnchorPane targetDayPane = dayPanes.get(dayIndex);
                
                // On positionne l'événement de manière absolue dans le panneau du jour
                AnchorPane.setTopAnchor(eventNode, topOffset);
                AnchorPane.setLeftAnchor(eventNode, 2.0);
                AnchorPane.setRightAnchor(eventNode, 2.0);
                
                ((Region) eventNode).setPrefHeight(eventNodeHeight);
                ((Region) eventNode).setMinHeight(eventNodeHeight);

                targetDayPane.getChildren().add(eventNode);
            }
        }
    }

    // --- Méthodes privées pour construire l'interface ---

    /**
     * MODIFIÉ: Crée la barre de navigation avec les flèches aux extrémités,
     * comme dans la vue du secouriste.
     */
    private GridPane createWeekNavigationBar(LocalDate weekStart) {
        GridPane navGrid = new GridPane();
        navGrid.getStyleClass().add("week-nav-grid");
        
        // --- DÉBUT DE LA CORRECTION ---
        
        // 1. Définition des colonnes : Flèche, 7 jours, Flèche
        ColumnConstraints arrowColumn = new ColumnConstraints();
        arrowColumn.setPercentWidth(8); // Colonnes des flèches
        navGrid.getColumnConstraints().addAll(arrowColumn, new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), arrowColumn);

        for (int i = 1; i <= 7; i++) {
            ColumnConstraints dayCol = navGrid.getColumnConstraints().get(i);
            dayCol.setPercentWidth(84.0 / 7.0); // Répartit le reste de l'espace
        }

        // 2. Création et positionnement des éléments
        prevWeekButton = new Button("←");
        prevWeekButton.getStyleClass().add("week-nav-button");
        GridPane.setHalignment(prevWeekButton, HPos.CENTER);
        navGrid.add(prevWeekButton, 0, 1);

        nextWeekButton = new Button("→");
        nextWeekButton.getStyleClass().add("week-nav-button");
        GridPane.setHalignment(nextWeekButton, HPos.CENTER);
        navGrid.add(nextWeekButton, 8, 1);

        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
        String monthYearText = weekStart.format(monthYearFormatter);
        monthYearText = monthYearText.substring(0, 1).toUpperCase() + monthYearText.substring(1);
        Label monthYearLabel = new Label(monthYearText);
        monthYearLabel.getStyleClass().add("month-year-label");
        
        todayButton = new Button("Aujourd'hui");
        todayButton.getStyleClass().add("week-nav-button");

        // On groupe le mois et le bouton "Aujourd'hui" au centre
        VBox centerHeader = new VBox(5, monthYearLabel, todayButton);
        centerHeader.setAlignment(Pos.CENTER);
        navGrid.add(centerHeader, 1, 0, 7, 1); // Span sur les 7 colonnes du milieu

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE d", Locale.FRENCH);
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            String dayName = day.format(dayFormatter);
            Label dayHeader = new Label(dayName.substring(0, 1).toUpperCase() + dayName.substring(1));
            dayHeader.getStyleClass().add("day-header-label");
            if(day.equals(LocalDate.now())) {
                dayHeader.getStyleClass().add("today-header-label");
            }
            GridPane.setHalignment(dayHeader, HPos.CENTER);
            navGrid.add(dayHeader, i + 1, 1);
        }
        
        // --- FIN DE LA CORRECTION ---
        
        return navGrid;
    }

    private GridPane createCalendarGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("calendar-grid");
        
        // Colonne pour les heures
        grid.getColumnConstraints().add(new ColumnConstraints(50));
        // 7 colonnes pour les jours
        for (int i = 0; i < 7; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(10, 100, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true));
        }

        // Lignes pour les heures
        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            RowConstraints rowConstraints = new RowConstraints(HOUR_HEIGHT);
            rowConstraints.setValignment(VPos.TOP);
            grid.getRowConstraints().add(rowConstraints);

            Label timeLabel = new Label(String.format("%02dh", hour));
            timeLabel.getStyleClass().add("time-label");
            GridPane.setMargin(timeLabel, new Insets(-8, 5, 0, 0));
            grid.add(timeLabel, 0, hour - START_HOUR);
        }
        
        // Crée les "toiles" (AnchorPanes) sur lesquelles les événements seront dessinés
        this.dayPanes = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            AnchorPane dayPane = new AnchorPane();
            dayPane.getStyleClass().add("grid-cell"); // Style pour les bordures de la grille
            // Le panneau s'étend sur toutes les lignes des heures
            grid.add(dayPane, day + 1, 0, 1, END_HOUR - START_HOUR);
            dayPanes.add(dayPane);
        }

        return grid;
    }

    private Node createEventNode(DpsCalendarEvent event) {
        VBox eventBox = new VBox(2);
        eventBox.getStyleClass().add("admin-event-box");
        eventBox.setPadding(new Insets(4));

        String titleText = event.dps().getSport().getNom() + " - " + event.dps().getSite().getNom();
        Label title = new Label(titleText);
        title.getStyleClass().add("admin-event-title");
        title.setWrapText(true);

        String countText = "Affectés : " + event.assignedCount() + " / " + event.requiredCount();
        Label countLabel = new Label(countText);
        countLabel.getStyleClass().add("admin-event-count");

        // Applique un style différent basé sur le statut d'affectation
        if (event.requiredCount() > 0) {
            if (event.assignedCount() >= event.requiredCount()) {
                eventBox.getStyleClass().add("event-complete");
            } else if (event.assignedCount() > 0) {
                eventBox.getStyleClass().add("event-in-progress");
            } else {
                eventBox.getStyleClass().add("event-empty");
            }
        }
        
        eventBox.getChildren().addAll(title, countLabel);
        return eventBox;
    }
}