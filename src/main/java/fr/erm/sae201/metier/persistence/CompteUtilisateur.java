package fr.erm.sae201.metier.persistence;

import java.io.Serializable;
import java.util.Objects;

/**
 * Représente un compte utilisateur dans le système.
 * <p>
 * Cette classe contient les informations d'authentification et de rôle
 * d'un utilisateur, qu'il soit secouriste ou administrateur.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class CompteUtilisateur implements Serializable {

    private static final long serialVersionUID = 2L;

    /** L'email de l'utilisateur, servant de login et de clé primaire. */
    private String login;

    /** Le mot de passe de l'utilisateur, stocké sous forme de hachage. */
    private String motDePasseHash;

    /** Le rôle de l'utilisateur (ex: SECOURISTE, ADMINISTRATEUR). */
    private Role role;

    /** L'ID du secouriste associé à ce compte, peut être null pour les administrateurs. */
    private Long idSecouriste;

    /**
     * Construit un nouveau CompteUtilisateur.
     *
     * @param login          L'email de l'utilisateur.
     * @param motDePasseHash Le mot de passe haché.
     * @param role           Le rôle de l'utilisateur.
     * @param idSecouriste   L'ID du profil secouriste associé (peut être null).
     */
    public CompteUtilisateur(String login, String motDePasseHash, Role role, Long idSecouriste) {
        this.login = login;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
        this.idSecouriste = idSecouriste;
    }

    /**
     * Retourne le login (email) de l'utilisateur.
     *
     * @return Le login de l'utilisateur.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Définit le login (email) de l'utilisateur.
     *
     * @param login Le nouveau login.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Retourne le mot de passe haché.
     *
     * @return Le mot de passe haché.
     */
    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    /**
     * Définit le mot de passe haché.
     *
     * @param motDePasseHash Le nouveau mot de passe haché.
     */
    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    /**
     * Retourne le rôle de l'utilisateur.
     *
     * @return Le rôle de l'utilisateur.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Définit le rôle de l'utilisateur.
     *
     * @param role Le nouveau rôle.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Retourne l'ID du secouriste associé.
     *
     * @return L'ID du secouriste, ou null s'il n'y en a pas.
     */
    public Long getIdSecouriste() {
        return idSecouriste;
    }

    /**
     * Définit l'ID du secouriste associé.
     *
     * @param idSecouriste Le nouvel ID du secouriste.
     */
    public void setIdSecouriste(Long idSecouriste) {
        this.idSecouriste = idSecouriste;
    }

    /**
     * Compare ce compte à un autre objet.
     * Deux comptes sont considérés comme égaux si leurs logins sont identiques.
     *
     * @param o L'objet à comparer.
     * @return `true` si les objets sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompteUtilisateur that = (CompteUtilisateur) o;
        return Objects.equals(login, that.login);
    }

    /**
     * Génère un code de hachage pour le compte, basé sur son login.
     *
     * @return Un entier représentant le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    /**
     * Retourne une représentation textuelle du compte utilisateur.
     *
     * @return Une chaîne de caractères décrivant le compte.
     */
    @Override
    public String toString() {
        return "CompteUtilisateur{" +
               "login='" + login + '\'' +
               ", role=" + role +
               ", idSecouriste=" + idSecouriste +
               '}';
    }
}