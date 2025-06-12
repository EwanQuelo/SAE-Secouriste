package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.SignupView;

public class SignupController {

    private final SignupView view;
    private final MainApp navigator;

    public SignupController(SignupView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
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

        System.out.println("CONTROLLER: Tentative d'inscription...");
        System.out.println("Prénom: " + firstName);
        System.out.println("Nom: " + lastName);
        System.out.println("Email: " + email);

        // Logique d'inscription ici
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("ERREUR: Tous les champs sont requis.");
            // Idéalement, afficher une alerte dans la vue
        } else {
            // Ici, vous appelleriez votre service d'authentification
            // boolean success = authService.register(firstName, lastName, email, password);
            boolean success = true; // Simuler un succès
            
            if (success) {
                System.out.println("SUCCÈS: Inscription réussie. Navigation vers la connexion.");
                // Après une inscription réussie, on redirige généralement l'utilisateur vers la page de connexion
                navigator.showLoginScreen();
            } else {
                System.out.println("ERREUR: L'inscription a échoué (ex: email déjà utilisé).");
                // Afficher une alerte
            }
        }
    }

    private void handleNavigateToLogin() {
        System.out.println("CONTROLLER: Demande de retour vers la page de connexion.");
        navigator.showLoginScreen();
    }
}