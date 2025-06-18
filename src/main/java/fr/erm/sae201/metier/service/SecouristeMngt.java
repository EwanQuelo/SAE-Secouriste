package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SecouristeMngt {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    // ... (les méthodes existantes comme getSecouriste, getAllSecouristes, etc. ne changent pas)
    public Optional<Secouriste> getSecouriste(long id) {
        return Optional.ofNullable(secouristeDAO.findByID(id));
    }

    public List<Secouriste> getAllSecouristes() {
        return secouristeDAO.findAll();
    }

    public int getTotalSecouristesCount(String query) {
        return secouristeDAO.countFiltered(query);
    }

    public List<Secouriste> getSecouristesByPage(String query, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return secouristeDAO.findFilteredAndPaginated(query, offset, pageSize);
    }

    public boolean deleteSecouriste(long secouristeId) {
        Optional<Secouriste> secouristeOpt = getSecouriste(secouristeId);
        if (secouristeOpt.isPresent()) {
            return secouristeDAO.delete(secouristeOpt.get()) > 0;
        }
        return false;
    }

    /**
     * MODIFIÉ : La méthode `update` est conservée pour des mises à jour simples.
     */
    public boolean update(Secouriste secouriste) {
        return secouristeDAO.update(secouriste) > 0;
    }

    /**
     * NOUVEAU : Méthode de service complète pour mettre à jour un secouriste.
     * Met à jour les informations personnelles et synchronise les compétences.
     *
     * @param secouristeToUpdate L'objet Secouriste avec les nouvelles informations personnelles.
     * @param newCompetences     L'ensemble complet des compétences que le secouriste doit avoir.
     * @return true si toutes les opérations ont réussi.
     */
    public boolean updateSecouristeInfoAndCompetences(Secouriste secouristeToUpdate, Set<Competence> newCompetences) {
        // Idéalement, tout ceci serait dans une transaction BDD.

        try {
            // 1. Mettre à jour les informations personnelles
            secouristeDAO.update(secouristeToUpdate);

            // 2. Synchroniser les compétences
            long secouristeId = secouristeToUpdate.getId();
            Set<Competence> oldCompetences = secouristeDAO.findCompetencesForSecouriste(secouristeId);

            // 2a. Trouver les compétences à ajouter
            Set<Competence> competencesToAdd = new HashSet<>(newCompetences);
            competencesToAdd.removeAll(oldCompetences);
            for (Competence c : competencesToAdd) {
                secouristeDAO.addCompetenceToSecouriste(secouristeId, c.getIntitule());
            }

            // 2b. Trouver les compétences à supprimer
            Set<Competence> competencesToRemove = new HashSet<>(oldCompetences);
            competencesToRemove.removeAll(newCompetences);
            for (Competence c : competencesToRemove) {
                secouristeDAO.removeCompetenceFromSecouriste(secouristeId, c.getIntitule());
            }

            return true; // Si aucune exception n'est levée

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour complète du secouriste: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Les autres méthodes de gestion de compétences/disponibilités restent utiles
    public boolean addCompetenceToSecouriste(long secouristeId, String intituleCompetence) {
        return secouristeDAO.addCompetenceToSecouriste(secouristeId, intituleCompetence) > 0;
    }

    public boolean removeCompetenceFromSecouriste(long secouristeId, String intituleCompetence) {
        return secouristeDAO.removeCompetenceFromSecouriste(secouristeId, intituleCompetence) > 0;
    }

    public boolean addAvailability(long secouristeId, LocalDate date) {
        return secouristeDAO.addAvailability(secouristeId, date) > 0;
    }

    public boolean removeAvailability(long secouristeId, LocalDate date) {
        return secouristeDAO.removeAvailability(secouristeId, date) > 0;
    }
}