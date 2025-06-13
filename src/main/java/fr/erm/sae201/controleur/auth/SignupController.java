package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.SignupView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Controller for the signup view. Handles user registration logic.
 */
public class SignupController {

    private final SignupView view;
    private final MainApp navigator;
    private final AuthService authService;

    /**
     * Constructor for SignupController.
     *
     * @param view        The associated {@link SignupView}.
     * @param navigator   The main application navigator for screen transitions.
     * @param authService The service responsible for authentication logic.
     */
    public SignupController(SignupView view, MainApp navigator, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
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
        LocalDate dob = view.getDateOfBirth(); // MODIFIED: Get LocalDate from the view's widget

        // MODIFIED: Added check for date of birth
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || dob == null) {
            NotificationUtils.showWarning("Incomplete Form", "Please fill in all required fields, including a valid date of birth.");
            return;
        }

        // Convert LocalDate to java.util.Date for the service layer
        Date dateOfBirth = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            // MODIFIED: Pass the actual date of birth
            boolean success = authService.registerSecouriste(firstName, lastName, email, password, dateOfBirth);
            if (success) {
                NotificationUtils.showSuccess("Registration Successful!", "You can now log in with your email.");
                navigator.showLoginScreen();
            } else {
                NotificationUtils.showError("Registration Error", "An unknown error occurred. Please try again.");
            }
        } catch (Exception e) {
            NotificationUtils.showError("Registration Error", e.getMessage());
        }
    }

    private void handleNavigateToLogin() {
        navigator.showLoginScreen();
    }
}