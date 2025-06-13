package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.DPS;

import java.util.List;
import java.util.Optional;

public class DPSMngt {

    private final DPSDAO dpsDAO = new DPSDAO();

    public Optional<DPS> getDps(long id) {
        return Optional.ofNullable(dpsDAO.findByID(id));
    }

    public List<DPS> getAllDps() {
        return dpsDAO.findAll();
    }

    public boolean setRequiredCompetence(long dpsId, String intituleCompetence, int nombre) {
        return dpsDAO.setRequiredCompetence(dpsId, intituleCompetence, nombre) >= 1; // >= 1 car ON DUPLICATE KEY peut retourner 2
    }

    public boolean removeRequiredCompetence(long dpsId, String intituleCompetence) {
        return dpsDAO.removeRequiredCompetence(dpsId, intituleCompetence) > 0;
    }

    // Exemple de logique métier : calculer le nombre total de secouristes requis pour un DPS
    public int getTotalPersonnelRequired(long dpsId) {
        Optional<DPS> dpsOpt = getDps(dpsId);
        if (dpsOpt.isEmpty()) {
            return 0;
        }
        return dpsOpt.get().getCompetencesRequises().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}