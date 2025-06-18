package fr.erm.sae201.vue.user;

import fr.erm.sae201.controleur.user.UserDispoController;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Journee;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDispoView extends BaseView {

    private final CompteUtilisateur compte;
    private final MainApp navigator;

    private YearMonth currentMonth;
    private VBox mainContainer;

    private Set<LocalDate> originalDisponibilites;
    private Set<LocalDate> addedDisponibilites = new HashSet<>();
    private Set<LocalDate> removedDisponibilites = new HashSet<>();
    
    private Button saveButton;
    private Button cancelButton;

    public UserDispoView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Disponibilités");
        this.compte = compte;
        this.navigator = navigator;
        this.currentMonth = YearMonth.now();

        // Cette logique est déplacée dans le contrôleur ou le service.
        // On garde juste l'état initial pour l'affichage.
        SecouristeDAO secouristeDAO = new SecouristeDAO();
        Set<Journee> journees = secouristeDAO.findAvailabilitiesForSecouriste(compte.getIdSecouriste());
        this.originalDisponibilites = journees.stream().map(Journee::getDate).collect(Collectors.toSet());

        populateView();
    }
    
    // NOUVEAU : Méthodes pour que le contrôleur se branche sur les boutons
    public void setSaveAction(EventHandler<ActionEvent> handler) {
        saveButton.setOnAction(handler);
    }
    
    public void setCancelAction(EventHandler<ActionEvent> handler) {
        cancelButton.setOnAction(handler);
    }
    
    // NOUVEAU : Getters pour que le contrôleur récupère les changements
    public Set<LocalDate> getAddedDisponibilites() {
        return new HashSet<>(addedDisponibilites);
    }

    public Set<LocalDate> getRemovedDisponibilites() {
        return new HashSet<>(removedDisponibilites);
    }

    // MODIFIÉ : Le code de création de la vue reste, mais la logique des actions est retirée.
    private Node createActionButtons() {
        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.getStyleClass().add("action-button-bar");

        saveButton = new Button("Enregistrer les modifications");
        saveButton.getStyleClass().add("login-button");
        // SUPPRIMÉ : setOnAction(e -> saveChanges());

        cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("signup-button");
        // SUPPRIMÉ : setOnAction(e -> cancelChanges());

        buttonBar.getChildren().addAll(saveButton, cancelButton);
        return buttonBar;
    }
    @Override
    protected Node createCenterContent() {
        mainContainer = new VBox(10);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.getStyleClass().add("dispo-container");
        return mainContainer;
    }

    private void populateView() {
        mainContainer.getChildren().clear();

        Node monthNavigationBar = createMonthNavigationBar();
        Node calendarGrid = createCalendarGrid();
        Node actionButtons = createActionButtons();

        mainContainer.getChildren().addAll(monthNavigationBar, calendarGrid, actionButtons);
    }

    private void changeMonth(int monthsToAdd) {
        currentMonth = currentMonth.plusMonths(monthsToAdd);
        populateView();
    }

    private Node createMonthNavigationBar() {
        HBox navBar = new HBox(20);
        navBar.setAlignment(Pos.CENTER);
        navBar.getStyleClass().add("month-nav-bar");

        Button prevMonthButton = new Button("←");
        prevMonthButton.getStyleClass().add("week-nav-button");
        prevMonthButton.setOnAction(e -> changeMonth(-1));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
        String monthText = currentMonth.format(formatter);
        monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);
        Label monthLabel = new Label(monthText);
        monthLabel.getStyleClass().add("month-year-label");

        Button nextMonthButton = new Button("→");
        nextMonthButton.getStyleClass().add("week-nav-button");
        nextMonthButton.setOnAction(e -> changeMonth(1));

        navBar.getChildren().addAll(prevMonthButton, monthLabel, nextMonthButton);
        return navBar;
    }

    private Node createCalendarGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("dispo-grid");

        String[] daysOfWeek = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" };
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.getStyleClass().add("dispo-day-header");
            GridPane.setHalignment(dayLabel, HPos.CENTER);
            grid.add(dayLabel, i, 0);
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeekOffset = firstDayOfMonth.getDayOfWeek().getValue() - 1;

        for (int i = 0; i < 42; i++) {
            int row = i / 7 + 1;
            int col = i % 7;
            int dayOfMonth = i - dayOfWeekOffset + 1;
            Button dayButton = new Button();
            dayButton.getStyleClass().add("dispo-day-button");
            dayButton.setFocusTraversable(false);

            if (dayOfMonth > 0 && dayOfMonth <= currentMonth.lengthOfMonth()) {
                LocalDate date = currentMonth.atDay(dayOfMonth);
                dayButton.setText(String.valueOf(dayOfMonth));
                dayButton.setDisable(false);
                updateButtonStyle(date, dayButton);
                dayButton.setOnAction(e -> handleDayClick(date, dayButton));
            } else {
                dayButton.setText("");
                dayButton.setDisable(true);
                dayButton.getStyleClass().add("empty-day-button");
            }
            grid.add(dayButton, col, row);
        }
        return grid;
    }

    private void handleDayClick(LocalDate date, Button dayButton) {
        boolean isOriginallyAvailable = originalDisponibilites.contains(date);
        boolean isCurrentlyAvailable = !removedDisponibilites.contains(date)
                && (originalDisponibilites.contains(date) || addedDisponibilites.contains(date));

        if (isCurrentlyAvailable) {
            if (isOriginallyAvailable) {
                removedDisponibilites.add(date);
            } else {
                addedDisponibilites.remove(date);
            }
        } else {
            if (isOriginallyAvailable) {
                removedDisponibilites.remove(date);
            } else {
                addedDisponibilites.add(date);
            }
        }
        updateButtonStyle(date, dayButton);
    }

    private void updateButtonStyle(LocalDate day, Button button) {
        button.getStyleClass().removeAll("dispo-available", "dispo-to-add", "dispo-to-remove");
        boolean isOriginallyAvailable = originalDisponibilites.contains(day);
        boolean isAdded = addedDisponibilites.contains(day);
        boolean isRemoved = removedDisponibilites.contains(day);

        if ((isOriginallyAvailable && !isRemoved) || isAdded) {
            if (isAdded) {
                button.getStyleClass().add("dispo-to-add");
            } else {
                button.getStyleClass().add("dispo-available");
            }
        } else {
            if (isRemoved) {
                button.getStyleClass().add("dispo-to-remove");
            }
        }
    }
}