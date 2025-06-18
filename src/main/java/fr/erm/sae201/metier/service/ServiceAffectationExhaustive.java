package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erm.sae201.metier.service.ModelesAlgorithme.Poste;
import fr.erm.sae201.metier.service.ModelesAlgorithme.AffectationResultat;

public class ServiceAffectationExhaustive {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    private List<AffectationResultat> meilleureSolutionGlobale;

    public List<AffectationResultat> trouverMeilleureAffectationPourDPS(DPS dpsCible) {
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);
        
        this.meilleureSolutionGlobale = new ArrayList<>();
        
        resoudre(postesAPourvoir, secouristesLibres, new ArrayList<>());
        
        return this.meilleureSolutionGlobale;
    }

    // --- CORRECTION DE LA LOGIQUE DE LA MÉTHODE RESOUDRE ---
    private void resoudre(List<Poste> postesRestants, List<Secouriste> secouristesDisponibles, List<AffectationResultat> affectationsActuelles) {
        
        // La solution actuelle est toujours une candidate pour être la meilleure.
        // On la compare à chaque étape de la récursion.
        if (affectationsActuelles.size() > meilleureSolutionGlobale.size()) {
            meilleureSolutionGlobale = new ArrayList<>(affectationsActuelles);
        }

        // Condition d'arrêt : s'il n'y a plus de postes ou plus de secouristes, on arrête d'explorer cette branche.
        if (postesRestants.isEmpty() || secouristesDisponibles.isEmpty()) {
            return;
        }

        Poste posteCourant = postesRestants.get(0);
        List<Poste> prochainsPostes = postesRestants.subList(1, postesRestants.size());
        
        // Essayer d'affecter un secouriste à ce poste
        for (Secouriste secouriste : secouristesDisponibles) {
            if (estApte(secouriste, posteCourant)) {
                
                affectationsActuelles.add(new AffectationResultat(secouriste, posteCourant));
                
                List<Secouriste> prochainsSecouristes = new ArrayList<>(secouristesDisponibles);
                prochainsSecouristes.remove(secouriste);
                
                resoudre(prochainsPostes, prochainsSecouristes, affectationsActuelles);
                
                // Backtracking
                affectationsActuelles.remove(affectationsActuelles.size() - 1);
            }
        }
        
        // Essayer de ne PAS pourvoir ce poste
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

    private List<Secouriste> trouverSecouristesLibresPour(DPS dpsCible) {
        List<Secouriste> tousLesSecouristes = secouristeDAO.findAll();
        List<Affectation> affectationsDuJour = affectationDAO.findAllByDate(dpsCible.getJournee().getDate());

        // CORRECTION : On ne garde que les IDs des secouristes qui sont occupés
        // sur un AUTRE DPS que celui que nous sommes en train de traiter.
        Set<Long> idsSecouristesOccupes = affectationsDuJour.stream()
                .filter(affectation -> affectation.getDps().getId() != dpsCible.getId()) // <<<--- LA LIGNE CLÉ
                .map(affectation -> affectation.getSecouriste().getId())
                .collect(Collectors.toSet());

        // Le reste est inchangé et devrait maintenant fonctionner.
        return tousLesSecouristes.stream()
                .filter(secouriste -> estDisponibleCeJour(secouriste, dpsCible.getJournee().getDate()))
                .filter(secouriste -> !idsSecouristesOccupes.contains(secouriste.getId()))
                .collect(Collectors.toList());
    }

    private boolean estApte(Secouriste secouriste, Poste poste) {
        // La seule chose à vérifier ici est la compétence.
        return serviceCompetences.possedeCompetenceRequiseOuSuperieure(
            secouriste.getCompetences(), 
            poste.competenceRequise()
        );
    }
    

    private boolean estDisponibleCeJour(Secouriste secouriste, java.time.LocalDate date) {
        for (Journee jourDispo : secouriste.getDisponibilites()) {
            if (jourDispo.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }
}