package fr.erm.sae201.metier.graphe.modele;

import fr.erm.sae201.metier.graphe.algorithme.ModelesAlgorithme.Poste;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.util.List;

/**
 * Représente un graphe biparti pour le problème d'affectation.
 * Contient les deux ensembles de sommets (Secouristes et Postes)
 * et une matrice d'adjacence représentant les aptitudes.
 */
public class Graphe {

    private final List<Secouriste> secouristes;
    private final List<Poste> postes;
    private final int[][] adjacenceMatrice;

    /**
     * Construit un objet Graphe.
     *
     * @param secouristes      La liste des secouristes (un des ensembles de sommets).
     * @param postes           La liste des postes à pourvoir (l'autre ensemble de sommets).
     * @param adjacenceMatrice La matrice où matrice[i][j] == 1 si le secouriste i est apte pour le poste j.
     */
    public Graphe(List<Secouriste> secouristes, List<Poste> postes, int[][] adjacenceMatrice) {
        this.secouristes = secouristes;
        this.postes = postes;
        this.adjacenceMatrice = adjacenceMatrice;
    }

    public List<Secouriste> getSecouristes() {
        return secouristes;
    }

    public List<Poste> getPostes() {
        return postes;
    }

    public int[][] getAdjacenceMatrice() {
        return adjacenceMatrice;
    }

    public int getNombreSecouristes() {
        return secouristes.size();
    }

    public int getNombrePostes() {
        return postes.size();
    }
}