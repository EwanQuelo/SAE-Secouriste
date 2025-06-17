package fr.erm.sae201.vue.user;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserCalendrierView extends BaseView {

    private final CompteUtilisateur compte;
    private final AffectationDAO affectationDAO;
    private LocalDate currentWeekStart;
    private VBox mainContainer;

    private static final int START_HOUR = 8;
    private static final int END_HOUR = 24;
    
    // NOUVEAU : Constante pour la hauteur d'une heure en pixels.
    // Cela nous donne une base de calcul : 60 pixels/heure = 1 pixel/minute.
    private static final double HOUR_HEIGHT = 60.0;

    public UserCalendrierView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Calendrier");
        this.compte = compte;
        this.affectationDAO = new AffectationDAO();
        // Date de départ arbitraire pour l'exemple
        this.currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        populateCalendar();
    }

    @Override
    protected Node createCenterContent() {
        mainContainer = new VBox(10);
        mainContainer.getStyleClass().add("calendar-container");
        return mainContainer;
    }

    private void populateCalendar() {
        refreshCalendarView();
    }

    private void changeWeek(int weeksToAdd) {
        currentWeekStart = currentWeekStart.plusWeeks(weeksToAdd);
        refreshCalendarView();
    }

    private void refreshCalendarView() {
        mainContainer.getChildren().clear();

        GridPane weekNavigationBar = createWeekNavigationBar();
        GridPane calendarGrid = createCalendarGrid();

        populateCalendarGrid(calendarGrid);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("calendar-scroll-pane");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContainer.getChildren().addAll(weekNavigationBar, scrollPane);
    }

    private GridPane createWeekNavigationBar() {
        GridPane navGrid = new GridPane();
        navGrid.getStyleClass().add("week-nav-grid");

        ColumnConstraints leftArrowCol = new ColumnConstraints();
        leftArrowCol.setPercentWidth(5);
        navGrid.getColumnConstraints().add(leftArrowCol);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints dayCol = new ColumnConstraints();
            dayCol.setPercentWidth(90.0 / 7.0);
            navGrid.getColumnConstraints().add(dayCol);
        }

        ColumnConstraints rightArrowCol = new ColumnConstraints();
        rightArrowCol.setPercentWidth(5);
        navGrid.getColumnConstraints().add(rightArrowCol);

        Button prevWeekButton = new Button("←");
        prevWeekButton.getStyleClass().add("week-nav-button");
        prevWeekButton.setOnAction(e -> changeWeek(-1));
        GridPane.setHalignment(prevWeekButton, HPos.CENTER);
        navGrid.add(prevWeekButton, 0, 1);

        Button nextWeekButton = new Button("→");
        nextWeekButton.getStyleClass().add("week-nav-button");
        nextWeekButton.setOnAction(e -> changeWeek(1));
        GridPane.setHalignment(nextWeekButton, HPos.CENTER);
        navGrid.add(nextWeekButton, 8, 1);

        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
        String monthYearText = currentWeekStart.format(monthYearFormatter);
        monthYearText = monthYearText.substring(0, 1).toUpperCase() + monthYearText.substring(1);
        
        Label monthYearLabel = new Label(monthYearText);
        monthYearLabel.getStyleClass().add("month-year-label");
        GridPane.setHalignment(monthYearLabel, HPos.CENTER);
        navGrid.add(monthYearLabel, 1, 0, 7, 1);

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d", Locale.FRENCH);
        DateTimeFormatter dayNameFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH);

        for (int i = 0; i < 7; i++) {
            LocalDate day = currentWeekStart.plusDays(i);
            Button dayButton = new Button();
            String dayName = day.format(dayNameFormatter);
            String dayText = dayName.substring(0, 1).toUpperCase() + dayName.substring(1) + "\n" + day.format(dayFormatter);
            dayButton.setText(dayText);
            dayButton.getStyleClass().add("day-header-button");
            
            if (day.equals(LocalDate.now())) {
                dayButton.getStyleClass().add("today-header-button");
            }
            
            GridPane.setHalignment(dayButton, HPos.CENTER);
            navGrid.add(dayButton, i + 1, 1);
        }
        
        return navGrid;
    }

    private GridPane createCalendarGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("calendar-grid");

        ColumnConstraints leftTimeColumn = new ColumnConstraints();
        leftTimeColumn.setPercentWidth(5);
        grid.getColumnConstraints().add(leftTimeColumn);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints dayColumn = new ColumnConstraints();
            dayColumn.setPercentWidth(90.0 / 7.0);
            grid.getColumnConstraints().add(dayColumn);
        }
        
        // La colonne de droite n'est plus nécessaire car les flèches sont dans la barre de nav
        // ColumnConstraints rightSpacerColumn = new ColumnConstraints();
        // rightSpacerColumn.setPercentWidth(5);
        // grid.getColumnConstraints().add(rightSpacerColumn);

        return grid;
    }

    // MODIFIÉ : La logique de cette méthode est entièrement revue.
    private void populateCalendarGrid(GridPane calendarGrid) {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();

        // 1. Définir des contraintes de ligne pour une hauteur fixe par heure
        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            RowConstraints rowConstraints = new RowConstraints(HOUR_HEIGHT);
            rowConstraints.setValignment(VPos.TOP);
            calendarGrid.getRowConstraints().add(rowConstraints);
        }

        // 2. Créer les étiquettes d'heure et les lignes de fond de la grille (pour l'aspect visuel)
        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            int row = hour - START_HOUR;
            Label timeLabel = new Label(String.format("%02dh", hour));
            timeLabel.getStyleClass().add("time-label");
            GridPane.setHalignment(timeLabel, HPos.RIGHT);
            GridPane.setMargin(timeLabel, new Insets(0, 5, 0, 0));
            calendarGrid.add(timeLabel, 0, row);

            for (int day = 0; day < 7; day++) {
                Pane gridCell = new Pane();
                gridCell.getStyleClass().add("grid-cell");
                calendarGrid.add(gridCell, day + 1, row);
            }
        }
        
        // 3. Créer une "toile" (AnchorPane) par jour pour y positionner les événements
        List<AnchorPane> dayPanes = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            AnchorPane dayPane = new AnchorPane();
            // On ajoute ce panneau au-dessus des cellules de la grille, en le faisant s'étendre sur toutes les lignes
            calendarGrid.add(dayPane, day + 1, 0, 1, END_HOUR - START_HOUR);
            dayPanes.add(dayPane);
        }

        // 4. Récupérer les affectations et les placer sur les "toiles"
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        List<Affectation> affectations = affectationDAO
                .findAffectationsForSecouristeBetweenDates(compte.getIdSecouriste(), currentWeekStart, weekEnd);

        for (Affectation affectation : affectations) {
            // Indice de la colonne (0=Lundi, ..., 6=Dimanche)
            int dayIndex = affectation.getDps().getJournee().getDate().getDayOfWeek().getValue() - 1;
            
            if (dayIndex < 0 || dayIndex >= 7) continue; // Sécurité

            int startHour = affectation.getDps().getHoraireDepart()[0];
            int startMinute = affectation.getDps().getHoraireDepart()[1];
            int endHour = affectation.getDps().getHoraireFin()[0];
            int endMinute = affectation.getDps().getHoraireFin()[1];
            
            // Ignorer les événements qui commencent en dehors de la plage visible
            if (startHour < START_HOUR) continue;

            // Calcul du décalage vertical (Y) en pixels
            double startOffsetInMinutes = (startHour - START_HOUR) * 60 + startMinute;
            double topOffset = startOffsetInMinutes * (HOUR_HEIGHT / 60.0); // 1 pixel par minute

            // Calcul de la durée et de la hauteur du noeud en pixels
            double durationInMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute);
            double eventNodeHeight = Math.max(15, durationInMinutes * (HOUR_HEIGHT / 60.0)); // Hauteur min de 15px

            // Création du noeud graphique pour l'événement
            Node eventNode = createEventNode(affectation);
            
            // Positionnement précis dans l'AnchorPane du jour correspondant
            AnchorPane targetDayPane = dayPanes.get(dayIndex);
            
            AnchorPane.setTopAnchor(eventNode, topOffset);
            AnchorPane.setLeftAnchor(eventNode, 2.0);  // Petite marge à gauche
            AnchorPane.setRightAnchor(eventNode, 2.0); // Petite marge à droite
            
            // Définir la hauteur de l'élément
            ((Region) eventNode).setPrefHeight(eventNodeHeight);
            ((Region) eventNode).setMinHeight(eventNodeHeight);

            targetDayPane.getChildren().add(eventNode);
        }
    }

    private Node createEventNode(Affectation affectation) {
        VBox eventBox = new VBox(2);
        eventBox.getStyleClass().add("event-box");
        eventBox.setPadding(new Insets(5));

        Label title = new Label(affectation.getDps().getSport().getNom());
        title.getStyleClass().add("event-title");
        title.setWrapText(true);

        Label subtitle = new Label(affectation.getDps().getSite().getNom());
        subtitle.getStyleClass().add("event-subtitle");
        subtitle.setWrapText(true);

        int startHour = affectation.getDps().getHoraireDepart()[0];
        int startMinute = affectation.getDps().getHoraireDepart()[1];
        int endHour = affectation.getDps().getHoraireFin()[0];
        int endMinute = affectation.getDps().getHoraireFin()[1];

        String timeText = String.format("%02d:%02d - %02d:%02d", startHour, startMinute, endHour, endMinute);
        Label time = new Label(timeText);
        time.getStyleClass().add("event-time");

        eventBox.getChildren().addAll(title, subtitle, time);
        return eventBox;
    }
}