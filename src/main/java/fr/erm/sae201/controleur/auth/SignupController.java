package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.SignupView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Contrôleur pour la vue d'inscription.
 * 
 * Cette classe gère le processus d'inscription de l'utilisateur. Elle récupère
 * les informations de l'utilisateur depuis la vue, les valide, puis utilise
 * l'AuthService pour créer un nouveau compte de type "Secouriste".
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SignupController {

    /** La vue d'inscription associée à ce contrôleur. */
    private final SignupView view;

    /** Le navigateur principal de l'application pour changer d'écran. */
    private final MainApp navigator;

    /** Le service d'authentification qui gère la logique d'inscription. */
    private final AuthService authService;

    /**
     * Construit un nouveau SignupController.
     *
     * @param view        La vue que ce contrôleur gère.
     * @param navigator   L'instance de MainApp utilisée pour naviguer entre les écrans.
     * @param authService L'instance d'AuthService responsable de l'inscription
     *                    et des autres logiques d'authentification.
     */
    public SignupController(SignupView view, MainApp navigator, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
        initializeListeners();
    }

    /**
     * Initialise les écouteurs d'événements pour les composants de la vue.
     * Met en place les actions pour le bouton d'inscription et le lien pour
     * naviguer vers l'écran de connexion.
     */
    private void initializeListeners() {
        view.getSignupButton().setOnAction(e -> handleSignup());
        view.getLoginLink().setOnAction(e -> handleNavigateToLogin());
    }

    /**
     * Gère le processus d'inscription de l'utilisateur.
     * 
     * Récupère les informations (prénom, nom, email, mot de passe et date de naissance)
     * depuis la vue. Valide que tous les champs sont remplis. Tente d'inscrire
     * l'utilisateur comme "Secouriste". Si l'inscription réussit, navigue vers l'écran
     * de connexion. Affiche des notifications appropriées pour le succès, l'échec
     * ou les erreurs de validation.
     * 
     */
    private void handleSignup() {
        String firstName = view.getFirstName();
        String lastName = view.getLastName();
        String email = view.getEmail();
        String password = view.getPassword();
        LocalDate dob = view.getDateOfBirth();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || dob == null) {
            NotificationUtils.showWarning("Formulaire incomplet", "Veuillez remplir tous les champs requis, y compris une date de naissance valide.");
            return;
        }

        // Convertit le LocalDate en java.util.Date pour la couche de service.
        Date dateOfBirth = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            boolean success = authService.registerSecouriste(firstName, lastName, email, password, dateOfBirth);
            if (success) {
                NotificationUtils.showSuccess("Inscription réussie !", "Vous pouvez maintenant vous connecter avec votre email.");
                navigator.showLoginScreen();
            } else {
                NotificationUtils.showError("Erreur d'inscription", "Une erreur inconnue est survenue. Veuillez réessayer.");
            }
        } catch (Exception e) {
            NotificationUtils.showError("Erreur d'inscription", e.getMessage());
        }
    }

    /**
     * Gère la navigation pour retourner à l'écran de connexion.
     * Cette action est déclenchée lorsque l'utilisateur clique sur le lien "Se connecter".
     */
    private void handleNavigateToLogin() {
        navigator.showLoginScreen();
    }
}