package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.ForgotPasswordView;

/**
 * Controller for the "Forgot Password" view.
 */
public class ForgotPasswordController {

    private final ForgotPasswordView view;
    private final MainApp navigator;
    private final AuthService authService;
    private String generatedCode;

    public ForgotPasswordController(ForgotPasswordView view, MainApp navigator, AuthService authService) {
        this.authService = authService;
        this.view = view;
        this.navigator = navigator;
        initializeListeners();
    }

    private void initializeListeners() {
        view.getSendCodeButton().setOnAction(e -> handleSendCode());
        // ADDED: Listener for the new back-to-login link
        view.getLoginLink().setOnAction(e -> handleNavigateToLogin());
    }

    private void handleSendCode() {
        String email = view.getEmail();
        System.out.println("CONTROLLER: Requesting code for email: " + email);

        if (email.isEmpty() || !email.contains("@")) {
            NotificationUtils.showWarning("Email invalide", "Veuillez saisir un email valide.");
            return;
        }

        try {
            this.generatedCode = authService.sendResetCode(email);

            if (this.generatedCode != null) {
                NotificationUtils.showSuccess("Code envoyé", "Un code a été envoyé à votre adresse email.");
                System.out.println("INFO: Code input field is now visible.");
                view.showCodeField();
                
                view.getSendCodeButton().setText("Valider le code");
                view.getSendCodeButton().setOnAction(e -> handleValidateCode());
            } else {
                 NotificationUtils.showError("Erreur", "Le code n'a pas pu être envoyé.");
            }
        } catch (Exception ex) {
            System.err.println("ERROR sending email: " + ex.getMessage());
            NotificationUtils.showError("Erreur d'envoi", "Impossible d'envoyer l'email. Vérifiez votre configuration ou contactez un administrateur.");
        }
    }
    
    private void handleValidateCode() {
        String email = view.getEmail();
        String codeFromUser = view.getCode();
        System.out.println("CONTROLLER: Validating code '" + codeFromUser + "' for email " + email);
        
        boolean isCodeValid = generatedCode != null && generatedCode.equals(codeFromUser);

        if (isCodeValid) {
            System.out.println("SUCCESS: Code is valid. Navigating to password reset.");
            navigator.showResetPasswordScreen(email); 
        } else {
            System.out.println("ERROR: Invalid code.");
            NotificationUtils.showError("Code incorrect", "Le code que vous avez saisi est incorrect.");
        }
    }

    /**
     * ADDED: Handles navigation back to the login screen.
     */
    private void handleNavigateToLogin() {
        System.out.println("CONTROLLER: Navigating back to login screen.");
        navigator.showLoginScreen();
    }
}