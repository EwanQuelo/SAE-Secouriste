// src/main/java/fr/erm/sae201/vue/admin/AdminNavbar.java
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

/**
 * La barre de navigation horizontale affichée en haut des vues de l'interface d'administration.
 * Elle contient les liens de navigation vers les différentes sections (Accueil, Dispositifs, etc.),
 * ainsi que les informations de l'utilisateur connecté et un bouton d'accès aux paramètres.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminNavbar extends HBox {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    /**
     * Construit la barre de navigation de l'administrateur.
     *
     * @param navigator L'instance principale de l'application, utilisée pour la navigation entre les vues.
     * @param compte Le compte de l'administrateur actuellement connecté.
     * @param activeViewName Le nom de la vue actuellement active, pour mettre en surbrillance le bouton correspondant.
     */
    public AdminNavbar(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        super(20);
        this.getStyleClass().add("navbar");
        this.setAlignment(Pos.CENTER_LEFT);

        HBox navLinks = createNavLinks(navigator, compte, activeViewName);
        Region spacer = new Region();
        // Espace flexible pour pousser les informations utilisateur vers la droite de la barre.
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox userInfo = createUserInfo(navigator, compte);

        this.getChildren().addAll(navLinks, spacer, userInfo);
    }

    /**
     * Crée le conteneur avec les boutons de navigation vers les différentes sections de l'administration.
     *
     * @param navigator L'objet MainApp pour gérer les actions de navigation.
     * @param compte Le compte de l'utilisateur connecté.
     * @param activeViewName Le nom de la vue active pour le style du bouton.
     * @return Un HBox contenant tous les boutons de navigation.
     */
    private HBox createNavLinks(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        HBox navLinks = new HBox(15);
        navLinks.setAlignment(Pos.CENTER_LEFT);

        Button accueilBtn = createNavButton("Accueil", "Accueil".equals(activeViewName));
        accueilBtn.setOnAction(e -> navigator.showAdminDashboard(compte));

        Button dispositifBtn = createNavButton("Dispositifs", "Dispositifs".equals(activeViewName));
        dispositifBtn.setOnAction(e -> navigator.showAdminDispositifView(compte));

        Button utilisateursBtn = createNavButton("Utilisateurs", "Utilisateurs".equals(activeViewName));
        utilisateursBtn.setOnAction(e -> navigator.showAdminUtilisateursView(compte));

        Button competencesBtn = createNavButton("Compétences", "Compétences".equals(activeViewName));
        competencesBtn.setOnAction(e -> navigator.showAdminCompetencesView(compte));

        Button affectationsBtn = createNavButton("Affectations", "Affectations".equals(activeViewName));
        affectationsBtn.setOnAction(e -> navigator.showAdminAffectationsView(compte));

        Button visualiserBtn = createNavButton("Visualiser", "Visualiser".equals(activeViewName));
        visualiserBtn.setOnAction(e -> navigator.showAdminVisualiserView(compte));
        
        navLinks.getChildren().addAll(accueilBtn, dispositifBtn, utilisateursBtn, competencesBtn, affectationsBtn, visualiserBtn);
        return navLinks;
    }

    /**
     * Crée un bouton de navigation avec un style de base et un style "actif" si nécessaire.
     *
     * @param name Le texte à afficher sur le bouton.
     * @param isActive Un booléen indiquant si le bouton doit avoir le style actif.
     * @return Le bouton (Button) créé.
     */
    private Button createNavButton(String name, boolean isActive) {
        Button navButton = new Button(name);
        navButton.getStyleClass().add("nav-button");
        if (isActive) {
            navButton.getStyleClass().add("nav-button-active");
        }
        return navButton;
    }
    
    /**
     * Crée le conteneur affichant les informations de l'utilisateur (nom, rôle),
     * son image de profil et un bouton de paramètres.
     *
     * @param navigator L'objet MainApp pour gérer la navigation.
     * @param compte Le compte de l'utilisateur connecté.
     * @return Un HBox contenant les informations de l'utilisateur.
     */
    private HBox createUserInfo(MainApp navigator, CompteUtilisateur compte) {
        HBox userInfo = new HBox(15);
        userInfo.setAlignment(Pos.CENTER_RIGHT);

        Button settingsButton = new Button();
        ImageView settingsIcon = new ImageView(RessourceLoader.loadImage("settings_icon.png"));
        settingsIcon.setFitHeight(24);
        settingsIcon.setFitWidth(24);
        settingsButton.setGraphic(settingsIcon);
        settingsButton.getStyleClass().add("settings-button");
        
        settingsButton.setOnAction(e -> navigator.showAdminParametresView(compte));
        
        Secouriste secouriste = null;
        if (compte.getIdSecouriste() != null) {
            secouriste = secouristeDAO.findByID(compte.getIdSecouriste());
        }

        // Si l'administrateur n'a pas de profil secouriste associé, un nom par défaut est utilisé.
        String nomComplet = secouriste != null ? secouriste.getPrenom() + " " + secouriste.getNom() : "Super Utilisateur";
        String role = "Administrateur";

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
        
        userInfo.getChildren().addAll(textInfo, profilePic, settingsButton);
        return userInfo;
    }
}