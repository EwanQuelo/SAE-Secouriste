package fr.erm.sae201.metier.graphe.algorithme;

import fr.erm.sae201.metier.graphe.modele.AffectationResultat;
import fr.erm.sae201.metier.graphe.modele.Poste;
import fr.erm.sae201.metier.graphe.modele.Graphe;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implémente un algorithme d'affectation simple et rapide (glouton).
 * Il parcourt chaque poste et lui assigne le premier secouriste apte et disponible qu'il trouve.
 * Ne garantit pas une solution optimale.
 * 
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AlgorithmeAffectationGloutonne {

    /**
     * Point d'entrée pour résoudre le problème d'affectation pour le graphe donné.
     *
     * @param graphe Le graphe représentant le problème (secouristes, postes, et aptitudes).
     * @return Une liste d'affectations trouvées.
     */
    public List<AffectationResultat> resoudre(Graphe graphe) {
        List<AffectationResultat> affectationsTrouvees = new ArrayList<>();
        
        if (graphe.getNombrePostes() == 0 || graphe.getNombreSecouristes() == 0) {
            return affectationsTrouvees;
        }

        // Utilise les indices pour suivre les secouristes déjà affectés
        Set<Integer> indicesSecouristesDejaAffectes = new HashSet<>();
        List<Poste> postes = graphe.getPostes();
        List<Secouriste> secouristes = graphe.getSecouristes();
        int[][] matrice = graphe.getAdjacenceMatrice();

        // On parcourt les POSTES en premier.
        for (int j = 0; j < postes.size(); j++) {
            Poste posteCourant = postes.get(j);

            // Pour chaque poste, on cherche le premier secouriste libre et apte.
            for (int i = 0; i < secouristes.size(); i++) {
                // Si le secouriste n'a pas déjà été pris
                if (!indicesSecouristesDejaAffectes.contains(i)) {
                    // Si le secouriste est apte pour le poste (vérification dans la matrice)
                    if (matrice[i][j] == 1) {
                        // On crée l'affectation
                        affectationsTrouvees.add(new AffectationResultat(secouristes.get(i), posteCourant));
                        // On marque le secouriste comme pris
                        indicesSecouristesDejaAffectes.add(i);
                        // On arrête de chercher un secouriste pour CE poste et on passe au suivant
                        break;
                    }
                }
            }
        }

        return affectationsTrouvees;
    }
}