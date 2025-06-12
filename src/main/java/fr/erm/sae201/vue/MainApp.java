package fr.erm.sae201.vue;

import fr.erm.sae201.controleur.auth.LoginController;
import fr.erm.sae201.controleur.auth.SignupController;
// Importez les autres vues et contrôleurs nécessaires
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import fr.erm.sae201.utils.RessourceLoader;
import fr.erm.sae201.vue.auth.LoginView;
import fr.erm.sae201.vue.auth.SignupView;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SECOURS"); // Titre général

        // Démarrer l'application en affichant l'écran de connexion
        showLoginScreen();

        primaryStage.show();
    }

    /**
     * Crée et affiche la vue de connexion.
     * C'est la méthode que les autres contrôleurs appelleront pour revenir à l'écran de connexion.
     */
    public void showLoginScreen() {
        LoginView view = new LoginView();
        // On passe une référence de MainApp (le navigateur) au contrôleur.
        LoginController controller = new LoginController(view, this); 
        
        Scene scene = new Scene(view.getView(), 1024, 768);
        scene.getStylesheets().add(RessourceLoader.loadCSS("login.css"));
        
        primaryStage.setTitle("SECOURS - Connexion");
        primaryStage.setScene(scene);
    }

    /**
     * Crée et affiche la vue d'inscription.
     */
    public void showSignupScreen() {
        SignupView view = new SignupView();
        SignupController controller = new SignupController(view, this);

        Scene scene = new Scene(view.getView(), 1024, 768);
        scene.getStylesheets().add(RessourceLoader.loadCSS("login.css"));

        primaryStage.setTitle("SECOURS - Inscription");
        primaryStage.setScene(scene);
    }
    
    /**
     * Crée et affiche la vue de mot de passe oublié.
     */
    public void showForgotPasswordScreen() {
        // ForgotPasswordView view = new ForgotPasswordView();
        // ForgotPasswordController controller = new ForgotPasswordController(view, this);
        // ... créer et afficher la scène
        System.out.println("NAVIGATOR: Affichage de la vue Mot de passe oublié (à implémenter)");
    }

    /**
     * Crée et affiche la vue de réinitialisation de mot de passe.
     */
    public void showResetPasswordScreen() {
        // ResetPasswordView view = new ResetPasswordView();
        // ResetPasswordController controller = new ResetPasswordController(view, this);
        // ... créer et afficher la scène
        System.out.println("NAVIGATOR: Affichage de la vue Réinitialiser le mot de passe (à implémenter)");
    }


    public static void main(String[] args) {
        launch(args);
    }
}