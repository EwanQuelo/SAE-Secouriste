package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Site;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des entités Site.
 * <p>
 * Un Site représente un lieu géographique, identifié par un 'code' unique,
 * et inclut un nom, une longitude et une latitude. Cette classe gère les
 * opérations CRUD pour les enregistrements de Site.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SiteDAO extends DAO<Site> {

    /**
     * Récupère tous les sites de la base de données.
     *
     * @return Une liste de tous les objets Site trouvés.
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
     * Recherche un site spécifique par son 'code' unique.
     *
     * @param code Le code unique du site à trouver.
     * @return L'objet Site si trouvé ; sinon `null`.
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
     * Crée un nouvel enregistrement de Site dans la base de données.
     * Le 'code' du site sert de clé primaire.
     *
     * @param site L'objet Site à persister. Ne doit pas être null.
     * @return Le nombre de lignes affectées (1 si succès, -1 si erreur).
     * @throws IllegalArgumentException si l'objet site est null.
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
            pstmt.setFloat(4, site.getLatitude());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Site " + site.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Met à jour les informations d'un site existant (nom, longitude, latitude).
     * Le 'code' du site, utilisé pour l'identifier, ne peut pas être modifié par cette méthode.
     *
     * @param site L'objet Site avec les informations mises à jour. Son 'code' doit être défini.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     * @throws IllegalArgumentException si l'objet site est null.
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
            pstmt.setFloat(3, site.getLatitude());
            pstmt.setString(4, site.getCode());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Site " + site.getCode() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Supprime un site de la base de données en se basant sur son 'code'.
     *
     * @param site L'objet Site à supprimer. Son 'code' doit être défini.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     * @throws IllegalArgumentException si l'objet site est null.
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

    /**
     * Non supporté. La clé primaire de Site est une chaîne de caractères ('code').
     * Utilisez `findByCode(String)`.
     */
    @Override
    public Site findByID(Long id) {
        throw new UnsupportedOperationException("Site ID is String (code). Use findByCode(String).");
    }

    /**
     * Transforme une ligne d'un ResultSet en un objet Site.
     *
     * @param rs Le ResultSet positionné sur la ligne à traiter.
     * @return Un nouvel objet Site.
     * @throws SQLException Si une erreur se produit lors de l'accès au ResultSet.
     */
    private Site mapResultSetToSite(ResultSet rs) throws SQLException {
        return new Site(
                rs.getString("code"),
                rs.getString("nom"),
                rs.getFloat("longitude"),
                rs.getFloat("latitude"));
    }
}