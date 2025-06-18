package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecouristeMngt {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    /**
     * MODIFIÉ : Récupère un secouriste par son ID.
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
        try {
            Secouriste secouriste = getSecouriste(secouristeId);
            return secouristeDAO.delete(secouriste) > 0;
        } catch (EntityNotFoundException e) {
            // Le secouriste n'existe pas, donc la suppression est "réussie" dans un sens.
            return false;
        }
    }

    public boolean update(Secouriste secouriste) {
        return secouristeDAO.update(secouriste) > 0;
    }

    public boolean updateSecouristeInfoAndCompetences(Secouriste secouristeToUpdate, Set<Competence> newCompetences) {
        try {
            secouristeDAO.update(secouristeToUpdate);

            long secouristeId = secouristeToUpdate.getId();
            Set<Competence> oldCompetences = secouristeDAO.findCompetencesForSecouriste(secouristeId);

            Set<Competence> competencesToAdd = new HashSet<>(newCompetences);
            competencesToAdd.removeAll(oldCompetences);
            for (Competence c : competencesToAdd) {
                secouristeDAO.addCompetenceToSecouriste(secouristeId, c.getIntitule());
            }

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