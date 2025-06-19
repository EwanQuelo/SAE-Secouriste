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


public class ServiceAffectationGloutonne {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    public List<AffectationResultat> trouverAffectationPourDPS(DPS dpsCible) {
        // Préparation des données SANS TRI
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);

        if (postesAPourvoir.isEmpty() || secouristesLibres.isEmpty()) {
            return new ArrayList<>();
        }

        List<AffectationResultat> affectationsTrouvees = new ArrayList<>();
        Set<Long> idsSecouristesDejaAffectes = new HashSet<>();

        // On parcourt les POSTES en premier.
        for (Poste poste : postesAPourvoir) {
            // Pour chaque poste, on cherche le premier secouriste libre et apte.
            for (Secouriste secouriste : secouristesLibres) {
                if (!idsSecouristesDejaAffectes.contains(secouriste.getId())) {
                    if (estApte(secouriste, poste)) {
                        affectationsTrouvees.add(new AffectationResultat(secouriste, poste));
                        idsSecouristesDejaAffectes.add(secouriste.getId());
                        break; 
                    }
                }
            }
        }
        
        return affectationsTrouvees;
    }

    // --- Méthodes utilitaires simples, avec des boucles for ---
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