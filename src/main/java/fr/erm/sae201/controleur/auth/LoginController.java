package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.exception.AuthenticationException;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.LoginView;

/**
 * Contrôleur pour la vue de connexion.
 * 
 * Gère la logique d'authentification de l'utilisateur. Il récupère les
 * identifiants saisis, les valide via l'AuthService et, en cas de succès,
 * redirige l'utilisateur vers le tableau de bord correspondant à son rôle
 * (administrateur ou secouriste).
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class LoginController {

    /** La vue de connexion associée à ce contrôleur. */
    private final LoginView view;

    /** Le navigateur principal de l'application pour changer d'écran. */
    private final MainApp navigator;

    /** Le service d'authentification qui gère la logique de connexion. */
    private final AuthService authService;

    /**
     * Constructeur du contrôleur de connexion.
     *
     * @param view        La vue à contrôler.
     * @param navigator   Le navigateur principal de l'application.
     * @param authService Le service d'authentification.
     */
    public LoginController(LoginView view, MainApp navigator, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
        initializeListeners();
    }

    /**
     * Initialise les écouteurs d'événements pour les boutons de la vue.
     */
    private void initializeListeners() {
        view.getLoginButton().setOnAction(e -> handleLogin());
        view.getSignupButton().setOnAction(e -> handleSignup());
        view.getForgotPasswordLink().setOnAction(e -> handleForgotPassword());
    }

    /**
     * Gère la tentative de connexion de l'utilisateur.
     * Récupère les identifiants, appelle le service d'authentification et gère
     * la redirection en cas de succès ou l'affichage d'une erreur en cas d'échec.
     */
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
            CompteUtilisateur compte = authService.login(email, password);
            System.out.println("SUCCÈS: Connexion réussie pour " + compte.getLogin() + " avec le rôle " + compte.getRole());

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
        } catch (EntityNotFoundException | AuthenticationException e) {
            NotificationUtils.showError("Échec de la connexion", "L'email ou le mot de passe est incorrect.");
            System.err.println("Login failed: " + e.getMessage());
        } catch (Exception e) {
            // Capture les autres erreurs imprévues (ex: problème de connexion à la BDD).
            NotificationUtils.showError("Erreur serveur", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    /**
     * Navigue vers l'écran d'inscription.
     */
    private void handleSignup() {
        navigator.showSignupScreen();
    }

    /**
     * Navigue vers l'écran de mot de passe oublié.
     */
    private void handleForgotPassword() {
        navigator.showForgotPasswordScreen();
    }
}