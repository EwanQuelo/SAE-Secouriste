package fr.erm.sae201.metier.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Dispositif Prévisionnel de Secours (DPS).
 * @author Raphael Mille, Ewan Quelo, Matheo Biet 
 * @version 1.0
 */
public class DPS {
    private long id;
    private int[] horaireDepart; // int[2] -> {hour, minute}
    private int[] horaireFin;    // int[2] -> {hour, minute}
    private Site site;           // Relation ALieuDans (DPS 0..* - 1 Site)
    private Journee journee;     // Relation EstProgramme (Journee 1 - DPS 0..*)
    private Sport sport;         // Relation Concerne (DPS 0..* - 1 Sport)
    private Map<Competence, Integer> competencesRequises; // Relation ABesoin (Competence 1..* - DPS 0..*) with attribute 'nombre'

    /**
     * Constructs a new DPS.
     * @param id The unique ID of the DPS.
     * @param horaireDepart The departure time {hour, minute}. Must be a 2-element array, hour 0-23, minute 0-59. A copy is stored.
     * @param horaireFin The end time {hour, minute}. Must be a 2-element array, hour 0-23, minute 0-59. A copy is stored.
     * @param site The site where the DPS takes place. Must not be null.
     * @param journee The day the DPS is programmed for. Must not be null.
     * @param sport The sport concerned by the DPS. Must not be null.
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    public DPS(long id, int[] horaireDepart, int[] horaireFin, Site site, Journee journee, Sport sport) {
        setId(id);
        setHoraireDepart(horaireDepart); // Setter clones and validates
        setHoraireFin(horaireFin);       // Setter clones and validates
        setSite(site);
        setJournee(journee);
        setSport(sport);
        this.competencesRequises = new HashMap<>(); // Initialize to avoid null later
    }

    /**
     * Gets the ID of the DPS.
     * @return The ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the DPS.
     * @param id The new ID.
     */
    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number.");
        }
        this.id = id;
    }

    /**
     * Gets a copy of the departure time.
     * @return A new array {hour, minute} representing the departure time.
     */
    public int[] getHoraireDepart() {
        return horaireDepart.clone(); // Return a copy
    }

    /**
     * Sets the departure time.
     * A copy of the provided array is stored.
     * @param horaireDepart The new departure time {hour, minute}. Must be a 2-element array, hour 0-23, minute 0-59.
     * @throws IllegalArgumentException if horaireDepart is null, not length 2, or values are out of range.
     */
    public void setHoraireDepart(int[] horaireDepart) {
        if (horaireDepart == null || horaireDepart.length != 2) {
            throw new IllegalArgumentException("Horaire depart must be an array of 2 integers {hour, minute}.");
        }
        if (horaireDepart[0] < 0 || horaireDepart[0] > 23 || horaireDepart[1] < 0 || horaireDepart[1] > 59) {
            throw new IllegalArgumentException("Horaire depart has invalid hour/minute values.");
        }
        this.horaireDepart = horaireDepart.clone(); // Store a copy
    }

    /**
     * Gets a copy of the end time.
     * @return A new array {hour, minute} representing the end time.
     */
    public int[] getHoraireFin() {
        return horaireFin.clone(); // Return a copy
    }

    /**
     * Sets the end time.
     * A copy of the provided array is stored.
     * @param horaireFin The new end time {hour, minute}. Must be a 2-element array, hour 0-23, minute 0-59.
     * @throws IllegalArgumentException if horaireFin is null, not length 2, or values are out of range.
     */
    public void setHoraireFin(int[] horaireFin) {
        if (horaireFin == null || horaireFin.length != 2) {
            throw new IllegalArgumentException("Horaire fin must be an array of 2 integers {hour, minute}.");
        }
        if (horaireFin[0] < 0 || horaireFin[0] > 23 || horaireFin[1] < 0 || horaireFin[1] > 59) {
            throw new IllegalArgumentException("Horaire fin has invalid hour/minute values.");
        }
        this.horaireFin = horaireFin.clone(); // Store a copy
    }

    /**
     * Gets the site of the DPS.
     * @return The site.
     */
    public Site getSite() {
        return site; // Site is assumed to be well-behaved or immutable enough not to need cloning on get
                     // If Site were mutable in a problematic way, you'd clone it here too.
    }

    /**
     * Sets the site of the DPS.
     * @param site The new site. Must not be null.
     * @throws IllegalArgumentException if site is null.
     */
    public void setSite(Site site) {
        if (site == null) {
            throw new IllegalArgumentException("Site cannot be null.");
        }
        this.site = site;
    }

    /**
     * Gets the Journee (day) of the DPS.
     * @return The Journee.
     */
    public Journee getJournee() {
        return journee; // Journee is composed of primitives, no deep copy needed on get
    }

    /**
     * Sets the Journee (day) of the DPS.
     * @param journee The new Journee. Must not be null.
     * @throws IllegalArgumentException if journee is null.
     */
    public void setJournee(Journee journee) {
        if (journee == null) {
            throw new IllegalArgumentException("Journee cannot be null.");
        }
        this.journee = journee;
    }

    /**
     * Gets the Sport concerned by the DPS.
     * @return The Sport.
     */
    public Sport getSport() {
        return sport; // Sport is assumed to be well-behaved or immutable enough not to need cloning on get
    }

    /**
     * Sets the Sport concerned by the DPS.
     * @param sport The new Sport. Must not be null.
     * @throws IllegalArgumentException if sport is null.
     */
    public void setSport(Sport sport) {
        if (sport == null) {
            throw new IllegalArgumentException("Sport cannot be null.");
        }
        this.sport = sport;
    }

    /**
     * Gets a copy of the map of required competences and their numbers.
     * @return A new map of competences to the required number of personnel.
     */
    public Map<Competence, Integer> getCompetencesRequises() {
        return new HashMap<>(competencesRequises); // Return a copy
    }

    /**
     * Sets the map of required competences.
     * The provided map is copied.
     * @param competencesRequises The map of competences to the required number. Must not be null. Keys and values must not be null. Numbers must be positive.
     * @throws IllegalArgumentException if map is null, or contains null keys/values, or non-positive numbers.
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
                throw new IllegalArgumentException("Number for competence " + entry.getKey().getNom() + " cannot be null.");
            }
            if (entry.getValue() <= 0) {
                throw new IllegalArgumentException("Number for competence " + entry.getKey().getNom() + " must be positive.");
            }
            tempMap.put(entry.getKey(), entry.getValue());
        }
        this.competencesRequises = tempMap; // Store the validated copy
    }
    
    /**
     * Adds or updates a required competence and its number.
     * @param competence The competence. Must not be null.
     * @param nombre The number required. Must be positive.
     * @throws IllegalArgumentException if competence is null or nombre is not positive.
     */
    public void addOrUpdateCompetenceRequise(Competence competence, int nombre) {
        if (competence == null) {
            throw new IllegalArgumentException("Competence to add/update cannot be null.");
        }
        if (nombre <= 0) {
            throw new IllegalArgumentException("Number for competence " + competence.getNom() + " must be positive.");
        }
        this.competencesRequises.put(competence, nombre);
    }

    /**
     * Removes a required competence.
     * @param competence The competence to remove.
     * @return The number previously associated with the competence, or null if not found.
     */
    public Integer removeCompetenceRequise(Competence competence) {
        if (competence == null) return null;
        return this.competencesRequises.remove(competence);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DPS dps = (DPS) o;
        return id == dps.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

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