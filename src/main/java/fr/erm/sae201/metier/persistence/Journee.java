package fr.erm.sae201.metier.persistence;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a specific day using java.time.LocalDate, matching the DATE SQL type.
 * @author L'IA qui corrige
 * @version 2.0
 */
public class Journee {
    private LocalDate date;

    /**
     * Constructs a new Journee from a LocalDate object.
     * @param date The specific date. Must not be null.
     * @throws IllegalArgumentException if date is null.
     */
    public Journee(LocalDate date) {
        setDate(date);
    }

    /**
     * Legacy constructor for creating a Journee from day, month, and year.
     * This now internally creates a LocalDate object.
     * @param jour The day of the month.
     * @param mois The month of the year.
     * @param annee The year.
     * @throws java.time.DateTimeException if the date is invalid.
     */
    public Journee(int jour, int mois, int annee) {
        this.date = LocalDate.of(annee, mois, jour);
    }

    /**
     * Gets the date.
     * @return The LocalDate object representing the day.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date.
     * @param date The new date. Must not be null.
     * @throws IllegalArgumentException if date is null.
     */
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journee journee = (Journee) o;
        return Objects.equals(date, journee.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "Journee{" +
               "date=" + date +
               '}';
    }
}