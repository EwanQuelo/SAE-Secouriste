package fr.erm.sae201.vue.admin;

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

public class AdminNavbar extends HBox {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    public AdminNavbar(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        super(20);
        this.getStyleClass().add("navbar");
        this.setAlignment(Pos.CENTER_LEFT);

        HBox navLinks = createNavLinks(navigator, compte, activeViewName);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox userInfo = createUserInfo(navigator, compte);

        this.getChildren().addAll(navLinks, spacer, userInfo);
    }

    private HBox createNavLinks(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        HBox navLinks = new HBox(15);
        navLinks.setAlignment(Pos.CENTER_LEFT);

        Button accueilBtn = createNavButton("Accueil", "Accueil".equals(activeViewName));
        accueilBtn.setOnAction(e -> navigator.showAdminDashboard(compte));

        Button dispositifBtn = createNavButton("Dispositif", "Dispositif".equals(activeViewName));
        dispositifBtn.setOnAction(e -> navigator.showAdminDispositifView(compte));

        Button utilisateursBtn = createNavButton("Utilisateurs", "Utilisateurs".equals(activeViewName));
        utilisateursBtn.setOnAction(e -> navigator.showAdminUtilisateursView(compte));

        Button affectationsBtn = createNavButton("Affectations", "Affectations".equals(activeViewName));
        affectationsBtn.setOnAction(e -> navigator.showAdminAffectationsView(compte));
        
        navLinks.getChildren().addAll(accueilBtn, dispositifBtn, utilisateursBtn, affectationsBtn);
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

    private HBox createUserInfo(MainApp navigator, CompteUtilisateur compte) {
        HBox userInfo = new HBox(15);
        userInfo.setAlignment(Pos.CENTER_RIGHT);

        Button settingsButton = new Button();
        ImageView settingsIcon = new ImageView(RessourceLoader.loadImage("settings_icon.png"));
        settingsIcon.setFitHeight(24);
        settingsIcon.setFitWidth(24);
        settingsButton.setGraphic(settingsIcon);
        settingsButton.getStyleClass().add("settings-button");
        
        settingsButton.setOnAction(e -> navigator.showUserParametreView(compte));
        
        Secouriste secouriste = null;
        if (compte.getIdSecouriste() != null) {
            secouriste = secouristeDAO.findByID(compte.getIdSecouriste());
        }

        String nomComplet = secouriste != null ? secouriste.getPrenom() + " " + secouriste.getNom() : "Super Utilisateur";
        String role = "Administrateur";

        Label nameLabel = new Label(nomComplet);
        nameLabel.getStyleClass().add("user-info-text");
        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("user-info-role");

        VBox textInfo = new VBox(-2, nameLabel, roleLabel);
        textInfo.setAlignment(Pos.CENTER_LEFT);

        ImageView profilePic = new ImageView(RessourceLoader.loadImage("admin_profile.png"));
        profilePic.setFitHeight(40);
        profilePic.setFitWidth(40);
        Circle clip = new Circle(20, 20, 20);
        profilePic.setClip(clip);
        
        userInfo.getChildren().addAll(textInfo, profilePic, settingsButton);
        return userInfo;
    }
}