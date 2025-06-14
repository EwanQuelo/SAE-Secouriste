package fr.erm.sae201.vue.user;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class UserCalendrierView extends BaseView {

    public UserCalendrierView(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Calendrier");
    }

    @Override
    protected Node createCenterContent() {
        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getStyleClass().add("center-content-panel");

        Label placeholderLabel = new Label("Ceci est la vue du Calendrier.");
        placeholderLabel.setStyle("-fx-font-size: 20px;");

        centerContent.getChildren().add(placeholderLabel);
        return centerContent;
    }
}