package fr.erm.sae201.vue.auth;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import fr.erm.sae201.utils.RessourceLoader;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Represents the user interface for the signup screen.
 * It provides fields for first name, last name, email, password, and date of birth.
 */
public class SignupView {

    private StackPane rootPane;
    private TextField emailField;
    private PasswordField passwordField;
    private TextField firstNameField;
    private TextField lastNameField;
    private Button signupButton;
    private Hyperlink loginLink;
    private ComboBox<Integer> dayComboBox;
    private ComboBox<Integer> monthComboBox;
    private ComboBox<Integer> yearComboBox;

    public SignupView() {
        createView();
    }

    private void createView() {
        rootPane = new StackPane();
        rootPane.getStyleClass().add("login-root");

        ImageView backgroundImageView = new ImageView(RessourceLoader.loadImage("background.png"));
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
        rootPane.getChildren().add(backgroundImageView);

        VBox formBox = new VBox(15);
        formBox.getStyleClass().add("login-box");
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxSize(420, 600);

        // MODIFIED: Translated texts
        Label titleLabel = new Label("INSCRIPTION");
        titleLabel.getStyleClass().add("title-label");

        firstNameField = new TextField();
        firstNameField.setPromptText("Prénom");
        firstNameField.getStyleClass().add("login-input");

        lastNameField = new TextField();
        lastNameField.setPromptText("Nom");
        lastNameField.getStyleClass().add("login-input");

        Label dobLabel = new Label("Date de naissance");
        dobLabel.getStyleClass().add("form-label");

        dayComboBox = new ComboBox<>();
        dayComboBox.setPromptText("Jour");
        dayComboBox.getStyleClass().add("login-input");
        for (int i = 1; i <= 31; i++) {
            dayComboBox.getItems().add(i);
        }

        monthComboBox = new ComboBox<>();
        monthComboBox.setPromptText("Mois");
        monthComboBox.getStyleClass().add("login-input");
        for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }

        yearComboBox = new ComboBox<>();
        yearComboBox.setPromptText("Année");
        yearComboBox.getStyleClass().add("login-input");
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 18; i >= currentYear - 100; i--) {
            yearComboBox.getItems().add(i);
        }

        HBox dobBox = new HBox(10, dayComboBox, monthComboBox, yearComboBox);
        dobBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(dayComboBox, Priority.ALWAYS);
        HBox.setHgrow(monthComboBox, Priority.ALWAYS);
        HBox.setHgrow(yearComboBox, Priority.ALWAYS);

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("login-input");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.getStyleClass().add("login-input");

        signupButton = new Button("S'inscrire");
        signupButton.getStyleClass().add("login-button");
        signupButton.setMaxWidth(Double.MAX_VALUE);

        loginLink = new Hyperlink("Déjà un compte ? Connectez-vous");
        loginLink.getStyleClass().add("forgot-password-link");

        formBox.getChildren().addAll(
                titleLabel,
                firstNameField,
                lastNameField,
                dobLabel,
                dobBox,
                emailField,
                passwordField,
                signupButton,
                loginLink
        );

        ImageView olympicRingsView = new ImageView(RessourceLoader.loadImage("olympic_rings.png"));
        olympicRingsView.setFitHeight(60);
        olympicRingsView.setPreserveRatio(true);

        VBox mainContent = new VBox(40);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(formBox, olympicRingsView);

        rootPane.getChildren().add(mainContent);
        StackPane.setAlignment(mainContent, Pos.CENTER);
    }

    // --- Getters for the controller ---
    public StackPane getView() { return rootPane; }
    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public String getFirstName() { return firstNameField.getText(); }
    public String getLastName() { return lastNameField.getText(); }
    public Button getSignupButton() { return signupButton; }
    public Hyperlink getLoginLink() { return loginLink; }
    
    public LocalDate getDateOfBirth() {
        Integer day = dayComboBox.getValue();
        Integer month = monthComboBox.getValue();
        Integer year = yearComboBox.getValue();

        if (day == null || month == null || year == null) {
            return null;
        }

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            System.err.println("Invalid date selected: " + e.getMessage());
            return null;
        }
    }
}