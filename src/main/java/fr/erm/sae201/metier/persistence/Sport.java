package fr.erm.sae201.metier.persistence;

import java.util.Objects;

/**
 * Represents a type of sport.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet 
 * @version 1.0
 */
public class Sport {
    
    private String code;
    private String nom;

    /**
     * Constructs a new Sport.
     * @param code The unique code of the sport. Must not be null or empty.
     * @param nom The name of the sport. Must not be null or empty.
     * @throws IllegalArgumentException if code or nom is null or empty.
     */
    public Sport(String code, String nom) {
        setCode(code);
        setNom(nom);
    }

    /**
     * Gets the code of the sport.
     * @return The sport code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code of the sport.
     * @param code The new code. Must not be null or empty.
     * @throws IllegalArgumentException if code is null or empty.
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Sport code cannot be null or empty.");
        }
        this.code = code;
    }

    /**
     * Gets the name of the sport.
     * @return The sport name.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Sets the name of the sport.
     * @param nom The new name. Must not be null or empty.
     * @throws IllegalArgumentException if nom is null or empty.
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Sport name cannot be null or empty.");
        }
        this.nom = nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sport sport = (Sport) o;
        return Objects.equals(code, sport.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Sport{" +
               "code='" + code + '\'' +
               ", nom='" + nom + '\'' +
               '}';
    }
}