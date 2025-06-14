package fr.erm.sae201.vue.user;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.utils.RessourceLoader;
import fr.erm.sae201.vue.MainApp; // Import du navigator
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

public class UserNavbar extends HBox {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    // MODIFIÉ : Le constructeur accepte le MainApp pour la navigation
    public UserNavbar(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        super(20);
        this.getStyleClass().add("navbar");
        this.setAlignment(Pos.CENTER_LEFT);

        // MODIFIÉ : On passe les dépendances pour la navigation
        HBox navLinks = createNavLinks(navigator, compte, activeViewName);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox userInfo = createUserInfo(compte);

        this.getChildren().addAll(navLinks, spacer, userInfo);
    }

    // MODIFIÉ : La méthode crée maintenant des boutons fonctionnels
    private HBox createNavLinks(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        HBox navLinks = new HBox(15);
        navLinks.setAlignment(Pos.CENTER_LEFT);

        // Accueil
        Button accueilBtn = createNavButton("Accueil", "Accueil".equals(activeViewName));
        accueilBtn.setOnAction(e -> navigator.showSecouristeDashboard(compte));
        
        // Calendrier
        Button calendrierBtn = createNavButton("Calendrier", "Calendrier".equals(activeViewName));
        calendrierBtn.setOnAction(e -> navigator.showUserCalendrierView(compte));

        // Carte
        Button carteBtn = createNavButton("Carte", "Carte".equals(activeViewName));
        carteBtn.setOnAction(e -> navigator.showUserCarteView(compte));

        // Compétences
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

    // La méthode createUserInfo reste inchangée
    private HBox createUserInfo(CompteUtilisateur compte) {
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

        ImageView profilePic = new ImageView(RessourceLoader.loadImage("default_profile.png"));
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

        userInfo.getChildren().addAll(settingsButton, textInfo, profilePic);
        return userInfo;
    }
}