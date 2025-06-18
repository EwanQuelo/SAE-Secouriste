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

public class UserParametresController {

    private final UserParametresView view;
    private final MainApp navigator;
    private final CompteUtilisateur compte;
    private final AuthService authService;
    private final SecouristeMngt secouristeMngt;
    private Secouriste currentUser;

    public UserParametresController(UserParametresView view, MainApp navigator, CompteUtilisateur compte, AuthService authService) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.authService = authService;
        this.secouristeMngt = new SecouristeMngt();

        initialize();
    }

    private void initialize() {
        loadUserData();
        
        view.setSaveInfoButtonAction(e -> handleUpdateInfo());
        view.setSavePasswordButtonAction(e -> handleUpdatePassword());
        view.setLogoutButtonAction(e -> navigator.showLoginScreen());
    }

    private void loadUserData() {
        if (compte.getIdSecouriste() != null) {
            try {
                // MODIFIÉ : On appelle directement getSecouriste qui retourne un objet ou lève une exception.
                Secouriste secouriste = secouristeMngt.getSecouriste(compte.getIdSecouriste());
                this.currentUser = secouriste;
                view.setSecouristeData(currentUser);
            } catch (EntityNotFoundException e) {
                // MODIFIÉ : On gère l'exception si le secouriste n'est pas trouvé.
                NotificationUtils.showError("Erreur critique", "Impossible de charger les données du secouriste associé à ce compte.");
                System.err.println(e.getMessage());
            }
        }
    }

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