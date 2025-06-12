package fr.erm.sae201.controleur.auth;

import javax.management.Notification;

import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.ForgotPasswordView;

public class ForgotPasswordController {

    private final ForgotPasswordView view;
    private final MainApp navigator;

    public ForgotPasswordController(ForgotPasswordView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        initializeListeners();
    }

    private void initializeListeners() {
        // Au début, le bouton sert à envoyer le code
        view.getSendCodeButton().setOnAction(e -> handleSendCode());
    }

    private void handleSendCode() {
        String email = view.getEmail();
        System.out.println("CONTROLLER: Demande d'envoi de code pour l'email: " + email);

        if (email.isEmpty() || !email.contains("@")) {
            System.out.println("ERREUR: Email invalide.");
            // Afficher une alerte
            NotificationUtils.showWarning("Email invalide", "Veuillez saisir un email valide.");
            return;
        }

        // Logique pour envoyer le code de réinitialisation par email
        // authService.sendResetCode(email);

        System.out.println("INFO: Le champ pour saisir le code est maintenant visible.");
        view.showCodeField();
        
        // On change le texte et l'action du bouton pour qu'il serve à valider le code
        view.getSendCodeButton().setText("Valider le code");
        view.getSendCodeButton().setOnAction(e -> handleValidateCode());
    }
    
    private void handleValidateCode() {
        String email = view.getEmail();
        String code = view.getCode();
        System.out.println("CONTROLLER: Validation du code '" + code + "' pour l'email " + email);
        
        // Logique de validation du code...
        // boolean isCodeValid = authService.validateResetCode(email, code);
        boolean isCodeValid = true; // Simuler une validation réussie

        if (isCodeValid) {
            System.out.println("SUCCÈS: Le code est valide. Navigation vers la réinitialisation du mot de passe.");
            // Si le code est bon, on navigue vers l'écran suivant
            navigator.showResetPasswordScreen();
        } else {
            System.out.println("ERREUR: Le code est invalide.");
            // Afficher une alerte
        }
    }
}