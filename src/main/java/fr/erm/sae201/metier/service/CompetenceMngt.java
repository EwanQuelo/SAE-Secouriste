package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.metier.graphe.algorithme.TriTopologique;
import fr.erm.sae201.metier.persistence.Competence;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * NOUVEAU : Service pour gérer la logique métier des compétences.
 * Ce service encapsule les règles de validation, comme la détection de cycles.
 */
public class CompetenceMngt {

    private final CompetenceDAO competenceDAO = new CompetenceDAO();
    private final TriTopologique validator = new TriTopologique();

    /**
     * Exception personnalisée pour une règle métier spécifique.
     */
    public static class CycleDetectedException extends Exception {
        public CycleDetectedException(String message) {
            super(message);
        }
    }

    /**
     * Crée une nouvelle compétence et ses prérequis après avoir validé
     * qu'aucun cycle n'est créé.
     *
     * @param intitule      Le nom de la nouvelle compétence.
     * @param prerequisites La liste de ses prérequis.
     * @throws CycleDetectedException Si un cycle de dépendance est détecté.
     * @throws SQLException           Si une erreur BDD survient (ex: nom dupliqué).
     */
    public void createCompetence(String intitule, List<Competence> prerequisites) throws CycleDetectedException, SQLException {
        // 1. On récupère le graphe actuel des compétences
        Set<Competence> graphToTest = new HashSet<>(competenceDAO.findAll());

        // 2. On simule l'ajout de la nouvelle compétence et de ses prérequis
        Competence newCompetenceNode = new Competence(intitule);
        newCompetenceNode.setPrerequisites(new HashSet<>(prerequisites));
        graphToTest.add(newCompetenceNode);

        // 3. On utilise le validateur pour vérifier la présence d'un cycle
        if (!validator.estAcyclique(graphToTest)) {
            throw new CycleDetectedException("L'ajout de ces prérequis créerait un cycle de dépendances.");
        }

        // 4. Si la validation passe, on procède à la création en BDD (idéalement dans une transaction)
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
     * qu'aucun cycle n'est créé par la modification.
     *
     * @param competenceToEdit      La compétence à modifier.
     * @param newPrerequisitesList  La nouvelle liste de prérequis.
     * @throws CycleDetectedException Si la modification crée un cycle.
     */
    public void updatePrerequisites(Competence competenceToEdit, List<Competence> newPrerequisitesList) throws CycleDetectedException {
        Set<Competence> allCompetences = new HashSet<>(competenceDAO.findAll());

        // On trouve la compétence à modifier dans notre copie du graphe
        Competence nodeToUpdate = allCompetences.stream()
            .filter(c -> c.equals(competenceToEdit))
            .findFirst().orElse(null);

        if (nodeToUpdate != null) {
            // On met à jour ses prérequis dans la copie pour le test
            nodeToUpdate.setPrerequisites(new HashSet<>(newPrerequisitesList));

            if (!validator.estAcyclique(allCompetences)) {
                throw new CycleDetectedException("Cette modification créerait un cycle de dépendances.");
            }
        }

        // Si le test est réussi, on applique les changements en BDD
        // D'abord, on supprime tous les anciens prérequis
        Set<Competence> oldPrerequisites = competenceToEdit.getPrerequisites();
        for (Competence oldPrereq : oldPrerequisites) {
            competenceDAO.removePrerequisite(competenceToEdit.getIntitule(), oldPrereq.getIntitule());
        }

        // Ensuite, on ajoute les nouveaux
        for (Competence newPrereq : newPrerequisitesList) {
            competenceDAO.addPrerequisite(competenceToEdit.getIntitule(), newPrereq.getIntitule());
        }
    }
}