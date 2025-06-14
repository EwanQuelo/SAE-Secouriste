package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.ResetPasswordView;

/**
 * Controller for the "Reset Password" view.
 * This class handles the logic for resetting a user's password after they have
 * successfully validated a reset code. It takes the new password, confirms it,
 * and updates it via the {@link AuthService}.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class ResetPasswordController {

    private final ResetPasswordView view;
    private final MainApp navigator;
    private final AuthService authService;

    /** 
     * The email of the user whose password is being reset. 
     * This is used to identify the user in the {@link AuthService}.
     */
    private final String userEmail; 


    /**
     * Constructs a new ResetPasswordController
     *
     * @param view The {@link ResetPasswordView} instance this controller manages.
     * @param navigator The {@link MainApp} instance used for navigating between screens.
     * @param authService The {@link AuthService} instance for handling password reset logic.
     * @param email The email address of the user for whom the password is being reset.
     */
    public ResetPasswordController(ResetPasswordView view, MainApp navigator, AuthService authService, String email) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
        this.userEmail = email; // On stocke l'email de l'utilisateur
        initializeListeners();
    }

    /**
     * Initializes event listeners for the UI components in the {@link ResetPasswordView}.
     * Sets up actions for the validate button, and links to navigate to the login or signup screens.
     */
    private void initializeListeners() {
        view.getValidateButton().setOnAction(e -> handleValidate());
        view.getLoginLink().setOnAction(e -> handleNavigateToLogin());
        view.getSignupLink().setOnAction(e -> handleNavigateToSignup());
    }

    /**
     * Handles the password reset validation process,
     * Retrieves the new password and its confirmation from the {@link ResetPasswordView}.
     * Performs validation checks:
     * <ul>
     *     <li>Ensures fields are not empty.</li>
     *     <li>Checks if the new password meets minimum length requirements (e.g., 8 characters).</li>
     *     <li>Verifies that the new password and confirmation password match.</li>
     * </ul>
     * If all checks pass, it attempts to reset the password for the {@code userEmail}
     * using the {@link AuthService}. Navigates to the login screen on success,
     * or displays an error notification on failure.
     */
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

    /**
     * Handles navigation back to the login screen.
     * This action is triggered when the user clicks the "Login" link.
     */
    private void handleNavigateToLogin() {
        System.out.println("CONTROLLER: Demande de navigation vers la page de connexion.");
        navigator.showLoginScreen();
    }

    /**
     * Handles navigation to the signup screen.
     * This action is triggered when the user clicks the "Signup" link.
     */
    private void handleNavigateToSignup() {
        System.out.println("CONTROLLER: Demande de navigation vers la page d'inscription.");
        navigator.showSignupScreen();
    }
}