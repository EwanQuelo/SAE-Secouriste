package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.exception.AuthenticationException;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.LoginView;

/**
 * Controller for the Login view.
 * MODIFIÉ: Utilise try-catch pour gérer les exceptions d'authentification.
 */
public class LoginController {

    private final LoginView view;
    private final MainApp navigator;
    private final AuthService authService;

    public LoginController(LoginView view, MainApp navigator, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
        initializeListeners();
    }

    private void initializeListeners() {
        view.getLoginButton().setOnAction(e -> handleLogin());
        view.getSignupButton().setOnAction(e -> handleSignup());
        view.getForgotPasswordLink().setOnAction(e -> handleForgotPassword());
    }

    private void handleLogin() {
        String email = view.getEmail();
        String password = view.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            //email = "test@mail.com"; // Secouriste
            email = "admin@jo2030.fr"; // Administrateur
            password = "password"; // Pour les tests, à supprimer en production

            // NotificationUtils.showWarning("Champs requis", "Veuillez saisir votre email et votre mot de passe.");
            // return;
        }

        try {
            // MODIFIÉ : On appelle directement la méthode login qui retourne un CompteUtilisateur ou lève une exception.
            CompteUtilisateur compte = authService.login(email, password);

            System.out.println("SUCCÈS: Connexion réussie pour " + compte.getLogin() + " avec le rôle " + compte.getRole());

            // Redirection en fonction du rôle
            switch (compte.getRole()) {
                case SECOURISTE:
                    navigator.showSecouristeDashboard(compte);
                    System.out.println("Navigation vers le tableau de bord Secouriste...");
                    break;
                case ADMINISTRATEUR:
                    navigator.showAdminDashboard(compte);
                    System.out.println("Navigation vers le tableau de bord Administrateur...");
                    break;
            }
        // MODIFIÉ : On capture les exceptions spécifiques et on affiche un message clair.
        } catch (EntityNotFoundException | AuthenticationException e) {
            NotificationUtils.showError("Échec de la connexion", "L'email ou le mot de passe est incorrect.");
            System.err.println("Login failed: " + e.getMessage());
        } catch (Exception e) {
            // Capture d'autres erreurs imprévues (ex: problème BDD)
            NotificationUtils.showError("Erreur serveur", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    private void handleSignup() {
        navigator.showSignupScreen();
    }

    private void handleForgotPassword() {
        navigator.showForgotPasswordScreen();
    }
}