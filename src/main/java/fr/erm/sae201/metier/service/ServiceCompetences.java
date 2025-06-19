package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.metier.persistence.Competence;
import java.util.*;

/**
 * Service pour gérer la logique métier liée à la hiérarchie des compétences.
 * <p>
 * Il construit un graphe des prérequis en mémoire au moment de son instanciation,
 * puis l'utilise pour vérifier si une compétence possédée par un secouriste est
 * suffisante pour un poste requérant une compétence inférieure.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class ServiceCompetences {

    /** Le graphe des compétences, représenté par une liste d'adjacence. */
    private final Map<String, Set<String>> grapheAdjacence = new HashMap<>();

    /**
     * Constructeur du service.
     * Charge l'ensemble des compétences depuis la base de données et construit
     * une représentation en mémoire du graphe des prérequis pour des consultations rapides.
     */
    public ServiceCompetences() {
        CompetenceDAO competenceDAO = new CompetenceDAO();
        List<Competence> toutesLesCompetences = competenceDAO.findAll();
        for (Competence competence : toutesLesCompetences) {
            Set<String> prerequisIntitules = new HashSet<>();
            for (Competence prerequis : competence.getPrerequisites()) {
                prerequisIntitules.add(prerequis.getIntitule());
            }
            grapheAdjacence.put(competence.getIntitule(), prerequisIntitules);
        }
    }

    /**
     * Vérifie si un secouriste, avec son ensemble de compétences, satisfait une
     * compétence requise en tenant compte de la hiérarchie.
     * <p>
     * Pour chaque compétence possédée par le secouriste, elle vérifie si celle-ci
     * est équivalente ou supérieure à la compétence requise en explorant le graphe des prérequis.
     * </p>
     *
     * @param competencesPossedees L'ensemble des compétences du secouriste.
     * @param competenceRequise    La compétence demandée pour le poste.
     * @return `true` si une des compétences possédées est égale ou supérieure à la compétence requise.
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
     * Détermine par une exploration récursive si une compétence possédée est
     * équivalente ou hiérarchiquement supérieure à une compétence requise.
     *
     * @param intitulePossedee L'intitulé de la compétence que le secouriste possède.
     * @param intituleRequise  L'intitulé de la compétence requise pour le poste.
     * @return `true` si la compétence possédée est un "parent" ou est égale à la compétence requise.
     */
    private boolean estSuperieureOuEgale(String intitulePossedee, String intituleRequise) {
        // Cas de base 1 : La compétence possédée est exactement celle qui est requise.
        if (intitulePossedee.equals(intituleRequise)) {
            return true;
        }

        Set<String> prerequisDirects = grapheAdjacence.get(intitulePossedee);

        // Cas de base 2 : La compétence possédée n'a pas de prérequis, l'exploration s'arrête ici.
        if (prerequisDirects == null || prerequisDirects.isEmpty()) {
            return false;
        }

        // Étape récursive : On vérifie si l'un des prérequis de la compétence possédée
        // est lui-même supérieur ou égal à la compétence requise.
        for (String prerequis : prerequisDirects) {
            if (estSuperieureOuEgale(prerequis, intituleRequise)) {
                return true;
            }
        }

        return false;
    }
}