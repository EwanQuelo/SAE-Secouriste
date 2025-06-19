package fr.erm.sae201.metier.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Représente un Dispositif Prévisionnel de Secours (DPS).
 *
 * @author Raphael MILLE
 * @author Ewan QUELO
 * @author Matheo BIET
 * @version 1.2
 */
public class DPS {
    /** L'identifiant unique du DPS. */
    private long id;
    /** L'horaire de début, sous forme de tableau {heure, minute}. */
    private int[] horaireDepart;
    /** L'horaire de fin, sous forme de tableau {heure, minute}. */
    private int[] horaireFin;
    /** Le site où se déroule le DPS. */
    private Site site;
    /** La journée concernée par le DPS. */
    private Journee journee;
    /** Le sport concerné par le DPS. */
    private Sport sport;
    /** Une carte des compétences requises et du nombre de secouristes nécessaires pour chacune. */
    private Map<Competence, Integer> competencesRequises;

    /**
     * Construit un nouveau DPS.
     *
     * @param id            L'identifiant du DPS.
     * @param horaireDepart L'horaire de début {heure, minute}.
     * @param horaireFin    L'horaire de fin {heure, minute}.
     * @param site          Le site de l'événement.
     * @param journee       La journée de l'événement.
     * @param sport         Le sport de l'événement.
     */
    public DPS(long id, int[] horaireDepart, int[] horaireFin, Site site, Journee journee, Sport sport) {
        this.id = id;
        setHoraireDepart(horaireDepart);
        setHoraireFin(horaireFin);
        setSite(site);
        setJournee(journee);
        setSport(sport);
        this.competencesRequises = new HashMap<>(); // Initialise pour éviter les erreurs de type NullPointerException.
    }

    /**
     * Retourne l'ID du DPS.
     *
     * @return L'ID du DPS.
     */
    public long getId() {
        return id;
    }

    /**
     * Définit l'ID du DPS.
     *
     * @param id Le nouvel ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retourne une copie de l'horaire de départ.
     *
     * @return Un tableau {heure, minute}.
     */
    public int[] getHoraireDepart() {
        return horaireDepart.clone();
    }

    /**
     * Définit l'horaire de départ.
     *
     * @param horaireDepart Un tableau {heure, minute}.
     * @throws IllegalArgumentException si le format du tableau ou les valeurs sont invalides.
     */
    public void setHoraireDepart(int[] horaireDepart) {
        if (horaireDepart == null || horaireDepart.length != 2) {
            throw new IllegalArgumentException("Horaire depart must be an array of 2 integers {hour, minute}.");
        }
        if (horaireDepart[0] < 0 || horaireDepart[0] > 23 || horaireDepart[1] < 0 || horaireDepart[1] > 59) {
            throw new IllegalArgumentException("Horaire depart has invalid hour/minute values.");
        }
        this.horaireDepart = horaireDepart.clone();
    }

    /**
     * Retourne une copie de l'horaire de fin.
     *
     * @return Un tableau {heure, minute}.
     */
    public int[] getHoraireFin() {
        return horaireFin.clone();
    }

    /**
     * Définit l'horaire de fin.
     *
     * @param horaireFin Un tableau {heure, minute}.
     * @throws IllegalArgumentException si le format du tableau ou les valeurs sont invalides.
     */
    public void setHoraireFin(int[] horaireFin) {
        if (horaireFin == null || horaireFin.length != 2) {
            throw new IllegalArgumentException("Horaire fin must be an array of 2 integers {hour, minute}.");
        }
        if (horaireFin[0] < 0 || horaireFin[0] > 23 || horaireFin[1] < 0 || horaireFin[1] > 59) {
            throw new IllegalArgumentException("Horaire fin has invalid hour/minute values.");
        }
        this.horaireFin = horaireFin.clone();
    }

    /**
     * Retourne le site du DPS.
     *
     * @return Le site.
     */
    public Site getSite() {
        return site;
    }

    /**
     * Définit le site du DPS.
     *
     * @param site Le nouveau site.
     * @throws IllegalArgumentException si le site est null.
     */
    public void setSite(Site site) {
        if (site == null) {
            throw new IllegalArgumentException("Site cannot be null.");
        }
        this.site = site;
    }

    /**
     * Retourne la journée du DPS.
     *
     * @return La journée.
     */
    public Journee getJournee() {
        return journee;
    }

    /**
     * Définit la journée du DPS.
     *
     * @param journee La nouvelle journée.
     * @throws IllegalArgumentException si la journée est null.
     */
    public void setJournee(Journee journee) {
        if (journee == null) {
            throw new IllegalArgumentException("Journee cannot be null.");
        }
        this.journee = journee;
    }

    /**
     * Retourne le sport du DPS.
     *
     * @return Le sport.
     */
    public Sport getSport() {
        return sport;
    }

    /**
     * Définit le sport du DPS.
     *
     * @param sport Le nouveau sport.
     * @throws IllegalArgumentException si le sport est null.
     */
    public void setSport(Sport sport) {
        if (sport == null) {
            throw new IllegalArgumentException("Sport cannot be null.");
        }
        this.sport = sport;
    }

    /**
     * Retourne une copie de la carte des compétences requises.
     *
     * @return La carte des compétences et du nombre requis.
     */
    public Map<Competence, Integer> getCompetencesRequises() {
        return new HashMap<>(competencesRequises);
    }

    /**
     * Définit la carte des compétences requises.
     *
     * @param competencesRequises La nouvelle carte des compétences.
     * @throws IllegalArgumentException si la carte, une clé ou une valeur est null ou invalide.
     */
    public void setCompetencesRequises(Map<Competence, Integer> competencesRequises) {
        if (competencesRequises == null) {
            throw new IllegalArgumentException("Competences requises map cannot be null.");
        }
        Map<Competence, Integer> tempMap = new HashMap<>();
        for (Map.Entry<Competence, Integer> entry : competencesRequises.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Competence key in map cannot be null.");
            }
            if (entry.getValue() == null) {
                throw new IllegalArgumentException("Number for competence " + entry.getKey().getIntitule() + " cannot be null.");
            }
            if (entry.getValue() <= 0) {
                throw new IllegalArgumentException("Number for competence " + entry.getKey().getIntitule() + " must be positive.");
            }
            tempMap.put(entry.getKey(), entry.getValue());
        }
        this.competencesRequises = tempMap;
    }

    /**
     * Ajoute ou met à jour une compétence requise et le nombre de secouristes associé.
     *
     * @param competence La compétence à ajouter ou mettre à jour.
     * @param nombre Le nombre de secouristes requis.
     * @throws IllegalArgumentException si la compétence est null ou le nombre est invalide.
     */
    public void addOrUpdateCompetenceRequise(Competence competence, int nombre) {
        if (competence == null) {
            throw new IllegalArgumentException("Competence to add/update cannot be null.");
        }
        if (nombre <= 0) {
            throw new IllegalArgumentException("Number for competence " + competence.getIntitule() + " must be positive.");
        }
        this.competencesRequises.put(competence, nombre);
    }

    /**
     * Supprime une compétence requise de la liste.
     *
     * @param competence La compétence à supprimer.
     * @return Le nombre qui était associé à la compétence, ou `null` si elle n'était pas présente.
     */
    public Integer removeCompetenceRequise(Competence competence) {
        if (competence == null) return null;
        return this.competencesRequises.remove(competence);
    }

    /**
     * Compare ce DPS à un autre objet.
     * Deux DPS sont considérés comme égaux si leurs ID sont identiques.
     *
     * @param o L'objet à comparer.
     * @return `true` si les objets sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DPS dps = (DPS) o;
        return id == dps.id;
    }

    /**
     * Génère un code de hachage pour le DPS, basé sur son ID.
     *
     * @return Un entier représentant le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retourne une représentation textuelle du DPS.
     *
     * @return Une chaîne de caractères décrivant le DPS.
     */
    @Override
    public String toString() {
        return "DPS{" +
               "id=" + id +
               ", horaireDepart=" + Arrays.toString(horaireDepart) +
               ", horaireFin=" + Arrays.toString(horaireFin) +
               ", site=" + (site != null ? site.getNom() : "N/A") +
               ", journee=" + journee +
               ", sport=" + (sport != null ? sport.getNom() : "N/A") +
               '}';
    }
}