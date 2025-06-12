package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.SignupView;

public class SignupController {

    private final SignupView view;
    private final MainApp navigator;
    private final AuthService authService;

    public SignupController(SignupView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.authService = new AuthService();
        initializeListeners();
    }

    private void initializeListeners() {
        view.getSignupButton().setOnAction(e -> handleSignup());
        view.getLoginLink().setOnAction(e -> handleNavigateToLogin());
    }

    private void handleSignup() {
        String firstName = view.getFirstName();
        String lastName = view.getLastName();
        String email = view.getEmail();
        String password = view.getPassword();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            NotificationUtils.showWarning("Formulaire incomplet", "Veuillez remplir tous les champs requis.");
            return;
        }

        try {
            boolean success = authService.registerSecouriste(firstName, lastName, email, password);
            if (success) {
                NotificationUtils.showSuccess("Inscription réussie !", "Vous pouvez maintenant vous connecter avec votre email.");
                navigator.showLoginScreen();
            } else {
                NotificationUtils.showError("Erreur d'inscription", "Une erreur inconnue est survenue. Veuillez réessayer.");
            }
        } catch (Exception e) {
            // Affiche une erreur claire à l'utilisateur, ex: "Cet email est déjà utilisé"
            NotificationUtils.showError("Erreur d'inscription", e.getMessage());
        }
    }

    private void handleNavigateToLogin() {
        navigator.showLoginScreen();
    }
}