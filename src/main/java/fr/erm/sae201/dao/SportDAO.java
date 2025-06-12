package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Sport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Sport entity, extending the generic DAO.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.2
 */
public class SportDAO extends DAO<Sport> {

    @Override
    public List<Sport> findAll() {
        String sql = "SELECT code, nom FROM Sport";
        List<Sport> sports = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sports.add(new Sport(
                        rs.getString("code"),
                        rs.getString("nom")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Sports: " + e.getMessage());
        }
        return sports;
    }

    /**
     * {@inheritDoc}
     * Not applicable for Sport as its ID is String (code).
     * Use findByCode(String code) instead.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public Sport findByID(Long id) {
        throw new UnsupportedOperationException("Sport ID is String (code). Use findByCode(String).");
    }

    /**
     * Finds a Sport by its code.
     * @param code The code of the sport.
     * @return The Sport object if found, otherwise null.
     */
    public Sport findByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        String sql = "SELECT code, nom FROM Sport WHERE code = ?";
        Sport sport = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    sport = new Sport(
                            rs.getString("code"),
                            rs.getString("nom")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Sport by code " + code + ": " + e.getMessage());
        }
        return sport;
    }

    @Override
    public int create(Sport sport) {
        if (sport == null) {
            throw new IllegalArgumentException("Sport to create cannot be null.");
        }
        String sql = "INSERT INTO Sport (code, nom) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sport.getCode());
            pstmt.setString(2, sport.getNom());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Sport " + sport.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int update(Sport sport) {
        if (sport == null || sport.getCode() == null || sport.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Sport or its code cannot be null for updating.");
        }
        String sql = "UPDATE Sport SET nom = ? WHERE code = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sport.getNom());
            pstmt.setString(2, sport.getCode());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Sport " + sport.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int delete(Sport sport) {
        if (sport == null || sport.getCode() == null || sport.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Sport or its code cannot be null for deleting.");
        }
        String sql = "DELETE FROM Sport WHERE code = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sport.getCode());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Sport " + sport.getCode() + ": " + e.getMessage());
            return -1;
        }
    }
    
    public int deleteByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty for deleting Sport.");
        }
        String sql = "DELETE FROM Sport WHERE code = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Sport by code " + code + ": " + e.getMessage());
            return -1;
        }
    }
}