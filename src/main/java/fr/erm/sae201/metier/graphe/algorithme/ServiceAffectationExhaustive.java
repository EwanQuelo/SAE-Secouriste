package fr.erm.sae201.metier.graphe.algorithme;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.graphe.algorithme.ModelesAlgorithme.AffectationResultat;
import fr.erm.sae201.metier.graphe.algorithme.ModelesAlgorithme.Poste;
import fr.erm.sae201.metier.persistence.*;
import fr.erm.sae201.metier.service.ServiceCompetences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implémente un algorithme d'affectation par recherche exhaustive.
 * Il garantit de trouver la solution optimale (le plus grand nombre d'affectations)
 * en explorant toutes les combinaisons possibles via le backtracking.
 * CETTE VERSION EST CORRIGÉE POUR ÊTRE FONCTIONNELLE.
 */
public class ServiceAffectationExhaustive {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    private List<AffectationResultat> meilleureSolutionTrouvee;

    public List<AffectationResultat> trouverMeilleureAffectationPourDPS(DPS dpsCible) {
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);

        meilleureSolutionTrouvee = new ArrayList<>();
        
        // Lancement de la recherche
        chercherSolution(0, new boolean[secouristesLibres.size()], new ArrayList<>(), postesAPourvoir, secouristesLibres);
        
        return meilleureSolutionTrouvee;
    }

    /**
     * Méthode récursive de backtracking.
     * Pour chaque poste, elle essaie de l'assigner à chaque secouriste apte et disponible.
     * Elle explore aussi la branche où le poste n'est pas pourvu.
     */
    private void chercherSolution(int indexPoste, boolean[] secouristeEstPris, List<AffectationResultat> affectationActuelle, List<Poste> tousLesPostes, List<Secouriste> tousLesSecouristes) {

        // Si on a considéré tous les postes, on a une solution complète potentielle
        if (indexPoste == tousLesPostes.size()) {
            if (affectationActuelle.size() > meilleureSolutionTrouvee.size()) {
                meilleureSolutionTrouvee = new ArrayList<>(affectationActuelle);
            }
            return;
        }

        Poste posteCourant = tousLesPostes.get(indexPoste);

        // BRANCHE 1 : On essaie d'affecter le poste courant
        for (int i = 0; i < tousLesSecouristes.size(); i++) {
            // Si le secouriste n'est pas déjà pris ET qu'il est apte
            if (!secouristeEstPris[i] && estApte(tousLesSecouristes.get(i), posteCourant)) {
                
                // On l'affecte provisoirement
                secouristeEstPris[i] = true;
                affectationActuelle.add(new AffectationResultat(tousLesSecouristes.get(i), posteCourant));

                // On passe au poste suivant
                chercherSolution(indexPoste + 1, secouristeEstPris, affectationActuelle, tousLesPostes, tousLesSecouristes);

                // BACKTRACKING : on annule l'affectation pour tester d'autres possibilités
                affectationActuelle.remove(affectationActuelle.size() - 1);
                secouristeEstPris[i] = false;
            }
        }

        // BRANCHE 2 : On explore la possibilité de NE PAS pourvoir ce poste
        chercherSolution(indexPoste + 1, secouristeEstPris, affectationActuelle, tousLesPostes, tousLesSecouristes);
    }

    // --- Méthodes utilitaires ---
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
        Set<Long> idsSecouristesOccupes = new HashSet<>();
        for (Affectation affectation : affectationsDuJour) {
            if (affectation.getDps().getId() != dpsCible.getId()) {
                idsSecouristesOccupes.add(affectation.getSecouriste().getId());
            }
        }
        List<Secouriste> secouristesLibres = new ArrayList<>();
        for (Secouriste secouriste : tousLesSecouristes) {
            if (estDisponibleCeJour(secouriste, dpsCible.getJournee().getDate()) && !idsSecouristesOccupes.contains(secouriste.getId())) {
                secouristesLibres.add(secouriste);
            }
        }
        return secouristesLibres;
    }

    public boolean estApte(Secouriste secouriste, Poste poste) {
        return serviceCompetences.possedeCompetenceRequiseOuSuperieure(secouriste.getCompetences(), poste.competenceRequise());
    }

    private boolean estDisponibleCeJour(Secouriste secouriste, java.time.LocalDate date) {
        if (secouriste.getDisponibilites() == null) return false;
        for (Journee jourDispo : secouriste.getDisponibilites()) {
            if (jourDispo.getDate().equals(date)) return true;
        }
        return false;
    }
}