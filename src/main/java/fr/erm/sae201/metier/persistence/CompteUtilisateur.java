package fr.erm.sae201.metier.persistence;

import java.io.Serializable;
import java.util.Objects;

public class CompteUtilisateur implements Serializable {

    private static final long serialVersionUID = 2L;

    private String login; // Email, clé primaire
    private String motDePasseHash;
    private Role role;
    private Long idSecouriste; // Peut être null (pour les administrateurs purs)

    public CompteUtilisateur(String login, String motDePasseHash, Role role, Long idSecouriste) {
        this.login = login;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
        this.idSecouriste = idSecouriste;
    }

    // --- Getters and Setters ---

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getIdSecouriste() {
        return idSecouriste;
    }

    public void setIdSecouriste(Long idSecouriste) {
        this.idSecouriste = idSecouriste;
    }

    // --- equals, hashCode, toString ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompteUtilisateur that = (CompteUtilisateur) o;
        return Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "CompteUtilisateur{" +
               "login='" + login + '\'' +
               ", role=" + role +
               ", idSecouriste=" + idSecouriste +
               '}';
    }
}