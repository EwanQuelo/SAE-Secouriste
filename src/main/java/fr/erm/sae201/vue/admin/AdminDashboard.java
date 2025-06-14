package fr.erm.sae201.vue.admin;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Le tableau de bord spécifique pour un administrateur.
 * Il étend BaseView pour obtenir la structure commune (fond + navbar)
 * et fournit son contenu via createCenterContent().
 */
public class AdminDashboard extends BaseView {

    /**
     * Constructeur du tableau de bord administrateur.
     * @param navigator L'instance de MainApp pour la navigation.
     * @param compte L'utilisateur connecté.
     */
    public AdminDashboard(MainApp navigator, CompteUtilisateur compte) {
        // On appelle le constructeur parent en lui passant le compte et le nom de la vue active ("Accueil")
        super(navigator, compte, "Accueil");
    }

    /**
     * Crée et retourne le contenu spécifique à ce tableau de bord.
     * @return Un Node contenant les éléments du tableau de bord.
     */
    @Override
    protected Node createCenterContent() {
        // Créez ici le contenu de votre tableau de bord
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getStyleClass().add("center-content-panel");

        Label welcomeLabel = new Label("Bienvenue sur le tableau de bord Administrateur !");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #333;");

        Label infoLabel = new Label("Ici, vous pouvez gérer les dispositifs, les utilisateurs et les affectations.");
        infoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        centerContent.getChildren().addAll(welcomeLabel, infoLabel);

        return centerContent;
    }
}