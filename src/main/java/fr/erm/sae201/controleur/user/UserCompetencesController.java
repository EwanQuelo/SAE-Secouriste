package fr.erm.sae201.controleur.user;

import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.service.SecouristeMngt;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.user.UserCompetencesView;
import javafx.application.Platform;

import java.util.Set;

/**
 * Contrôleur pour la vue affichant les compétences d'un secouriste.
 * 
 * Cette classe est responsable de charger les compétences associées à
 * l'utilisateur connecté et de les transmettre à la vue pour affichage.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class UserCompetencesController {

    /** La vue des compétences gérée par ce contrôleur. */
    private final UserCompetencesView view;

    /** Le compte de l'utilisateur secouriste connecté. */
    private final CompteUtilisateur compte;

    /** Le service métier pour la gestion des secouristes. */
    private final SecouristeMngt secouristeMngt;

    /**
     * Constructeur du contrôleur des compétences de l'utilisateur.
     *
     * @param view   La vue à contrôler.
     * @param compte Le compte de l'utilisateur connecté.
     */
    public UserCompetencesController(UserCompetencesView view, CompteUtilisateur compte) {
        this.view = view;
        this.compte = compte;
        this.secouristeMngt = new SecouristeMngt();

        loadUserCompetences();
    }

    /**
     * Charge les compétences de l'utilisateur de manière asynchrone.
     * Récupère l'objet Secouriste via le service, puis extrait ses compétences
     * et met à jour la vue sur le thread de l'application JavaFX.
     * Gère le cas où le profil du secouriste ne serait pas trouvé.
     */
    private void loadUserCompetences() {
        new Thread(() -> {
            if (compte.getIdSecouriste() != null) {
                try {
                    Secouriste secouriste = secouristeMngt.getSecouriste(compte.getIdSecouriste());
                    Set<Competence> competences = secouriste.getCompetences();
                    Platform.runLater(() -> view.displayCompetences(competences));
                } catch (EntityNotFoundException e) {
                    System.err.println("Erreur: Impossible de charger les compétences car le secouriste n'a pas été trouvé.");
                    Platform.runLater(() -> NotificationUtils.showError("Erreur" ,"Profil utilisateur introuvable."));
                }
            }
        }).start();
    }
}