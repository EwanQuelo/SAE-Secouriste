package fr.erm.sae201.metier.graphe.algorithme;

import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Secouriste;

/**
 * Ce fichier contient les modèles de données (records) partagés
 * par les différents algorithmes d'affectation.
 */
public class ModelesAlgorithme {

    /**
     * Représente un poste de travail unique à pourvoir.
     */
    public record Poste(long idDps, Competence competenceRequise) {}

    /**
     * Représente une affectation simple : un secouriste pour un poste.
     */
    public record AffectationResultat(Secouriste secouriste, Poste poste) {}

}