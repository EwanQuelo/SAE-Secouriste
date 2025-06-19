package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.graphe.algorithme.AlgorithmeAffectationExhaustive;
import fr.erm.sae201.metier.graphe.algorithme.AlgorithmeAffectationGloutonne;
import fr.erm.sae201.metier.graphe.algorithme.ModelesAlgorithme.AffectationResultat;
import fr.erm.sae201.metier.graphe.algorithme.ModelesAlgorithme.Poste;
import fr.erm.sae201.metier.graphe.modele.Graphe;
import fr.erm.sae201.metier.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service principal pour orchestrer la création et la résolution des problèmes d'affectation.
 * Ce service utilise les DAOs pour récupérer les données, construit un graphe,
 * puis délègue la résolution à des classes d'algorithmes spécifiques.
 */
public class ServiceAffectation {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    /**
     * Trouve la meilleure affectation possible pour un DPS en utilisant l'approche exhaustive.
     *
     * @param dpsCible Le DPS pour lequel on cherche une affectation.
     * @return Une liste de résultats d'affectation.
     */
    public List<AffectationResultat> trouverAffectationExhaustive(DPS dpsCible) {
        Graphe graphe = construireGraphePourDPS(dpsCible);
        AlgorithmeAffectationExhaustive algorithme = new AlgorithmeAffectationExhaustive();
        return algorithme.resoudre(graphe);
    }

    /**
     * Trouve une affectation pour un DPS en utilisant l'approche gloutonne.
     *
     * @param dpsCible Le DPS pour lequel on cherche une affectation.
     * @return Une liste de résultats d'affectation.
     */
    public List<AffectationResultat> trouverAffectationGloutonne(DPS dpsCible) {
        Graphe graphe = construireGraphePourDPS(dpsCible);
        AlgorithmeAffectationGloutonne algorithme = new AlgorithmeAffectationGloutonne();
        return algorithme.resoudre(graphe);
    }

    /**
     * Méthode centrale qui prépare toutes les données et construit l'objet Graphe.
     *
     * @param dpsCible Le DPS concerné.
     * @return Un objet Graphe prêt à être utilisé par les algorithmes.
     */
    private Graphe construireGraphePourDPS(DPS dpsCible) {
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
        // MODIFIÉ : La méthode trouvera maintenant les secouristes libres en tenant compte des horaires.
        List<Secouriste> secouristesLibres = trouverSecouristesLibresPour(dpsCible);

        int[][] matrice = new int[secouristesLibres.size()][postesAPourvoir.size()];
        for (int i = 0; i < secouristesLibres.size(); i++) {
            for (int j = 0; j < postesAPourvoir.size(); j++) {
                if (estApte(secouristesLibres.get(i), postesAPourvoir.get(j))) {
                    matrice[i][j] = 1;
                } else {
                    matrice[i][j] = 0;
                }
            }
        }

        return new Graphe(secouristesLibres, postesAPourvoir, matrice);
    }

    // --- Méthodes utilitaires ---

    /**
     * NOUVEAU : Vérifie si les horaires de deux DPS se chevauchent.
     * Convertit les horaires en minutes depuis minuit pour faciliter la comparaison.
     *
     * @param dps1 Le premier DPS.
     * @param dps2 Le second DPS.
     * @return true si les intervalles de temps se chevauchent, false sinon.
     */
    private boolean horairesSeChevauchent(DPS dps1, DPS dps2) {
        int[] horaireDepart1 = dps1.getHoraireDepart();
        int[] horaireFin1 = dps1.getHoraireFin();
        int startMinutes1 = horaireDepart1[0] * 60 + horaireDepart1[1];
        int endMinutes1 = horaireFin1[0] * 60 + horaireFin1[1];

        int[] horaireDepart2 = dps2.getHoraireDepart();
        int[] horaireFin2 = dps2.getHoraireFin();
        int startMinutes2 = horaireDepart2[0] * 60 + horaireDepart2[1];
        int endMinutes2 = horaireFin2[0] * 60 + horaireFin2[1];

        // La condition de non-chevauchement est que l'un se termine avant que l'autre ne commence.
        // Si cette condition est fausse, alors il y a chevauchement.
        // Formule de chevauchement d'intervalles : [start1, end1] et [start2, end2]
        // Ils se chevauchent si (start1 < end2) ET (start2 < end1)
        return startMinutes1 < endMinutes2 && startMinutes2 < endMinutes1;
    }

    /**
     * MODIFIÉ : Trouve les secouristes libres en vérifiant non seulement la disponibilité
     * du jour, mais aussi les conflits d'horaires avec d'autres affectations.
     */
    private List<Secouriste> trouverSecouristesLibresPour(DPS dpsCible) {
        List<Secouriste> tousLesSecouristes = secouristeDAO.findAll();
        List<Affectation> affectationsDuJour = affectationDAO.findAllByDate(dpsCible.getJournee().getDate());

        // On pré-traite les affectations du jour pour un accès facile par ID de secouriste
        Map<Long, List<Affectation>> affectationsParSecouriste = new HashMap<>();
        for (Affectation aff : affectationsDuJour) {
            long secouristeId = aff.getSecouriste().getId();
            // Crée la liste si elle n'existe pas encore pour ce secouriste
            if (!affectationsParSecouriste.containsKey(secouristeId)) {
                affectationsParSecouriste.put(secouristeId, new ArrayList<>());
            }
            affectationsParSecouriste.get(secouristeId).add(aff);
        }

        List<Secouriste> secouristesLibres = new ArrayList<>();
        for (Secouriste secouriste : tousLesSecouristes) {
            // Étape 1 : Le secouriste est-il disponible pour la journée entière ?
            if (!estDisponibleCeJour(secouriste, dpsCible.getJournee().getDate())) {
                continue; // Passe au secouriste suivant
            }

            // Étape 2 : Le secouriste a-t-il des affectations ce jour-là qui entrent en conflit ?
            boolean aUnConflit = false;
            List<Affectation> sesAffectations = affectationsParSecouriste.get(secouriste.getId());

            if (sesAffectations != null) {
                for (Affectation affectationExistante : sesAffectations) {
                    // On ne compare pas le DPS cible avec lui-même
                    if (affectationExistante.getDps().getId() == dpsCible.getId()) {
                        continue;
                    }

                    // Vérification du chevauchement d'horaires
                    if (horairesSeChevauchent(affectationExistante.getDps(), dpsCible)) {
                        aUnConflit = true;
                        break; // Un seul conflit suffit pour l'exclure
                    }
                }
            }

            // Si, après toutes les vérifications, il n'y a pas de conflit, on l'ajoute.
            if (!aUnConflit) {
                secouristesLibres.add(secouriste);
            }
        }
        return secouristesLibres;
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

    private boolean estApte(Secouriste secouriste, Poste poste) {
        return serviceCompetences.possedeCompetenceRequiseOuSuperieure(secouriste.getCompetences(), poste.competenceRequise());
    }

    private boolean estDisponibleCeJour(Secouriste secouriste, LocalDate date) {
        if (secouriste.getDisponibilites() == null) {
            return false;
        }
        for (Journee jourDispo : secouriste.getDisponibilites()) {
            if (jourDispo.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }
}