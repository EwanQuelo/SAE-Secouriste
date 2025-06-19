package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.metier.graphe.algorithme.TriTopologique;
import fr.erm.sae201.metier.persistence.Competence;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service pour gérer la logique métier des compétences.
 * 
 * Ce service encapsule les règles de validation, comme la détection de
 * dépendances cycliques entre les compétences, avant d'appliquer les
 * changements en base de données.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class CompetenceMngt {

    private final CompetenceDAO competenceDAO = new CompetenceDAO();
    private final TriTopologique validator = new TriTopologique();

    /**
     * Exception personnalisée levée lorsqu'un cycle de dépendances est détecté.
     */
    public static class CycleDetectedException extends Exception {
        /**
         * Construit une nouvelle CycleDetectedException avec un message détaillé.
         * @param message Le message expliquant l'erreur de cycle.
         */
        public CycleDetectedException(String message) {
            super(message);
        }
    }

    /**
     * Crée une nouvelle compétence et ses prérequis après avoir validé
     * qu'aucun cycle de dépendance n'est introduit.
     *
     * @param intitule      Le nom de la nouvelle compétence.
     * @param prerequisites La liste de ses prérequis.
     * @throws CycleDetectedException Si un cycle de dépendance est détecté.
     * @throws SQLException           Si une erreur BDD survient, par exemple si le nom est dupliqué.
     */
    public void createCompetence(String intitule, List<Competence> prerequisites) throws CycleDetectedException, SQLException {
        // Simule l'ajout de la nouvelle compétence et de ses prérequis au graphe actuel pour tester la cyclicité.
        Set<Competence> graphToTest = new HashSet<>(competenceDAO.findAll());
        Competence newCompetenceNode = new Competence(intitule);
        newCompetenceNode.setPrerequisites(new HashSet<>(prerequisites));
        graphToTest.add(newCompetenceNode);

        if (!validator.estAcyclique(graphToTest)) {
            throw new CycleDetectedException("L'ajout de ces prérequis créerait un cycle de dépendances.");
        }

        // Si la validation passe, on procède à la création en BDD.
        // Idéalement, cette partie devrait être dans une transaction pour garantir l'atomicité.
        Competence competenceToSave = new Competence(intitule);
        if (competenceDAO.create(competenceToSave) <= 0) {
            throw new SQLException("Une compétence avec ce nom existe déjà ou une erreur est survenue.");
        }

        for (Competence prereq : prerequisites) {
            competenceDAO.addPrerequisite(intitule, prereq.getIntitule());
        }
    }

    /**
     * Met à jour les prérequis d'une compétence existante après avoir validé
     * que la modification n'introduit pas de cycle de dépendance.
     *
     * @param competenceToEdit      La compétence à modifier.
     * @param newPrerequisitesList  La nouvelle liste de prérequis.
     * @throws CycleDetectedException Si la modification crée un cycle.
     */
    public void updatePrerequisites(Competence competenceToEdit, List<Competence> newPrerequisitesList) throws CycleDetectedException {
        Set<Competence> allCompetences = new HashSet<>(competenceDAO.findAll());

        // Recherche la compétence à modifier dans une copie locale du graphe.
        Competence nodeToUpdate = allCompetences.stream()
            .filter(c -> c.equals(competenceToEdit))
            .findFirst().orElse(null);

        if (nodeToUpdate != null) {
            // Met à jour ses prérequis dans la copie pour effectuer le test de cyclicité.
            nodeToUpdate.setPrerequisites(new HashSet<>(newPrerequisitesList));

            if (!validator.estAcyclique(allCompetences)) {
                throw new CycleDetectedException("Cette modification créerait un cycle de dépendances.");
            }
        }

        // Si le test réussit, applique les changements en base de données.
        // D'abord, supprime tous les anciens prérequis.
        Set<Competence> oldPrerequisites = competenceToEdit.getPrerequisites();
        for (Competence oldPrereq : oldPrerequisites) {
            competenceDAO.removePrerequisite(competenceToEdit.getIntitule(), oldPrereq.getIntitule());
        }

        // Ensuite, ajoute les nouveaux.
        for (Competence newPrereq : newPrerequisitesList) {
            competenceDAO.addPrerequisite(competenceToEdit.getIntitule(), newPrereq.getIntitule());
        }
    }
}