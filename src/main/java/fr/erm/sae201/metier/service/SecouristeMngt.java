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