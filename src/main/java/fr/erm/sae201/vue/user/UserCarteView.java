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

/**
 * La vue "Carte" pour l'utilisateur secouriste.
 * Elle affiche une liste de toutes les affectations de l'utilisateur à gauche et une
 * carte interactive à droite. La carte affiche des marqueurs colorés pour chaque DPS.
 * Un clic sur un élément de la liste centre la carte sur le marqueur correspondant.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class UserCarteView extends BaseView {

    private VBox affectationsListVBox;
    private WebView webView;
    private WebEngine webEngine;
    private boolean isMapReady = false;
    private UserCarteController controller;

    /**
     * Construit la vue de la carte pour le secouriste.
     *
     * @param navigator L'instance principale de l'application pour la navigation.
     * @param compte Le compte de l'utilisateur connecté.
     */
    public UserCarteView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Carte");
    }

    /**
     * Lie la vue à son contrôleur.
     *
     * @param controller Le contrôleur à associer à cette vue.
     */
    public void setController(UserCarteController controller) {
        this.controller = controller;
    }

    /**
     * Fournit une couleur constante pour un DPS donné en se basant sur son identifiant.
     *
     * @param dps Le DPS pour lequel obtenir une couleur.
     * @return Une chaîne de caractères hexadécimale représentant la couleur (ex: "#2196F3").
     */
    public static String getDpsColor(DPS dps) {
        String[] colors = {"#2196F3", "#4CAF50", "#FF9800", "#f44336", "#9c27b0", "#009688", "#3F51B5", "#FF5722", "#795548", "#607D8B", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722"};
        // Utilisation de Math.abs au cas où un ID serait négatif, bien que peu probable.
        return colors[(int)(Math.abs(dps.getId()) % colors.length)];
    }

    /**
     * Crée et retourne le contenu central de la vue, composé d'un panneau latéral
     * pour la liste des affectations et d'un panneau principal pour la carte.
     *
     * @return Le nœud (Node) principal du contenu de la vue.
     */
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

    /**
     * Crée le panneau de gauche qui contient la liste des affectations.
     *
     * @return Un VBox contenant le titre et la liste déroulante des affectations.
     */
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
    
    /**
     * Configure un écouteur sur le moteur web pour savoir quand la page de la carte est prête.
     */
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

    /**
     * Charge le fichier HTML contenant la carte Leaflet dans le WebView.
     */
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

    /**
     * Ajoute des écouteurs sur la taille du conteneur de la carte pour la redessiner
     * en cas de redimensionnement de la fenêtre, évitant ainsi les problèmes d'affichage.
     *
     * @param mapContainer La région (le conteneur) de la carte.
     */
    private void setupMapResizeListeners(Region mapContainer) {
        mapContainer.heightProperty().addListener((obs, oldV, newV) -> {
            if (isMapReady) Platform.runLater(() -> executeMapScript("if(map) map.invalidateSize();"));
        });
        mapContainer.widthProperty().addListener((obs, oldV, newV) -> {
            if (isMapReady) Platform.runLater(() -> executeMapScript("if(map) map.invalidateSize();"));
        });
    }

    /**
     * Ajoute une représentation visuelle d'une affectation à la liste de gauche.
     *
     * @param affectation L'affectation à ajouter.
     */
    public void addAffectationToList(Affectation affectation) {
        affectationsListVBox.getChildren().add(createAffectationItemNode(affectation));
    }
    
    /**
     * Vide la liste des affectations affichée à gauche.
     */
    public void clearAffectationsList() {
        affectationsListVBox.getChildren().clear();
    }

    /**
     * Affiche un message dans la liste de gauche indiquant qu'aucune affectation n'a été trouvée.
     */
    public void showNoAffectationsMessage() {
        Label noAffectationLabel = new Label("Aucune affectation trouvée.");
        noAffectationLabel.getStyleClass().add("placeholder-text");
        affectationsListVBox.getChildren().add(noAffectationLabel);
    }
    
    /**
     * Centre la carte sur des coordonnées géographiques données avec un niveau de zoom.
     *
     * @param latitude La latitude du point central.
     * @param longitude La longitude du point central.
     * @param zoom Le niveau de zoom.
     */
    public void centerMapOn(double latitude, double longitude, int zoom) {
        executeMapScript(String.format(Locale.US, "flyTo(%f, %f, %d);", latitude, longitude, zoom));
    }

    /**
     * Supprime tous les marqueurs actuellement présents sur la carte.
     */
    public void clearAllMarkersFromMap() {
        executeMapScript("clearAllMarkers();");
    }

    /**
     * Exécute un script JavaScript sur la carte de manière sécurisée.
     *
     * @param script La chaîne de caractères du script à exécuter.
     */
    public void executeMapScript(String script) {
        if (isMapReady && Platform.isFxApplicationThread()) {
            try {
                webEngine.executeScript(script);
            } catch (Exception e) {
                System.err.println("Error executing map script: " + script + " | " + e.getMessage());
            }
        } else if (isMapReady) {
            // S'assure que le script est exécuté sur le thread de l'application JavaFX.
            Platform.runLater(() -> executeMapScript(script));
        }
    }

    /**
     * Crée le nœud graphique pour un élément de la liste des affectations.
     *
     * @param affectation L'affectation à représenter.
     * @return Un nœud (Node) formaté pour être ajouté à la liste.
     */
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