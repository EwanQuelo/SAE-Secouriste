package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Represents an assignment of a Secouriste with a specific Competence to a DPS.
 * This maps to the 'Affectation' join table in the database.
 * PK: (idDPS, idSecouriste, intituleCompetence)
 */
public class Affectation {
    private DPS dps; // Foreign Key to DPS
    private Secouriste secouriste; // Foreign Key to Secouriste
    private Competence competence; // Foreign Key to Competence

    public Affectation(DPS dps, Secouriste secouriste, Competence competence) {
        setDps(dps);
        setSecouriste(secouriste);
        setCompetence(competence);
    }

    public DPS getDps() {
        return dps;
    }

    public void setDps(DPS dps) {
        if (dps == null) {
            throw new IllegalArgumentException("DPS cannot be null in Affectation.");
        }
        this.dps = dps;
    }

    public Secouriste getSecouriste() {
        return secouriste;
    }

    public void setSecouriste(Secouriste secouriste) {
        if (secouriste == null) {
            throw new IllegalArgumentException("Secouriste cannot be null in Affectation.");
        }
        this.secouriste = secouriste;
    }

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        if (competence == null) {
            throw new IllegalArgumentException("Competence cannot be null in Affectation.");
        }
        this.competence = competence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Affectation that = (Affectation) o;
        return Objects.equals(dps, that.dps) &&
               Objects.equals(secouriste, that.secouriste) &&
               Objects.equals(competence, that.competence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dps, secouriste, competence);
    }

    @Override
    public String toString() {
        return "Affectation{" +
               "dps_id=" + (dps != null ? dps.getId() : "null") +
               ", secouriste_id=" + (secouriste != null ? secouriste.getId() : "null") +
               ", competence_code='" + (competence != null ? competence.getCode() : "null") + '\'' +
               '}';
    }
}