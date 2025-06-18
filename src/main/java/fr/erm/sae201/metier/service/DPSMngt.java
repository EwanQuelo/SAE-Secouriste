package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.JourneeDAO;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Journee;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DPSMngt {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();

    /**
     * MODIFIÉ : Récupère un DPS par son ID.
     * @param id L'ID du DPS.
     * @return L'objet DPS s'il est trouvé.
     * @throws EntityNotFoundException si aucun DPS avec cet ID n'est trouvé.
     */
    public DPS getDps(long id) {
        DPS dps = dpsDAO.findByID(id);
        if (dps == null) {
            throw new EntityNotFoundException("DPS non trouvé avec l'ID : " + id);
        }
        return dps;
    }

    public List<DPS> getAllDps() {
        return dpsDAO.findAll();
    }
    
    public long createDps(DPS newDps) throws SQLException {
        journeeDAO.create(newDps.getJournee());

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

    public int getTotalPersonnelRequired(long dpsId) {
        try {
            DPS dps = getDps(dpsId);
            return dps.getCompetencesRequises().values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();
        } catch (EntityNotFoundException e) {
            return 0;
        }
    }
}