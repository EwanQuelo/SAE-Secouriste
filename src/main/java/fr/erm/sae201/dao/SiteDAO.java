package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Site;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing {@link Site} entities.
 * A {@link Site} represents a geographical location, identified by a unique
 * 'code',
 * and includes a name, longitude, and latitude.
 * This class handles CRUD (Create, Read, Update, Delete) operations for Site
 * records.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class SiteDAO extends DAO<Site> {

    /**
     * Retrieves all {@link Site} records from the database.
     *
     * @return A {@link List} of all {@link Site} objects found.
     *         The list may be empty if no Site records exist or if an error occurs.
     */
    @Override
    public List<Site> findAll() {
        String sql = "SELECT code, nom, longitude, latitude FROM Site";
        List<Site> sites = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sites.add(mapResultSetToSite(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Sites: " + e.getMessage());
        }
        return sites;
    }

    /**
     * Finds a specific {@link Site} by its unique 'code'.
     *
     * @param code The unique code of the Site to find. Can be 'null' or empty.
     * @return The {@link Site} object if found; 'null' if no Site with the given
     *         code
     *         exists, if the provided 'code' is 'null' or empty, or if an error
     *         occurs.
     */
    public Site findByCode(String code) {
        if (code == null || code.trim().isEmpty())
            return null;
        String sql = "SELECT code, nom, longitude, latitude FROM Site WHERE code = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSite(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Site by code " + code + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates a new {@link Site} record in the database.
     * The 'code' of the Site serves as its primary key.
     *
     * @param site The {@link Site} object to persist. Its 'code', 'nom',
     *             'longitude',
     *             and 'latitude' must be set. Must not be 'null'.
     * @return The number of rows affected (typically 1 on success). Returns -1 if
     *         an
     *         {@link SQLException} occurs (e.g., if the code already exists due to
     *         primary key constraint) or if 'site' is 'null'.
     * @throws IllegalArgumentException if 'site' is 'null'.
     */
    @Override
    public int create(Site site) {
        if (site == null)
            throw new IllegalArgumentException("Site to create cannot be null.");
        String sql = "INSERT INTO Site (code, nom, longitude, latitude) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, site.getCode());
            pstmt.setString(2, site.getNom());
            pstmt.setFloat(3, site.getLongitude());
            pstmt.setFloat(4, site.getLatitude()); // Nom de colonne corrigé
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Site " + site.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Updates an existing {@link Site}'s details (name, longitude, latitude) in the
     * database.
     * The 'code' of the Site is used to identify the record to update and cannot be
     * changed
     * by this method.
     *
     * @param site The {@link Site} object with updated information. Its 'code' must
     *             be set.
     *             Must not be 'null'.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         code was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'site' is 'null'.
     * @throws IllegalArgumentException if 'site' is 'null'.
     */
    @Override
    public int update(Site site) {
        if (site == null)
            throw new IllegalArgumentException("Site to update cannot be null.");
        String sql = "UPDATE Site SET nom = ?, longitude = ?, latitude = ? WHERE code = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, site.getNom());
            pstmt.setFloat(2, site.getLongitude());
            pstmt.setFloat(3, site.getLatitude()); // Nom de colonne corrigé
            pstmt.setString(4, site.getCode());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Site " + site.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Deletes a {@link Site} record from the database based on its 'code'.
     * Note: This does not automatically handle related records (e.g., in 'DPS')
     * unless 'ON DELETE CASCADE' is set up in the database schema.
     *
     * @param site The {@link Site} object to delete. Its 'code' must be set.
     *             Must not be 'null'.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         code was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'site' is 'null'.
     * @throws IllegalArgumentException if 'site' is 'null'.
     */
    @Override
    public int delete(Site site) {
        if (site == null)
            throw new IllegalArgumentException("Site to delete cannot be null.");
        String sql = "DELETE FROM Site WHERE code = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, site.getCode());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Site " + site.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public Site findByID(Long id) {
        throw new UnsupportedOperationException("Site ID is String (code). Use findByCode(String).");
    }

    /**
     * Maps a row from a {@link ResultSet} to a {@link Site} object.
     * Assumes the {@link ResultSet} contains columns 'code', 'nom', 'longitude',
     * and 'latitude'.
     *
     * @param rs The {@link ResultSet} currently positioned at the row to map.
     * @return A new {@link Site} object populated with data from the current row.
     * @throws SQLException If an error occurs while accessing the
     *                      {@link ResultSet}.
     */
    private Site mapResultSetToSite(ResultSet rs) throws SQLException {
        return new Site(
                rs.getString("code"),
                rs.getString("nom"),
                rs.getFloat("longitude"),
                rs.getFloat("latitude"));
    }
}