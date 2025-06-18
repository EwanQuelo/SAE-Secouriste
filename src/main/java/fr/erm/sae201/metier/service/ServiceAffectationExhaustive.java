package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ServiceAffectationExhaustive {

    public record Poste(long idDps, Competence competenceRequise) {}
    public record AffectationResultat(Secouriste secouriste, Poste poste) {}

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private List<AffectationResultat> meilleureSolutionGlobale;

    public List<AffectationResultat> trouverMeilleureAffectationPourDPS(DPS dpsCible) {
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        List<Secouriste> tousLesSecouristes = secouristeDAO.findAll();
        
        this.meilleureSolutionGlobale = new ArrayList<>();
        
        System.out.println("Lancement de l'algorithme exhaustif pour le DPS n°" + dpsCible.getId() + "...");
        resoudre(postesAPourvoir, tousLesSecouristes, new ArrayList<>());
        System.out.println("Recherche terminée. Meilleure solution : " + meilleureSolutionGlobale.size() + " affectations.");
        
        return this.meilleureSolutionGlobale;
    }

    private void resoudre(List<Poste> postesRestants, List<Secouriste> secouristesDisponibles, List<AffectationResultat> affectationsActuelles) {
        if (postesRestants.isEmpty()) {
            if (affectationsActuelles.size() > meilleureSolutionGlobale.size()) {
                meilleureSolutionGlobale = new ArrayList<>(affectationsActuelles);
            }
            return;
        }

        Poste posteCourant = postesRestants.get(0);
        List<Poste> prochainsPostes = new ArrayList<>(postesRestants.subList(1, postesRestants.size()));
        
        for (Secouriste secouriste : secouristesDisponibles) {
            if (estApte(secouriste, posteCourant, affectationsActuelles)) {
                affectationsActuelles.add(new AffectationResultat(secouriste, posteCourant));
                List<Secouriste> prochainsSecouristes = new ArrayList<>(secouristesDisponibles);
                prochainsSecouristes.remove(secouriste);
                resoudre(prochainsPostes, prochainsSecouristes, affectationsActuelles);
                affectationsActuelles.remove(affectationsActuelles.size() - 1);
            }
        }
        resoudre(prochainsPostes, secouristesDisponibles, affectationsActuelles);
    }
    
    private List<Poste> preparerPostesPourUnSeulDps(DPS dps) {
        List<Poste> postes = new ArrayList<>();
        Map<Competence, Integer> besoins = dpsDAO.findRequiredCompetencesForDps(dps.getId());
        for (Map.Entry<Competence, Integer> besoin : besoins.entrySet()) {
            for (int i = 0; i < besoin.getValue(); i++) {
                postes.add(new Poste(dps.getId(), besoin.getKey()));
            }
        }
        return postes;
    }

    private boolean estApte(Secouriste secouriste, Poste poste, List<AffectationResultat> affectationsActuelles) {
        DPS dpsDuPoste = dpsDAO.findByID(poste.idDps());
        if (dpsDuPoste == null) return false;

        for (AffectationResultat aff : affectationsActuelles) {
            if (aff.secouriste().equals(secouriste) && aff.poste().idDps() == poste.idDps()) {
                return false;
            }
        }
        
        boolean estDisponible = false;
        for (Journee jourDispo : secouriste.getDisponibilites()) {
            if (jourDispo.getDate().equals(dpsDuPoste.getJournee().getDate())) {
                estDisponible = true;
                break;
            }
        }
        if (!estDisponible) return false;

        // TODO: Remplacer par la logique du graphe de compétences
        for (Competence c : secouriste.getCompetences()) {
            if (c.equals(poste.competenceRequise())) {
                return true;
            }
        }
        return false;
    }
}