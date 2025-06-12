package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.ResetPasswordView;

public class ResetPasswordController {

    private final ResetPasswordView view;
    private final MainApp navigator;

    public ResetPasswordController(ResetPasswordView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        initializeListeners();
    }

    private void initializeListeners() {
        view.getValidateButton().setOnAction(e -> handleValidate());
        view.getLoginLink().setOnAction(e -> handleNavigateToLogin());
        view.getSignupLink().setOnAction(e -> handleNavigateToSignup());
    }

    private void handleValidate() {
        String newPassword = view.getNewPassword();
        String confirmPassword = view.getConfirmPassword();

        System.out.println("CONTROLLER: Tentative de réinitialisation du mot de passe.");

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("ERREUR: Les champs ne peuvent pas être vides.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("ERREUR: Les mots de passe ne correspondent pas.");
            return;
        }

        // Logique pour sauvegarder le nouveau mot de passe
        // boolean success = authService.resetPassword(token, newPassword);
        boolean success = true; // Simuler un succès
        
        if (success) {
            System.out.println("SUCCÈS: Le mot de passe a été réinitialisé. Navigation vers la connexion.");
            navigator.showLoginScreen();
        } else {
            System.out.println("ERREUR: La réinitialisation a échoué.");
        }
    }

    private void handleNavigateToLogin() {
        System.out.println("CONTROLLER: Demande de navigation vers la page de connexion.");
        navigator.showLoginScreen();
    }

    private void handleNavigateToSignup() {
        System.out.println("CONTROLLER: Demande de navigation vers la page d'inscription.");
        navigator.showSignupScreen();
    }
}