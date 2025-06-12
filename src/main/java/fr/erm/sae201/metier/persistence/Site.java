package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Represents a geographical site.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet 
 * @version 1.0
 */
public class Site {
    private String code;
    private String nom;
    private float longitude;
    private float latitude; // Corrected from lattitude

    /**
     * Constructs a new Site.
     * @param code The unique code of the site. Must not be null or empty.
     * @param nom The name of the site. Must not be null or empty.
     * @param longitude The longitude of the site.
     * @param latitude The latitude of the site.
     * @throws IllegalArgumentException if code or nom is null or empty.
     */
    public Site(String code, String nom, float longitude, float latitude) {
        setCode(code);
        setNom(nom);
        setLongitude(longitude); // Setters handle validation if any (not strictly needed for float here)
        setLatitude(latitude);
    }

    /**
     * Gets the code of the site.
     * @return The site code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code of the site.
     * @param code The new code. Must not be null or empty.
     * @throws IllegalArgumentException if code is null or empty.
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Site code cannot be null or empty.");
        }
        this.code = code;
    }

    /**
     * Gets the name of the site.
     * @return The site name.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Sets the name of the site.
     * @param nom The new name. Must not be null or empty.
     * @throws IllegalArgumentException if nom is null or empty.
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Site name cannot be null or empty.");
        }
        this.nom = nom;
    }

    /**
     * Gets the longitude of the site.
     * @return The site longitude.
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the site.
     * @param longitude The new longitude.
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the latitude of the site.
     * @return The site latitude.
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the site.
     * @param latitude The new latitude.
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return Objects.equals(code, site.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

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