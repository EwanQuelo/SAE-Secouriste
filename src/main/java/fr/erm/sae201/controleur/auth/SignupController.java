package fr.erm.sae201.controleur.auth;

import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.auth.SignupView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Controller for the signup view.
 * This class manages the user registration process. It collects user details
 * from the {@link SignupView}, validates them, and then uses the {@link AuthService}
 * to create a new "Secouriste" (rescuer) account.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class SignupController {

    private final SignupView view;
    private final MainApp navigator;
    private final AuthService authService;

    /**
     * Constructs a new SignupController.
     *
     * @param view        The {@link SignupView} instance this controller manages.
     * @param navigator   The {@link MainApp} instance used for navigating between screens.
     * @param authService The {@link AuthService} instance responsible for user registration
     *                    and other authentication logic.
     */
    public SignupController(SignupView view, MainApp navigator, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.authService = authService;
        initializeListeners();
    }

    /**
     * Initializes event listeners for the UI components in the {@link SignupView}.
     * Sets up actions for the signup button and the link to navigate to the login screen.
     */
    private void initializeListeners() {
        view.getSignupButton().setOnAction(e -> handleSignup());
        view.getLoginLink().setOnAction(e -> handleNavigateToLogin());
    }

    /**
     * Handles the user signup process.
     * Retrieves user information (first name, last name, email, password, and date of birth)
     * from the {@link SignupView}. Validates that all fields are filled.
     * Converts the {@link LocalDate} date of birth to {@link java.util.Date} as expected by the
     * {@link AuthService#registerSecouriste(String, String, String, String, Date)} method.
     * Attempts to register the user as a "Secouriste".
     * If registration is successful, navigates to the login screen.
     * Displays appropriate notifications for success, failure, or validation errors.
     */
    private void handleSignup() {
        String firstName = view.getFirstName();
        String lastName = view.getLastName();
        String email = view.getEmail();
        String password = view.getPassword();
        LocalDate dob = view.getDateOfBirth(); 


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || dob == null) {
            NotificationUtils.showWarning("Incomplete Form", "Please fill in all required fields, including a valid date of birth.");
            return;
        }

        // Convert LocalDate to java.util.Date for the service layer
        Date dateOfBirth = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
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

    /**
     * Handles navigation back to the login screen.
     * This action is triggered when the user clicks the "Login" link.
     */
    private void handleNavigateToLogin() {
        navigator.showLoginScreen();
    }
}