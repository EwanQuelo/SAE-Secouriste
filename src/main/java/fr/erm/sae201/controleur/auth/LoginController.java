package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.LoginView;

import java.util.Optional;

/**
 * Controller for the Login view.
 * This class handles user authentication by validating credentials through the {@link AuthService}.
 * It also provides navigation to the signup and forgot password screens.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class LoginController {

    private final LoginView view;
    private final MainApp navigator;
    private final AuthService authService;

    /**
     * Constructs a new LoginController
     *
     * @param view The {@link LoginView} instance this controller manages
     * @param navigator The {@link MainApp} instance used for navigating between different application screens
     * @param authService The {@link AuthService} instance used for user authentication
     */
    public LoginController(LoginView view, MainApp navigator, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
        initializeListeners();
    }

    /**
     * Initializes event listeners for UI components in the {@link LoginView}.
     * Sets up actions for the login button, signup button, and forgot password link.
     */
    private void initializeListeners() {
        view.getLoginButton().setOnAction(e -> handleLogin());
        view.getSignupButton().setOnAction(e -> handleSignup());
        view.getForgotPasswordLink().setOnAction(e -> handleForgotPassword());
    }

     /**
     * Handles the login attempt.
     * Retrieves email and password from the {@link LoginView}, validates them,
     * and attempts to authenticate the user via the {@link AuthService}.
     * If authentication is successful, it navigates to the appropriate dashboard
     * based on the user's role ({@link fr.erm.sae201.metier.persistence.CompteUtilisateur.Role}).
     * Displays notifications for missing fields, or login success/failure.
     */
    private void handleLogin() {
        String email = view.getEmail();
        String password = view.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            email = "talebismochel@gmail.com";
            password = "password"; // Pour les tests, à supprimer en production

            // NotificationUtils.showWarning("Champs requis", "Veuillez saisir votre email et votre mot de passe.");
            // return;
        }

        Optional<CompteUtilisateur> compteOpt = authService.login(email, password);

        if (compteOpt.isPresent()) {
            CompteUtilisateur compte = compteOpt.get();
            System.out.println("SUCCÈS: Connexion réussie pour " + compte.getLogin() + " avec le rôle " + compte.getRole());

            // Redirection en fonction du rôle
            switch (compte.getRole()) {
                case SECOURISTE:
                    navigator.showSecouristeDashboard(compte);
                    System.out.println("Navigation vers le tableau de bord Secouriste...");
                    break;
                case ADMINISTRATEUR:
                    // navigator.showAdminDashboard(compte);
                    System.out.println("Navigation vers le tableau de bord Administrateur...");
                    break;
            }
        } else {
            NotificationUtils.showError("Échec de la connexion", "L'email ou le mot de passe est incorrect.");
        }
    }

    /**
     * Handles the navigation to the signup screen.
     * Uses the {@link MainApp} navigator to display the signup view.
     */
    private void handleSignup() {
        navigator.showSignupScreen();
    }

    /**
     * Handles the navigation to the forgot password screen.
     * Uses the {@link MainApp} navigator to display the forgot password view.
     */
    private void handleForgotPassword() {
        navigator.showForgotPasswordScreen();
    }
}