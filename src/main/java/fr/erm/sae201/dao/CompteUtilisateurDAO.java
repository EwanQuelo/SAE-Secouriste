package fr.erm.sae201.dao;

import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des entités CompteUtilisateur.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class CompteUtilisateurDAO extends DAO<CompteUtilisateur> {

    /**
     * Recherche un CompteUtilisateur par son 'login' unique (email).
     *
     * @param login L'identifiant de connexion du compte à trouver.
     * @return Le CompteUtilisateur s'il est trouvé.
     * @throws EntityNotFoundException si aucun compte avec ce login n'existe.
     * @throws IllegalArgumentException si le login est null ou vide.
     * @throws RuntimeException si une erreur SQL survient.
     */
    public CompteUtilisateur findByLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty.");
        }
        String sql = "SELECT login, motDePasseHash, role, idSecouriste FROM CompteUtilisateur WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new CompteUtilisateur(
                            rs.getString("login"),
                            rs.getString("motDePasseHash"),
                            Role.valueOf(rs.getString("role")),
                            (Long) rs.getObject("idSecouriste"));
                } else {
                    throw new EntityNotFoundException("Aucun compte utilisateur trouvé avec le login : " + login);
                }
            }
        } catch (SQLException e) {
            // "Encapsule" l'erreur SQL dans une RuntimeException pour ne pas
            // forcer la gestion de l'exception dans les couches supérieures.
            throw new RuntimeException("Erreur BDD lors de la recherche du compte : " + login, e);
        }
    }

    /**
     * Crée un nouvel enregistrement de CompteUtilisateur dans la base de données.
     * Le champ 'idSecouriste' peut être null si le compte n'est pas lié à un secouriste.
     *
     * @param compte L'objet CompteUtilisateur à persister. Ne doit pas être null.
     * @return Le nombre de lignes affectées (1 en cas de succès, -1 en cas d'erreur).
     * @throws IllegalArgumentException si l'objet compte est null.
     */
    @Override
    public int create(CompteUtilisateur compte) {
        if (compte == null)
            throw new IllegalArgumentException("CompteUtilisateur ne peut pas être null.");
        String sql = "INSERT INTO CompteUtilisateur (login, motDePasseHash, role, idSecouriste) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, compte.getLogin());
            pstmt.setString(2, compte.getMotDePasseHash());
            pstmt.setString(3, compte.getRole().name());
            if (compte.getIdSecouriste() != null) {
                pstmt.setLong(4, compte.getIdSecouriste());
            } else {
                pstmt.setNull(4, Types.BIGINT);
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de la création du CompteUtilisateur " + compte.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Met à jour le mot de passe hashé pour un compte utilisateur identifié par son login.
     *
     * @param login           Le login du compte à mettre à jour.
     * @param newPasswordHash Le nouveau mot de passe hashé.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     */
    public int updatePassword(String login, String newPasswordHash) {
        String sql = "UPDATE CompteUtilisateur SET motDePasseHash = ? WHERE login = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, login);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe pour " + login + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Met à jour les attributs d'un CompteUtilisateur (hors mot de passe).
     * Peut modifier le rôle et l'idSecouriste associé.
     *
     * @param compte L'objet CompteUtilisateur contenant les nouvelles valeurs.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     */
    @Override
    public int update(CompteUtilisateur compte) {
        String sql = "UPDATE CompteUtilisateur SET role = ?, idSecouriste = ? WHERE login = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, compte.getRole().name());
            if (compte.getIdSecouriste() != null) {
                pstmt.setLong(2, compte.getIdSecouriste());
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            pstmt.setString(3, compte.getLogin());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de la mise à jour du CompteUtilisateur " + compte.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Supprime un CompteUtilisateur de la base de données en se basant sur le login.
     *
     * @param compte L'objet CompteUtilisateur à supprimer.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     */
    @Override
    public int delete(CompteUtilisateur compte) {
        if (compte == null)
            return -1;
        String sql = "DELETE FROM CompteUtilisateur WHERE login = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, compte.getLogin());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de la suppression du CompteUtilisateur " + compte.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Non implémenté. Retourne une liste vide.
     * La récupération de tous les comptes n'est pas un besoin actuel de l'application.
     */
    @Override
    public List<CompteUtilisateur> findAll() {
        return new ArrayList<>();
    }

    /**
     * Non supporté. Utilisez `findByLogin(String)` pour cette classe.
     */
    @Override
    public CompteUtilisateur findByID(Long id) {
        throw new UnsupportedOperationException("Utiliser findByLogin(String) pour cette classe.");
    }
}