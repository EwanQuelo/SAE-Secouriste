package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.metier.persistence.Affectation;
import java.util.List;

/**
 * Service pour gérer la logique métier des affectations.
 * <p>
 * Cette classe sert d'intermédiaire entre les contrôleurs et le DAO des affectations,
 * en encapsulant la logique de récupération des données liées aux affectations.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AffectationMngt {

    /** Le DAO pour l'accès aux données des affectations. */
    private final AffectationDAO affectationDAO = new AffectationDAO();
    
    /**
     * Récupère le nombre total de secouristes affectés à un DPS donné.
     *
     * @param dpsId L'identifiant du DPS.
     * @return Le nombre d'affectations pour ce DPS.
     */
    public int getAssignmentCountForDps(long dpsId) {
        return affectationDAO.countAffectationsForDps(dpsId);
    }
    
    /**
     * Récupère toutes les affectations pour un secouriste spécifique.
     *
     * @param secouristeId L'identifiant du secouriste.
     * @return Une liste d'objets Affectation, qui peut être vide si le
     *         secouriste n'a aucune affectation.
     */
    public List<Affectation> getAssignmentsForSecouriste(long secouristeId) {
        return affectationDAO.findAffectationsBySecouristeId(secouristeId);
    }
}