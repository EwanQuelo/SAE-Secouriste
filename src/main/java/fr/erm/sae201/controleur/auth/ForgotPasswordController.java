package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.ForgotPasswordView;

/**
 * Contrôleur pour la vue de réinitialisation de mot de passe ("Mot de passe oublié").
 * 
 * Cette classe gère le processus en plusieurs étapes : elle envoie un code de
 * vérification à l'email de l'utilisateur, valide le code saisi, puis navigue
 * vers l'écran final de réinitialisation si la validation est réussie.
 * Elle gère également les cas d'erreur, comme un email non trouvé.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class ForgotPasswordController {

    /** La vue associée à ce contrôleur. */
    private final ForgotPasswordView view;

    /** Le navigateur principal de l'application pour changer d'écran. */
    private final MainApp navigator;

    /** Le service d'authentification qui gère la logique d'envoi de code. */
    private final AuthService authService;

    /** Le code de vérification qui a été généré et envoyé à l'utilisateur. */
    private String generatedCode;

    /**
     * Constructeur du contrôleur de mot de passe oublié.
     *
     * @param view          La vue à contrôler.
     * @param navigator     Le navigateur principal de l'application.
     * @param authService   Le service d'authentification.
     */
    public ForgotPasswordController(ForgotPasswordView view, MainApp navigator, AuthService authService) {
        this.authService = authService;
        this.view = view;
        this.navigator = navigator;
        initializeListeners();
    }

    /**
     * Initialise les écouteurs d'événements pour les éléments interactifs de la vue.
     */
    private void initializeListeners() {
        view.getSendCodeButton().setOnAction(e -> handleSendCode());
        view.getLoginLink().setOnAction(e -> handleNavigateToLogin());
    }

    /**
     * Gère la demande d'envoi du code de réinitialisation.
     * Valide le format de l'email, puis appelle le service pour envoyer le code.
     * Si l'envoi réussit, la vue est modifiée pour permettre la saisie du code.
     * Si l'email n'existe pas en base de données, une erreur spécifique est affichée.
     */
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
                view.showCodeField();

                view.getSendCodeButton().setText("Valider le code");
                view.getSendCodeButton().setOnAction(e -> handleValidateCode());
            } else {
                 NotificationUtils.showError("Erreur", "Le code n'a pas pu être envoyé.");
            }
        } catch (EntityNotFoundException ex) {
            NotificationUtils.showError("Utilisateur inconnu", "Aucun compte n'est associé à cette adresse email.");
        } catch (Exception ex) {
            System.err.println("ERROR sending email: " + ex.getMessage());
            NotificationUtils.showError("Erreur d'envoi", "Impossible d'envoyer l'email. Vérifiez votre configuration ou contactez un administrateur.");
        }
    }

    /**
     * Gère la validation du code saisi par l'utilisateur.
     * Compare le code entré avec celui stocké dans le contrôleur. Si le code est
     * correct, l'utilisateur est redirigé vers l'écran de réinitialisation.
     * Sinon, un message d'erreur est affiché.
     */
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
     * Gère l'action de retour à l'écran de connexion.
     */
    private void handleNavigateToLogin() {
        System.out.println("CONTROLLER: Navigating back to login screen.");
        navigator.showLoginScreen();
    }
}