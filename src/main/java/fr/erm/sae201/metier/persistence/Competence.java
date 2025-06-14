package fr.erm.sae201.metier.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a skill or competence, identified by its unique title (intitule).
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 2.0
 */
public class Competence {
    private String intitule;
    private Set<Competence> prerequisites;

    /**
     * Constructs a new Competence.
     * @param intitule The unique title of the competence. Must not be null or empty.
     */
    public Competence(String intitule) {
        // MODIFIÉ : Le constructeur n'accepte plus qu'un seul argument.
        setIntitule(intitule);
        this.prerequisites = new HashSet<>();
    }

    /**
     * Gets the title of the competence.
     * @return The competence title.
     */
    public String getIntitule() {
        return intitule;
    }

    /**
     * Sets the title of the competence.
     * @param intitule The new title. Must not be null or empty.
     * @throws IllegalArgumentException if intitule is null or empty.
     */
    public void setIntitule(String intitule) {
        if (intitule == null || intitule.trim().isEmpty()) {
            throw new IllegalArgumentException("Competence intitule cannot be null or empty.");
        }
        this.intitule = intitule;
    }

    // Les méthodes pour les prérequis ne changent pas.
    public Set<Competence> getPrerequisites() {
        return new HashSet<>(prerequisites);
    }
    public void setPrerequisites(Set<Competence> prerequisites) {
        if (prerequisites == null) {
            throw new IllegalArgumentException("Prerequisites set cannot be null.");
        }
        for (Competence c : prerequisites) {
            if (c == null) {
                throw new IllegalArgumentException("Prerequisite competence cannot be null.");
            }
        }
        this.prerequisites = new HashSet<>(prerequisites);
    }
    public void addPrerequisite(Competence competence) {
        if (competence == null) {
            throw new IllegalArgumentException("Prerequisite competence to add cannot be null.");
        }
        this.prerequisites.add(competence);
    }
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
        // MODIFIÉ : La comparaison se fait sur 'intitule'.
        return Objects.equals(intitule, that.intitule);
    }

    @Override
    public int hashCode() {
        // MODIFIÉ : Le hash code est basé sur 'intitule'.
        return Objects.hash(intitule);
    }

    @Override
    public String toString() {
        // MODIFIÉ : L'affichage est simplifié.
        return "Competence{intitule='" + intitule + "'}";
    }
}