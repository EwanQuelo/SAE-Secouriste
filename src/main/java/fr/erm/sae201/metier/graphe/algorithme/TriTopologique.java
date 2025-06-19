package fr.erm.sae201.metier.graphe.algorithme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Map;

import fr.erm.sae201.metier.persistence.Competence;

/**
 * Classe permettant de vérifier si un ensemble de compétences forme un graphe sans cycle
 * et d'en fournir un tri topologique si possible.
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class TriTopologique {

    /**
     * Trie topologiquement les compétences si le graphe est acyclique.
     * 
     * @param competences l'ensemble des compétences à trier
     * @return une liste triée des compétences selon leurs prérequis
     */
    private static List<Competence> getListeTrier(Set<Competence> competences) {
        // Initilise le dictionnaire des degrés entrants
        Map<Competence, Integer> degresEntrants = new HashMap<>();
        for (Competence comp : competences) {
            degresEntrants.put(comp, 0);
        }

        // Remplit le dictionnaire avec les degrés entrants de chaque compétence
        for (Competence comp : competences) {
            for (Competence prerequis : comp.getPrerequisites()) {
                if (competences.contains(prerequis)) {
                    degresEntrants.put(comp, degresEntrants.get(comp) + 1);
                }
            }
        }

        // Crée une file pour les compétences avec un degré entrant de 0
        Queue<Competence> file = new LinkedList<>();
        for (Map.Entry<Competence, Integer> entree : degresEntrants.entrySet()) {
            if (entree.getValue() == 0) {
                file.offer(entree.getKey());
            }
        }

        // Liste pour stocker le résultat du tri topologique
        List<Competence> resultat = new ArrayList<>();

        // Traitement du tri topologique
        while (!file.isEmpty()) {
            Competence courante = file.poll();
            resultat.add(courante);

            // Le but ici est de supprimer les compétences qui n'ont pas de degré entrant
            // et de mettre à jour les degrés entrants des compétences dépendantes
            for (Competence comp : competences) {
                if (comp.getPrerequisites().contains(courante)) {
                    int nouveauDegre = degresEntrants.get(comp) - 1;
                    degresEntrants.put(comp, nouveauDegre);

                    if (nouveauDegre == 0) {
                        file.offer(comp);
                    }
                }
            }
        }

        return resultat;
    }

    /**
     * Vérifie si l'ensemble de compétences ne contient aucun cycle.
     * 
     * @param competences l'ensemble des compétences à analyser
     * @return true si le graphe est acyclique, false sinon
     */
    public boolean estAcyclique(Set<Competence> competences) {
        List<Competence> listeTriee = getListeTrier(competences);
        return listeTriee.size() == competences.size();
    }
}
