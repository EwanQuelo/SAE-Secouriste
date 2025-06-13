package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Sport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Sport entity.
 * The primary key is a String 'code'.
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
                sports.add(mapResultSetToSport(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Sports: " + e.getMessage());
        }
        return sports;
    }

    /**
     * Finds a Sport by its unique code.
     * @param code The code of the sport to find.
     * @return The Sport object if found, otherwise null.
     */
    public Sport findByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        String sql = "SELECT code, nom FROM Sport WHERE code = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSport(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Sport by code " + code + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public int create(Sport sport) {
        if (sport == null || sport.getCode() == null) throw new IllegalArgumentException("Sport and its code cannot be null.");
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
        if (sport == null || sport.getCode() == null) throw new IllegalArgumentException("Sport and its code cannot be null for updating.");
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
        if (sport == null || sport.getCode() == null) throw new IllegalArgumentException("Sport to delete must not be null.");
        return deleteByCode(sport.getCode());
    }

    /**
     * Deletes a Sport by its unique code.
     * @param code The code of the Sport to delete.
     * @return The number of rows affected.
     */
    public int deleteByCode(String code) {
        if (code == null || code.trim().isEmpty()) throw new IllegalArgumentException("Code cannot be null or empty.");
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

    @Override
    public Sport findByID(Long id) {
        throw new UnsupportedOperationException("Sport ID is a String (code). Use findByCode(String).");
    }

    private Sport mapResultSetToSport(ResultSet rs) throws SQLException {
        return new Sport(
            rs.getString("code"),
            rs.getString("nom")
        );
    }
}