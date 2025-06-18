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

public class UserCompetencesController {

    private final UserCompetencesView view;
    private final CompteUtilisateur compte;
    private final SecouristeMngt secouristeMngt;

    public UserCompetencesController(UserCompetencesView view, CompteUtilisateur compte) {
        this.view = view;
        this.compte = compte;
        this.secouristeMngt = new SecouristeMngt();

        loadUserCompetences();
    }

    private void loadUserCompetences() {
        new Thread(() -> {
            if (compte.getIdSecouriste() != null) {
                try {
                    // MODIFIÉ : On récupère directement l'objet Secouriste.
                    Secouriste secouriste = secouristeMngt.getSecouriste(compte.getIdSecouriste());
                    Set<Competence> competences = secouriste.getCompetences();
                    Platform.runLater(() -> view.displayCompetences(competences));
                } catch (EntityNotFoundException e) {
                    // MODIFIÉ : On gère le cas où le secouriste n'est pas trouvé.
                    System.err.println("Erreur: Impossible de charger les compétences car le secouriste n'a pas été trouvé.");
                    // On pourrait aussi afficher un message dans la vue
                    Platform.runLater(() -> NotificationUtils.showError("Erreur" ,"Profil utilisateur introuvable."));
                }
            }
        }).start();
    }
}