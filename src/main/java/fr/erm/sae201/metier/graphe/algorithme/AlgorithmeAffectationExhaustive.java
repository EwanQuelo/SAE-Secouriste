package fr.erm.sae201.metier.graphe.algorithme;

import fr.erm.sae201.metier.graphe.modele.AffectationResultat;
import fr.erm.sae201.metier.graphe.modele.Poste;
import fr.erm.sae201.metier.graphe.modele.Graphe;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémente un algorithme d'affectation par recherche exhaustive (backtracking).
 * Il garantit de trouver la solution optimale (le plus grand nombre d'affectations)
 * en explorant toutes les combinaisons possibles.
 * 
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AlgorithmeAffectationExhaustive {

    private List<AffectationResultat> meilleureSolutionTrouvee;
    private Graphe graphe;

    /**
     * Point d'entrée pour résoudre le problème d'affectation pour le graphe donné.
     *
     * @param graphe Le graphe représentant le problème (secouristes, postes, et aptitudes).
     * @return La meilleure liste d'affectations trouvée.
     */
    public List<AffectationResultat> resoudre(Graphe graphe) {
        this.graphe = graphe;
        this.meilleureSolutionTrouvee = new ArrayList<>();

        // Initialisation pour le backtracking
        boolean[] secouristeEstPris = new boolean[graphe.getNombreSecouristes()];
        List<AffectationResultat> affectationActuelle = new ArrayList<>();

        // Lancement de la recherche récursive depuis le premier poste (index 0)
        chercherSolution(0, secouristeEstPris, affectationActuelle);

        return meilleureSolutionTrouvee;
    }

    /**
     * Méthode récursive de backtracking.
     * Pour chaque poste, elle essaie de l'assigner à chaque secouriste apte et disponible.
     * Elle explore aussi la branche où le poste n'est pas pourvu.
     *
     * @param indexPoste          L'index du poste que l'on essaie de pourvoir.
     * @param secouristeEstPris   Tableau de booléens pour savoir si un secouriste est déjà affecté.
     * @param affectationActuelle La liste des affectations construite dans la branche actuelle.
     */
    private void chercherSolution(int indexPoste, boolean[] secouristeEstPris, List<AffectationResultat> affectationActuelle) {
        // Condition de base : si on a considéré tous les postes, on a une solution complète potentielle
        if (indexPoste == graphe.getNombrePostes()) {
            if (affectationActuelle.size() > meilleureSolutionTrouvee.size()) {
                meilleureSolutionTrouvee = new ArrayList<>(affectationActuelle);
            }
            return;
        }

        Poste posteCourant = graphe.getPostes().get(indexPoste);
        List<Secouriste> secouristes = graphe.getSecouristes();
        int[][] matrice = graphe.getAdjacenceMatrice();

        // BRANCHE 1 : On essaie d'affecter le poste courant à un secouriste
        for (int i = 0; i < secouristes.size(); i++) {
            // Si le secouriste n'est pas déjà pris ET qu'il est apte (via la matrice)
            if (!secouristeEstPris[i] && matrice[i][indexPoste] == 1) {
                // On l'affecte provisoirement
                secouristeEstPris[i] = true;
                affectationActuelle.add(new AffectationResultat(secouristes.get(i), posteCourant));

                // On passe récursivement au poste suivant
                chercherSolution(indexPoste + 1, secouristeEstPris, affectationActuelle);

                // BACKTRACKING : on annule l'affectation pour tester d'autres possibilités
                affectationActuelle.remove(affectationActuelle.size() - 1);
                secouristeEstPris[i] = false;
            }
        }

        // BRANCHE 2 : On explore la possibilité de NE PAS pourvoir ce poste et de passer au suivant
        chercherSolution(indexPoste + 1, secouristeEstPris, affectationActuelle);
    }
}