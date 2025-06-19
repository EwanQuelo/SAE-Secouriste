package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Représente l'affectation d'un secouriste à un DPS pour une compétence spécifique.
 * 
 * Cette classe correspond à un enregistrement de la table de jointure 'Affectation',
 * liant un DPS, un Secouriste et une Compétence.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class Affectation {

    /** Le Dispositif Prévisionnel de Secours (DPS) de l'affectation. */
    private DPS dps;

    /** Le secouriste affecté. */
    private Secouriste secouriste;

    /** La compétence que le secouriste utilise pour ce poste. */
    private Competence competence;

    /**
     * Construit une nouvelle affectation.
     *
     * @param dps        Le DPS pour lequel l'affectation est faite. Ne peut pas être null.
     * @param secouriste Le secouriste qui est affecté. Ne peut pas être null.
     * @param competence La compétence spécifique que le secouriste remplit. Ne peut pas être null.
     * @throws IllegalArgumentException si l'un des paramètres est null.
     */
    public Affectation(DPS dps, Secouriste secouriste, Competence competence) {
        if (dps == null || secouriste == null || competence == null) {
            throw new IllegalArgumentException("DPS, Secouriste, and Competence for an Affectation cannot be null.");
        }
        this.dps = dps;
        this.secouriste = secouriste;
        this.competence = competence;
    }

    /**
     * Retourne le DPS de l'affectation.
     *
     * @return Le DPS de l'affectation.
     */
    public DPS getDps() {
        return dps;
    }

    /**
     * Retourne le secouriste de l'affectation.
     *
     * @return Le secouriste de l'affectation.
     */
    public Secouriste getSecouriste() {
        return secouriste;
    }

    /**
     * Retourne la compétence de l'affectation.
     *
     * @return La compétence de l'affectation.
     */
    public Competence getCompetence() {
        return competence;
    }

    /**
     * Compare cette affectation à un autre objet pour vérifier l'égalité.
     * Deux affectations sont égales si leur DPS, leur secouriste et leur
     * compétence sont identiques.
     *
     * @param o L'objet à comparer avec cette affectation.
     * @return `true` si les objets sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Affectation that = (Affectation) o;
        return Objects.equals(dps, that.dps) &&
               Objects.equals(secouriste, that.secouriste) &&
               Objects.equals(competence, that.competence);
    }

    /**
     * Génère un code de hachage pour l'affectation.
     * Le code est basé sur les objets DPS, Secouriste et Competence.
     *
     * @return Un entier représentant le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dps, secouriste, competence);
    }

    /**
     * Retourne une représentation textuelle de l'affectation.
     *
     * @return Une chaîne de caractères décrivant l'affectation.
     */
    @Override
    public String toString() {
        return "Affectation{" +
               "dpsId=" + dps.getId() +
               ", secouristeId=" + secouriste.getId() +
               ", competence=" + competence.getIntitule() +
               '}';
    }
}