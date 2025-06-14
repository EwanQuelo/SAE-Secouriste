package fr.erm.sae201.vue.admin;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AdminDispositifView extends BaseView {

    public AdminDispositifView(MainApp navigator, CompteUtilisateur compte) {
        // Le 3ème paramètre "Dispositif" assure que le bon bouton sera souligné
        super(navigator, compte, "Dispositif");
    }

    @Override
    protected Node createCenterContent() {
        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getStyleClass().add("center-content-panel");

        Label placeholderLabel = new Label("Ceci est la vue de gestion des Dispositifs (DPS).");
        placeholderLabel.setStyle("-fx-font-size: 20px;");

        centerContent.getChildren().add(placeholderLabel);
        return centerContent;
    }
}