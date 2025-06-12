package fr.erm.sae201.metier.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a skill or competence.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.0
 */
public class Competence {
    private String code;
    private String nom;
    private Set<Competence> prerequisites; // For Necessite (Competence 0..* - Competence 0..*)

    /**
     * Constructs a new Competence.
     * @param code The unique code of the competence. Must not be null or empty.
     * @param nom The name of the competence. Must not be null or empty.
     */
    public Competence(String code, String nom) {
        setCode(code);
        setNom(nom);
        this.prerequisites = new HashSet<>(); // Initialize to avoid nulls later
    }

    /**
     * Gets the code of the competence.
     * @return The competence code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code of the competence.
     * @param code The new code. Must not be null or empty.
     * @throws IllegalArgumentException if code is null or empty.
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Competence code cannot be null or empty.");
        }
        this.code = code;
    }

    /**
     * Gets the name of the competence.
     * @return The competence name.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Sets the name of the competence.
     * @param nom The new name. Must not be null or empty.
     * @throws IllegalArgumentException if nom is null or empty.
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Competence name cannot be null or empty.");
        }
        this.nom = nom;
    }

    /**
     * Gets a copy of the set of prerequisite competences.
     * @return A new set containing prerequisite competences.
     */
    public Set<Competence> getPrerequisites() {
        return new HashSet<>(prerequisites); // Return a copy
    }

    /**
     * Sets the prerequisite competences.
     * The provided set is copied.
     * @param prerequisites The set of prerequisite competences. Must not be null. Elements cannot be null.
     * @throws IllegalArgumentException if prerequisites set is null or contains null elements.
     */
    public void setPrerequisites(Set<Competence> prerequisites) {
        if (prerequisites == null) {
            throw new IllegalArgumentException("Prerequisites set cannot be null.");
        }
        for (Competence c : prerequisites) {
            if (c == null) {
                throw new IllegalArgumentException("Prerequisite competence cannot be null.");
            }
        }
        this.prerequisites = new HashSet<>(prerequisites); // Store a copy
    }

    /**
     * Adds a prerequisite competence.
     * @param competence The prerequisite to add. Must not be null.
     * @throws IllegalArgumentException if competence is null.
     */
    public void addPrerequisite(Competence competence) {
        if (competence == null) {
            throw new IllegalArgumentException("Prerequisite competence to add cannot be null.");
        }
        this.prerequisites.add(competence);
    }

    /**
     * Removes a prerequisite competence.
     * @param competence The prerequisite to remove.
     * @return true if the prerequisite was removed, false otherwise.
     */
    public boolean removePrerequisite(Competence competence) {
        if (competence == null) {
            return false;
        }
        return this.prerequisites.remove(competence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competence that = (Competence) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Competence{" +
               "code='" + code + '\'' +
               ", nom='" + nom + '\'' +
               '}';
    }
}