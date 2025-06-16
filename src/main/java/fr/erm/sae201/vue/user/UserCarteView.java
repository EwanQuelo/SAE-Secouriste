package fr.erm.sae201.vue.user;

import java.net.URL;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class UserCarteView extends BaseView {

    public UserCarteView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Carte");
    }

    @Override
    protected Node createCenterContent() {
        // 1. CRÉATION DU CONTENEUR PRINCIPA
        BorderPane contentLayout = new BorderPane();
        contentLayout.getStyleClass().add("center-content-panel");

        // 1. CRÉATION DE LA PARTIE GAUCHE
        // --------------------------------------------------------
        VBox leftPanel = new VBox(10); 
        leftPanel.setPadding(new Insets(10)); 
        leftPanel.setPrefWidth(300); 
        leftPanel.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #d3d3d3; -fx-border-width: 0 1 0 0;"); 

        Label title = new Label("Mes Affectations");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Une zone de texte pour indiquer ce qui viendra ici.
        // Plus tard, vous remplacerez ce Label par une TableView ou une ListView.
        Label placeholderText = new Label("La liste de vos affectations s'affichera ici.");
        placeholderText.setWrapText(true); 

        // Ajout des éléments au panneau de gauche
        leftPanel.getChildren().addAll(title, placeholderText);
        
        // On place ce panneau à gauche du BorderPane
        contentLayout.setLeft(leftPanel);


        // 2. CRÉATION DE LA PARTIE CENTRALE 
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        URL htmlCarteUrl = getClass().getResource("/html/leaflet_map.html");
        
        if (htmlCarteUrl != null) {
            webEngine.load(htmlCarteUrl.toExternalForm());
        } else {
            // Message d'erreur si le fichier HTML n'est pas trouvé
            System.err.println("Erreur: Le fichier HTML de la carte n'a pas été trouvé dans /resources/html/");
            webEngine.loadContent("<h1>Erreur : Fichier carte non trouvé</h1>");
        }

        // On place la carte au centre du BorderPane
        contentLayout.setCenter(webView);


        // 3. RETOURNER LE CONTENEUR PRINCIPAL
        return contentLayout;
    }
}