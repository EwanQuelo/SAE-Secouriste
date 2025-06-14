package fr.erm.sae201.vue.base;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;
import fr.erm.sae201.utils.RessourceLoader;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminNavbar;
import fr.erm.sae201.vue.user.UserNavbar;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Classe de base abstraite pour toutes les vues de l'application post-connexion.
 * Elle fournit un fond d'écran commun et une barre de navigation (Navbar)
 * qui s'adapte en fonction du rôle de l'utilisateur.
 * Les classes enfants doivent implémenter createCenterContent() pour fournir
 * le contenu spécifique à leur vue.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.1
 */
public abstract class BaseView {

    private final StackPane rootPane;
    private final BorderPane mainLayout;

    /**
     * Construit la vue de base.
     *
     * @param navigator L'instance de MainApp utilisée pour la navigation.
     * @param compte L'objet CompteUtilisateur de l'utilisateur connecté.
     * @param activeViewName Le nom de la vue active (ex: "Accueil") pour surligner le bouton correspondant dans la navbar.
     */
    public BaseView(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        rootPane = new StackPane();
        mainLayout = new BorderPane();

        // 1. Fond d'écran (couche inférieure)
        ImageView backgroundImageView = new ImageView(RessourceLoader.loadImage("background.png"));
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
        
        // 2. Création de la barre de navigation en fonction du rôle
        HBox navbar = createNavbar(navigator, compte, activeViewName);
        mainLayout.setTop(navbar);
        BorderPane.setMargin(navbar, new Insets(20, 40, 0, 40)); // Marge pour ne pas coller aux bords

        // 3. Création du contenu central (fourni par la classe enfant)
        Node centerContent = createCenterContent();
        mainLayout.setCenter(centerContent);
        BorderPane.setMargin(centerContent, new Insets(20, 40, 40, 40));

        // 4. Superposition
        rootPane.getChildren().addAll(backgroundImageView, mainLayout);
    }

    /**
     * Crée la barre de navigation appropriée en fonction du rôle de l'utilisateur.
     *
     * @param navigator Le gestionnaire de navigation.
     * @param compte L'utilisateur connecté.
     * @param activeViewName Le nom de la vue active.
     * @return Un HBox représentant la barre de navigation.
     */
    private HBox createNavbar(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        if (compte.getRole() == Role.ADMINISTRATEUR) {
            return new AdminNavbar(navigator, compte, activeViewName);
        } else { // Par défaut, ou si c'est un SECOURISTE
            return new UserNavbar(navigator, compte, activeViewName);
        }
    }

    /**
     * Méthode abstraite que les classes filles doivent implémenter
     * pour fournir le contenu principal de leur vue.
     *
     * @return Le Node qui sera placé au centre de la vue.
     */
    protected abstract Node createCenterContent();

    /**
     * Retourne le conteneur racine de la vue, prêt à être ajouté à une scène.
     *
     * @return Le StackPane racine.
     */
    public StackPane getView() {
        return rootPane;
    }
}