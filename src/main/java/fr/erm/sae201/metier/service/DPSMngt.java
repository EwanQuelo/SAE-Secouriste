package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.JourneeDAO;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.DPS;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service pour gérer la logique métier des Dispositifs Prévisionnels de Secours (DPS).
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class DPSMngt {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();

    /**
     * Récupère un DPS par son ID et l'hydrate avec ses compétences requises.
     * "Hydrater" signifie charger et attacher les données liées à l'objet principal.
     *
     * @param id L'ID du DPS.
     * @return L'objet DPS complet s'il est trouvé.
     * @throws EntityNotFoundException si aucun DPS avec cet ID n'est trouvé.
     */
    public DPS getDps(long id) {
        DPS dps = dpsDAO.findByID(id);
        if (dps == null) {
            throw new EntityNotFoundException("DPS non trouvé avec l'ID : " + id);
        }
        Map<Competence, Integer> requirements = dpsDAO.findRequiredCompetencesForDps(id);
        dps.setCompetencesRequises(requirements);
        return dps;
    }

    /**
     * Récupère tous les DPS entre deux dates et les hydrate avec leurs besoins en compétences.
     *
     * @param startDate La date de début de la recherche.
     * @param endDate La date de fin de la recherche.
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

    /**
     * Récupère tous les DPS de la base de données sans hydratation profonde.
     *
     * @return Une liste de tous les objets DPS.
     */
    public List<DPS> getAllDps() {
        return dpsDAO.findAll();
    }

    /**
     * Calcule le nombre total de postes à pourvoir pour un DPS.
     *
     * @param dpsId L'ID du DPS.
     * @return Le nombre total de secouristes requis.
     */
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
    
    /**
     * Crée un nouveau DPS. S'assure que la journée associée existe avant la création.
     *
     * @param newDps Le DPS à créer.
     * @return L'ID du DPS nouvellement créé, ou -1 en cas d'échec.
     * @throws SQLException Si une erreur SQL survient.
     */
    public long createDps(DPS newDps) throws SQLException {
        if (journeeDAO.findByDate(newDps.getJournee().getDate()) == null) {
            journeeDAO.create(newDps.getJournee());
        }

        int result = dpsDAO.create(newDps);
        
        if (result > 0) {
            return newDps.getId();
        }
        return -1;
    }

    /**
     * Met à jour un DPS existant.
     *
     * @param dpsToUpdate Le DPS à mettre à jour.
     * @return `true` si la mise à jour a réussi, `false` sinon.
     */
    public boolean updateDps(DPS dpsToUpdate) {
        return dpsDAO.update(dpsToUpdate) > 0;
    }

    /**
     * Définit ou met à jour le nombre de secouristes requis pour une compétence donnée sur un DPS.
     *
     * @param dpsId L'ID du DPS.
     * @param intituleCompetence L'intitulé de la compétence.
     * @param nombre Le nombre de secouristes requis.
     * @return `true` si l'opération a réussi, `false` sinon.
     */
    public boolean setRequiredCompetence(long dpsId, String intituleCompetence, int nombre) {
        return dpsDAO.setRequiredCompetence(dpsId, intituleCompetence, nombre) >= 1;
    }

    /**
     * Supprime une exigence de compétence pour un DPS.
     *
     * @param dpsId L'ID du DPS.
     * @param intituleCompetence L'intitulé de la compétence à supprimer.
     * @return `true` si la suppression a réussi, `false` sinon.
     */
    public boolean removeRequiredCompetence(long dpsId, String intituleCompetence) {
        return dpsDAO.removeRequiredCompetence(dpsId, intituleCompetence) > 0;
    }

    /**
     * Supprime toutes les affectations liées à un DPS.
     *
     * @param dpsId L'ID du DPS.
     * @return `true` si la suppression a réussi (même si 0 ligne est supprimée), `false` sinon.
     */
    public boolean deleteAllAffectationsForDps(long dpsId) {
        return affectationDAO.deleteAllAffectationsForDps(dpsId) >= 0;
    }
}