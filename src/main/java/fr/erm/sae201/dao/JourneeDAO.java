package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Journee;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JourneeDAO extends DAO<Journee> {

    @Override
    public List<Journee> findAll() {
        String sql = "SELECT jour FROM Journee";
        List<Journee> journees = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                journees.add(new Journee(rs.getDate("jour").toLocalDate()));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Journees: " + e.getMessage());
        }
        return journees;
    }

    public Journee findByDate(LocalDate date) {
        if (date == null) return null;
        String sql = "SELECT jour FROM Journee WHERE jour = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Journee(rs.getDate("jour").toLocalDate());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Journee by date " + date + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public int create(Journee journee) {
        if (journee == null) throw new IllegalArgumentException("Journee to create cannot be null.");
        String sql = "INSERT INTO Journee (jour) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(journee.getDate()));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            // Peut échouer si la date existe déjà (clé primaire)
            System.err.println("Error creating Journee " + journee + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int delete(Journee journee) {
        if (journee == null) throw new IllegalArgumentException("Journee to delete cannot be null.");
        String sql = "DELETE FROM Journee WHERE jour = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(journee.getDate()));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Journee " + journee + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public Journee findByID(Long id) {
        throw new UnsupportedOperationException("Journee ID is a LocalDate, not a Long. Use findByDate(LocalDate).");
    }

    @Override
    public int update(Journee element) {
        throw new UnsupportedOperationException("Updating a primary key (jour) is not recommended. Use delete then create.");
    }
}