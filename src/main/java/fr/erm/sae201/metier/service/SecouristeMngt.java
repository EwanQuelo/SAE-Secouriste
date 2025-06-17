package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SecouristeMngt {

    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    public Optional<Secouriste> getSecouriste(long id) {
        return Optional.ofNullable(secouristeDAO.findByID(id));
    }

    public List<Secouriste> getAllSecouristes() {
        return secouristeDAO.findAll();
    }

    public int getTotalSecouristesCount() {
        return secouristeDAO.countAll();
    }

    public List<Secouriste> getSecouristesByPage(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return secouristeDAO.findPaginated(offset, pageSize);
    }

    public int getTotalSecouristesCount(String query) {
        return secouristeDAO.countFiltered(query);
    }

    public List<Secouriste> getSecouristesByPage(String query, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return secouristeDAO.findFilteredAndPaginated(query, offset, pageSize);
    }

    /**
     * Supprime un secouriste de la base de données.
     * Le compte utilisateur associé est également supprimé grâce à ON DELETE CASCADE.
     *
     * @param secouristeId L'ID du secouriste à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean deleteSecouriste(long secouristeId) {
        Optional<Secouriste> secouristeOpt = getSecouriste(secouristeId);
        if (secouristeOpt.isPresent()) {
            return secouristeDAO.delete(secouristeOpt.get()) > 0;
        }
        return false;
    }

    /**
     * Met à jour les informations d'un secouriste dans la base de données.
     * @param secouriste Le secouriste avec les informations mises à jour.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean update(Secouriste secouriste) {
        return secouristeDAO.update(secouriste) > 0;
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

    // Exemple de logique métier : vérifier si un secouriste est disponible ET a une compétence
    public boolean isAvailableAndQualified(long secouristeId, LocalDate date, String intituleCompetence) {
        Optional<Secouriste> secouristeOpt = getSecouriste(secouristeId);
        if (secouristeOpt.isEmpty()) {
            return false;
        }
        
        Secouriste secouriste = secouristeOpt.get();
        boolean hasCompetence = secouriste.getCompetences().stream()
            .anyMatch(c -> c.getIntitule().equals(intituleCompetence));
            
        boolean isAvailable = secouriste.getDisponibilites().stream()
            .anyMatch(j -> j.getDate().equals(date));

        return hasCompetence && isAvailable;
    }
}