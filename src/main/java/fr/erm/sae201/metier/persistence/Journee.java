package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Represents a specific day using basic numerical validation.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet 
 * @version 1.1
 */
public class Journee {
    private int jour;
    private int mois;
    private int annee;

    private static final int MIN_JOUR = 1;
    private static final int MAX_JOUR = 31;
    private static final int MIN_MOIS = 1;
    private static final int MAX_MOIS = 12;
    private static final int MIN_ANNEE = 1; // Assuming year must be a positive number

    /**
     * Constructs a new Journee.
     * Note: This performs basic range checks (e.g., day 1-31) but does not validate
     * the actual existence of the date (e.g., February 30th would be considered valid by these checks).
     * @param jour The day of the month. Must be between 1 and 31.
     * @param mois The month of the year. Must be between 1 and 12.
     * @param annee The year. Must be greater than or equal to 1.
     * @throws IllegalArgumentException if jour, mois, or annee are out of their respective valid ranges.
     */
    public Journee(int jour, int mois, int annee) {
        if (jour < MIN_JOUR || jour > MAX_JOUR) {
            throw new IllegalArgumentException("Jour (day) must be between " + MIN_JOUR + " and " + MAX_JOUR + ". Received: " + jour);
        }
        if (mois < MIN_MOIS || mois > MAX_MOIS) {
            throw new IllegalArgumentException("Mois (month) must be between " + MIN_MOIS + " and " + MAX_MOIS + ". Received: " + mois);
        }
        if (annee < MIN_ANNEE) {
            // You might want a more practical lower bound, e.g., 1900, depending on your application.
            throw new IllegalArgumentException("Annee (year) must be greater than or equal to " + MIN_ANNEE + ". Received: " + annee);
        }
        this.jour = jour;
        this.mois = mois;
        this.annee = annee;
    }

    /**
     * Gets the day of the month.
     * @return The day.
     */
    public int getJour() {
        return jour;
    }

    /**
     * Sets the day of the month.
     * Note: This only checks if the day is between 1 and 31, not if it's valid for the current month/year.
     * @param jour The new day. Must be between 1 and 31.
     * @throws IllegalArgumentException if jour is out of the range 1-31.
     */
    public void setJour(int jour) {
        if (jour < MIN_JOUR || jour > MAX_JOUR) {
            throw new IllegalArgumentException("Jour (day) must be between " + MIN_JOUR + " and " + MAX_JOUR + ". Received: " + jour);
        }
        this.jour = jour;
    }

    /**
     * Gets the month of the year.
     * @return The month.
     */
    public int getMois() {
        return mois;
    }

    /**
     * Sets the month of the year.
     * Note: This only checks if the month is between 1 and 12.
     * @param mois The new month. Must be between 1 and 12.
     * @throws IllegalArgumentException if mois is out of the range 1-12.
     */
    public void setMois(int mois) {
         if (mois < MIN_MOIS || mois > MAX_MOIS) {
            throw new IllegalArgumentException("Mois (month) must be between " + MIN_MOIS + " and " + MAX_MOIS + ". Received: " + mois);
        }
        this.mois = mois;
    }

    /**
     * Gets the year.
     * @return The year.
     */
    public int getAnnee() {
        return annee;
    }

    /**
     * Sets the year.
     * Note: This only checks if the year is greater than or equal to 1.
     * @param annee The new year. Must be greater than or equal to 1.
     * @throws IllegalArgumentException if annee is less than 1.
     */
    public void setAnnee(int annee) {
        if (annee < MIN_ANNEE) {
            throw new IllegalArgumentException("Annee (year) must be greater than or equal to " + MIN_ANNEE + ". Received: " + annee);
        }
        this.annee = annee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journee journee = (Journee) o;
        return jour == journee.jour &&
               mois == journee.mois &&
               annee == journee.annee;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jour, mois, annee);
    }

    @Override
    public String toString() {
        return "Journee{" +
               "jour=" + jour +
               ", mois=" + mois +
               ", annee=" + annee +
               '}';
    }
}