package fr.erm.sae201.metier.persistence;

import java.io.Serializable;
import java.util.Objects;

public class Utilisateur implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String login; // PK
    private String nom;   // Corresponds to 'nom' in DB, used as 'nomComplet' in view
    private String role;

    public Utilisateur() {
    }

    public Utilisateur(String login, String nom, String role) {
        setLogin(login);
        setNom(nom);
        setRole(role);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty.");
        }
        this.login = login;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        // Nom can be empty or null if allowed by DB
        this.nom = nom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        // Role can be empty or null if allowed by DB
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
               "login='" + login + '\'' +
               ", nom='" + nom + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}