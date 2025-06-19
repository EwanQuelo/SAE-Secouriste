package fr.erm.sae201.vue;

import fr.erm.sae201.controleur.UserCarteController;
import fr.erm.sae201.controleur.auth.ForgotPasswordController;
import fr.erm.sae201.controleur.auth.LoginController;
import fr.erm.sae201.controleur.auth.ResetPasswordController;
import fr.erm.sae201.controleur.auth.SignupController;
import fr.erm.sae201.controleur.user.UserCompetencesController;
import fr.erm.sae201.controleur.user.UserDispoController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.RessourceLoader;
import fr.erm.sae201.vue.admin.*;
import fr.erm.sae201.vue.auth.ForgotPasswordView;
import fr.erm.sae201.vue.auth.LoginView;
import fr.erm.sae201.vue.auth.ResetPasswordView;
import fr.erm.sae201.vue.auth.SignupView;
import fr.erm.sae201.vue.user.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX.
 * Elle gère la fenêtre principale (Stage), la scène, et agit comme un navigateur
 * central pour afficher les différentes vues de l'application (un routeur), que ce soit pour
 * l'authentification, l'administration ou l'interface secouriste.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private Scene mainScene;
    private AuthService authService;

    /**
     * Point d'entrée principal de l'application JavaFX.
     * Initialise la fenêtre, la scène, les services et affiche l'écran de connexion initial.
     *
     * @param primaryStage La fenêtre principale fournie par le runtime JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SECOURS");

        this.authService = new AuthService();

        this.mainScene = new Scene(new StackPane(), 1280, 800);
        applyCommonStyles();
        primaryStage.setScene(mainScene);

        showLoginScreen();

        primaryStage.getIcons().add(RessourceLoader.loadImage("logo.png"));

        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    /**
     * Applique les feuilles de style CSS communes à la scène principale.
     */
    private void applyCommonStyles() {
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("notifications.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("login.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("application.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("carte.css"));
    }

    /**
     * Affiche l'écran de connexion.
     */
    public void showLoginScreen() {
        LoginView view = new LoginView();
        new LoginController(view, this, authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Connexion");
    }

    /**
     * Affiche l'écran d'inscription.
     */
    public void showSignupScreen() {
        SignupView view = new SignupView();
        new SignupController(view, this, authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Inscription");
    }

    /**
     * Affiche l'écran de mot de passe oublié.
     */
    public void showForgotPasswordScreen() {
        ForgotPasswordView view = new ForgotPasswordView();
        new ForgotPasswordController(view, this, authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Mot de passe oublié");
    }

    /**
     * Affiche l'écran de réinitialisation de mot de passe.
     *
     * @param email L'adresse e-mail de l'utilisateur qui réinitialise son mot de passe.
     */
    public void showResetPasswordScreen(String email) {
        ResetPasswordView view = new ResetPasswordView();
        new ResetPasswordController(view, this, authService, email);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Réinitialisation");
    }

    /**
     * Affiche le tableau de bord de l'administrateur.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showAdminDashboard(CompteUtilisateur compte) {
        AdminDashboard view = new AdminDashboard(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Tableau de Bord Admin");
    }

    /**
     * Affiche la vue de gestion des dispositifs pour l'administrateur.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showAdminDispositifView(CompteUtilisateur compte) {
        AdminDispositifView view = new AdminDispositifView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Gestion des Dispositifs");
    }

    /**
     * Affiche la vue de gestion des utilisateurs pour l'administrateur.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showAdminUtilisateursView(CompteUtilisateur compte) {
        AdminUtilisateursView view = new AdminUtilisateursView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Gestion des Utilisateurs");
    }
    
    /**
     * Affiche la vue d'édition pour un utilisateur secouriste spécifique.
     *
     * @param adminCompte Le compte de l'administrateur qui effectue l'action.
     * @param secouristeToEdit Le secouriste dont le profil est à modifier.
     */
    public void showAdminEditUserView(CompteUtilisateur adminCompte, Secouriste secouristeToEdit) {
        AdminEditUserView view = new AdminEditUserView(this, adminCompte, secouristeToEdit);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Modifier Utilisateur");
    }

    /**
     * Affiche la vue de gestion des compétences pour l'administrateur.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showAdminCompetencesView(CompteUtilisateur compte) {
        AdminCompetencesView view = new AdminCompetencesView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Gestion des Compétences");
    }

    /**
     * Affiche la vue de gestion des affectations pour l'administrateur.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showAdminAffectationsView(CompteUtilisateur compte) {
        AdminAffectationsView view = new AdminAffectationsView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Gestion des Affectations");
    }

    /**
     * Affiche la vue des paramètres pour l'administrateur.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showAdminParametresView(CompteUtilisateur compte) {
        AdminParametresView view = new AdminParametresView(this, compte, this.authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Paramètres Administrateur");
    }

    /**
     * Affiche la vue des paramètres pour le secouriste.
     *
     * @param compte Le compte du secouriste connecté.
     */
    public void showUserParametreView(CompteUtilisateur compte) {
        UserParametresView view = new UserParametresView(this, compte, this.authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Paramètres");
    }

    /**
     * Affiche le tableau de bord du secouriste.
     *
     * @param compte Le compte du secouriste connecté.
     */
    public void showSecouristeDashboard(CompteUtilisateur compte) {
        SecouristeDashboard view = new SecouristeDashboard(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Accueil Secouriste");
    }

    /**
     * Affiche le calendrier du secouriste.
     *
     * @param compte Le compte du secouriste connecté.
     */
    public void showUserCalendrierView(CompteUtilisateur compte) {
        UserCalendrierView view = new UserCalendrierView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Calendrier");
    }

    /**
     * Affiche la vue de la carte pour le secouriste.
     *
     * @param compte Le compte du secouriste connecté.
     */
    public void showUserCarteView(CompteUtilisateur compte) {
        UserCarteView view = new UserCarteView(this, compte);
        new UserCarteController(view, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Carte");
    }

    /**
     * Affiche la vue des compétences pour le secouriste.
     *
     * @param compte Le compte du secouriste connecté.
     */
    public void showUserCompetencesView(CompteUtilisateur compte) {
        UserCompetencesView view = new UserCompetencesView(this, compte);
        new UserCompetencesController(view, compte); 
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Mes Compétences");
    }

    /**
     * Affiche la vue de gestion des disponibilités pour le secouriste.
     *
     * @param compte Le compte du secouriste connecté.
     */
    public void showUserDispoView(CompteUtilisateur compte) {
        UserDispoView view = new UserDispoView(this, compte);
        new UserDispoController(view, this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Mes Disponibilités");
    }

    /**
     * Affiche la vue de création de dispositif.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showCreateDpsView(CompteUtilisateur compte) {
        AdminEditDpsView view = new AdminEditDpsView(this, compte, null);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Création de Dispositif");
    }

    /**
     * Affiche la vue de modification de dispositif.
     *
     * @param compte Le compte de l'administrateur connecté.
     * @param dps Le dispositif à modifier.
     */
    public void showEditDpsView(CompteUtilisateur compte, DPS dps) {
        AdminEditDpsView view = new AdminEditDpsView(this, compte, dps);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Modification de Dispositif");
    }

    /**
     * Affiche la vue de création d'un nouvel utilisateur secouriste.
     *
     * @param adminCompte Le compte de l'administrateur qui effectue l'action.
     */
    public void showAdminCreateUserView(CompteUtilisateur adminCompte) {
        AdminCreateUserView view = new AdminCreateUserView(this, adminCompte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Créer un Nouvel Utilisateur");
    }

    /**
     * Affiche la vue de visualisation du calendrier pour l'administrateur.
     *
     * @param compte Le compte de l'administrateur connecté.
     */
    public void showAdminVisualiserView(CompteUtilisateur compte) {
        AdminVisualiserView view = new AdminVisualiserView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Visualisation Calendrier");
    }

    /**
     * Méthode principale standard qui lance l'application JavaFX.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        launch(args);
    }
}