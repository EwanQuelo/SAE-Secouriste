package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.vue.MainApp; // <-- Importer notre navigateur
import fr.erm.sae201.vue.auth.LoginView;

public class LoginController {

    private final LoginView view;
    private final MainApp navigator; // <-- Référence vers le navigateur

    // Le constructeur accepte la vue ET le navigateur
    public LoginController(LoginView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        initializeListeners();
    }

    private void initializeListeners() {
        view.getLoginButton().setOnAction(e -> handleLogin());
        view.getSignupButton().setOnAction(e -> handleSignup());
        view.getForgotPasswordLink().setOnAction(e -> handleForgotPassword());
    }

    private void handleLogin() {
        // ... la logique de connexion reste ici, elle ne change pas.
        System.out.println("CONTROLLER: Tentative de connexion...");
    }

    private void handleSignup() {
        System.out.println("CONTROLLER: Demande de navigation vers la page d'inscription.");
        // Le contrôleur demande simplement au navigateur de faire son travail.
        navigator.showSignupScreen();
    }

    private void handleForgotPassword() {
        System.out.println("CONTROLLER: Demande de navigation vers mot de passe oublié.");
        navigator.showForgotPasswordScreen();
    }
}