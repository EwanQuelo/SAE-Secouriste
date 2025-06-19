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

/**
 * La vue du calendrier hebdomadaire pour l'utilisateur secouriste.
 * Elle affiche les affectations de l'utilisateur sur une grille temporelle,
 * permettant une navigation par semaine pour visualiser l'emploi du temps passé et futur.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class UserCalendrierView extends BaseView {

    /** Le compte de l'utilisateur secouriste actuellement connecté. */
    private final CompteUtilisateur compte;
    /** Le DAO pour accéder aux données des affectations. */
    private final AffectationDAO affectationDAO;
    /** La date du premier jour (lundi) de la semaine actuellement affichée. */
    private LocalDate currentWeekStart;
    /** Le conteneur principal de la vue du calendrier. */
    private VBox mainContainer;

    /** Heure de début de l'affichage du calendrier (8h). */
    private static final int START_HOUR = 8;
    /** Heure de fin de l'affichage du calendrier (24h). */
    private static final int END_HOUR = 24;
    
    /** Hauteur en pixels pour représenter une heure, créant un ratio de 1 pixel par minute. */
    private static final double HOUR_HEIGHT = 60.0;

    /**
     * Construit la vue du calendrier pour le secouriste.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'utilisateur connecté.
     */
    public UserCalendrierView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Calendrier");
        this.compte = compte;
        this.affectationDAO = new AffectationDAO();
        // Initialise la vue sur la semaine en cours, en se calant sur le lundi.
        this.currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        populateCalendar();
    }

    /**
     * Crée et retourne le contenu central de la vue, qui est le conteneur principal du calendrier.
     *
     * @return Le nœud (Node) racine du contenu de la vue.
     */
    @Override
    protected Node createCenterContent() {
        mainContainer = new VBox(10);
        mainContainer.getStyleClass().add("calendar-container");
        return mainContainer;
    }

    /**
     * Déclenche le rafraîchissement complet de la vue du calendrier.
     */
    private void populateCalendar() {
        refreshCalendarView();
    }

    /**
     * Gère la navigation entre les semaines.
     *
     * @param weeksToAdd Le nombre de semaines à ajouter (1 pour la suivante, -1 pour la précédente).
     */
    private void changeWeek(int weeksToAdd) {
        currentWeekStart = currentWeekStart.plusWeeks(weeksToAdd);
        refreshCalendarView();
    }

    /**
     * Rafraîchit l'intégralité de l'affichage du calendrier en reconstruisant ses composants.
     */
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

    /**
     * Crée la barre de navigation en haut du calendrier avec les boutons et les jours.
     *
     * @return Un GridPane représentant la barre de navigation de la semaine.
     */
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

    /**
     * Crée la grille principale du calendrier avec les colonnes pour les heures et les jours.
     *
     * @return Un GridPane prêt à être peuplé.
     */
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
        
        return grid;
    }

    /**
     * Peuple la grille du calendrier avec les affectations de l'utilisateur.
     *
     * @param calendarGrid La grille à remplir.
     */
    private void populateCalendarGrid(GridPane calendarGrid) {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();

        // 1. Définir des contraintes de ligne pour une hauteur fixe par heure.
        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            RowConstraints rowConstraints = new RowConstraints(HOUR_HEIGHT);
            rowConstraints.setValignment(VPos.TOP);
            calendarGrid.getRowConstraints().add(rowConstraints);
        }

        // 2. Créer les étiquettes d'heure et les cellules de fond.
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
        
        // 3. Créer une "toile" (AnchorPane) par jour pour positionner les événements avec précision.
        // Cette technique permet de superposer les événements sur la grille de fond.
        List<AnchorPane> dayPanes = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            AnchorPane dayPane = new AnchorPane();
            calendarGrid.add(dayPane, day + 1, 0, 1, END_HOUR - START_HOUR);
            dayPanes.add(dayPane);
        }

        // 4. Récupérer et placer les affectations sur les "toiles" correspondantes.
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        List<Affectation> affectations = affectationDAO
                .findAffectationsForSecouristeBetweenDates(compte.getIdSecouriste(), currentWeekStart, weekEnd);

        for (Affectation affectation : affectations) {
            int dayIndex = affectation.getDps().getJournee().getDate().getDayOfWeek().getValue() - 1;
            
            if (dayIndex < 0 || dayIndex >= 7) continue;

            int startHour = affectation.getDps().getHoraireDepart()[0];
            int startMinute = affectation.getDps().getHoraireDepart()[1];
            int endHour = affectation.getDps().getHoraireFin()[0];
            int endMinute = affectation.getDps().getHoraireFin()[1];
            
            if (startHour < START_HOUR) continue;

            double startOffsetInMinutes = (startHour - START_HOUR) * 60 + startMinute;
            double topOffset = startOffsetInMinutes * (HOUR_HEIGHT / 60.0);

            double durationInMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute);
            double eventNodeHeight = Math.max(15, durationInMinutes * (HOUR_HEIGHT / 60.0));

            Node eventNode = createEventNode(affectation);
            
            AnchorPane targetDayPane = dayPanes.get(dayIndex);
            
            AnchorPane.setTopAnchor(eventNode, topOffset);
            AnchorPane.setLeftAnchor(eventNode, 2.0);
            AnchorPane.setRightAnchor(eventNode, 2.0);
            
            ((Region) eventNode).setPrefHeight(eventNodeHeight);
            ((Region) eventNode).setMinHeight(eventNodeHeight);

            targetDayPane.getChildren().add(eventNode);
        }
    }

    /**
     * Crée la représentation graphique d'une affectation.
     *
     * @param affectation L'affectation à représenter.
     * @return Un nœud (Node) contenant les détails de l'affectation.
     */
    private Node createEventNode(Affectation affectation) {
        VBox eventBox = new VBox(2);
        eventBox.getStyleClass().add("event-box");
        eventBox.setPadding(new Insets(2));

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