package fr.erm.sae201.metier.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Représente une compétence, identifiée par son titre unique (intitulé).
 *
 * @author Raphael MILLE
 * @author Ewan QUELO
 * @author Matheo BIET
 * @version 2.0
 */
public class Competence {
    /** L'intitulé unique de la compétence, servant de clé d'identification. */
    private String intitule;

    /** L'ensemble des compétences requises comme prérequis pour acquérir celle-ci. */
    private Set<Competence> prerequisites;

    /**
     * Construit une nouvelle Compétence.
     *
     * @param intitule L'intitulé unique de la compétence. Ne peut pas être null ou vide.
     */
    public Competence(String intitule) {
        setIntitule(intitule);
        this.prerequisites = new HashSet<>();
    }

    /**
     * Retourne l'intitulé de la compétence.
     *
     * @return L'intitulé de la compétence.
     */
    public String getIntitule() {
        return intitule;
    }

    /**
     * Définit l'intitulé de la compétence.
     *
     * @param intitule Le nouvel intitulé. Ne peut pas être null ou vide.
     * @throws IllegalArgumentException si l'intitulé est null ou vide.
     */
    public void setIntitule(String intitule) {
        if (intitule == null || intitule.trim().isEmpty()) {
            throw new IllegalArgumentException("Competence intitule cannot be null or empty.");
        }
        this.intitule = intitule;
    }

    /**
     * Retourne une copie de l'ensemble des prérequis de cette compétence.
     *
     * @return Un ensemble de compétences prérequises.
     */
    public Set<Competence> getPrerequisites() {
        return new HashSet<>(prerequisites);
    }

    /**
     * Définit l'ensemble des prérequis pour cette compétence.
     *
     * @param prerequisites L'ensemble des nouvelles compétences prérequises.
     * @throws IllegalArgumentException si l'ensemble ou l'une de ses compétences est null.
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
        this.prerequisites = new HashSet<>(prerequisites);
    }

    /**
     * Ajoute une compétence à la liste des prérequis.
     *
     * @param competence La compétence prérequise à ajouter.
     * @throws IllegalArgumentException si la compétence est null.
     */
    public void addPrerequisite(Competence competence) {
        if (competence == null) {
            throw new IllegalArgumentException("Prerequisite competence to add cannot be null.");
        }
        this.prerequisites.add(competence);
    }

    /**
     * Supprime une compétence de la liste des prérequis.
     *
     * @param competence La compétence prérequise à supprimer.
     * @return `true` si la compétence a été supprimée, `false` sinon.
     */
    public boolean removePrerequisite(Competence competence) {
        if (competence == null) {
            return false;
        }
        return this.prerequisites.remove(competence);
    }

    /**
     * Compare cette compétence à un autre objet.
     * Deux compétences sont considérées comme égales si leurs intitulés sont identiques.
     *
     * @param o L'objet à comparer.
     * @return `true` si les objets sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competence that = (Competence) o;
        return Objects.equals(intitule, that.intitule);
    }

    /**
     * Génère un code de hachage pour la compétence, basé sur son intitulé.
     *
     * @return Un entier représentant le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(intitule);
    }

    /**
     * Retourne une représentation textuelle de la compétence.
     *
     * @return Une chaîne de caractères décrivant la compétence.
     */
    @Override
    public String toString() {
        return "Competence{intitule='" + intitule + "'}";
    }
}