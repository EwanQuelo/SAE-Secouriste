package fr.erm.sae201.metier.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Competence {
    private String intitule;
    // AJOUT : Pour stocker les prérequis en mémoire
    private Set<Competence> prerequisites;

    public Competence(String intitule) {
        setIntitule(intitule);
        // AJOUT : Initialiser l'ensemble
        this.prerequisites = new HashSet<>();
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        if (intitule == null || intitule.trim().isEmpty()) {
            throw new IllegalArgumentException("Competence intitule cannot be null or empty.");
        }
        this.intitule = intitule;
    }

    // --- AJOUT : Méthodes pour gérer les prérequis ---
    public Set<Competence> getPrerequisites() {
        return new HashSet<>(prerequisites); // Retourne une copie pour protéger l'ensemble interne
    }

    public void setPrerequisites(Set<Competence> prerequisites) {
        if (prerequisites == null) {
            this.prerequisites = new HashSet<>();
        } else {
            this.prerequisites = new HashSet<>(prerequisites);
        }
    }

    public void addPrerequisite(Competence competence) {
        if (competence != null) {
            this.prerequisites.add(competence);
        }
    }
    // --- Fin des ajouts ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competence that = (Competence) o;
        return Objects.equals(intitule, that.intitule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intitule);
    }

    @Override
    public String toString() {
        return "Competence{intitule='" + intitule + "'}";
    }
}