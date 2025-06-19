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
import javafx.scene.effect.GaussianBlur;

/**
 * Classe de base abstraite pour toutes les vues de l'application qui nécessitent
 * une authentification. Elle définit une structure commune composée d'un arrière-plan
 * flouté et d'un BorderPane. Ce dernier contient une barre de navigation en haut,
 * qui s'adapte au rôle de l'utilisateur (administrateur ou secouriste), et une zone
 * centrale pour le contenu spécifique à chaque vue.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.1
 */
public abstract class BaseView {

    private final StackPane rootPane;
    private final BorderPane mainLayout;

    /**
     * Construit la vue de base en assemblant ses composants principaux.
     *
     * @param navigator L'instance de MainApp utilisée pour la navigation entre les vues.
     * @param compte L'objet CompteUtilisateur de l'utilisateur connecté, utilisé pour déterminer le rôle.
     * @param activeViewName Le nom de la vue active (ex: "Accueil"), utilisé pour la mise en évidence dans la barre de navigation.
     */
    public BaseView(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        rootPane = new StackPane();
        mainLayout = new BorderPane();

        // 1. Arrière-plan (couche inférieure)
        ImageView backgroundImageView = new ImageView(RessourceLoader.loadImage("background.png"));
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());

        // pour le flou
        GaussianBlur blurEffect = new GaussianBlur(10);
        backgroundImageView.setEffect(blurEffect);
        backgroundImageView.setSmooth(true);
        
        // 2. Création de la barre de navigation en fonction du rôle de l'utilisateur
        HBox navbar = createNavbar(navigator, compte, activeViewName);
        mainLayout.setTop(navbar);
        BorderPane.setMargin(navbar, new Insets(20, 40, 0, 40));

        // 3. Création du contenu central (défini par la classe enfant)
        Node centerContent = createCenterContent();
        mainLayout.setCenter(centerContent);
        BorderPane.setMargin(centerContent, new Insets(20, 40, 40, 40));

        // 4. Superposition de l'arrière-plan et de la mise en page principale
        rootPane.getChildren().addAll(backgroundImageView, mainLayout);
    }

    /**
     * Crée la barre de navigation appropriée en fonction du rôle de l'utilisateur connecté.
     *
     * @param navigator Le gestionnaire de navigation principal de l'application.
     * @param compte L'utilisateur connecté.
     * @param activeViewName Le nom de la vue actuellement affichée.
     * @return Un HBox représentant la barre de navigation (soit AdminNavbar, soit UserNavbar).
     */
    private HBox createNavbar(MainApp navigator, CompteUtilisateur compte, String activeViewName) {
        // Cette logique permet d'afficher une interface adaptée au rôle de l'utilisateur.
        if (compte.getRole() == Role.ADMINISTRATEUR) {
            return new AdminNavbar(navigator, compte, activeViewName);
        } else {
            return new UserNavbar(navigator, compte, activeViewName);
        }
    }

    /**
     * Méthode abstraite que les classes filles doivent implémenter pour fournir le contenu
     * principal et spécifique de leur vue. Ce contenu sera placé au centre de la mise en page.
     *
     * @return Le Node (nœud graphique) qui constitue le contenu central de la vue.
     */
    protected abstract Node createCenterContent();

    /**
     * Retourne le conteneur racine de la vue, prêt à être ajouté à la scène principale.
     *
     * @return Le StackPane racine qui contient tous les éléments de la vue.
     */
    public StackPane getView() {
        return rootPane;
    }
}