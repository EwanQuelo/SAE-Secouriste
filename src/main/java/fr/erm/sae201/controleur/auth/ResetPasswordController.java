package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.ResetPasswordView;

public class ResetPasswordController {

    private final ResetPasswordView view;
    private final MainApp navigator;
    private final AuthService authService;
    private final String userEmail; // Pour savoir quel utilisateur est concerné

    public ResetPasswordController(ResetPasswordView view, MainApp navigator, AuthService authService, String email) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
        this.userEmail = email; // On stocke l'email de l'utilisateur
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

        System.out.println("CONTROLLER: Tentative de réinitialisation du mot de passe pour " + userEmail);

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            NotificationUtils.showWarning("Champs requis", "Les champs ne peuvent pas être vides.");
            return;
        }

        if (newPassword.length() < 8) { // Ajout d'une règle de sécurité simple
            NotificationUtils.showWarning("Mot de passe faible", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            NotificationUtils.showError("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Logique pour sauvegarder le nouveau mot de passe via le service
        boolean success = authService.resetPassword(userEmail, newPassword);
        
        if (success) {
            NotificationUtils.showSuccess("Succès", "Votre mot de passe a été réinitialisé. Vous pouvez maintenant vous connecter.");
            System.out.println("SUCCÈS: Le mot de passe a été réinitialisé. Navigation vers la connexion.");
            navigator.showLoginScreen();
        } else {
            NotificationUtils.showError("Erreur", "La réinitialisation a échoué. Veuillez réessayer ou contacter le support.");
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