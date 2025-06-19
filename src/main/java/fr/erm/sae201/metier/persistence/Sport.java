package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Représente un type de sport.
 *
 * @author Raphael MILLE
 * @author Ewan QUELO
 * @author Matheo BIET
 * @version 1.0
 */
public class Sport {
    
    /** Le code unique du sport, servant de clé d'identification. */
    private String code;
    /** Le nom complet du sport. */
    private String nom;

    /**
     * Construit un nouveau Sport.
     *
     * @param code Le code unique du sport.
     * @param nom Le nom du sport.
     * @throws IllegalArgumentException si le code ou le nom est null ou vide.
     */
    public Sport(String code, String nom) {
        setCode(code);
        setNom(nom);
    }

    /**
     * Retourne le code du sport.
     *
     * @return Le code du sport.
     */
    public String getCode() {
        return code;
    }

    /**
     * Définit le code du sport.
     *
     * @param code Le nouveau code. Ne doit pas être null ou vide.
     * @throws IllegalArgumentException si le code est null ou vide.
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Sport code cannot be null or empty.");
        }
        this.code = code;
    }

    /**
     * Retourne le nom du sport.
     *
     * @return Le nom du sport.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du sport.
     *
     * @param nom Le nouveau nom. Ne doit pas être null ou vide.
     * @throws IllegalArgumentException si le nom est null ou vide.
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Sport name cannot be null or empty.");
        }
        this.nom = nom;
    }

    /**
     * Compare ce sport à un autre objet.
     * Deux sports sont considérés comme égaux si leurs codes sont identiques.
     *
     * @param o L'objet à comparer.
     * @return `true` si les objets sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sport sport = (Sport) o;
        return Objects.equals(code, sport.code);
    }

    /**
     * Génère un code de hachage pour le sport, basé sur son code.
     *
     * @return Un entier représentant le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * Retourne une représentation textuelle du sport.
     *
     * @return Une chaîne de caractères décrivant le sport.
     */
    @Override
    public String toString() {
        return "Sport{" +
               "code='" + code + '\'' +
               ", nom='" + nom + '\'' +
               '}';
    }
}