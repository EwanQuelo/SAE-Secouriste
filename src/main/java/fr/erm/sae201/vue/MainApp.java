package fr.erm.sae201.vue;

import fr.erm.sae201.vue.user.SecouristeDashboard;
import fr.erm.sae201.controleur.auth.ForgotPasswordController;
import fr.erm.sae201.controleur.auth.LoginController;
import fr.erm.sae201.controleur.auth.ResetPasswordController;
import fr.erm.sae201.controleur.auth.SignupController;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.RessourceLoader;
import fr.erm.sae201.vue.auth.ForgotPasswordView;
import fr.erm.sae201.vue.auth.LoginView;
import fr.erm.sae201.vue.auth.ResetPasswordView;
import fr.erm.sae201.vue.auth.SignupView;
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

        this.mainScene = new Scene(new StackPane(), 1024, 768);
        applyCommonStyles();
        primaryStage.setScene(mainScene);
        
        showLoginScreen();
        
        primaryStage.show();
    }

    private void applyCommonStyles() {
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("notifications.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("login.css"));
    }

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

    /**
     * Affiche la vue de réinitialisation de mot de passe pour un email spécifique.
     * @param email L'email du compte à réinitialiser.
     */
    public void showResetPasswordScreen(String email) { // MODIFIÉ : Accepte l'email
        ResetPasswordView view = new ResetPasswordView();
        // MODIFIÉ : On passe l'email et le service au contrôleur
        new ResetPasswordController(view, this, authService, email); 
        
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Réinitialisation");
    }

    public void showSecouristeDashboard(CompteUtilisateur compte) {
        SecouristeDashboard view = new SecouristeDashboard();
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Dashboard");
        
        // Agrandir la fenêtre pour le dashboard
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.centerOnScreen();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}