package fr.erm.sae201.vue.user;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Journee;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
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
    private final SecouristeDAO secouristeDAO;
    private final MainApp navigator;

    private YearMonth currentMonth;
    private VBox mainContainer;

    // Pour gérer l'état des disponibilités
    private Set<LocalDate> originalDisponibilites;
    private Set<LocalDate> addedDisponibilites = new HashSet<>();
    private Set<LocalDate> removedDisponibilites = new HashSet<>();

    public UserDispoView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Disponibilités");
        this.compte = compte;
        this.navigator = navigator;
        this.secouristeDAO = new SecouristeDAO();
        this.currentMonth = YearMonth.now();

        // On récupère les disponibilités initiales une seule fois
        Set<Journee> journees = secouristeDAO.findAvailabilitiesForSecouriste(compte.getIdSecouriste());
        this.originalDisponibilites = journees.stream().map(Journee::getDate).collect(Collectors.toSet());

        populateView();
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
        // ... (Code identique à la proposition précédente)
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

        // En-têtes des jours (L, M, M, J, V, S, D)
        String[] daysOfWeek = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" };
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.getStyleClass().add("dispo-day-header");
            GridPane.setHalignment(dayLabel, HPos.CENTER);
            grid.add(dayLabel, i, 0);
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeekOffset = firstDayOfMonth.getDayOfWeek().getValue() - 1; // 0 pour Lundi

        // --- DÉBUT DE LA MODIFICATION : On dessine TOUJOURS 6 semaines ---

        // On boucle sur 42 cases (6 semaines * 7 jours)
        for (int i = 0; i < 42; i++) {
            int row = i / 7 + 1; // +1 pour passer la ligne d'en-têtes
            int col = i % 7;

            int dayOfMonth = i - dayOfWeekOffset + 1;

            Button dayButton = new Button();
            dayButton.getStyleClass().add("dispo-day-button");
            dayButton.setFocusTraversable(false); // Empêche la navigation au clavier, plus propre

            // On vérifie si ce jour appartient bien au mois en cours
            if (dayOfMonth > 0 && dayOfMonth <= currentMonth.lengthOfMonth()) {
                // C'est un vrai jour du mois
                LocalDate date = currentMonth.atDay(dayOfMonth);
                dayButton.setText(String.valueOf(dayOfMonth));
                dayButton.setDisable(false); // Le bouton est cliquable

                updateButtonStyle(date, dayButton); // Applique le style initial
                dayButton.setOnAction(e -> handleDayClick(date, dayButton));

            } else {
                // C'est une case vide (jour du mois précédent ou suivant)
                dayButton.setText("");
                dayButton.setDisable(true); // Le bouton est non cliquable
                dayButton.getStyleClass().add("empty-day-button"); // Style pour le rendre "invisible"
            }

            grid.add(dayButton, col, row);
        }
        // --- FIN DE LA MODIFICATION ---

        return grid;
    }

    private void handleDayClick(LocalDate date, Button dayButton) {
        boolean isOriginallyAvailable = originalDisponibilites.contains(date);
        boolean isCurrentlyAvailable = !removedDisponibilites.contains(date)
                && (originalDisponibilites.contains(date) || addedDisponibilites.contains(date));

        if (isCurrentlyAvailable) {
            // Le rendre indisponible
            if (isOriginallyAvailable) {
                removedDisponibilites.add(date); // Marquer pour suppression
            } else {
                addedDisponibilites.remove(date); // Annuler un ajout
            }
        } else {
            // Le rendre disponible
            if (isOriginallyAvailable) {
                removedDisponibilites.remove(date); // Annuler une suppression
            } else {
                addedDisponibilites.add(date); // Marquer pour ajout
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
            // Il est ou sera disponible
            if (isAdded) {
                button.getStyleClass().add("dispo-to-add"); // Vert clair pour les ajouts
            } else {
                button.getStyleClass().add("dispo-available"); // Vert pour les dispos existantes
            }
        } else {
            // Il n'est pas ou ne sera plus disponible
            if (isRemoved) {
                button.getStyleClass().add("dispo-to-remove"); // Rouge pour les suppressions
            }
        }
    }

    private Node createActionButtons() {
        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.getStyleClass().add("action-button-bar");

        Button saveButton = new Button("Enregistrer les modifications");
        // On réutilise le style du bouton de connexion principal
        saveButton.getStyleClass().add("login-button");
        saveButton.setOnAction(e -> saveChanges());

        Button cancelButton = new Button("Annuler");
        // On réutilise le style du bouton d'inscription, plus sombre
        cancelButton.getStyleClass().add("signup-button");
        cancelButton.setOnAction(e -> cancelChanges());

        buttonBar.getChildren().addAll(saveButton, cancelButton);
        return buttonBar;
    }

    private void saveChanges() {
        for (LocalDate date : addedDisponibilites) {
            secouristeDAO.addAvailability(compte.getIdSecouriste(), date);
        }
        for (LocalDate date : removedDisponibilites) {
            secouristeDAO.removeAvailability(compte.getIdSecouriste(), date);
        }

        // On utilise la variable navigator stockée dans la classe
        navigator.showUserCalendrierView(compte);
    }

    private void cancelChanges() {
        // On utilise la variable navigator stockée dans la classe
        navigator.showUserCalendrierView(compte);
    }
}