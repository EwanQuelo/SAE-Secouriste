package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Site;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SiteDAO extends DAO<Site> {

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

    public Site findByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;
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

    @Override
    public int create(Site site) {
        if (site == null) throw new IllegalArgumentException("Site to create cannot be null.");
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

    @Override
    public int update(Site site) {
        if (site == null) throw new IllegalArgumentException("Site to update cannot be null.");
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

    @Override
    public int delete(Site site) {
        if (site == null) throw new IllegalArgumentException("Site to delete cannot be null.");
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
    
    private Site mapResultSetToSite(ResultSet rs) throws SQLException {
        return new Site(
            rs.getString("code"),
            rs.getString("nom"),
            rs.getFloat("longitude"),
            rs.getFloat("latitude")
        );
    }
}