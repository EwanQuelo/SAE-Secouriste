package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminParametresView;

/**
 * Contrôleur pour l'écran des paramètres de l'administrateur.
 * <p>
 * Cette classe gère les actions disponibles dans la vue des paramètres,
 * comme la modification du mot de passe de l'administrateur et la déconnexion.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminParametresController {

    /** La vue des paramètres, gérée par ce contrôleur. */
    private final AdminParametresView view;

    /** Le navigateur principal pour gérer les changements d'écran. */
    private final MainApp navigator;

    /** Le compte de l'administrateur actuellement connecté. */
    private final CompteUtilisateur compte;

    /** Le service d'authentification pour la mise à jour du mot de passe. */
    private final AuthService authService;

    /**
     * Constructeur du contrôleur des paramètres.
     *
     * @param view        La vue à contrôler.
     * @param navigator   Le navigateur principal de l'application.
     * @param compte      Le compte de l'utilisateur administrateur.
     * @param authService Le service pour gérer l'authentification.
     */
    public AdminParametresController(AdminParametresView view, MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.authService = authService;

        initialize();
    }

    /**
     * Initialise la vue avec les données du compte et lie les actions des boutons
     * aux méthodes correspondantes du contrôleur.
     */
    private void initialize() {
        view.setEmail(compte.getLogin());
        view.setSavePasswordButtonAction(e -> handleUpdatePassword());
        view.setLogoutButtonAction(e -> navigator.showLoginScreen());
    }

    /**
     * Gère la tentative de mise à jour du mot de passe de l'administrateur.
     * Elle récupère les mots de passe saisis, effectue une série de validations
     * (non vide, longueur minimale, correspondance) et, si tout est correct,
     * appelle le service d'authentification pour effectuer le changement.
     */
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