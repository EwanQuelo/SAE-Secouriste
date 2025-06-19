package fr.erm.sae201.controleur.user;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.user.UserDispoView;

import java.time.LocalDate;

/**
 * Contrôleur pour la vue de gestion des disponibilités de l'utilisateur.
 * <p>
 * Cette classe contient la logique métier pour sauvegarder ou annuler les
 * changements de disponibilités effectués par le secouriste dans la vue associée.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class UserDispoController {

    /** La vue de gestion des disponibilités associée. */
    private final UserDispoView view;

    /** Le navigateur principal de l'application. */
    private final MainApp navigator;

    /** Le compte de l'utilisateur connecté. */
    private final CompteUtilisateur compte;

    /** Le DAO pour mettre à jour les disponibilités en base de données. */
    private final SecouristeDAO secouristeDAO;

    /**
     * Constructeur du contrôleur des disponibilités.
     *
     * @param view      La vue à contrôler.
     * @param navigator Le navigateur principal.
     * @param compte    Le compte de l'utilisateur connecté.
     */
    public UserDispoController(UserDispoView view, MainApp navigator, CompteUtilisateur compte) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.secouristeDAO = new SecouristeDAO();

        this.view.setSaveAction(e -> handleSaveChanges());
        this.view.setCancelAction(e -> handleCancelChanges());
    }

    /**
     * Gère l'enregistrement des modifications de disponibilités.
     * Itère sur les listes de dates à ajouter et à supprimer fournies par la vue,
     * appelle le DAO pour chaque date, puis affiche une notification de succès
     * avant de ramener l'utilisateur à son calendrier.
     */
    private void handleSaveChanges() {
        for (LocalDate date : view.getAddedDisponibilites()) {
            secouristeDAO.addAvailability(compte.getIdSecouriste(), date);
        }
        for (LocalDate date : view.getRemovedDisponibilites()) {
            secouristeDAO.removeAvailability(compte.getIdSecouriste(), date);
        }
        NotificationUtils.showSuccess("Succès", "Vos disponibilités ont été mises à jour.");
        navigator.showUserCalendrierView(compte);
    }

    /**
     * Gère l'annulation des modifications.
     * Ramène simplement l'utilisateur à la vue du calendrier sans enregistrer
     * aucun des changements effectués.
     */
    private void handleCancelChanges() {
        navigator.showUserCalendrierView(compte);
    }
}