package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for managing {@link CompteUtilisateur} entities.
 * A 'CompteUtilisateur' (user account) stores login credentials (login, hashed
 * password),
 * a {@link Role}, and optionally a link to a 'Secouriste' ID if the account
 * belongs to a rescuer.
 * This class handles database operations such as creating, retrieving,
 * updating,
 * and deleting user accounts.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class CompteUtilisateurDAO extends DAO<CompteUtilisateur> {

    /**
     * Finds a {@link CompteUtilisateur} by its unique 'login' (username/email).
     *
     * @param login The login identifier of the user account to find.
     *              Should not be null or empty.
     * @return An {@link Optional} containing the {@link CompteUtilisateur} if
     *         found;
     *         an empty {@link Optional} if no account with the given 'login'
     *         exists,
     *         if 'login' is invalid, or if a database error occurs.
     */
    public Optional<CompteUtilisateur> findByLogin(String login) {
        if (login == null || login.trim().isEmpty())
            return Optional.empty();
        String sql = "SELECT login, motDePasseHash, role, idSecouriste FROM CompteUtilisateur WHERE login = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CompteUtilisateur(
                            rs.getString("login"),
                            rs.getString("motDePasseHash"),
                            Role.valueOf(rs.getString("role")),
                            (Long) rs.getObject("idSecouriste")));
                }
            }
        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de la recherche du CompteUtilisateur par login " + login + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Creates a new {@link CompteUtilisateur} record in the database.
     * The 'idSecouriste' field can be null if the account is not linked to a
     * specific rescuer.
     *
     * @param compte The {@link CompteUtilisateur} object to persist. Must not be
     *               'null'.
     *               Its 'login', 'motDePasseHash', and 'role' fields must be set.
     * @return The number of rows affected (typically 1 on success, or -1 if an
     *         SQLException occurs).
     * @throws IllegalArgumentException if 'compte' is 'null'.
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
     * Updates the 'motDePasseHash' (hashed password) for a user account identified
     * by its 'login'.
     *
     * @param login           The 'login' of the account whose password is to be
     *                        updated.
     * @param newPasswordHash The new hashed password.
     * @return The number of rows affected (typically 1 if the account exists and
     *         password was updated,
     *         0 if no account with that 'login' was found, or -1 if an SQLException
     *         occurs).
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
     * Updates attributes of a {@link CompteUtilisateur} other than the password.
     * Specifically, this method can update the 'role' and 'idSecouriste' of an
     * account.
     * The password should be updated using the 'updatePassword' method.
     *
     * @param compte The {@link CompteUtilisateur} object containing the new 'role'
     *               and/or 'idSecouriste'
     *               values. The 'login' field identifies the account to update.
     *               Must not be 'null'.
     * @return The number of rows affected (typically 1 if the account exists and
     *         was updated,
     *         0 if no account with that 'login' was found, or -1 if an SQLException
     *         occurs).
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
     * Deletes a {@link CompteUtilisateur} from the database.
     * The deletion is based on the 'login' of the provided account object.
     *
     * @param compte The {@link CompteUtilisateur} object to delete. If 'null', the
     *               method returns -1.
     *               The 'login' field must be set to identify the account.
     * @return The number of rows affected (typically 1 on success, 0 if no account
     *         with that 'login'
     *         was found, or -1 if 'compte' is 'null' or an SQLException occurs).
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

    @Override
    public List<CompteUtilisateur> findAll() {
        // Implémentation si nécessaire, souvent peu utile pour les comptes.
        return new ArrayList<>();
    }

    @Override
    public CompteUtilisateur findByID(Long id) {
        throw new UnsupportedOperationException("Utiliser findByLogin(String) pour cette classe.");
    }
}