package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.ForgotPasswordView;

public class ForgotPasswordController {

    private final ForgotPasswordView view;
    private final MainApp navigator;
    private final AuthService authService; // AJOUT
    private String generatedCode; // AJOUT : Pour stocker le code généré

    // MODIFIÉ : Le constructeur accepte AuthService
    public ForgotPasswordController(ForgotPasswordView view, MainApp navigator, AuthService authService) {
        this.authService = authService;
        this.view = view;
        this.navigator = navigator;
        initializeListeners();
    }

    private void initializeListeners() {
        view.getSendCodeButton().setOnAction(e -> handleSendCode());
    }

    private void handleSendCode() {
        String email = view.getEmail();
        System.out.println("CONTROLLER: Demande d'envoi de code pour l'email: " + email);

        if (email.isEmpty() || !email.contains("@")) {
            NotificationUtils.showWarning("Email invalide", "Veuillez saisir un email valide.");
            return;
        }

        try {
            // Logique pour envoyer le code de réinitialisation par email
            this.generatedCode = authService.sendResetCode(email);

            if (this.generatedCode != null) {
                NotificationUtils.showSuccess("Code envoyé", "Un code a été envoyé à votre adresse email.");
                System.out.println("INFO: Le champ pour saisir le code est maintenant visible.");
                view.showCodeField();
                
                // On change le texte et l'action du bouton
                view.getSendCodeButton().setText("Valider le code");
                view.getSendCodeButton().setOnAction(e -> handleValidateCode());
            } else {
                 NotificationUtils.showError("Erreur", "Le code n'a pas pu être envoyé.");
            }
        } catch (Exception ex) {
            System.err.println("ERREUR lors de l'envoi de l'email : " + ex.getMessage());
            NotificationUtils.showError("Erreur d'envoi", "Impossible d'envoyer l'email. Vérifiez votre configuration ou contactez un administrateur.");
        }
    }
    
    private void handleValidateCode() {
        String email = view.getEmail();
        String codeFromUser = view.getCode();
        System.out.println("CONTROLLER: Validation du code '" + codeFromUser + "' pour l'email " + email);
        
        boolean isCodeValid = generatedCode != null && generatedCode.equals(codeFromUser);

        if (isCodeValid) {
            System.out.println("SUCCÈS: Le code est valide. Navigation vers la réinitialisation du mot de passe.");
            // MODIFIÉ : On passe l'email à la méthode de navigation
            navigator.showResetPasswordScreen(email); 
        } else {
            System.out.println("ERREUR: Le code est invalide.");
            NotificationUtils.showError("Code incorrect", "Le code que vous avez saisi est incorrect.");
        }
    }
}