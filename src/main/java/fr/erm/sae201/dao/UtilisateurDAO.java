package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for the Utilisateur entity.
 * Assumes table structure: Utilisateur (login VARCHAR PK, nom VARCHAR, role VARCHAR)
 * Example data:
 * INSERT INTO Utilisateur (login, nom, role) VALUES ('ewanquelo', 'Ewan QUELO', 'DIEU');
 * INSERT INTO Utilisateur (login, nom, role) VALUES ('nchalolo', 'Nolan CHALOLO', 'PSET');
 */
public class UtilisateurDAO extends DAO<Utilisateur> {

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT login, nom, role FROM Utilisateur";
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(
                        rs.getString("login"),
                        rs.getString("nom"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Utilisateurs: " + e.getMessage());
        }
        return utilisateurs;
    }

    /**
     * {@inheritDoc}
     * This method is not applicable for Utilisateur as its ID is a String (login), not Long.
     * Use findByLogin(String login) instead.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public Utilisateur findByID(Long id) {
        throw new UnsupportedOperationException("Utilisateur ID is String (login), not Long. Use findByLogin(String).");
    }

    public Optional<Utilisateur> findByLogin(String login) {
        if (login == null || login.trim().isEmpty()) return Optional.empty();
        String sql = "SELECT login, nom, role FROM Utilisateur WHERE login = ?";
        Utilisateur utilisateur = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    utilisateur = new Utilisateur(
                            rs.getString("login"),
                            rs.getString("nom"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Utilisateur by login " + login + ": " + e.getMessage());
        }
        return Optional.ofNullable(utilisateur);
    }

    @Override
    public int create(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getLogin() == null || utilisateur.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Utilisateur or its login cannot be null or empty for creation.");
        }
        String sql = "INSERT INTO Utilisateur (login, nom, role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getLogin());
            pstmt.setString(2, utilisateur.getNom());
            pstmt.setString(3, utilisateur.getRole());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Utilisateur " + utilisateur.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int update(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getLogin() == null || utilisateur.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Utilisateur or its login cannot be null or empty for update.");
        }
        // Note: Updating the primary key (login) is generally not done.
        // This method updates other fields based on the login.
        String sql = "UPDATE Utilisateur SET nom = ?, role = ? WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getRole());
            pstmt.setString(3, utilisateur.getLogin());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Utilisateur " + utilisateur.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int delete(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getLogin() == null || utilisateur.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Utilisateur or its login cannot be null or empty for deletion.");
        }
        return deleteByLogin(utilisateur.getLogin());
    }

    public int deleteByLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty for deleting.");
        }
        String sql = "DELETE FROM Utilisateur WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Utilisateur by login " + login + ": " + e.getMessage());
            return -1;
        }
    }
}