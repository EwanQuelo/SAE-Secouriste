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
 */
public class ServiceAffectationExhaustive {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    // Stocke TOUTES les affectations complètes possibles
    private List<List<AffectationResultat>> toutesLesSolutions;

    /**
     * Trouve la meilleure affectation possible en explorant toutes les
     * combinaisons.
     *
     * @param dpsCible Le Dispositif Prévisionnel de Secours à optimiser
     * @return La liste d'affectations optimale
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
        // solutions
        genererToutesLesAffectations(postesAPourvoir, secouristesLibres, new ArrayList<>());

        // Une fois que TOUTES les solutions ont été générées, on choisit la meilleure
        List<AffectationResultat> resultat = trouverMeilleureSolutionParmiToutes();

        long endTimeAlgo = System.nanoTime();
        long durationAlgo = (endTimeAlgo - startTimeAlgo) / 1_000_000;
        System.out.println("Fin du calcul de l'algorithme. Temps de calcul pur : " + durationAlgo + " ms.");

        return resultat;
    }

    /**
     * Méthode récursive qui génère toutes les affectations possibles et les stocke
     *
     * @param postesRestants         La liste des postes qu'il reste à pourvoir
     * @param secouristesDisponibles La liste des secouristes qui ne sont pas encore
     *                               affectés
     * @param affectationEnCours     La solution partielle en cours de construction
     */
    private void genererToutesLesAffectations(List<Poste> postesRestants, List<Secouriste> secouristesDisponibles,
            List<AffectationResultat> affectationEnCours) {
        // Condition d'arrêt : s'il n'y a plus de postes, cette affectation est
        // "terminée"
        // On l'ajoute à notre liste de solutions
        if (postesRestants.isEmpty()) {
            toutesLesSolutions.add(new ArrayList<>(affectationEnCours));
            return;
        }

        Poste posteCourant = postesRestants.get(0);
        List<Poste> prochainsPostes = postesRestants.subList(1, postesRestants.size());
        boolean posteAffecte = false;

        // On essaie d'affecter le poste courant à chaque secouriste possible
        for (Secouriste secouriste : secouristesDisponibles) {
            if (estApte(secouriste, posteCourant)) {
                posteAffecte = true;

                // Affectation provisoire
                affectationEnCours.add(new AffectationResultat(secouriste, posteCourant));
                List<Secouriste> prochainsSecouristes = new ArrayList<>(secouristesDisponibles);
                prochainsSecouristes.remove(secouriste);

                // Appel récursif pour le sous-problème
                genererToutesLesAffectations(prochainsPostes, prochainsSecouristes, affectationEnCours);

                // on annule pour tester le secouriste suivant pour ce même poste
                affectationEnCours.remove(affectationEnCours.size() - 1);
            }
        }

        // Si, après avoir testé tous les secouristes, le poste n'a pas pu être affecté,
        // on continue la recherche en ignorant ce poste
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

    /**
     * Transforme les besoins d'un DPS (ex: "2 PSE2", "1 CE") en une liste d'objets
     * {@link Poste} unitaires.
     * Si un DPS a besoin de 2 secouristes PSE2, cette méthode créera deux objets
     * `Poste` distincts
     * avec la compétence requise PSE2. Cela simplifie grandement le traitement par
     * les algorithmes d'affectation.
     *
     * @param dps Le Dispositif Prévisionnel de Secours dont les besoins doivent
     *            être transformés.
     * @return Une liste plate de tous les postes individuels à pourvoir pour ce
     *         DPS.
     */
    private List<Poste> preparerPostesPourUnSeulDps(DPS dps) {
        // Initialise une liste vide qui contiendra tous les postes à pourvoir.
        List<Poste> postes = new ArrayList<>();
        // Récupère depuis la base de données une map des besoins : Clé = Compétence,
        // Valeur = nombre requis.
        Map<Competence, Integer> besoins = dpsDAO.findRequiredCompetencesForDps(dps.getId());

        // Itère sur chaque besoin (ex: <Competence "PSE2", Integer 2>).
        for (Map.Entry<Competence, Integer> besoin : besoins.entrySet()) {
            // Pour chaque besoin, on crée autant d'objets Poste que le nombre requis.
            for (int i = 0; i < besoin.getValue(); i++) {
                postes.add(new Poste(dps.getId(), besoin.getKey()));
            }
        }
        return postes;
    }

    /**
     * Identifie et retourne la liste de tous les secouristes qui sont
     * potentiellement affectables
     * pour un DPS donné. Un secouriste est considéré "libre" si :
     * 1. Il a déclaré être disponible à la date du DPS.
     * 2. Il n'est pas déjà affecté à un AUTRE DPS le même jour.
     *
     * @param dpsCible Le DPS pour lequel on cherche des secouristes.
     * @return Une liste de secouristes disponibles et non occupés ailleurs.
     */
    private List<Secouriste> trouverSecouristesLibresPour(DPS dpsCible) {
        // Récupère la totalité des secouristes enregistrés dans le système.
        List<Secouriste> tousLesSecouristes = secouristeDAO.findAll();
        // Récupère toutes les affectations déjà enregistrées pour la date du DPS cible.
        List<Affectation> affectationsDuJour = affectationDAO.findAllByDate(dpsCible.getJournee().getDate());

        // Crée un ensemble (Set) contenant les IDs des secouristes déjà occupés ce
        // jour-là.
        // On filtre pour ne garder que les affectations sur d'autres DPS que celui en
        // cours de traitement.
        // Cela permet de modifier une affectation existante sans que le secouriste soit
        // considéré comme "occupé".
        Set<Long> idsSecouristesOccupes = affectationsDuJour.stream()
                .filter(affectation -> affectation.getDps().getId() != dpsCible.getId())
                .map(affectation -> affectation.getSecouriste().getId())
                .collect(Collectors.toSet());

        // On filtre la liste de tous les secouristes pour ne garder que ceux qui
        // remplissent les deux conditions.
        return tousLesSecouristes.stream()
                // Condition 1 : Le secouriste doit avoir indiqué sa disponibilité pour ce jour.
                .filter(secouriste -> estDisponibleCeJour(secouriste, dpsCible.getJournee().getDate()))
                // Condition 2 : L'ID du secouriste ne doit pas être dans la liste des
                // secouristes déjà occupés.
                .filter(secouriste -> !idsSecouristesOccupes.contains(secouriste.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un secouriste est apte à pourvoir un poste donné.
     * L'aptitude est ici définie par la possession de la compétence requise.
     * Cette méthode délègue la logique complexe de vérification (ex: un PSE2 est
     * apte pour un poste PSE1)
     * au service spécialisé {@link ServiceCompetences}.
     *
     * @param secouriste Le secouriste candidat.
     * @param poste      Le poste à pourvoir.
     * @return {@code true} si le secouriste a la compétence requise (ou une
     *         compétence supérieure), {@code false} sinon.
     */
    private boolean estApte(Secouriste secouriste, Poste poste) {
        return serviceCompetences.possedeCompetenceRequiseOuSuperieure(
                secouriste.getCompetences(),
                poste.competenceRequise());
    }

    /**
     * Vérifie si un secouriste a déclaré être disponible à une date précise.
     * La méthode parcourt la liste des disponibilités du secouriste.
     *
     * @param secouriste Le secouriste concerné.
     * @param date       La date pour laquelle on vérifie la disponibilité.
     * @return {@code true} si une journée de disponibilité correspond à la date
     *         fournie, {@code false} sinon.
     */
    private boolean estDisponibleCeJour(Secouriste secouriste, java.time.LocalDate date) {
        // Parcours de la liste des objets Journee associés au secouriste.
        for (Journee jourDispo : secouriste.getDisponibilites()) {
            // Si la date d'une de ses disponibilités correspond à la date recherchée...
            if (jourDispo.getDate().equals(date)) {
                // ... il est disponible. On retourne true et on arrête la boucle.
                return true;
            }
        }
        // Si la boucle se termine sans avoir trouvé de correspondance, il n'est pas
        // disponible.
        return false;
    }
}