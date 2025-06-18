package fr.erm.sae201.controleur.user;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.user.UserDispoView;

import java.time.LocalDate;

/**
 * NOUVEAU : Contrôleur pour la vue de gestion des disponibilités de l'utilisateur.
 * Il contient la logique de sauvegarde et d'annulation.
 */
public class UserDispoController {

    private final UserDispoView view;
    private final MainApp navigator;
    private final CompteUtilisateur compte;
    private final SecouristeDAO secouristeDAO;

    public UserDispoController(UserDispoView view, MainApp navigator, CompteUtilisateur compte) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.secouristeDAO = new SecouristeDAO();

        // Le contrôleur branche la logique sur les boutons de la vue
        this.view.setSaveAction(e -> handleSaveChanges());
        this.view.setCancelAction(e -> handleCancelChanges());
    }

    private void handleSaveChanges() {
        // La logique de sauvegarde est maintenant ici
        for (LocalDate date : view.getAddedDisponibilites()) {
            secouristeDAO.addAvailability(compte.getIdSecouriste(), date);
        }
        for (LocalDate date : view.getRemovedDisponibilites()) {
            secouristeDAO.removeAvailability(compte.getIdSecouriste(), date);
        }
        NotificationUtils.showSuccess("Succès", "Vos disponibilités ont été mises à jour.");
        // Le contrôleur gère la navigation
        navigator.showUserCalendrierView(compte);
    }

    private void handleCancelChanges() {
        // Le contrôleur gère la navigation
        navigator.showUserCalendrierView(compte);
    }
}