package fr.erm.sae201.vue.user;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class UserCalendrierView extends BaseView {

    private final CompteUtilisateur compte;
    private final AffectationDAO affectationDAO;
    private LocalDate currentWeekStart;
    private VBox mainContainer;
    


    private static final int START_HOUR = 8;
    private static final int END_HOUR = 24;

    public UserCalendrierView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Calendrier");
        this.compte = compte;
        this.affectationDAO = new AffectationDAO();
        this.currentWeekStart = LocalDate.of(2030, 2, 10).with(DayOfWeek.MONDAY);
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
    mainContainer.getChildren().clear(); // On vide le conteneur principal

    GridPane weekNavigationBar = createWeekNavigationBar();
    GridPane calendarGrid = createCalendarGrid();
    
    populateCalendarGrid(calendarGrid); // On remplit la grille

    // --- DÉBUT DE LA MODIFICATION POUR LE SCROLL ---

    // 1. On crée un ScrollPane.
    ScrollPane scrollPane = new ScrollPane();
    
    // 2. On met notre grille de calendrier À L'INTÉRIEUR du ScrollPane.
    scrollPane.setContent(calendarGrid);
    
    // 3. Quelques styles pour une meilleure intégration :
    scrollPane.setFitToWidth(true); // Fait en sorte que le contenu s'adapte à la largeur du ScrollPane
    scrollPane.getStyleClass().add("calendar-scroll-pane"); // Pour le stylage CSS

    // On s'assure que le ScrollPane prend toute la hauteur verticale disponible
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    // 4. On ajoute le ScrollPane (qui contient maintenant la grille) au conteneur principal.
    mainContainer.getChildren().addAll(weekNavigationBar, scrollPane);

}

    private GridPane createWeekNavigationBar() {
        GridPane navGrid = new GridPane();
        navGrid.getStyleClass().add("week-nav-grid"); // Nouvelle classe CSS pour le style

        // --- On applique la MÊME structure de colonnes que le calendrier principal ---
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

        // --- Placement des éléments dans la grille de navigation ---

        // 1. Flèche gauche dans la première colonne (colonne 0)
        Button prevWeekButton = new Button("←");
        prevWeekButton.getStyleClass().add("week-nav-button");
        prevWeekButton.setOnAction(e -> changeWeek(-1));
        GridPane.setHalignment(prevWeekButton, HPos.CENTER);
        navGrid.add(prevWeekButton, 0, 1); // Ligne 1 (en dessous du mois/année)

        // 2. Flèche droite dans la dernière colonne (colonne 8)
        Button nextWeekButton = new Button("→");
        nextWeekButton.getStyleClass().add("week-nav-button");
        nextWeekButton.setOnAction(e -> changeWeek(1));
        GridPane.setHalignment(nextWeekButton, HPos.CENTER);
        navGrid.add(nextWeekButton, 8, 1);

        // 3. Label Mois/Année, qui s'étend sur les 7 colonnes du milieu
        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
        String monthYearText = currentWeekStart.format(monthYearFormatter);
        monthYearText = monthYearText.substring(0, 1).toUpperCase() + monthYearText.substring(1);
        
        Label monthYearLabel = new Label(monthYearText);
        monthYearLabel.getStyleClass().add("month-year-label");
        GridPane.setHalignment(monthYearLabel, HPos.CENTER);
        // Ajouté à la colonne 1, rangée 0, et il s'étend sur 7 colonnes
        navGrid.add(monthYearLabel, 1, 0, 7, 1);

        // 4. Jours de la semaine, chacun dans sa colonne respective
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
            navGrid.add(dayButton, i + 1, 1); // Colonnes 1 à 7
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

        ColumnConstraints rightSpacerColumn = new ColumnConstraints();
        rightSpacerColumn.setPercentWidth(5);
        grid.getColumnConstraints().add(rightSpacerColumn);

        return grid;
    }

    private void populateCalendarGrid(GridPane calendarGrid) {
        calendarGrid.getChildren().clear();

        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            int row = hour - START_HOUR;
            Label timeLabel = new Label(String.format("%02dh", hour));
            timeLabel.getStyleClass().add("time-label");
            GridPane.setHalignment(timeLabel, HPos.RIGHT);
            calendarGrid.add(timeLabel, 0, row);

            for (int day = 0; day < 7; day++) {
                Pane gridCell = new Pane();
                gridCell.getStyleClass().add("grid-cell");
                calendarGrid.add(gridCell, day + 1, row);
            }
        }

        LocalDate weekEnd = currentWeekStart.plusDays(6);
        List<Affectation> affectations = affectationDAO
                .findAffectationsForSecouristeBetweenDates(compte.getIdSecouriste(), currentWeekStart, weekEnd);

        for (Affectation affectation : affectations) {
            int column = affectation.getDps().getJournee().getDate().getDayOfWeek().getValue();
            int startRow = affectation.getDps().getHoraireDepart()[0] - START_HOUR;
            int duration = Math.max(1, affectation.getDps().getHoraireFin()[0] - affectation.getDps().getHoraireDepart()[0]);

            if (startRow >= 0 && startRow < (END_HOUR - START_HOUR)) {
                Node eventNode = createEventNode(affectation);
                calendarGrid.add(eventNode, column, startRow, 1, duration);
            }
        }
    }

    private Node createEventNode(Affectation affectation) {
        VBox eventBox = new VBox(2);
        eventBox.getStyleClass().add("event-box");
        eventBox.setPadding(new Insets(5));

        Label title = new Label(affectation.getDps().getSport().getNom());
        title.getStyleClass().add("event-title");

        Label subtitle = new Label(affectation.getDps().getSite().getNom());
        subtitle.getStyleClass().add("event-subtitle");

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