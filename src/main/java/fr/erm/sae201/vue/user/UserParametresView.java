package fr.erm.sae201.vue.user;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class UserParametresView extends BaseView {

    // Variables pour stocker les dépendances
    private final CompteUtilisateur compte;
    private final MainApp navigator;
    private final AuthService authService;

    // CORRECTION : "Boîte vide" que nous allons donner à BaseView
    private VBox centerContentContainer;

    public UserParametresView(MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        // 1. Appel du constructeur parent. C'est ici que createCenterContent() est
        // appelé.
        super(navigator, compte, "Paramètres");

        // 2. À ce stade, super() est terminé. On peut initialiser nos variables sans
        // risque.
        this.compte = compte;
        this.navigator = navigator;
        this.authService = authService;

        // 3. Maintenant que nos variables sont prêtes, on remplit la "boîte vide"
        populateCenterContent();
    }

    /**
     * CORRECTION : Cette méthode est appelée par le constructeur de BaseView TROP
     * TÔT.
     * Pour éviter le NullPointerException, elle crée et retourne une "boîte vide"
     * (un conteneur)
     * et la stocke dans une variable de l'instance pour la remplir plus tard.
     */
    @Override
    protected Node createCenterContent() {
        this.centerContentContainer = new VBox(); // On crée la boîte vide
        this.centerContentContainer.setAlignment(Pos.TOP_CENTER); // On peut déjà la styliser un peu
        return this.centerContentContainer; // On la donne à BaseView
    }

    /**
     * CORRECTION : C'est cette nouvelle méthode qui contient la VRAIE logique de
     * construction.
     * Elle est appelée par notre constructeur seulement APRÈS que 'this.compte'
     * soit initialisé.
     */
    private void populateCenterContent() {
        VBox container = new VBox(40);
        container.setAlignment(Pos.CENTER); // Centrer tout le conteneur principal
        container.getStyleClass().add("form-container");

        Text scenetitle = new Text("Paramètres du compte");
        scenetitle.getStyleClass().add("form-title");

        VBox emailInfoBox = createEmailInfoSection();
        VBox passwordBox = createPasswordSection();

        Button logoutButton = new Button("Déconnexion");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> navigator.showLoginScreen());
        // Centrer le bouton de déconnexion aussi
        VBox.setMargin(logoutButton, new Insets(10, 0, 0, 0));

        container.getChildren().addAll(scenetitle, emailInfoBox, passwordBox, logoutButton);

        this.centerContentContainer.getChildren().add(container);
    }

    // Le reste des méthodes est maintenant appelé par populateCenterContent et est
    // donc sécurisé.
    // AUCUN CHANGEMENT N'EST NÉCESSAIRE EN DESSOUS DE CETTE LIGNE.

    private VBox createEmailInfoSection() {
        // Le conteneur principal de la section reste un VBox.
        VBox sectionContainer = new VBox(5);
        sectionContainer.getStyleClass().add("settings-section");

        // 1. On crée un HBox pour aligner "Adresse mail:" et l'adresse elle-même.
        HBox emailLine = new HBox(10); // 10px d'espacement entre les éléments
        emailLine.setAlignment(Pos.CENTER_LEFT); // Aligne les éléments sur la ligne de base

        Label emailTitle = new Label("Adresse e-mail:"); // Texte du label
        emailTitle.getStyleClass().add("section-title"); // Utilise le style de titre de section existant

        Label emailLabel = new Label(this.compte.getLogin());
        emailLabel.getStyleClass().add("info-text-main"); // Utilise le style de texte principal

        // Ajoute le titre et l'email au HBox
        emailLine.getChildren().addAll(emailTitle, emailLabel);

        // 2. Le label d'information reste seul sur sa propre ligne.
        Label infoLabel = new Label("Votre adresse mail ne peut pas être modifiée.");
        infoLabel.getStyleClass().add("info-text-secondary");

        // 3. On ajoute la ligne HBox et le label d'info au conteneur principal de la
        // section.
        sectionContainer.getChildren().addAll(emailLine, infoLabel);

        return sectionContainer;
    }

    private VBox createPasswordSection() {
        VBox box = new VBox(10);
        box.getStyleClass().add("settings-section");
        box.setAlignment(Pos.CENTER_LEFT); // Aligner le contenu de cette section à gauche

        Label passwordTitle = new Label("Modifier le mot de passe");
        passwordTitle.getStyleClass().add("section-title");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        newPasswordField.getStyleClass().add("settings-input"); // NOUVELLE CLASSE CSS

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le nouveau mot de passe");
        confirmPasswordField.getStyleClass().add("settings-input"); // NOUVELLE CLASSE CSS

        Button savePasswordButton = new Button("Enregistrer le mot de passe");
        savePasswordButton.getStyleClass().add("save-button"); // NOUVELLE CLASSE CSS
        savePasswordButton.setOnAction(e -> handleUpdatePassword(
                newPasswordField.getText(),
                confirmPasswordField.getText()));

        // Enveloppe le bouton dans un HBox pour le centrer
        HBox buttonContainer = new HBox(savePasswordButton);
        buttonContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonContainer, new Insets(10, 0, 0, 0));

        box.getChildren().addAll(passwordTitle, newPasswordField, confirmPasswordField, buttonContainer);
        return box;
    }

    private void handleUpdatePassword(String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            NotificationUtils.showError("Champ invalide", "Le nouveau mot de passe ne peut pas être vide.");
            return;
        }

        if (newPassword.length() < 6) {
            NotificationUtils.showError("Mot de passe trop court",
                    "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            NotificationUtils.showError("Erreur de confirmation", "Les mots de passe ne correspondent pas.");
            return;
        }

        boolean success = authService.resetPassword(compte.getLogin(), newPassword);

        if (success) {
            NotificationUtils.showSuccess("Succès", "Mot de passe mis à jour avec succès !");
        } else {
            NotificationUtils.showError("Erreur serveur",
                    "Une erreur est survenue lors de la mise à jour du mot de passe.");
        }
    }
}