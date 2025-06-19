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
    // Utilisation des services pour la logique métier
    private final SecouristeMngt secouristeMngt = new SecouristeMngt();
    private final DPSMngt dpsMngt = new DPSMngt();
    
    /**
     * Tente d'affecter un secouriste à un DPS pour une compétence donnée.
     *
     * @param secouristeId L'ID du secouriste.
     * @param dpsId L'ID du DPS.
     * @param competenceIntitule L'intitulé de la compétence à remplir.
     * @return true si l'affectation a réussi, false sinon.
     * @throws EntityNotFoundException si le secouriste ou le DPS n'existe pas.
     * @throws Exception pour d'autres erreurs.
     */
    public boolean assignSecouriste(long secouristeId, long dpsId, String competenceIntitule) throws Exception {
        // 1. Récupérer les objets de la base via les services.
        // Ces appels lèveront une EntityNotFoundException si l'entité n'existe pas.
        Secouriste secouriste = secouristeMngt.getSecouriste(secouristeId);
        DPS dps = dpsMngt.getDps(dpsId);

        // 2. Vérifier si l'affectation est possible
        if (!canAssign(secouriste, dps, competenceIntitule)) {
            System.err.println("Assignation impossible pour " + secouriste.getPrenom() + " au DPS " + dps.getId());
            return false;
        }
        
        // 3. Créer l'objet Affectation
        Competence competenceToAssign = dps.getCompetencesRequises().keySet().stream()
            .filter(c -> c.getIntitule().equals(competenceIntitule))
            .findFirst()
            .orElseThrow(() -> new Exception("La compétence " + competenceIntitule + " n'est pas requise pour ce DPS."));

        Affectation nouvelleAffectation = new Affectation(dps, secouriste, competenceToAssign);

        // 4. Sauvegarder en base de données
        return affectationDAO.create(nouvelleAffectation) > 0;
    }

    /**
     * NOUVEAU: Récupère le nombre d'affectations pour un DPS.
     * @param dpsId L'ID du DPS.
     * @return Le nombre de secouristes affectés.
     */
    public int getAssignmentCountForDps(long dpsId) {
        return affectationDAO.countAffectationsForDps(dpsId);
    }


    /**
     * Vérifie si un secouriste peut être affecté à un DPS pour une compétence.
     *
     * @param secouriste L'objet Secouriste.
     * @param dps L'objet DPS.
     * @param competenceIntitule La compétence à vérifier.
     * @return true si toutes les conditions sont remplies.
     */
    public boolean canAssign(Secouriste secouriste, DPS dps, String competenceIntitule) {
        // Règle 1: Le secouriste est-il disponible le jour du DPS ?
        boolean isAvailable = secouriste.getDisponibilites().stream()
            .anyMatch(journee -> journee.getDate().equals(dps.getJournee().getDate()));
        if (!isAvailable) {
            System.err.println("Règle échouée: " + secouriste.getPrenom() + " n'est pas disponible le " + dps.getJournee().getDate());
            return false;
        }

        // Règle 2: Le secouriste possède-t-il la compétence requise ?
        boolean hasCompetence = secouriste.getCompetences().stream()
            .anyMatch(c -> c.getIntitule().equals(competenceIntitule));
        if (!hasCompetence) {
             System.err.println("Règle échouée: " + secouriste.getPrenom() + " ne possède pas la compétence " + competenceIntitule);
            return false;
        }

        // Règle 3: Y a-t-il encore un besoin pour cette compétence sur ce DPS ?
        Map<Competence, Integer> besoins = dps.getCompetencesRequises();
        int nombreRequis = besoins.entrySet().stream()
                                  .filter(entry -> entry.getKey().getIntitule().equals(competenceIntitule))
                                  .mapToInt(Map.Entry::getValue)
                                  .findFirst().orElse(0);

        if (nombreRequis == 0) {
            System.err.println("Règle échouée: La compétence " + competenceIntitule + " n'est pas requise pour le DPS " + dps.getId());
            return false;
        }

        long nombreDejaAffecte = getAssignmentsForDps(dps.getId()).stream()
            .filter(aff -> aff.getCompetence().getIntitule().equals(competenceIntitule))
            .count();

        if (nombreDejaAffecte >= nombreRequis) {
            System.err.println("Règle échouée: Le besoin pour la compétence " + competenceIntitule + " est déjà comblé (" + nombreDejaAffecte + "/" + nombreRequis + ").");
            return false;
        }

        return true;
    }
    
    public boolean unassignSecouriste(Affectation affectation) {
        return affectationDAO.delete(affectation) > 0;
    }

    public List<Affectation> getAssignmentsForDps(long dpsId) {
        return affectationDAO.findAffectationsByDpsId(dpsId);
    }
    
    public List<Affectation> getAssignmentsForSecouriste(long secouristeId) {
        return affectationDAO.findAffectationsBySecouristeId(secouristeId);
    }
}