package fr.erm.sae201.controleur.user;

import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.metier.service.SecouristeMngt;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.user.UserParametresView;

import java.time.ZoneId;
import java.util.Date;

/**
 * Contrôleur pour la vue des paramètres du secouriste.
 * <p>
 * Gère le chargement des données personnelles de l'utilisateur, leur mise à jour,
 * la modification du mot de passe et la déconnexion.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class UserParametresController {

    /** La vue des paramètres associée à ce contrôleur. */
    private final UserParametresView view;

    /** Le navigateur principal de l'application. */
    private final MainApp navigator;

    /** Le compte de l'utilisateur connecté. */
    private final CompteUtilisateur compte;

    /** Le service d'authentification pour la gestion du mot de passe. */
    private final AuthService authService;

    /** Le service métier pour la gestion des informations du secouriste. */
    private final SecouristeMngt secouristeMngt;

    /** L'objet Secouriste correspondant à l'utilisateur, chargé depuis la BDD. */
    private Secouriste currentUser;

    /**
     * Constructeur du contrôleur des paramètres utilisateur.
     *
     * @param view        La vue à contrôler.
     * @param navigator   Le navigateur principal.
     * @param compte      Le compte de l'utilisateur connecté.
     * @param authService Le service d'authentification.
     */
    public UserParametresController(UserParametresView view, MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.authService = authService;
        this.secouristeMngt = new SecouristeMngt();

        initialize();
    }

    /**
     * Initialise le contrôleur en chargeant les données de l'utilisateur et en
     * liant les actions des boutons de la vue aux méthodes correspondantes.
     */
    private void initialize() {
        loadUserData();

        view.setSaveInfoButtonAction(e -> handleUpdateInfo());
        view.setSavePasswordButtonAction(e -> handleUpdatePassword());
        view.setLogoutButtonAction(e -> navigator.showLoginScreen());
    }

    /**
     * Charge les informations détaillées du secouriste et les affiche dans la vue.
     * Gère le cas où le profil du secouriste ne serait pas trouvé en base de données.
     */
    private void loadUserData() {
        if (compte.getIdSecouriste() != null) {
            try {
                Secouriste secouriste = secouristeMngt.getSecouriste(compte.getIdSecouriste());
                this.currentUser = secouriste;
                view.setSecouristeData(currentUser);
            } catch (EntityNotFoundException e) {
                NotificationUtils.showError("Erreur critique", "Impossible de charger les données du secouriste associé à ce compte.");
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Gère la mise à jour des informations personnelles du secouriste.
     * Récupère les données de la vue, met à jour l'objet Secouriste et appelle
     * le service pour sauvegarder les modifications en base de données.
     */
    private void handleUpdateInfo() {
        if (currentUser == null) {
            NotificationUtils.showError("Erreur", "Utilisateur non trouvé.");
            return;
        }

        try {
            currentUser.setPrenom(view.getPrenom());
            currentUser.setNom(view.getNom());
            currentUser.setTel(view.getTel());
            currentUser.setAddresse(view.getAdresse());
            if (view.getDateNaissance() != null) {
                currentUser.setDateNaissance(Date.from(view.getDateNaissance().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            boolean success = secouristeMngt.update(currentUser);
            if (success) {
                NotificationUtils.showSuccess("Succès", "Vos informations ont été mises à jour.");
                navigator.showUserParametreView(compte);
            } else {
                NotificationUtils.showError("Échec", "La mise à jour de vos informations a échoué.");
            }

        } catch (IllegalArgumentException e) {
            NotificationUtils.showError("Données invalides", e.getMessage());
        }
    }

    /**
     * Gère la mise à jour du mot de passe de l'utilisateur.
     * Valide les champs de mot de passe (non vide, longueur, correspondance)
     * et appelle le service d'authentification pour effectuer le changement.
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