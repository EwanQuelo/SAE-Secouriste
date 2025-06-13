package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service pour gérer la logique métier des affectations.
 * C'est le chef d'orchestre qui vérifie les règles avant d'affecter un secouriste.
 */
public class AffectationMngt {

    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final DPSDAO dpsDAO = new DPSDAO();
    
    /**
     * Tente d'affecter un secouriste à un DPS pour une compétence donnée.
     * Effectue toutes les vérifications nécessaires avant de procéder.
     *
     * @param secouristeId L'ID du secouriste.
     * @param dpsId L'ID du DPS.
     * @param competenceIntitule L'intitulé de la compétence à remplir.
     * @return true si l'affectation a réussi, false sinon.
     * @throws Exception si le secouriste ou le DPS n'existe pas.
     */
    public boolean assignSecouriste(long secouristeId, long dpsId, String competenceIntitule) throws Exception {
        // 1. Récupérer les objets de la base
        Optional<Secouriste> secouristeOpt = Optional.ofNullable(secouristeDAO.findByID(secouristeId));
        Optional<DPS> dpsOpt = Optional.ofNullable(dpsDAO.findByID(dpsId));

        if (secouristeOpt.isEmpty()) throw new Exception("Secouriste non trouvé avec l'ID: " + secouristeId);
        if (dpsOpt.isEmpty()) throw new Exception("DPS non trouvé avec l'ID: " + dpsId);

        Secouriste secouriste = secouristeOpt.get();
        DPS dps = dpsOpt.get();

        // 2. Vérifier si l'affectation est possible
        if (!canAssign(secouriste, dps, competenceIntitule)) {
            System.err.println("Assignation impossible pour " + secouriste.getPrenom() + " au DPS " + dps.getId());
            return false;
        }
        
        // 3. Créer l'objet Affectation
        // On récupère l'objet Competence via la map du DPS pour être sûr qu'elle est requise.
        Competence competenceToAssign = dps.getCompetencesRequises().keySet().stream()
            .filter(c -> c.getIntitule().equals(competenceIntitule))
            .findFirst()
            .orElseThrow(() -> new Exception("La compétence " + competenceIntitule + " n'est pas requise pour ce DPS."));

        Affectation nouvelleAffectation = new Affectation(dps, secouriste, competenceToAssign);

        // 4. Sauvegarder en base de données
        return affectationDAO.create(nouvelleAffectation) > 0;
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

        // Règle 2: Le secouriste possède-t-il la compétence requise (et ses prérequis via le service) ?
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

        // Si toutes les règles passent
        return true;
    }
    
    /**
     * Désaffecte un secouriste d'un DPS pour une compétence.
     *
     * @param affectation L'objet Affectation à supprimer.
     * @return true si la suppression a réussi.
     */
    public boolean unassignSecouriste(Affectation affectation) {
        return affectationDAO.delete(affectation) > 0;
    }

    /**
     * Récupère toutes les affectations pour un DPS donné.
     */
    public List<Affectation> getAssignmentsForDps(long dpsId) {
        return affectationDAO.findAffectationsByDpsId(dpsId);
    }
    
    /**
     * Récupère toutes les affectations pour un Secouriste donné.
     */
    public List<Affectation> getAssignmentsForSecouriste(long secouristeId) {
        return affectationDAO.findAffectationsBySecouristeId(secouristeId);
    }
}