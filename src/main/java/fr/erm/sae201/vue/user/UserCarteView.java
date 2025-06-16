package fr.erm.sae201.vue.user;

import fr.erm.sae201.controleur.UserCarteController;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;
import java.util.Locale;

public class UserCarteView extends BaseView {

    private VBox affectationsListVBox;
    private WebView webView;
    private WebEngine webEngine;
    private boolean isMapReady = false;
    private UserCarteController controller;

    public UserCarteView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Carte");
    }

    public void setController(UserCarteController controller) {
        this.controller = controller;
    }

    // --- NEW STATIC METHOD FOR COLOR ---
    /**
     * Provides a consistent color for a DPS based on its ID.
     * Can be used by both the View and the Controller.
     * @param dps The DPS object.
     * @return A hexadecimal color string (e.g., "#2196F3").
     */
    public static String getDpsColor(DPS dps) {
        String[] colors = {"#2196F3", "#4CAF50", "#FF9800", "#f44336", "#9c27b0", "#009688", "#3F51B5", "#FF5722", "#795548", "#607D8B", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722"};
        // Use Math.abs just in case of a negative ID, although unlikely with auto-increment.
        return colors[(int)(Math.abs(dps.getId()) % colors.length)];
    }

    @Override
    protected Node createCenterContent() {
        BorderPane contentLayout = new BorderPane();
        contentLayout.getStyleClass().add("center-content-panel");
        contentLayout.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        HBox splitContent = new HBox(15);
        splitContent.setPadding(new Insets(15));

        VBox leftPanel = createLeftPanel();

        StackPane mapContainer = new StackPane();
        mapContainer.getStyleClass().add("map-panel-container");
        HBox.setHgrow(mapContainer, Priority.ALWAYS);

        webView = new WebView();
        webEngine = webView.getEngine();
        mapContainer.getChildren().add(webView);

        setupWebEngineListener();
        loadMapHtml();
        setupMapResizeListeners(mapContainer);

        splitContent.getChildren().addAll(leftPanel, mapContainer);
        contentLayout.setCenter(splitContent);

        return contentLayout;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.getStyleClass().add("affectations-list-panel");
        leftPanel.setPrefWidth(350);
        leftPanel.setMinWidth(300);

        Label title = new Label("Mes Affectations");
        title.getStyleClass().add("affectations-title");

        affectationsListVBox = new VBox(10);
        affectationsListVBox.getStyleClass().add("affectation-items-container");

        ScrollPane scrollPane = new ScrollPane(affectationsListVBox);
        scrollPane.getStyleClass().add("content-scroll-pane");
        scrollPane.setFitToWidth(true);

        leftPanel.getChildren().addAll(title, scrollPane);
        return leftPanel;
    }
    
    private void setupWebEngineListener() {
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                isMapReady = true;
                System.out.println("VIEW: Map HTML loaded successfully.");
                if (controller != null) {
                    controller.onMapPageReady();
                }
            } else if (newState == Worker.State.FAILED) {
                isMapReady = false;
                System.err.println("VIEW: Failed to load map HTML.");
                webEngine.loadContent("<h1>Error loading map.</h1>");
            }
        });
    }

    private void loadMapHtml() {
        try {
            URL mapUrl = getClass().getResource("/html/leaflet_map.html");
            if (mapUrl == null) throw new NullPointerException("Map HTML file not found.");
            webEngine.load(mapUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading map file: " + e.getMessage());
            webEngine.loadContent("<h1>Map file not found in resources.</h1>");
        }
    }

    private void setupMapResizeListeners(Region mapContainer) {
        mapContainer.heightProperty().addListener((obs, oldV, newV) -> {
            if (isMapReady) Platform.runLater(() -> executeMapScript("if(map) map.invalidateSize();"));
        });
        mapContainer.widthProperty().addListener((obs, oldV, newV) -> {
            if (isMapReady) Platform.runLater(() -> executeMapScript("if(map) map.invalidateSize();"));
        });
    }

    // --- Public methods for the Controller ---
    public void addAffectationToList(Affectation affectation) {
        affectationsListVBox.getChildren().add(createAffectationItemNode(affectation));
    }
    
    public void clearAffectationsList() {
        affectationsListVBox.getChildren().clear();
    }

    public void showNoAffectationsMessage() {
        Label noAffectationLabel = new Label("Aucune affectation trouvée.");
        noAffectationLabel.getStyleClass().add("placeholder-text");
        affectationsListVBox.getChildren().add(noAffectationLabel);
    }
    
    public void centerMapOn(double latitude, double longitude, int zoom) {
        executeMapScript(String.format(Locale.US, "flyTo(%f, %f, %d);", latitude, longitude, zoom));
    }

    public void clearAllMarkersFromMap() {
        executeMapScript("clearAllMarkers();");
    }

    public void executeMapScript(String script) {
        if (isMapReady && Platform.isFxApplicationThread()) {
            try {
                webEngine.executeScript(script);
            } catch (Exception e) {
                System.err.println("Error executing map script: " + script + " | " + e.getMessage());
            }
        } else if (isMapReady) {
            Platform.runLater(() -> executeMapScript(script));
        }
    }

    private Node createAffectationItemNode(Affectation affectation) {
        VBox itemBox = new VBox(5);
        itemBox.getStyleClass().add("affectation-item");

        DPS dps = affectation.getDps();
        Site site = dps.getSite();
        
        HBox titleLine = new HBox(8);
        titleLine.setAlignment(Pos.CENTER_LEFT);

        Label markerNumber = new Label(String.valueOf(dps.getId()));
        markerNumber.getStyleClass().add("affectation-marker-icon-text");
        StackPane markerCircle = new StackPane(markerNumber);
        markerCircle.getStyleClass().add("affectation-marker-icon-circle");

        // IMPROVED: Use the centralized static method for color
        String color = UserCarteView.getDpsColor(dps);
        markerCircle.setStyle("-fx-background-color: " + color + ";");

        Label titleLabel = new Label(dps.getSport().getNom());
        titleLabel.getStyleClass().add("affectation-item-title");
        titleLine.getChildren().addAll(markerCircle, titleLabel);

        Label addressLabel = new Label(site.getNom());
        addressLabel.getStyleClass().add("affectation-item-address");

        Label infoTitleLabel = new Label("Rôle Attribué:");
        infoTitleLabel.getStyleClass().add("affectation-item-info-title");
        Label infoLabel = new Label(affectation.getCompetence().getIntitule());
        infoLabel.getStyleClass().add("affectation-item-info");

        itemBox.getChildren().addAll(titleLine, addressLabel, infoTitleLabel, infoLabel);

        itemBox.setOnMouseClicked(event -> {
            System.out.println("Clicked on assignment: " + site.getNom());
            centerMapOn(site.getLatitude(), site.getLongitude(), 15);
        });

        return itemBox;
    }
}