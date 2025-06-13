package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Represents the assignment of a Secouriste to a specific DPS for a specific Competence.
 * This corresponds to the 'Affectation' join table.
 */
public class Affectation {

    private DPS dps;
    private Secouriste secouriste;
    private Competence competence;

    /**
     * Constructs a new Affectation.
     *
     * @param dps        The DPS the assignment is for. Must not be null.
     * @param secouriste The Secouriste being assigned. Must not be null.
     * @param competence The specific Competence the Secouriste is fulfilling for this DPS. Must not be null.
     */
    public Affectation(DPS dps, Secouriste secouriste, Competence competence) {
        if (dps == null || secouriste == null || competence == null) {
            throw new IllegalArgumentException("DPS, Secouriste, and Competence for an Affectation cannot be null.");
        }
        this.dps = dps;
        this.secouriste = secouriste;
        this.competence = competence;
    }

    // Getters
    public DPS getDps() {
        return dps;
    }

    public Secouriste getSecouriste() {
        return secouriste;
    }

    public Competence getCompetence() {
        return competence;
    }

    // Setters could be added if needed, but entity is often treated as immutable once created.

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
               "dpsId=" + dps.getId() +
               ", secouristeId=" + secouriste.getId() +
               ", competence=" + competence.getIntitule() +
               '}';
    }
}