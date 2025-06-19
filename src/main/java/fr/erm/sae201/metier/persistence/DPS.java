package fr.erm.sae201.metier.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Dispositif Prévisionnel de Secours (DPS).
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.2
 */
public class DPS {
    private long id;
    private int[] horaireDepart; // int[2] -> {hour, minute}
    private int[] horaireFin;    // int[2] -> {hour, minute}
    private Site site;           // Relation ALieuDans (DPS 0..* - 1 Site)
    private Journee journee;     // Relation EstProgramme (Journee 1 - DPS 0..*)
    private Sport sport;         // Relation Concerne (DPS 0..* - 1 Sport)
    private Map<Competence, Integer> competencesRequises; // Relation ABesoin (Competence 1..* - DPS 0..*) with attribute 'nombre'

    public DPS(long id, int[] horaireDepart, int[] horaireFin, Site site, Journee journee, Sport sport) {
        // L'ID peut maintenant être 0 ou négatif pour un nouvel objet qui sera inséré avec AUTO_INCREMENT
        this.id = id;
        setHoraireDepart(horaireDepart); // Setter clones and validates
        setHoraireFin(horaireFin);       // Setter clones and validates
        setSite(site);
        setJournee(journee);
        setSport(sport);
        this.competencesRequises = new HashMap<>(); // Initialize to avoid null later
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int[] getHoraireDepart() {
        return horaireDepart.clone();
    }

    public void setHoraireDepart(int[] horaireDepart) {
        if (horaireDepart == null || horaireDepart.length != 2) {
            throw new IllegalArgumentException("Horaire depart must be an array of 2 integers {hour, minute}.");
        }
        if (horaireDepart[0] < 0 || horaireDepart[0] > 23 || horaireDepart[1] < 0 || horaireDepart[1] > 59) {
            throw new IllegalArgumentException("Horaire depart has invalid hour/minute values.");
        }
        this.horaireDepart = horaireDepart.clone();
    }

    public int[] getHoraireFin() {
        return horaireFin.clone();
    }

    public void setHoraireFin(int[] horaireFin) {
        if (horaireFin == null || horaireFin.length != 2) {
            throw new IllegalArgumentException("Horaire fin must be an array of 2 integers {hour, minute}.");
        }
        if (horaireFin[0] < 0 || horaireFin[0] > 23 || horaireFin[1] < 0 || horaireFin[1] > 59) {
            throw new IllegalArgumentException("Horaire fin has invalid hour/minute values.");
        }
        this.horaireFin = horaireFin.clone();
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        if (site == null) {
            throw new IllegalArgumentException("Site cannot be null.");
        }
        this.site = site;
    }

    public Journee getJournee() {
        return journee;
    }

    public void setJournee(Journee journee) {
        if (journee == null) {
            throw new IllegalArgumentException("Journee cannot be null.");
        }
        this.journee = journee;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        if (sport == null) {
            throw new IllegalArgumentException("Sport cannot be null.");
        }
        this.sport = sport;
    }

    public Map<Competence, Integer> getCompetencesRequises() {
        return new HashMap<>(competencesRequises);
    }

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
    
    public void addOrUpdateCompetenceRequise(Competence competence, int nombre) {
        if (competence == null) {
            throw new IllegalArgumentException("Competence to add/update cannot be null.");
        }
        if (nombre <= 0) {
            throw new IllegalArgumentException("Number for competence " + competence.getIntitule() + " must be positive.");
        }
        this.competencesRequises.put(competence, nombre);
    }

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