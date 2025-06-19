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

/**
 * La vue permettant à un utilisateur secouriste de gérer ses disponibilités.
 * Elle affiche un calendrier mensuel où l'utilisateur peut cliquer sur les jours
 * pour les marquer comme disponibles ou indisponibles. Les changements sont suivis
 * et peuvent être enregistrés ou annulés.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
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

    /**
     * Construit la vue des disponibilités pour le secouriste.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'utilisateur connecté.
     */
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
    
    /**
     * Lie une action au bouton "Enregistrer".
     *
     * @param handler Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setSaveAction(EventHandler<ActionEvent> handler) {
        saveButton.setOnAction(handler);
    }
    
    /**
     * Lie une action au bouton "Annuler".
     *
     * @param handler Le gestionnaire d'événement à exécuter lors du clic.
     */
    public void setCancelAction(EventHandler<ActionEvent> handler) {
        cancelButton.setOnAction(handler);
    }
    
    /**
     * Récupère l'ensemble des dates que l'utilisateur a marquées comme nouvellement disponibles.
     *
     * @return Une copie de l'ensemble des disponibilités ajoutées.
     */
    public Set<LocalDate> getAddedDisponibilites() {
        return new HashSet<>(addedDisponibilites);
    }

    /**
     * Récupère l'ensemble des dates que l'utilisateur a marquées comme nouvellement indisponibles.
     *
     * @return Une copie de l'ensemble des disponibilités supprimées.
     */
    public Set<LocalDate> getRemovedDisponibilites() {
        return new HashSet<>(removedDisponibilites);
    }

    /**
     * Crée la barre de boutons d'action "Enregistrer" et "Annuler".
     *
     * @return Un HBox contenant les boutons d'action.
     */
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
    
    /**
     * Crée et retourne le contenu central de la vue.
     *
     * @return Le conteneur VBox principal de la vue.
     */
    @Override
    protected Node createCenterContent() {
        mainContainer = new VBox(10);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.getStyleClass().add("dispo-container");
        return mainContainer;
    }

    /**
     * Peuple ou repeuple la vue du calendrier avec tous ses composants.
     */
    private void populateView() {
        mainContainer.getChildren().clear();

        Node monthNavigationBar = createMonthNavigationBar();
        Node calendarGrid = createCalendarGrid();
        Node actionButtons = createActionButtons();

        mainContainer.getChildren().addAll(monthNavigationBar, calendarGrid, actionButtons);
    }

    /**
     * Change le mois affiché dans le calendrier.
     *
     * @param monthsToAdd Le nombre de mois à avancer (positif) ou reculer (négatif).
     */
    private void changeMonth(int monthsToAdd) {
        currentMonth = currentMonth.plusMonths(monthsToAdd);
        populateView();
    }

    /**
     * Crée la barre de navigation du mois avec les flèches et le nom du mois.
     *
     * @return Un HBox représentant la barre de navigation.
     */
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

    /**
     * Crée la grille du calendrier pour le mois en cours.
     *
     * @return Un GridPane peuplé avec les jours du mois.
     */
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

    /**
     * Gère le clic sur un jour du calendrier, en basculant son état de disponibilité.
     *
     * @param date La date du jour cliqué.
     * @param dayButton Le bouton associé au jour.
     */
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

    /**
     * Met à jour le style visuel d'un bouton de jour en fonction de son état de disponibilité.
     *
     * @param day La date du jour à mettre à jour.
     * @param button Le bouton à styliser.
     */
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