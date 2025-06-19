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
     * Tente de trouver une affectation pour un DPS en suivant l'algorithme glouton
     *
     * @param dpsCible Le Dispositif Prévisionnel de Secours à traiter
     * @return Une liste d'affectations ({@link AffectationResultat}) trouvées
     */
    public List<AffectationResultat> trouverAffectationPourDPS(DPS dpsCible) {
        // On prépare les données nécessaires
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);

        if (postesAPourvoir.isEmpty() || secouristesLibres.isEmpty()) {
            return new ArrayList<>();
        }

        // --- Étape 1 : Trier les secouristes (lignes)
        // On calcule pour chaque secouriste combien de postes il peut couvrir.
        Map<Long, Long> polyvalenceSecouristes = secouristesLibres.stream().collect(Collectors.toMap(Secouriste::getId,
                secouriste -> postesAPourvoir.stream()
                                             .filter(poste -> estApte(secouriste, poste))
                                             .count()
            ));

        // On trie la liste des secouristes du moins polyvalent au plus polyvalent.
        secouristesLibres.sort(Comparator.comparing(secouriste -> polyvalenceSecouristes.get(secouriste.getId())));

        // --- Étape 2 : Trier les postes (colonnes) ---
        // On calcule pour chaque type de poste combien de secouristes sont aptes
        // On utilise une map pour compter par intitulé de compétence
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

        // On trie la liste des postes du plus rare (moins de candidats) au plus commun
        postesAPourvoir.sort(Comparator.comparing(poste -> raretePostes.get(poste.competenceRequise().getIntitule())));


        // --- Étape 3 : Appliquer l'affectation gloutonne simple 
        List<AffectationResultat> affectationsTrouvees = new ArrayList<>();
        Set<Long> secouristesDejaAffectes = new HashSet<>();
        Set<Integer> indexPostesPourvus = new HashSet<>(); // Utiliser l'index pour gérer les postes identiques

        // On parcourt les secouristes triés (les moins polyvalents d'abord)
        for (Secouriste secouriste : secouristesLibres) {
            // Pour chaque secouriste, on cherche le premier poste (trié par rareté) qu'il peut prendre
            for (int i = 0; i < postesAPourvoir.size(); i++) {
                if (!indexPostesPourvus.contains(i)) { // Si le poste n'est pas déjà pris
                    Poste poste = postesAPourvoir.get(i);
                    if (estApte(secouriste, poste)) {
                        affectationsTrouvees.add(new AffectationResultat(secouriste, poste));
                        secouristesDejaAffectes.add(secouriste.getId()); // Le secouriste est pris
                        indexPostesPourvus.add(i); // Le poste est pris
                        break; // On passe au secouriste suivant
                    }
                }
            }
        }
        
        return affectationsTrouvees;
    }

   /**
     * Transforme les besoins d'un DPS (ex: "2 PSE2", "1 CE") en une liste d'objets {@link Poste} unitaires.
     * Si un DPS a besoin de 2 secouristes PSE2, cette méthode créera deux objets `Poste` distincts
     * avec la compétence requise PSE2. Cela simplifie grandement le traitement par les algorithmes d'affectation.
     *
     * @param dps Le Dispositif Prévisionnel de Secours dont les besoins doivent être transformés.
     * @return Une liste plate de tous les postes individuels à pourvoir pour ce DPS.
     */
    private List<Poste> preparerPostesPourUnSeulDps(DPS dps) {
        // Initialise une liste vide qui contiendra tous les postes à pourvoir
        List<Poste> postes = new ArrayList<>();
        // Récupère depuis la base de données une map des besoins : Clé = Compétence, Valeur = nombre requis.
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
     * Identifie et retourne la liste de tous les secouristes qui sont potentiellement affectables
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

        // Crée un ensemble (Set) contenant les IDs des secouristes déjà occupés ce jour-là.
        // On filtre pour ne garder que les affectations sur d'autres DPS que celui en cours de traitement.
        // Cela permet de modifier une affectation existante sans que le secouriste soit considéré comme "occupé".
        Set<Long> idsSecouristesOccupes = affectationsDuJour.stream()
                .filter(affectation -> affectation.getDps().getId() != dpsCible.getId())
                .map(affectation -> affectation.getSecouriste().getId())
                .collect(Collectors.toSet());

        // On filtre la liste de tous les secouristes pour ne garder que ceux qui remplissent les deux conditions
        return tousLesSecouristes.stream()
                // Condition 1 : Le secouriste doit avoir être disponible ce jour-là
                .filter(secouriste -> estDisponibleCeJour(secouriste, dpsCible.getJournee().getDate()))
                // Condition 2 : L'ID du secouriste ne doit pas être dans la liste des secouristes déjà occupés
                .filter(secouriste -> !idsSecouristesOccupes.contains(secouriste.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un secouriste est apte à pourvoir un poste donné.
     * L'aptitude est ici définie par la possession de la compétence requise.
     * Cette méthode délègue la logique complexe de vérification (ex: un PSE2 est apte pour un poste PSE1)
     * au service spécialisé {@link ServiceCompetences}.
     *
     * @param secouriste Le secouriste candidat.
     * @param poste Le poste à pourvoir.
     * @return {@code true} si le secouriste a la compétence requise (ou une compétence supérieure), {@code false} sinon.
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
     * @param date La date pour laquelle on vérifie la disponibilité.
     * @return {@code true} si une journée de disponibilité correspond à la date fournie, {@code false} sinon.
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
        // Si la boucle se termine sans avoir trouvé de correspondance, il n'est pas disponible.
        return false;
    }
}