package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.LoginView;

import java.util.Optional;

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

    private void handleSignup() {
        navigator.showSignupScreen();
    }

    private void handleForgotPassword() {
        navigator.showForgotPasswordScreen();
    }
}