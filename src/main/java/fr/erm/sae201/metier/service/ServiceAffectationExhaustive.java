package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.*;
import fr.erm.sae201.metier.service.ModelesAlgorithme.Poste;
import fr.erm.sae201.metier.service.ModelesAlgorithme.AffectationResultat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implémente un algorithme d'affectation par recherche exhaustive 
 *
 * Cette approche simule le test de toutes les combinaisons possibles en
 * générant toutes les solutions maximales via un backtracking non optimisé,
 * puis en sélectionnant la meilleure à la toute fin.
 * C'est conceptuellement simple mais très coûteux en performance.
 */
public class ServiceAffectationExhaustive {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    // Stocke TOUTES les affectations complètes possibles.
    private List<List<AffectationResultat>> toutesLesSolutions;

    /**
     * Trouve la meilleure affectation possible en explorant toutes les
     * combinaisons.
     *
     * @param dpsCible Le Dispositif Prévisionnel de Secours à optimiser.
     * @return La liste d'affectations optimale.
     */
    public List<AffectationResultat> trouverMeilleureAffectationPourDPS(DPS dpsCible) {

        System.out.println("Début de la préparation des données...");
        long startTimePreparation = System.nanoTime();

        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);

        long endTimePreparation = System.nanoTime();
        long durationPreparation = (endTimePreparation - startTimePreparation) / 1_000_000;
        System.out.println("Fin de la préparation des données. Temps : " + durationPreparation + " ms.");

        this.toutesLesSolutions = new ArrayList<>();

        System.out.println("Début du calcul de l'algorithme exhaustif...");
        long startTimeAlgo = System.nanoTime();

        // Lancement de l'algorithme récursif qui va remplir la liste de toutes les
        // solutions.
        genererToutesLesAffectations(postesAPourvoir, secouristesLibres, new ArrayList<>());

        // Une fois que TOUTES les solutions ont été générées, on choisit la meilleure.
        List<AffectationResultat> resultat = trouverMeilleureSolutionParmiToutes();

        long endTimeAlgo = System.nanoTime();
        long durationAlgo = (endTimeAlgo - startTimeAlgo) / 1_000_000;
        System.out.println("Fin du calcul de l'algorithme. Temps de calcul pur : " + durationAlgo + " ms.");

        return resultat;
    }

    /**
     * Méthode récursive qui génère toutes les affectations possibles et les stocke.
     *
     * @param postesRestants         La liste des postes qu'il reste à pourvoir.
     * @param secouristesDisponibles La liste des secouristes qui ne sont pas encore
     *                               affectés.
     * @param affectationEnCours     La solution partielle en cours de construction.
     */
    private void genererToutesLesAffectations(List<Poste> postesRestants, List<Secouriste> secouristesDisponibles,
            List<AffectationResultat> affectationEnCours) {
        // Condition d'arrêt : s'il n'y a plus de postes, cette affectation est
        // "terminée".
        // On l'ajoute à notre liste de solutions.
        if (postesRestants.isEmpty()) {
            toutesLesSolutions.add(new ArrayList<>(affectationEnCours));
            return;
        }

        Poste posteCourant = postesRestants.get(0);
        List<Poste> prochainsPostes = postesRestants.subList(1, postesRestants.size());
        boolean posteAffecte = false;

        // On essaie d'affecter le poste courant à chaque secouriste possible.
        for (Secouriste secouriste : secouristesDisponibles) {
            if (estApte(secouriste, posteCourant)) {
                posteAffecte = true;

                // Affectation provisoire
                affectationEnCours.add(new AffectationResultat(secouriste, posteCourant));
                List<Secouriste> prochainsSecouristes = new ArrayList<>(secouristesDisponibles);
                prochainsSecouristes.remove(secouriste);

                // Appel récursif pour le sous-problème
                genererToutesLesAffectations(prochainsPostes, prochainsSecouristes, affectationEnCours);

                // Backtracking : on annule pour tester le secouriste suivant pour ce même
                // poste.
                affectationEnCours.remove(affectationEnCours.size() - 1);
            }
        }

        // Si, après avoir testé tous les secouristes, le poste n'a pas pu être affecté,
        // on continue la recherche en ignorant ce poste.
        if (!posteAffecte) {
            genererToutesLesAffectations(prochainsPostes, secouristesDisponibles, affectationEnCours);
        }
    }

    /**
     * Parcourt la liste de toutes les solutions générées et retourne celle qui a
     * le plus grand nombre d'affectations.
     *
     * @return La meilleure solution trouvée.
     */
    private List<AffectationResultat> trouverMeilleureSolutionParmiToutes() {
        if (toutesLesSolutions.isEmpty()) {
            return new ArrayList<>();
        }

        // On trie la liste de listes par leur taille (nombre d'affectations) et on
        // prend la plus grande.
        return toutesLesSolutions.stream()
                .max(Comparator.comparing(List::size))
                .orElse(new ArrayList<>());
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