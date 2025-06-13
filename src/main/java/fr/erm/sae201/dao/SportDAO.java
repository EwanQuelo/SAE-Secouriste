package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Sport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing {@link Sport} entities.
 * A {@link Sport} is identified by a unique 'code' (String) and has a 'nom'
 * (name).
 * This class handles CRUD (Create, Read, Update, Delete) operations for Sport
 * records.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class SportDAO extends DAO<Sport> {

    /**
     * Retrieves all {@link Sport} records from the database.
     *
     * @return A {@link List} of all {@link Sport} objects found.
     *         The list may be empty if no Sport records exist or if an error
     *         occurs.
     */
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
     * Finds a specific {@link Sport} by its unique 'code'.
     *
     * @param code The unique code of the Sport to find. Can be 'null' or empty.
     * @return The {@link Sport} object if found; 'null' if no Sport with the given
     *         code
     *         exists, if the provided 'code' is 'null' or empty, or if an error
     *         occurs.
     */
    public Sport findByCode(String code) {
        if (code == null || code.trim().isEmpty())
            return null;
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

    /**
     * Creates a new {@link Sport} record in the database.
     * The 'code' of the Sport serves as its primary key.
     *
     * @param sport The {@link Sport} object to persist. Its 'code' and 'nom' must
     *              be set.
     *              Neither the 'sport' object nor its 'code' can be 'null'.
     * @return The number of rows affected (typically 1 on success). Returns -1 if
     *         an
     *         {@link SQLException} occurs (e.g., if the code already exists due to
     *         primary key constraint) or if 'sport' or its 'code' is 'null'.
     * @throws IllegalArgumentException if 'sport' or its 'code' is 'null'.
     */
    @Override
    public int create(Sport sport) {
        if (sport == null || sport.getCode() == null)
            throw new IllegalArgumentException("Sport and its code cannot be null.");
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

    /**
     * Updates an existing {@link Sport}'s 'nom' (name) in the database.
     * The 'code' of the Sport is used to identify the record to update and cannot
     * be changed
     * by this method as it is the primary key.
     *
     * @param sport The {@link Sport} object with an updated 'nom'. Its 'code' must
     *              be set
     *              to identify the record. Neither 'sport' object nor its 'code'
     *              can be 'null'.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         code was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'sport' or its
     *         'code' is 'null'.
     * @throws IllegalArgumentException if 'sport' or its 'code' is 'null'.
     */
    @Override
    public int update(Sport sport) {
        if (sport == null || sport.getCode() == null)
            throw new IllegalArgumentException("Sport and its code cannot be null for updating.");
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

    /**
     * Deletes a {@link Sport} from the database using the provided {@link Sport}
     * object.
     * This method internally calls 'deleteByCode' using the 'code' from the 'sport'
     * object.
     * Note: This does not automatically handle related records (e.g., in 'DPS')
     * unless 'ON DELETE CASCADE' or 'ON DELETE SET NULL' is set up in the database
     * schema
     * for foreign keys referencing the Sport's code.
     *
     * @param sport The {@link Sport} object to delete. Its 'code' must be set.
     *              Neither 'sport' object nor its 'code' can be 'null'.
     * @return The number of rows affected.
     * @throws IllegalArgumentException if 'sport' or its 'code' is 'null'.
     */
    @Override
    public int delete(Sport sport) {
        if (sport == null || sport.getCode() == null)
            throw new IllegalArgumentException("Sport to delete must not be null.");
        return deleteByCode(sport.getCode());
    }

    /**
     * Deletes a {@link Sport} from the database by its unique 'code'.
     *
     * @param code The unique 'code' of the Sport to delete. Cannot be 'null' or
     *             empty.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         code was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'code' is invalid.
     * @throws IllegalArgumentException if 'code' is 'null' or empty.
     */
    public int deleteByCode(String code) {
        if (code == null || code.trim().isEmpty())
            throw new IllegalArgumentException("Code cannot be null or empty.");
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

    /**
     * Maps a row from a {@link ResultSet} to a {@link Sport} object.
     * Assumes the {@link ResultSet} contains columns 'code' and 'nom'.
     *
     * @param rs The {@link ResultSet} currently positioned at the row to map.
     * @return A new {@link Sport} object populated with data from the current row.
     * @throws SQLException If an error occurs while accessing the
     *                      {@link ResultSet}.
     */
    private Sport mapResultSetToSport(ResultSet rs) throws SQLException {
        return new Sport(
                rs.getString("code"),
                rs.getString("nom"));
    }
}