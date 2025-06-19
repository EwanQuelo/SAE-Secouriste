package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Représente un site géographique.
 *
 * @author Raphael MILLE
 * @author Ewan QUELO
 * @author Matheo BIET
 * @version 1.0
 */
public class Site {
    /** Le code unique du site, servant de clé d'identification. */
    private String code;
    /** Le nom complet du site. */
    private String nom;
    /** La coordonnée de longitude du site. */
    private float longitude;
    /** La coordonnée de latitude du site. */
    private float latitude;

    /**
     * Construit un nouveau Site.
     *
     * @param code Le code unique du site.
     * @param nom Le nom du site.
     * @param longitude La longitude du site.
     * @param latitude La latitude du site.
     * @throws IllegalArgumentException si le code ou le nom est null ou vide.
     */
    public Site(String code, String nom, float longitude, float latitude) {
        setCode(code);
        setNom(nom);
        setLongitude(longitude);
        setLatitude(latitude);
    }

    /**
     * Retourne le code du site.
     *
     * @return Le code du site.
     */
    public String getCode() {
        return code;
    }

    /**
     * Définit le code du site.
     *
     * @param code Le nouveau code. Ne doit pas être null ou vide.
     * @throws IllegalArgumentException si le code est null ou vide.
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Site code cannot be null or empty.");
        }
        this.code = code;
    }

    /**
     * Retourne le nom du site.
     *
     * @return Le nom du site.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du site.
     *
     * @param nom Le nouveau nom. Ne doit pas être null ou vide.
     * @throws IllegalArgumentException si le nom est null ou vide.
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Site name cannot be null or empty.");
        }
        this.nom = nom;
    }

    /**
     * Retourne la longitude du site.
     *
     * @return La longitude.
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Définit la longitude du site.
     *
     * @param longitude La nouvelle longitude.
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Retourne la latitude du site.
     *
     * @return La latitude.
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Définit la latitude du site.
     *
     * @param latitude La nouvelle latitude.
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Compare ce site à un autre objet.
     * Deux sites sont considérés comme égaux si leurs codes sont identiques.
     *
     * @param o L'objet à comparer.
     * @return `true` si les objets sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return Objects.equals(code, site.code);
    }

    /**
     * Génère un code de hachage pour le site, basé sur son code.
     *
     * @return Un entier représentant le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * Retourne une représentation textuelle du site.
     *
     * @return Une chaîne de caractères décrivant le site.
     */
    @Override
    public String toString() {
        return "Site{" +
               "code='" + code + '\'' +
               ", nom='" + nom + '\'' +
               ", longitude=" + longitude +
               ", latitude=" + latitude +
               '}';
    }
}