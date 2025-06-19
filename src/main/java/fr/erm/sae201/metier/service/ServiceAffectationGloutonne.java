package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.*;
import fr.erm.sae201.metier.service.ModelesAlgorithme.Poste;
import fr.erm.sae201.metier.service.ModelesAlgorithme.AffectationResultat;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implémente un algorithme d'affectation de type "glouton" avec priorités,
 * CONFORMÉMENT AUX DIAPOSITIVES DE PRÉSENTATION.
 * L'objectif est de suivre une approche simple et explicite :
 * 1. Trier les secouristes (lignes) par ordre croissant de polyvalence.
 * 2. Trier les postes (colonnes) par ordre croissant de "rareté" (nombre de candidats).
 * 3. Appliquer une affectation gloutonne simple sur la matrice ainsi triée.
 */
public class ServiceAffectationGloutonne {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    /**
     * Tente de trouver une affectation pour un DPS en suivant l'algorithme glouton des diapositives.
     *
     * @param dpsCible Le Dispositif Prévisionnel de Secours à traiter.
     * @return Une liste d'affectations ({@link AffectationResultat}) trouvées.
     */
    public List<AffectationResultat> trouverAffectationPourDPS(DPS dpsCible) {
        // --- Préparation des données ---
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);

        if (postesAPourvoir.isEmpty() || secouristesLibres.isEmpty()) {
            return new ArrayList<>();
        }

        // --- Étape 1 : Trier les secouristes (lignes) ---
        // On calcule pour chaque secouriste combien de postes il peut couvrir.
        Map<Long, Long> polyvalenceSecouristes = secouristesLibres.stream()
            .collect(Collectors.toMap(
                Secouriste::getId,
                secouriste -> postesAPourvoir.stream()
                                             .filter(poste -> estApte(secouriste, poste))
                                             .count()
            ));

        // On trie la liste des secouristes du moins polyvalent au plus polyvalent.
        secouristesLibres.sort(Comparator.comparing(secouriste -> polyvalenceSecouristes.get(secouriste.getId())));

        // --- Étape 2 : Trier les postes (colonnes) ---
        // On calcule pour chaque type de poste combien de secouristes sont aptes.
        // On utilise une map pour compter par intitulé de compétence.
        Map<String, Long> raretePostes = postesAPourvoir.stream()
            .map(poste -> poste.competenceRequise().getIntitule())
            .distinct()
            .collect(Collectors.toMap(
                Function.identity(),
                intitule -> secouristesLibres.stream()
                                             .filter(secouriste -> serviceCompetences.possedeCompetenceRequiseOuSuperieure(
                                                 secouriste.getCompetences(), 
                                                 new Competence(intitule))) // Crée une compétence temporaire pour le test
                                             .count()
            ));

        // On trie la liste des postes du plus rare (moins de candidats) au plus commun.
        postesAPourvoir.sort(Comparator.comparing(poste -> raretePostes.get(poste.competenceRequise().getIntitule())));


        // --- Étape 3 : Appliquer l'affectation gloutonne simple ---
        List<AffectationResultat> affectationsTrouvees = new ArrayList<>();
        Set<Long> secouristesDejaAffectes = new HashSet<>();
        Set<Integer> indexPostesPourvus = new HashSet<>(); // Utiliser l'index pour gérer les postes identiques

        // On parcourt les secouristes triés (les moins polyvalents d'abord).
        for (Secouriste secouriste : secouristesLibres) {
            // Pour chaque secouriste, on cherche le premier poste (trié par rareté) qu'il peut prendre.
            for (int i = 0; i < postesAPourvoir.size(); i++) {
                if (!indexPostesPourvus.contains(i)) { // Si le poste n'est pas déjà pris
                    Poste poste = postesAPourvoir.get(i);
                    if (estApte(secouriste, poste)) {
                        affectationsTrouvees.add(new AffectationResultat(secouriste, poste));
                        secouristesDejaAffectes.add(secouriste.getId()); // Le secouriste est pris.
                        indexPostesPourvus.add(i); // Le poste est pris.
                        break; // On passe au secouriste suivant.
                    }
                }
            }
        }
        
        return affectationsTrouvees;
    }

    // --- Les méthodes utilitaires restent les mêmes ---

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

        Set<Long> idsSecouristesOccupes = affectationsDuJour.stream()
                .filter(affectation -> affectation.getDps().getId() != dpsCible.getId())
                .map(affectation -> affectation.getSecouriste().getId())
                .collect(Collectors.toSet());

        return tousLesSecouristes.stream()
                .filter(secouriste -> estDisponibleCeJour(secouriste, dpsCible.getJournee().getDate()))
                .filter(secouriste -> !idsSecouristesOccupes.contains(secouriste.getId()))
                .collect(Collectors.toList());
    }

    private boolean estApte(Secouriste secouriste, Poste poste) {
        return serviceCompetences.possedeCompetenceRequiseOuSuperieure(
                secouriste.getCompetences(),
                poste.competenceRequise());
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