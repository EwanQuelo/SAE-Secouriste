package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.*;
// --- IMPORTS POUR LES MODÈLES PARTAGÉS ---
import fr.erm.sae201.metier.service.ModelesAlgorithme.Poste;
import fr.erm.sae201.metier.service.ModelesAlgorithme.AffectationResultat;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceAffectationGloutonne {


    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    
    public List<AffectationResultat> trouverAffectationPourDPS(DPS dpsCible) {
    System.out.println("\nLancement de l'algorithme glouton pour le DPS n°" + dpsCible.getId() + "...");

    List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
    List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);

    // --- CORRECTION DE CETTE LIGNE ---
    // On spécifie explicitement le type (Poste p) dans l'expression lambda.
    postesAPourvoir.sort(Comparator.comparingInt((Poste p) -> getProfondeurCompetence(p.competenceRequise())).reversed());
    
    List<AffectationResultat> affectationsTrouvees = new ArrayList<>();
    Set<Long> secouristesDejaAffectes = new HashSet<>();

    for (Poste poste : postesAPourvoir) {
            // On trie les candidats potentiels pour ce poste
            // du moins qualifié au plus qualifié pour éviter de "gâcher" un expert.
            List<Secouriste> candidatsAptes = secouristesLibres.stream()
                .filter(candidat -> !secouristesDejaAffectes.contains(candidat.getId()))
                .filter(candidat -> estApte(candidat, poste))
                .sorted(Comparator.comparingInt(s -> s.getCompetences().size())) // Stratégie simple : moins de compétences = moins qualifié
                .collect(Collectors.toList());

            // On prend le premier candidat apte (le moins sur-qualifié)
            if (!candidatsAptes.isEmpty()) {
                Secouriste meilleurCandidat = candidatsAptes.get(0);
                affectationsTrouvees.add(new AffectationResultat(meilleurCandidat, poste));
                secouristesDejaAffectes.add(meilleurCandidat.getId());
            }
        }

    System.out.println("Recherche gloutonne terminée. " + affectationsTrouvees.size() + " affectations trouvées.");
    return affectationsTrouvees;
}


    // --- Méthodes utilitaires (certaines sont copiées du service exhaustif) ---

    private int getProfondeurCompetence(Competence c) {
        // Fonction simple pour mesurer la "difficulté" d'une compétence.
        // Plus elle a de prérequis (directs ou indirects), plus elle est "profonde".
        if (c.getPrerequisites() == null || c.getPrerequisites().isEmpty()) {
            return 0;
        }
        int maxProfondeur = 0;
        for (Competence prerequis : c.getPrerequisites()) {
            maxProfondeur = Math.max(maxProfondeur, getProfondeurCompetence(prerequis));
        }
        return 1 + maxProfondeur;
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
        String nomSecouriste = secouriste.getPrenom() + " " + secouriste.getNom();
        String competenceRequise = poste.competenceRequise().getIntitule();

        System.out.println("\n--- Test d'aptitude pour le poste [" + competenceRequise + "] ---");
        System.out.println("Candidat : " + nomSecouriste);

        // --- Vérification de la compétence ---
        boolean aLaCompetence = serviceCompetences.possedeCompetenceRequiseOuSuperieure(
                secouriste.getCompetences(),
                poste.competenceRequise());

        if (!aLaCompetence) {
            System.out.println(" -> REJETÉ : N'a pas la compétence requise (" + competenceRequise + ").");
            System.out.println("   Compétences possédées : " + secouriste.getCompetences());
            return false;
        }

        // Si on arrive ici, c'est que toutes les conditions sont remplies
        System.out.println(" -> ACCEPTÉ : Le secouriste est apte pour ce poste.");
        return true;
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