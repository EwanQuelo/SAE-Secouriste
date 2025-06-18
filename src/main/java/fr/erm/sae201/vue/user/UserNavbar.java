package fr.erm.sae201.vue.user;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.utils.RessourceLoader;
import fr.erm.sae201.vue.MainApp;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

public class UserNavbar extends HBox {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    public UserNavbar(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        super(20);
        this.getStyleClass().add("navbar");
        this.setAlignment(Pos.CENTER_LEFT);

        HBox navLinks = createNavLinks(navigator, compte, activeViewName);

        // pour le crayon modifier les disponibilités
        if ("Calendrier".equals(activeViewName)) {
            Button dispoButton = createDispoButton(navigator, compte);
            navLinks.getChildren().add(dispoButton);
            HBox.setMargin(dispoButton, new javafx.geometry.Insets(0, 0, 0, 20)); 
        }
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox userInfo = createUserInfo(navigator, compte);

        this.getChildren().addAll(navLinks, spacer, userInfo);
    }

    private HBox createNavLinks(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        HBox navLinks = new HBox(15);
        navLinks.setAlignment(Pos.CENTER_LEFT);

        Button accueilBtn = createNavButton("Accueil", "Accueil".equals(activeViewName));
        accueilBtn.setOnAction(e -> navigator.showSecouristeDashboard(compte));
        
        Button calendrierBtn = createNavButton("Calendrier", "Calendrier".equals(activeViewName));
        calendrierBtn.setOnAction(e -> navigator.showUserCalendrierView(compte));

        Button carteBtn = createNavButton("Carte", "Carte".equals(activeViewName));
        carteBtn.setOnAction(e -> navigator.showUserCarteView(compte));

        Button competencesBtn = createNavButton("Compétences", "Compétences".equals(activeViewName));
        competencesBtn.setOnAction(e -> navigator.showUserCompetencesView(compte));

        navLinks.getChildren().addAll(accueilBtn, calendrierBtn, carteBtn, competencesBtn);
        return navLinks;
    }

    private Button createNavButton(String name, boolean isActive) {
        Button navButton = new Button(name);
        navButton.getStyleClass().add("nav-button");
        if (isActive) {
            navButton.getStyleClass().add("nav-button-active");
        }
        return navButton;
    }

    private Button createDispoButton(MainApp navigator, CompteUtilisateur compte) {
    Button dispoButton = new Button();

    // On dessine le crayon directement en code, pas besoin de fichier image.
    SVGPath pencilIcon = new SVGPath();
    pencilIcon.setContent("M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10");
    pencilIcon.getStyleClass().add("pencil-icon");
    dispoButton.setGraphic(pencilIcon);
    dispoButton.getStyleClass().add("dispo-button");
    
    dispoButton.setOnAction(e -> navigator.showUserDispoView(compte));
    
    return dispoButton;
}

    private HBox createUserInfo(MainApp navigator, CompteUtilisateur compte) {
        HBox userInfo = new HBox(15);
        userInfo.setAlignment(Pos.CENTER_RIGHT);

        Secouriste secouriste = null;
        if (compte.getIdSecouriste() != null) {
            secouriste = secouristeDAO.findByID(compte.getIdSecouriste());
        }

        String nomComplet = secouriste != null ? secouriste.getPrenom() + " " + secouriste.getNom() : compte.getLogin();
        String role = "Secouriste";

        Label nameLabel = new Label(nomComplet);
        nameLabel.getStyleClass().add("user-info-text");
        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("user-info-role");

        VBox textInfo = new VBox(-2, nameLabel, roleLabel);
        textInfo.setAlignment(Pos.CENTER_LEFT);

        ImageView profilePic = new ImageView(RessourceLoader.loadImage("profile-uniforme.png"));
        profilePic.setFitHeight(40);
        profilePic.setFitWidth(40);
        Circle clip = new Circle(20, 20, 20);
        profilePic.setClip(clip);

        Button settingsButton = new Button();
        ImageView settingsIcon = new ImageView(RessourceLoader.loadImage("settings_icon.png"));
        settingsIcon.setFitHeight(24);
        settingsIcon.setFitWidth(24);
        settingsButton.setGraphic(settingsIcon);
        settingsButton.getStyleClass().add("settings-button");
        
        settingsButton.setOnAction(e -> navigator.showUserParametreView(compte));

        // J'ai réorganisé l'ordre pour correspondre à votre screenshot
        userInfo.getChildren().addAll( textInfo, profilePic, settingsButton);
        return userInfo;
    }
}