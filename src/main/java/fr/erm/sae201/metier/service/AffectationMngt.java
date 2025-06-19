// src/main/java/fr/erm/sae201/metier/service/AffectationMngt.java
package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.util.List;
import java.util.Map;

/**
 * Service pour gérer la logique métier des affectations.
 * MODIFIÉ : Utilise les services qui lèvent des exceptions.
 */
public class AffectationMngt {

    private final AffectationDAO affectationDAO = new AffectationDAO();
    
    /**
     * NOUVEAU: Récupère le nombre d'affectations pour un DPS.
     * @param dpsId L'ID du DPS.
     * @return Le nombre de secouristes affectés.
     */
    public int getAssignmentCountForDps(long dpsId) {
        return affectationDAO.countAffectationsForDps(dpsId);
    }
    
    public List<Affectation> getAssignmentsForSecouriste(long secouristeId) {
        return affectationDAO.findAffectationsBySecouristeId(secouristeId);
    }
}