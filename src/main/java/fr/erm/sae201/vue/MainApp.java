package fr.erm.sae201.vue;

import fr.erm.sae201.controleur.UserCarteController;
import fr.erm.sae201.controleur.auth.ForgotPasswordController;
import fr.erm.sae201.controleur.auth.LoginController;
import fr.erm.sae201.controleur.auth.ResetPasswordController;
import fr.erm.sae201.controleur.auth.SignupController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste; // AJOUT
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

public class MainApp extends Application {

    private Stage primaryStage;
    private Scene mainScene;
    private AuthService authService;

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

    private void applyCommonStyles() {
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("notifications.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("login.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("application.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("carte.css"));
    }

    // --- Vues d'authentification ---
    public void showLoginScreen() {
        LoginView view = new LoginView();
        new LoginController(view, this, authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Connexion");
    }

    public void showSignupScreen() {
        SignupView view = new SignupView();
        new SignupController(view, this, authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Inscription");
    }

    public void showForgotPasswordScreen() {
        ForgotPasswordView view = new ForgotPasswordView();
        new ForgotPasswordController(view, this, authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Mot de passe oublié");
    }

    public void showResetPasswordScreen(String email) {
        ResetPasswordView view = new ResetPasswordView();
        new ResetPasswordController(view, this, authService, email);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Réinitialisation");
    }

    // --- Vues Administrateur ---
    public void showAdminDashboard(CompteUtilisateur compte) {
        AdminDashboard view = new AdminDashboard(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Tableau de Bord Admin");
    }

    public void showAdminDispositifView(CompteUtilisateur compte) {
        AdminDispositifView view = new AdminDispositifView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Gestion des Dispositifs");
    }

    public void showAdminUtilisateursView(CompteUtilisateur compte) {
        AdminUtilisateursView view = new AdminUtilisateursView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Gestion des Utilisateurs");
    }
    
    /**
     * AJOUT : Affiche la vue d'édition pour un utilisateur spécifique.
     * @param adminCompte Le compte de l'administrateur qui effectue l'action.
     * @param secouristeToEdit Le secouriste à modifier.
     */
    public void showAdminEditUserView(CompteUtilisateur adminCompte, Secouriste secouristeToEdit) {
        AdminEditUserView view = new AdminEditUserView(this, adminCompte, secouristeToEdit);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Modifier Utilisateur");
    }

    public void showAdminAffectationsView(CompteUtilisateur compte) {
        AdminAffectationsView view = new AdminAffectationsView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Gestion des Affectations");
    }

    // --- Vues Secouriste ---
    public void showSecouristeDashboard(CompteUtilisateur compte) {
        SecouristeDashboard view = new SecouristeDashboard(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Accueil Secouriste");
    }

    public void showUserCalendrierView(CompteUtilisateur compte) {
        UserCalendrierView view = new UserCalendrierView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Calendrier");
    }

    public void showUserCarteView(CompteUtilisateur compte) {
        UserCarteView view = new UserCarteView(this, compte);
        new UserCarteController(view, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Carte");
    }

    public void showUserCompetencesView(CompteUtilisateur compte) {
        UserCompetencesView view = new UserCompetencesView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Mes Compétences");
    }

    public void showUserParametreView(CompteUtilisateur compte) {
        UserParametresView view = new UserParametresView(this, compte, this.authService);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Paramètres");
    }

    public void showUserDispoView(CompteUtilisateur compte) {
        UserDispoView view = new UserDispoView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Mes Disponibilités");
    }

    public void showCreateDpsView(CompteUtilisateur compte) {
        AdminCreateDpsView view = new AdminCreateDpsView(this, compte);
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Création de Dispositif");
    }

    public static void main(String[] args) {
        launch(args);
    }
}