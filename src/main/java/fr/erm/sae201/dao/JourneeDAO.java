package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Journee;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing {@link Journee} entities.
 * A {@link Journee} (Day) represents a specific date, which serves as its primary key.
 * This class handles CRUD operations for Journee records.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class JourneeDAO extends DAO<Journee> {

    /**
     * Retrieves all {@link Journee} records from the database.
     * Each Journee is identified by a unique date.
     *
     * @return A {@link List} of all {@link Journee} objects found.
     *         The list may be empty if no Journee records exist or if an error occurs.
     */
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

    /**
     * Finds a specific {@link Journee} by its date.
     * The date acts as the primary key for a Journee.
     *
     * @param date The {@link LocalDate} to search for. Can be 'null'.
     * @return The {@link Journee} object if found; 'null' if no Journee with the given date
     *         exists, if the provided 'date' is 'null', or if an error occurs.
     */
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


    /**
     * Creates a new {@link Journee} record in the database.
     * The date of the Journee serves as its primary key.
     *
     * @param journee The {@link Journee} object to persist. Its date must be set.
     *                Must not be 'null'.
     * @return The number of rows affected (typically 1 on success). Returns -1 if an
     *         {@link SQLException} occurs (e.g., if the date already exists due to
     *         primary key constraint) or if 'journee' is 'null'.
     * @throws IllegalArgumentException if 'journee' is 'null'.
     */
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

    /**
     * Deletes a {@link Journee} record from the database based on its date
     *
     * @param journee The {@link Journee} object to delete. Its date must be set.
     *                Must not be 'null'.
     * @return The number of rows affected (1 if successful, 0 if no record with the date was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'journee' is 'null'.
     * @throws IllegalArgumentException if 'journee' is 'null'.
     */
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