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
import java.util.List;
import java.util.Map;

/**
 * Service principal pour orchestrer la création et la résolution des problèmes d'affectation.
 * <p>
 * Ce service utilise les DAOs pour récupérer les données nécessaires, construit
 * un graphe biparti représentant les secouristes et les postes, puis délègue
 * la résolution à des classes d'algorithmes spécifiques.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class ServiceAffectation {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ServiceCompetences serviceCompetences = new ServiceCompetences();

    /**
     * Trouve la meilleure affectation possible pour un DPS en utilisant une approche exhaustive.
     * Cette méthode explore toutes les combinaisons possibles pour garantir un résultat optimal.
     *
     * @param dpsCible Le DPS pour lequel chercher une affectation.
     * @return Une liste de résultats d'affectation, représentant la meilleure solution trouvée.
     */
    public List<AffectationResultat> trouverAffectationExhaustive(DPS dpsCible) {
        Graphe graphe = construireGraphePourDPS(dpsCible);
        AlgorithmeAffectationExhaustive algorithme = new AlgorithmeAffectationExhaustive();
        return algorithme.resoudre(graphe);
    }

    /**
     * Trouve une affectation pour un DPS en utilisant une approche gloutonne.
     * Cette méthode fournit une solution rapide mais pas nécessairement optimale.
     *
     * @param dpsCible Le DPS pour lequel chercher une affectation.
     * @return Une liste de résultats d'affectation.
     */
    public List<AffectationResultat> trouverAffectationGloutonne(DPS dpsCible) {
        Graphe graphe = construireGraphePourDPS(dpsCible);
        AlgorithmeAffectationGloutonne algorithme = new AlgorithmeAffectationGloutonne();
        return algorithme.resoudre(graphe);
    }

    /**
     * Construit l'objet Graphe biparti qui modélise le problème d'affectation pour un DPS.
     *
     * @param dpsCible Le DPS concerné.
     * @return Un objet Graphe prêt à être utilisé par les algorithmes.
     */
    private Graphe construireGraphePourDPS(DPS dpsCible) {
        List<Poste> postesAPourvoir = preparerPostesPourUnSeulDps(dpsCible);
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

    /**
     * Vérifie si les horaires de deux DPS se chevauchent.
     * Convertit les horaires en minutes depuis minuit pour faciliter la comparaison.
     *
     * @param dps1 Le premier DPS.
     * @param dps2 Le second DPS.
     * @return `true` si les intervalles de temps se chevauchent, `false` sinon.
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

        // Deux intervalles [start1, end1] et [start2, end2] se chevauchent si
        // le début de l'un est avant la fin de l'autre, et vice versa.
        return startMinutes1 < endMinutes2 && startMinutes2 < endMinutes1;
    }

    /**
     * Trouve les secouristes libres pour un DPS donné, en vérifiant leur disponibilité
     * pour la journée et l'absence de conflits horaires avec d'autres affectations.
     *
     * @param dpsCible Le DPS pour lequel on cherche des secouristes.
     * @return Une liste de secouristes disponibles et sans conflit.
     */
    private List<Secouriste> trouverSecouristesLibresPour(DPS dpsCible) {
        List<Secouriste> tousLesSecouristes = secouristeDAO.findAll();
        List<Affectation> affectationsDuJour = affectationDAO.findAllByDate(dpsCible.getJournee().getDate());

        // Prétraitement des affectations du jour pour un accès rapide par ID de secouriste.
        Map<Long, List<Affectation>> affectationsParSecouriste = new HashMap<>();
        for (Affectation aff : affectationsDuJour) {
            long secouristeId = aff.getSecouriste().getId();
            affectationsParSecouriste.computeIfAbsent(secouristeId, k -> new ArrayList<>()).add(aff);
        }

        List<Secouriste> secouristesLibres = new ArrayList<>();
        for (Secouriste secouriste : tousLesSecouristes) {
            if (!estDisponibleCeJour(secouriste, dpsCible.getJournee().getDate())) {
                continue;
            }

            boolean aUnConflit = false;
            List<Affectation> sesAffectations = affectationsParSecouriste.get(secouriste.getId());

            if (sesAffectations != null) {
                for (Affectation affectationExistante : sesAffectations) {
                    if (affectationExistante.getDps().getId() == dpsCible.getId()) {
                        continue;
                    }
                    if (horairesSeChevauchent(affectationExistante.getDps(), dpsCible)) {
                        aUnConflit = true;
                        break;
                    }
                }
            }

            if (!aUnConflit) {
                secouristesLibres.add(secouriste);
            }
        }
        return secouristesLibres;
    }

    /**
     * Crée la liste des postes à pourvoir pour un DPS à partir de ses besoins.
     *
     * @param dps Le DPS concerné.
     * @return Une liste d'objets Poste.
     */
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

    /**
     * Vérifie si un secouriste est apte pour un poste donné.
     *
     * @param secouriste Le secouriste.
     * @param poste Le poste.
     * @return `true` si le secouriste possède la compétence requise ou une compétence supérieure.
     */
    private boolean estApte(Secouriste secouriste, Poste poste) {
        return serviceCompetences.possedeCompetenceRequiseOuSuperieure(secouriste.getCompetences(), poste.competenceRequise());
    }

    /**
     * Vérifie si un secouriste s'est déclaré disponible pour une date donnée.
     *
     * @param secouriste Le secouriste.
     * @param date La date à vérifier.
     * @return `true` si le secouriste est disponible ce jour-là.
     */
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