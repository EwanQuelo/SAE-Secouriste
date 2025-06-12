package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Site;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Site entity, extending the generic DAO.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.2
 */
public class SiteDAO extends DAO<Site> {

    @Override
    public List<Site> findAll() {
        String sql = "SELECT code, nom, longitude, lattitude FROM Site"; // lattitude from SQL
        List<Site> sites = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sites.add(new Site(
                        rs.getString("code"),
                        rs.getString("nom"),
                        rs.getFloat("longitude"),
                        rs.getFloat("lattitude") // SQL uses 'lattitude', POJO Site needs 'latitude'
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Sites: " + e.getMessage());
        }
        return sites;
    }

    /**
     * {@inheritDoc}
     * Not applicable for Site as its ID is String (code).
     * Use findByCode(String code) instead.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public Site findByID(Long id) {
        throw new UnsupportedOperationException("Site ID is String (code). Use findByCode(String).");
    }

    /**
     * Finds a Site by its code.
     * @param code The code of the site.
     * @return The Site object if found, otherwise null.
     */
    public Site findByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        String sql = "SELECT code, nom, longitude, lattitude FROM Site WHERE code = ?";
        Site site = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    site = new Site(
                            rs.getString("code"),
                            rs.getString("nom"),
                            rs.getFloat("longitude"),
                            rs.getFloat("lattitude")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Site by code " + code + ": " + e.getMessage());
        }
        return site;
    }

    @Override
    public int create(Site site) {
        if (site == null) {
            throw new IllegalArgumentException("Site to create cannot be null.");
        }
        String sql = "INSERT INTO Site (code, nom, longitude, lattitude) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, site.getCode());
            pstmt.setString(2, site.getNom());
            pstmt.setFloat(3, site.getLongitude());
            pstmt.setFloat(4, site.getLatitude()); // POJO Site uses 'latitude'

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Site " + site.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int update(Site site) {
        if (site == null || site.getCode() == null || site.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Site or its code cannot be null for updating.");
        }
        String sql = "UPDATE Site SET nom = ?, longitude = ?, lattitude = ? WHERE code = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, site.getNom());
            pstmt.setFloat(2, site.getLongitude());
            pstmt.setFloat(3, site.getLatitude()); // POJO uses 'latitude', SQL uses 'lattitude'
            pstmt.setString(4, site.getCode());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Site " + site.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int delete(Site site) {
        if (site == null || site.getCode() == null || site.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Site or its code cannot be null for deleting.");
        }
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
    
    public int deleteByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty for deleting Site.");
        }
         String sql = "DELETE FROM Site WHERE code = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Site by code " + code + ": " + e.getMessage());
            return -1;
        }
    }
}