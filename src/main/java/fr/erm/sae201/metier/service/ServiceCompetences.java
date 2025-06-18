package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.metier.persistence.Competence;
import java.util.*;

/**
 * Service pour gérer la logique liée à la hiérarchie des compétences.
 * Il charge le graphe des compétences et permet de vérifier les relations de prérequis.
 */
public class ServiceCompetences {

    private final Map<String, Set<String>> grapheAdjacence = new HashMap<>();

    public ServiceCompetences() {
        // Charge toutes les compétences et construit un graphe simple (liste d'adjacence)
        CompetenceDAO competenceDAO = new CompetenceDAO();
        List<Competence> toutesLesCompetences = competenceDAO.findAll(); // Suppose que le DAO charge les prérequis
        for (Competence competence : toutesLesCompetences) {
            Set<String> prerequisIntitules = new HashSet<>();
            for (Competence prerequis : competence.getPrerequisites()) {
                prerequisIntitules.add(prerequis.getIntitule());
            }
            grapheAdjacence.put(competence.getIntitule(), prerequisIntitules);
        }
    }

    /**
     * Vérifie si un secouriste, avec son ensemble de compétences,
     * satisfait une compétence requise en tenant compte de la hiérarchie.
     * @param competencesPossedees Le Set des compétences du secouriste.
     * @param competenceRequise La compétence demandée pour le poste.
     * @return true si une des compétences possédées est égale ou supérieure à la compétence requise.
     */
    public boolean possedeCompetenceRequiseOuSuperieure(Set<Competence> competencesPossedees, Competence competenceRequise) {
        for (Competence possedee : competencesPossedees) {
            if (estSuperieureOuEgale(possedee.getIntitule(), competenceRequise.getIntitule())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fonction récursive qui vérifie si une compétence est "meilleure" qu'une autre.
     * Elle explore le graphe des prérequis.
     * @param intitulePossedee L'intitulé de la compétence qu'on a.
     * @param intituleRequise L'intitulé de la compétence qu'on vise.
     * @return true si la compétence possédée est égale ou un "parent" de la compétence requise.
     */
    private boolean estSuperieureOuEgale(String intitulePossedee, String intituleRequise) {
        // Condition d'arrêt 1 : C'est la même compétence.
        if (intitulePossedee.equals(intituleRequise)) {
            return true;
        }

        // On récupère les prérequis directs de la compétence que l'on possède.
        Set<String> prerequisDirects = grapheAdjacence.get(intitulePossedee);

        // Condition d'arrêt 2 : Si la compétence n'a pas de prérequis, on ne peut pas descendre plus bas.
        if (prerequisDirects == null || prerequisDirects.isEmpty()) {
            return false;
        }

        // Exploration récursive : on demande si l'un de nos prérequis est suffisant.
        for (String prerequis : prerequisDirects) {
            if (estSuperieureOuEgale(prerequis, intituleRequise)) {
                return true;
            }
        }

        return false;
    }
}