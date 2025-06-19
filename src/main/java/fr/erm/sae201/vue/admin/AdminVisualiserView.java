// src/main/java/fr/erm/sae201/vue/admin/AdminVisualiserView.java
package fr.erm.sae201.vue.admin;

import fr.erm.sae201.controleur.admin.AdminVisualiserController;
import fr.erm.sae201.controleur.admin.AdminVisualiserController.DpsStatusInfo;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * La vue de visualisation sous forme de calendrier hebdomadaire pour les administrateurs.
 * Elle affiche les Dispositifs Prévisionnels de Secours (DPS) sur une grille temporelle,
 * indiquant leur état d'affectation (complet, en cours, non pourvu) par un code couleur.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminVisualiserView extends BaseView {

    private VBox mainContainer;
    private Consumer<Integer> weekChangeHandler;

    private static final int START_HOUR = 8;
    private static final int END_HOUR = 24;
    private static final double HOUR_HEIGHT = 45.0;

    /**
     * Construit la vue de visualisation.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte    Le compte de l'administrateur connecté.
     */
    public AdminVisualiserView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Visualiser");
        new AdminVisualiserController(this, navigator, compte);
    }

    /**
     * Définit le gestionnaire d'événements à appeler lors d'un changement de semaine.
     *
     * @param handler Le Consumer qui sera exécuté, recevant -1 pour la semaine précédente
     *                et 1 pour la semaine suivante.
     */
    public void setWeekChangeHandler(Consumer<Integer> handler) {
        this.weekChangeHandler = handler;
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
     * Peuple l'ensemble de la vue du calendrier avec les données pour une semaine donnée.
     *
     * @param currentWeekStart La date du premier jour (lundi) de la semaine à afficher.
     * @param dpsStatusList    La liste des DPS et de leur statut d'affectation pour cette semaine.
     */
    public void populateCalendar(LocalDate currentWeekStart, List<DpsStatusInfo> dpsStatusList) {
        mainContainer.getChildren().clear();

        GridPane weekNavigationBar = createWeekNavigationBar(currentWeekStart);
        GridPane calendarGrid = createCalendarGrid();

        populateCalendarGrid(calendarGrid, dpsStatusList);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("calendar-scroll-pane");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContainer.getChildren().addAll(weekNavigationBar, scrollPane);
    }
    
    /**
     * Crée la barre de navigation en haut du calendrier, avec les boutons pour changer de semaine
     * et les en-têtes des jours.
     *
     * @param currentWeekStart La date du premier jour de la semaine affichée.
     * @return Un GridPane représentant la barre de navigation.
     */
    private GridPane createWeekNavigationBar(LocalDate currentWeekStart) {
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
        if (weekChangeHandler != null) {
            prevWeekButton.setOnAction(e -> weekChangeHandler.accept(-1));
        }
        GridPane.setHalignment(prevWeekButton, HPos.CENTER);
        navGrid.add(prevWeekButton, 0, 1);

        Button nextWeekButton = new Button("→");
        nextWeekButton.getStyleClass().add("week-nav-button");
        if (weekChangeHandler != null) {
            nextWeekButton.setOnAction(e -> weekChangeHandler.accept(1));
        }
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
     * Crée la grille principale du calendrier avec les colonnes pour l'échelle de temps et les sept jours.
     *
     * @return Un GridPane initialisé pour le calendrier.
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
     * Remplit la grille du calendrier avec les labels d'heures et les événements DPS.
     *
     * @param calendarGrid    La grille à remplir.
     * @param dpsStatusList   La liste des DPS à afficher.
     */
    private void populateCalendarGrid(GridPane calendarGrid, List<DpsStatusInfo> dpsStatusList) {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();

        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            RowConstraints rowConstraints = new RowConstraints(HOUR_HEIGHT);
            rowConstraints.setValignment(VPos.TOP);
            calendarGrid.getRowConstraints().add(rowConstraints);
        }

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
        
        List<AnchorPane> dayPanes = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            AnchorPane dayPane = new AnchorPane();
            calendarGrid.add(dayPane, day + 1, 0, 1, END_HOUR - START_HOUR);
            dayPanes.add(dayPane);
        }

        for (DpsStatusInfo statusInfo : dpsStatusList) {
            DPS dps = statusInfo.dps();
            if (dps == null) continue;
            
            int dayIndex = dps.getJournee().getDate().getDayOfWeek().getValue() - 1;
            if (dayIndex < 0 || dayIndex >= 7) continue;

            int startHour = dps.getHoraireDepart()[0];
            int startMinute = dps.getHoraireDepart()[1];
            int endHour = dps.getHoraireFin()[0];
            int endMinute = dps.getHoraireFin()[1];
            
            if (startHour < START_HOUR) continue;

            double startOffsetInMinutes = (startHour - START_HOUR) * 60 + startMinute;
            double topOffset = startOffsetInMinutes * (HOUR_HEIGHT / 60.0);

            double durationInMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute);
            double eventNodeHeight = Math.max(20, durationInMinutes * (HOUR_HEIGHT / 60.0));

            Node eventNode = createDpsNode(statusInfo);
            
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
     * Crée un nœud visuel pour un événement DPS, avec un style dépendant de son statut d'affectation.
     *
     * @param statusInfo Les informations du DPS, y compris le nombre de secouristes requis et assignés.
     * @return Un nœud (Node) représentant l'événement à afficher dans le calendrier.
     */
    private Node createDpsNode(DpsStatusInfo statusInfo) {
        VBox eventBox = new VBox(2);
        DPS dps = statusInfo.dps();
        int assigned = statusInfo.assigned();
        int required = statusInfo.required();

        eventBox.getStyleClass().add("admin-cal-event-box");
        
        if (required > 0) {
            if (assigned == 0) {
                eventBox.getStyleClass().add("status-empty");
            } else if (assigned < required) {
                eventBox.getStyleClass().add("status-in-progress");
            } else {
                eventBox.getStyleClass().add("status-complete");
            }
        }
        
        Label title = new Label(dps.getSport().getNom());
        title.getStyleClass().add("event-title");
        title.setWrapText(true);

        Label subtitle = new Label(dps.getSite().getNom());
        subtitle.getStyleClass().add("event-subtitle");
        subtitle.setWrapText(true);
        
        Label infoLabel = new Label(assigned + " / " + required + " assigné(s)");
        infoLabel.getStyleClass().add("event-info");
        
        eventBox.getChildren().addAll(title, subtitle, infoLabel);
        return eventBox;
    }
}