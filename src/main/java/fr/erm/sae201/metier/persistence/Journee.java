package fr.erm.sae201.metier.persistence;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Représente une journée spécifique en utilisant un objet LocalDate.
 * Cette classe correspond au type DATE en SQL.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 2.0
 */
public class Journee {
    /** La date qui définit cette journée. */
    private LocalDate date;

    /**
     * Construit une nouvelle Journee à partir d'un objet LocalDate.
     *
     * @param date La date spécifique. Ne peut pas être null.
     * @throws IllegalArgumentException si la date est null.
     */
    public Journee(LocalDate date) {
        setDate(date);
    }

    /**
     * Ancien constructeur pour créer une Journee à partir du jour, du mois et de l'année.
     * Crée en interne un objet LocalDate.
     *
     * @param jour  Le jour du mois.
     * @param mois  Le mois de l'année.
     * @param annee L'année.
     * @throws java.time.DateTimeException si la date est invalide.
     */
    public Journee(int jour, int mois, int annee) {
        this.date = LocalDate.of(annee, mois, jour);
    }

    /**
     * Retourne la date de la journée.
     *
     * @return L'objet LocalDate représentant la journée.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Définit la date de la journée.
     *
     * @param date La nouvelle date. Ne peut pas être null.
     * @throws IllegalArgumentException si la date est null.
     */
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.date = date;
    }

    /**
     * Compare cette journée à un autre objet.
     * Deux journées sont considérées comme égales si leurs dates sont identiques.
     *
     * @param o L'objet à comparer.
     * @return `true` si les objets sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journee journee = (Journee) o;
        return Objects.equals(date, journee.date);
    }

    /**
     * Génère un code de hachage pour la journée, basé sur sa date.
     *
     * @return Un entier représentant le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    /**
     * Retourne une représentation textuelle de la journée.
     *
     * @return Une chaîne de caractères décrivant la journée.
     */
    @Override
    public String toString() {
        return "Journee{" +
               "date=" + date +
               '}';
    }
}