package fr.erm.sae201.controleur.user;

import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.service.SecouristeMngt;
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
        // C'est une bonne pratique de faire les appels BDD dans un thread séparé
        // pour ne pas geler l'interface utilisateur.
        new Thread(() -> {
            if (compte.getIdSecouriste() != null) {
                // On récupère le profil complet du secouriste
                secouristeMngt.getSecouriste(compte.getIdSecouriste()).ifPresent(secouriste -> {
                    // On récupère ses compétences
                    Set<Competence> competences = secouriste.getCompetences();
                    // On met à jour l'interface graphique sur le thread JavaFX
                    Platform.runLater(() -> view.displayCompetences(competences));
                });
            }
        }).start();
    }
}