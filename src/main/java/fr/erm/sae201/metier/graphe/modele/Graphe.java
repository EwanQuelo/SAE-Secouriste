package fr.erm.sae201.metier.graphe.modele;

import fr.erm.sae201.metier.persistence.Secouriste;

import java.util.List;

/**
 * Représente un graphe biparti utilisé pour modéliser le problème d'affectation.
 * 
 * Cette structure de données contient les deux ensembles de sommets (les secouristes
 * et les postes à pourvoir) ainsi qu'une matrice d'adjacence qui définit les arêtes
 * possibles, c'est-à-dire l'aptitude d'un secouriste pour un poste donné.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class Graphe {

    /** La liste des secouristes, représentant le premier ensemble de sommets du graphe. */
    private final List<Secouriste> secouristes;

    /** La liste des postes à pourvoir, représentant le second ensemble de sommets. */
    private final List<Poste> postes;

    /** La matrice d'adjacence où matrice[i][j] = 1 si le secouriste i est apte pour le poste j. */
    private final int[][] adjacenceMatrice;

    /**
     * Construit un objet Graphe.
     * @param secouristes      La liste des secouristes (un des ensembles de sommets).
     * @param postes           La liste des postes à pourvoir (l'autre ensemble de sommets).
     * @param adjacenceMatrice La matrice d'adjacence représentant les aptitudes.
     */
    public Graphe(List<Secouriste> secouristes, List<Poste> postes, int[][] adjacenceMatrice) {
        this.secouristes = secouristes;
        this.postes = postes;
        this.adjacenceMatrice = adjacenceMatrice;
    }

    /**
     * Retourne la liste des secouristes.
     *
     * @return La liste des secouristes.
     */
    public List<Secouriste> getSecouristes() {
        return secouristes;
    }

    /**
     * Retourne la liste des postes.
     *
     * @return La liste des postes.
     */
    public List<Poste> getPostes() {
        return postes;
    }

    /**
     * Retourne la matrice d'adjacence.
     *
     * @return La matrice d'adjacence.
     */
    public int[][] getAdjacenceMatrice() {
        return adjacenceMatrice;
    }

    /**
     * Retourne le nombre total de secouristes dans le graphe.
     *
     * @return Le nombre de secouristes.
     */
    public int getNombreSecouristes() {
        return secouristes.size();
    }

    /**
     * Retourne le nombre total de postes dans le graphe.
     *
     * @return Le nombre de postes.
     */
    public int getNombrePostes() {
        return postes.size();
    }
}