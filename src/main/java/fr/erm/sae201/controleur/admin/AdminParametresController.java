package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminParametresView;

public class AdminParametresController {

    private final AdminParametresView view;
    private final MainApp navigator;
    private final CompteUtilisateur compte;
    private final AuthService authService;

    public AdminParametresController(AdminParametresView view, MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.authService = authService;

        initialize();
    }

    private void initialize() {
        // Affiche l'email de l'admin
        view.setEmail(compte.getLogin());
        
        // Lie les actions des boutons aux méthodes du contrôleur
        view.setSavePasswordButtonAction(e -> handleUpdatePassword());
        view.setLogoutButtonAction(e -> navigator.showLoginScreen());
    }

    private void handleUpdatePassword() {
        String newPassword = view.getNewPassword();
        String confirmPassword = view.getConfirmPassword();

        if (newPassword == null || newPassword.trim().isEmpty()) {
            NotificationUtils.showError("Champ invalide", "Le nouveau mot de passe ne peut pas être vide.");
            return;
        }

        if (newPassword.length() < 6) {
            NotificationUtils.showWarning("Mot de passe trop court", "Le mot de passe doit contenir au moins 6 caractères.");
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
            NotificationUtils.showError("Erreur serveur", "Une erreur est survenue lors de la mise à jour du mot de passe.");
        }
    }
}