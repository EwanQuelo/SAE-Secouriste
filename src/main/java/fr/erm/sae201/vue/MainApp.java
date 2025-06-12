package fr.erm.sae201.vue;

import fr.erm.sae201.controleur.auth.ForgotPasswordController;
import fr.erm.sae201.controleur.auth.LoginController;
import fr.erm.sae201.controleur.auth.ResetPasswordController;
import fr.erm.sae201.controleur.auth.SignupController;
import fr.erm.sae201.utils.RessourceLoader;
import fr.erm.sae201.vue.auth.ForgotPasswordView;
import fr.erm.sae201.vue.auth.LoginView;
import fr.erm.sae201.vue.auth.ResetPasswordView;
import fr.erm.sae201.vue.auth.SignupView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane; // Import pour le conteneur initial
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private Scene mainScene; // NOUVEAU: Le champ pour notre scène unique

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SECOURS");

        // 1. On crée la scène unique avec un conteneur racine vide au début.
        //    La taille est définie ici une bonne fois pour toutes.
        this.mainScene = new Scene(new StackPane(), 1024, 768);

        // 2. On applique les styles communs à la scène UNE SEULE FOIS.
        applyCommonStyles();

        // 3. On attache notre scène unique à la fenêtre principale.
        primaryStage.setScene(mainScene);

        // 4. On affiche la première vue (connexion).
        showLoginScreen();
        
        primaryStage.show();
    }

    /**
     * Applique les feuilles de style qui seront partagées par toutes les vues.
     */
    private void applyCommonStyles() {
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("notifications.css"));
        mainScene.getStylesheets().add(RessourceLoader.loadCSS("login.css"));
    }

    /**
     * Affiche la vue de connexion en changeant la racine de la scène principale.
     */
    public void showLoginScreen() {
        LoginView view = new LoginView();
        new LoginController(view, this);
        
        // On change simplement la racine de notre scène existante.
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Connexion");
    }

    /**
     * Affiche la vue d'inscription.
     */
    public void showSignupScreen() {
        SignupView view = new SignupView();
        new SignupController(view, this);

        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Inscription");
    }
    
    /**
     * Affiche la vue de mot de passe oublié.
     */
    public void showForgotPasswordScreen() {
        ForgotPasswordView view = new ForgotPasswordView();
        new ForgotPasswordController(view, this);
        
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Mot de passe oublié");
    }

    /**
     * Affiche la vue de réinitialisation de mot de passe.
     */
    public void showResetPasswordScreen() {
        ResetPasswordView view = new ResetPasswordView();
        new ResetPasswordController(view, this);
        
        mainScene.setRoot(view.getView());
        primaryStage.setTitle("SECOURS - Réinitialisation");
    }

    public static void main(String[] args) {
        launch(args);
    }
}