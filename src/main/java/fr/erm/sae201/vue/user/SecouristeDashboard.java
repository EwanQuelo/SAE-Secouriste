package fr.erm.sae201.vue.user;

import fr.erm.sae201.controleur.user.SecouristeDashboardController;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Site;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * La vue du tableau de bord principal pour un utilisateur secouriste.
 * Elle se compose de deux parties : un panneau d'emploi du temps journalier à gauche,
 * qui affiche les affectations sous forme de blocs sur une chronologie, et un panneau
 * de carte interactive à droite, qui géolocalise les sites des affectations.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SecouristeDashboard extends BaseView {

    private WebEngine webEngine;
    private boolean isMapReady = false;
    private SecouristeDashboardController controller;
    private Label dateHeaderLabel;
    private AnchorPane timelinePane;
    private Label mapFooterLabel;
    
    private final double HOUR_HEIGHT = 80.0;
    private final int START_HOUR = 8;
    private final int END_HOUR = 18;

    /**
     * Construit le tableau de bord du secouriste.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'utilisateur secouriste connecté.
     */
    public SecouristeDashboard(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Accueil");
        new SecouristeDashboardController(this, compte);
    }

    /**
     * Lie la vue à son contrôleur.
     *
     * @param controller Le contrôleur à associer à cette vue.
     */
    public void setController(SecouristeDashboardController controller) {
        this.controller = controller;
    }

    /**
     * Crée et retourne le contenu central de la vue, qui est une HBox
     * contenant le panneau de l'emploi du temps et le panneau de la carte.
     *
     * @return Le nœud (Node) principal du contenu de la vue.
     */
    @Override
    protected Node createCenterContent() {
        HBox mainContainer = new HBox(20);
        mainContainer.setPadding(new Insets(10, 20, 10, 20));

        Node schedulePanel = createSchedulePanel();
        Node mapPanel = createMapPanel();
        HBox.setHgrow(mapPanel, Priority.ALWAYS);

        mainContainer.getChildren().addAll(schedulePanel, mapPanel);
        return mainContainer;
    }

    /**
     * Crée le panneau de gauche affichant l'emploi du temps du jour.
     *
     * @return Un nœud (Node) représentant le panneau de l'emploi du temps.
     */
    private Node createSchedulePanel() {
        VBox schedulePanel = new VBox(0);
        schedulePanel.getStyleClass().add("schedule-panel-card"); 
        schedulePanel.setPrefWidth(280);
        schedulePanel.setMinWidth(280);

        dateHeaderLabel = new Label("Chargement...");
        dateHeaderLabel.getStyleClass().add("schedule-header");
        dateHeaderLabel.setMaxWidth(Double.MAX_VALUE);

        timelinePane = new AnchorPane();
        timelinePane.setPadding(new Insets(10, 10, 10, 0));

        for (int hour = START_HOUR; hour <= END_HOUR; hour++) {
            double yPos = (hour - START_HOUR) * HOUR_HEIGHT;
            Label hourLabel = new Label(String.format("%02dh", hour));
            hourLabel.getStyleClass().add("timeline-hour-label");
            AnchorPane.setTopAnchor(hourLabel, yPos - 8);
            AnchorPane.setLeftAnchor(hourLabel, 10.0);
            timelinePane.getChildren().add(hourLabel);

            if (hour < END_HOUR) {
                Line line = new Line(50, yPos + (HOUR_HEIGHT / 2), 270, yPos + (HOUR_HEIGHT / 2));
                line.getStyleClass().add("timeline-half-hour-line");
                timelinePane.getChildren().add(line);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(timelinePane);
        scrollPane.getStyleClass().add("schedule-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        schedulePanel.getChildren().addAll(dateHeaderLabel, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return schedulePanel;
    }

    /**
     * Crée le panneau de droite affichant la carte interactive.
     *
     * @return Un nœud (Node) représentant le panneau de la carte.
     */
    private Node createMapPanel() {
        VBox mapCard = new VBox(0);
        mapCard.getStyleClass().add("map-card");

        StackPane mapContainer = new StackPane();
        VBox.setVgrow(mapContainer, Priority.ALWAYS);

        WebView webView = new WebView();
        webEngine = webView.getEngine();
        mapContainer.getChildren().add(webView);

        mapFooterLabel = new Label("Chargement des affectations...");
        mapFooterLabel.getStyleClass().add("map-footer-label");
        HBox mapFooter = new HBox(mapFooterLabel);
        mapFooter.getStyleClass().add("map-footer");
        mapFooter.setAlignment(Pos.CENTER);

        setupWebEngine();
        
        mapCard.getChildren().addAll(mapContainer, mapFooter);
        return mapCard;
    }
    
    /**
     * Peuple l'emploi du temps avec les affectations pour une date donnée.
     *
     * @param dailyAssignments La liste des affectations du jour.
     * @param date La date à afficher.
     */
    public void populateSchedule(List<Affectation> dailyAssignments, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH);
        String formattedDate = date.format(formatter);
        dateHeaderLabel.setText(formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1));
        
        timelinePane.getChildren().removeIf(node -> node.getStyleClass().contains("timeline-appointment-box") || "no-assignment-label".equals(node.getId()));
        
        timelinePane.getChildren().removeIf(node -> "current-time-line".equals(node.getId()));

        if (dailyAssignments.isEmpty()) {
            Label noAssignmentLabel = new Label("Aucune affectation pour ce jour.");
            noAssignmentLabel.getStyleClass().add("appointment-subtitle");
            noAssignmentLabel.setId("no-assignment-label");
            AnchorPane.setTopAnchor(noAssignmentLabel, 20.0);
            AnchorPane.setLeftAnchor(noAssignmentLabel, 60.0);
            timelinePane.getChildren().add(noAssignmentLabel);
            return;
        }

        for (Affectation affectation : dailyAssignments) {
            DPS dps = affectation.getDps();
            int startH = dps.getHoraireDepart()[0];
            int startM = dps.getHoraireDepart()[1];
            int endH = dps.getHoraireFin()[0];
            int endM = dps.getHoraireFin()[1];

            double startOffsetInMinutes = (startH - START_HOUR) * 60 + startM;
            double topOffset = startOffsetInMinutes * (HOUR_HEIGHT / 60.0);

            double durationInMinutes = (endH * 60 + endM) - (startH * 60 + startM);
            double eventNodeHeight = Math.max(20, durationInMinutes * (HOUR_HEIGHT / 60.0));

            String title = dps.getSport().getNom() + " - " + affectation.getCompetence().getIntitule();
            String subtitle = dps.getSite().getNom();
            String time = String.format("%02d:%02d - %02d:%02d", startH, startM, endH, endM);
            Node appointmentNode = createAppointmentBox(title, subtitle, time);
            appointmentNode.getStyleClass().add("timeline-appointment-box");

            AnchorPane.setTopAnchor(appointmentNode, topOffset);
            AnchorPane.setLeftAnchor(appointmentNode, 60.0);
            AnchorPane.setRightAnchor(appointmentNode, 10.0);
            ((Region) appointmentNode).setPrefHeight(eventNodeHeight);
            ((Region) appointmentNode).setMinHeight(eventNodeHeight);

            timelinePane.getChildren().add(appointmentNode);
        }
        
        if (date.equals(LocalDate.now())) {
            double nowInMinutes = (LocalTime.now().getHour() - START_HOUR) * 60 + LocalTime.now().getMinute();
            double currentTimeOffset = nowInMinutes * (HOUR_HEIGHT / 60.0);
            if (currentTimeOffset >= 0 && currentTimeOffset < (END_HOUR - START_HOUR + 1) * HOUR_HEIGHT) {
                Line currentTimeLine = new Line(0, currentTimeOffset, 280, currentTimeOffset);
                currentTimeLine.setStroke(Color.RED);
                currentTimeLine.setStrokeWidth(2.0);
                currentTimeLine.setId("current-time-line");
                timelinePane.getChildren().add(currentTimeLine);
            }
        }
    }

    /**
     * Crée une boîte visuelle pour une affectation dans la chronologie.
     *
     * @param title Le titre principal de l'affectation.
     * @param subtitle Le sous-titre (généralement le lieu).
     * @param time La plage horaire.
     * @return Un nœud (Node) représentant la boîte de l'affectation.
     */
    private Node createAppointmentBox(String title, String subtitle, String time) {
        VBox box = new VBox(2);
        box.setPadding(new Insets(8, 8, 8, 8));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("appointment-title");
        titleLabel.setWrapText(true);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("appointment-subtitle");
        subtitleLabel.setWrapText(true);
        
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("appointment-subtitle");

        box.getChildren().addAll(titleLabel, subtitleLabel, timeLabel);
        return box;
    }
    
    /**
     * Met à jour le texte du pied de page de la carte.
     *
     * @param text Le texte à afficher.
     */
    public void setMapFooterText(String text) {
        mapFooterLabel.setText(text);
    }
    
    /**
     * Configure le moteur web pour charger la carte et gérer les états de chargement.
     */
    private void setupWebEngine() {
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                isMapReady = true;
                if (controller != null) controller.onMapReady();
            } else if (newState == Worker.State.FAILED) {
                isMapReady = false;
                webEngine.loadContent("<h1>Error loading map.</h1>");
            }
        });
        
        try {
            URL mapUrl = getClass().getResource("/html/leaflet_map.html");
            if (mapUrl == null) throw new NullPointerException("Map HTML file not found.");
            webEngine.load(mapUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Failed to load map HTML: " + e.getMessage());
            webEngine.loadContent("<h1>Map file could not be loaded.</h1>");
        }
    }

    /**
     * Ajoute un marqueur sur la carte pour une affectation donnée.
     *
     * @param affectation L'affectation à localiser sur la carte.
     */
    public void addMarkerToMap(Affectation affectation) {
        Site site = affectation.getDps().getSite();
        String popupText = String.format("<b>%s</b><br>%s",
                affectation.getDps().getSport().getNom().replace("'", "\\'"),
                site.getNom().replace("'", "\\'")
        );
        String script = String.format(Locale.US, "addDefaultMarker('affectation_%d', %f, %f, '%s');",
            affectation.getDps().getId(),
            site.getLatitude(),
            site.getLongitude(),
            popupText
        );
        executeMapScript(script);
    }
    
    /**
     * Centre la carte sur une coordonnée géographique spécifique avec un niveau de zoom.
     *
     * @param lat La latitude du centre.
     * @param lon La longitude du centre.
     * @param zoom Le niveau de zoom (plus le nombre est élevé, plus le zoom est proche).
     */
    public void centerMap(double lat, double lon, int zoom) {
        executeMapScript(String.format(Locale.US, "flyTo(%f, %f, %d);", lat, lon, zoom));
    }

    /**
     * Efface tous les marqueurs actuellement affichés sur la carte.
     */
    public void clearMapMarkers() {
        executeMapScript("clearAllMarkers();");
    }

    /**
     * Affiche un message d'erreur dans le panneau de l'emploi du temps.
     *
     * @param message Le message d'erreur à afficher.
     */
    public void showError(String message) {
        timelinePane.getChildren().clear();
        Label errorLabel = new Label(message);
        errorLabel.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(errorLabel, 20.0);
        AnchorPane.setLeftAnchor(errorLabel, 60.0);
        timelinePane.getChildren().add(errorLabel);
    }
    
    /**
     * Exécute un script JavaScript sur le moteur web de la carte de manière sécurisée.
     *
     * @param script Le script JavaScript à exécuter.
     */
    private void executeMapScript(String script) {
        // S'assure que le script est exécuté sur le thread de l'application JavaFX pour éviter les erreurs.
        if (isMapReady && Platform.isFxApplicationThread()) {
            webEngine.executeScript(script);
        } else if (isMapReady) {
            Platform.runLater(() -> executeMapScript(script));
        }
    }
}