// src/main/java/fr/erm/sae201/metier/service/DPSMngt.java

package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.JourneeDAO;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.Competence; // IMPORT
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Journee;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map; // IMPORT
import java.util.stream.Collectors;

public class DPSMngt {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();

    /**
     * MODIFIÉ : Récupère un DPS par son ID et l'hydrate avec ses compétences requises.
     * @param id L'ID du DPS.
     * @return L'objet DPS complet s'il est trouvé.
     * @throws EntityNotFoundException si aucun DPS avec cet ID n'est trouvé.
     */
    public DPS getDps(long id) {
        DPS dps = dpsDAO.findByID(id);
        if (dps == null) {
            throw new EntityNotFoundException("DPS non trouvé avec l'ID : " + id);
        }
        // HYDRATATION : On charge les compétences requises et on les attache à l'objet.
        Map<Competence, Integer> requirements = dpsDAO.findRequiredCompetencesForDps(id);
        dps.setCompetencesRequises(requirements);
        return dps;
    }

    /**
     * Récupère tous les DPS entre deux dates et les hydrate avec leurs besoins.
     * @param startDate Date de début.
     * @param endDate Date de fin.
     * @return Une liste de DPS complets.
     */
    public List<DPS> getAllDpsBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<DPS> dpsList = dpsDAO.findAllBetweenDates(startDate, endDate);

        dpsList.forEach(dps -> {
            if (dps != null) {
                Map<Competence, Integer> requirements = dpsDAO.findRequiredCompetencesForDps(dps.getId());
                dps.setCompetencesRequises(requirements);
            }
        });
        return dpsList;
    }
    


    public List<DPS> getAllDps() {
        return dpsDAO.findAll();
    }

    public int getTotalPersonnelRequired(long dpsId) {
        try {
            DPS dps = getDps(dpsId); // Utilise la méthode hydratée
            return dps.getCompetencesRequises().values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();
        } catch (EntityNotFoundException e) {
            return 0;
        }
    }
    
    public long createDps(DPS newDps) throws SQLException {
        // La création d'une journée ne devrait se faire que si elle n'existe pas.
        // Idéalement, la BDD gère cela avec un "INSERT IGNORE" ou le DAO vérifie avant.
        // Pour l'instant, on suppose que le service a le droit de la créer.
        if (journeeDAO.findByDate(newDps.getJournee().getDate()) == null) {
            journeeDAO.create(newDps.getJournee());
        }

        int result = dpsDAO.create(newDps);
        
        if (result > 0) {
            return newDps.getId();
        }
        return -1;
    }

    public boolean updateDps(DPS dpsToUpdate) {
        return dpsDAO.update(dpsToUpdate) > 0;
    }

    public boolean setRequiredCompetence(long dpsId, String intituleCompetence, int nombre) {
        return dpsDAO.setRequiredCompetence(dpsId, intituleCompetence, nombre) >= 1;
    }

    public boolean removeRequiredCompetence(long dpsId, String intituleCompetence) {
        return dpsDAO.removeRequiredCompetence(dpsId, intituleCompetence) > 0;
    }

}