package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.JourneeDAO;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Journee;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DPSMngt {

    private final DPSDAO dpsDAO = new DPSDAO();
    // NOUVEAU : Le service a besoin du JourneeDAO pour la création composite.
    private final JourneeDAO journeeDAO = new JourneeDAO();

    public Optional<DPS> getDps(long id) {
        return Optional.ofNullable(dpsDAO.findByID(id));
    }

    public List<DPS> getAllDps() {
        return dpsDAO.findAll();
    }
    
    /**
     * NOUVEAU : Crée un DPS en s'assurant que la Journee associée existe.
     * Encapsule la logique de création composite.
     *
     * @param newDps L'objet DPS à créer.
     * @return L'ID du DPS créé, ou -1 en cas d'échec.
     * @throws SQLException si une erreur de base de données se produit.
     */
    public long createDps(DPS newDps) throws SQLException {
        // 1. S'assurer que la journée existe.
        // On tente de la créer. Si elle existe déjà, la contrainte de PK lèvera une exception
        // que le DAO gère en retournant -1. Si une autre erreur survient, une SQLException sera levée.
        // Une approche plus robuste utiliserait `INSERT IGNORE` ou vérifierait l'existence d'abord.
        journeeDAO.create(newDps.getJournee()); // On ignore le résultat, le but est juste de s'assurer de l'existence.

        // 2. Créer le DPS.
        int result = dpsDAO.create(newDps);
        
        if (result > 0) {
            return newDps.getId(); // L'ID a été mis à jour par le DAO.
        }
        return -1;
    }

    /**
     * NOUVEAU : Met à jour un DPS. Pour l'instant, c'est un simple appel au DAO,
     * mais cela permet une extension future (ex: valider des changements).
     *
     * @param dpsToUpdate L'objet DPS avec les nouvelles données.
     * @return true si la mise à jour a réussi.
     */
    public boolean updateDps(DPS dpsToUpdate) {
        return dpsDAO.update(dpsToUpdate) > 0;
    }

    // Le reste des méthodes ne change pas
    public boolean setRequiredCompetence(long dpsId, String intituleCompetence, int nombre) {
        return dpsDAO.setRequiredCompetence(dpsId, intituleCompetence, nombre) >= 1;
    }

    public boolean removeRequiredCompetence(long dpsId, String intituleCompetence) {
        return dpsDAO.removeRequiredCompetence(dpsId, intituleCompetence) > 0;
    }

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