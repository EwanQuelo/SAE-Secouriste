package fr.erm.sae201.vue.user;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp; // Import du navigator
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SecouristeDashboard extends BaseView {

    /**
     * MODIFIÉ : Le constructeur accepte et transmet le navigator.
     * @param navigator L'instance de MainApp pour la navigation.
     * @param compte L'utilisateur connecté.
     */
    public SecouristeDashboard(MainApp navigator, CompteUtilisateur compte) {
        // MODIFIÉ : On passe le navigator au constructeur parent.
        super(navigator, compte, "Accueil");
    }

    @Override
    protected Node createCenterContent() {
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getStyleClass().add("center-content-panel");

        Label welcomeLabel = new Label("Bienvenue sur votre tableau de bord !");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #333;");

        Label infoLabel = new Label("C'est ici que vous verrez le calendrier, vos affectations à venir, etc.");
        infoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        centerContent.getChildren().addAll(welcomeLabel, infoLabel);
        return centerContent;
    }
}