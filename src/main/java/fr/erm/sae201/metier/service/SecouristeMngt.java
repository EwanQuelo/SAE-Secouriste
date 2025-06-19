package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service pour gérer la logique métier liée aux secouristes.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SecouristeMngt {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    /**
     * Récupère un secouriste par son ID.
     *
     * @param id L'ID du secouriste.
     * @return L'objet Secouriste s'il est trouvé.
     * @throws EntityNotFoundException si aucun secouriste avec cet ID n'est trouvé.
     */
    public Secouriste getSecouriste(long id) {
        Secouriste secouriste = secouristeDAO.findByID(id);
        if (secouriste == null) {
            throw new EntityNotFoundException("Secouriste non trouvé avec l'ID : " + id);
        }
        return secouriste;
    }

    /**
     * Récupère la liste de tous les secouristes.
     *
     * @return Une liste de tous les objets Secouriste.
     */
    public List<Secouriste> getAllSecouristes() {
        return secouristeDAO.findAll();
    }

    /**
     * Obtient le nombre total de secouristes, éventuellement filtré par une recherche.
     *
     * @param query Le terme de recherche (peut être vide).
     * @return Le nombre total de secouristes correspondants.
     */
    public int getTotalSecouristesCount(String query) {
        return secouristeDAO.countFiltered(query);
    }

    /**
     * Récupère une "page" de secouristes, filtrée et paginée.
     *
     * @param query      Le terme de recherche.
     * @param pageNumber Le numéro de la page à récupérer (commence à 1).
     * @param pageSize   Le nombre d'éléments par page.
     * @return Une liste de secouristes pour la page demandée.
     */
    public List<Secouriste> getSecouristesByPage(String query, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return secouristeDAO.findFilteredAndPaginated(query, offset, pageSize);
    }

    /**
     * Supprime un secouriste par son ID.
     *
     * @param secouristeId L'ID du secouriste à supprimer.
     * @return `true` si la suppression a réussi, `false` sinon.
     */
    public boolean deleteSecouriste(long secouristeId) {
        try {
            Secouriste secouriste = getSecouriste(secouristeId);
            return secouristeDAO.delete(secouriste) > 0;
        } catch (EntityNotFoundException e) {
            // Le secouriste n'existe déjà pas, la suppression n'est donc pas nécessaire.
            return false;
        }
    }

    /**
     * Met à jour les informations de base d'un secouriste.
     *
     * @param secouriste Le secouriste avec les informations mises à jour.
     * @return `true` si la mise à jour a réussi, `false` sinon.
     */
    public boolean update(Secouriste secouriste) {
        return secouristeDAO.update(secouriste) > 0;
    }

    /**
     * Met à jour les informations et les compétences d'un secouriste de manière transactionnelle.
     *
     * @param secouristeToUpdate Le secouriste avec les informations de base mises à jour.
     * @param newCompetences L'ensemble final des compétences que le secouriste doit posséder.
     * @return `true` si l'ensemble des opérations a réussi, `false` en cas d'erreur.
     */
    public boolean updateSecouristeInfoAndCompetences(Secouriste secouristeToUpdate, Set<Competence> newCompetences) {
        try {
            secouristeDAO.update(secouristeToUpdate);

            long secouristeId = secouristeToUpdate.getId();
            Set<Competence> oldCompetences = secouristeDAO.findCompetencesForSecouriste(secouristeId);

            // Calcule les compétences à ajouter
            Set<Competence> competencesToAdd = new HashSet<>(newCompetences);
            competencesToAdd.removeAll(oldCompetences);
            for (Competence c : competencesToAdd) {
                secouristeDAO.addCompetenceToSecouriste(secouristeId, c.getIntitule());
            }

            // Calcule les compétences à supprimer
            Set<Competence> competencesToRemove = new HashSet<>(oldCompetences);
            competencesToRemove.removeAll(newCompetences);
            for (Competence c : competencesToRemove) {
                secouristeDAO.removeCompetenceFromSecouriste(secouristeId, c.getIntitule());
            }

            return true;

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour complète du secouriste: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ajoute une compétence à un secouriste.
     *
     * @param secouristeId L'ID du secouriste.
     * @param intituleCompetence L'intitulé de la compétence.
     * @return `true` si l'ajout a réussi, `false` sinon.
     */
    public boolean addCompetenceToSecouriste(long secouristeId, String intituleCompetence) {
        return secouristeDAO.addCompetenceToSecouriste(secouristeId, intituleCompetence) > 0;
    }

    /**
     * Supprime une compétence d'un secouriste.
     *
     * @param secouristeId L'ID du secouriste.
     * @param intituleCompetence L'intitulé de la compétence.
     * @return `true` si la suppression a réussi, `false` sinon.
     */
    public boolean removeCompetenceFromSecouriste(long secouristeId, String intituleCompetence) {
        return secouristeDAO.removeCompetenceFromSecouriste(secouristeId, intituleCompetence) > 0;
    }

    /**
     * Ajoute une disponibilité pour un secouriste.
     *
     * @param secouristeId L'ID du secouriste.
     * @param date La date de disponibilité.
     * @return `true` si l'ajout a réussi, `false` sinon.
     */
    public boolean addAvailability(long secouristeId, LocalDate date) {
        return secouristeDAO.addAvailability(secouristeId, date) > 0;
    }

    /**
     * Supprime une disponibilité pour un secouriste.
     *
     * @param secouristeId L'ID du secouriste.
     * @param date La date de disponibilité à supprimer.
     * @return `true` si la suppression a réussi, `false` sinon.
     */
    public boolean removeAvailability(long secouristeId, LocalDate date) {
        return secouristeDAO.removeAvailability(secouristeId, date) > 0;
    }
}