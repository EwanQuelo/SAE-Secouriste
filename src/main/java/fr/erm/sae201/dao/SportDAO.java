package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Sport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des entités Sport.
 * 
 * Un Sport est identifié par un 'code' unique et possède un 'nom'.
 * Cette classe gère les opérations CRUD pour les enregistrements de Sport.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SportDAO extends DAO<Sport> {

    /**
     * Récupère tous les sports de la base de données.
     *
     * @return Une liste de tous les objets Sport trouvés.
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
     * Recherche un sport spécifique par son 'code' unique.
     *
     * @param code Le code unique du sport à trouver.
     * @return L'objet Sport si trouvé ; sinon `null`.
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
     * Crée un nouvel enregistrement de Sport dans la base de données.
     * Le 'code' du sport sert de clé primaire.
     *
     * @param sport L'objet Sport à persister. Son 'code' ne doit pas être null.
     * @return Le nombre de lignes affectées (1 si succès, -1 si erreur).
     * @throws IllegalArgumentException si l'objet sport ou son code est null.
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
     * Met à jour le 'nom' d'un sport existant dans la base de données.
     * Le 'code' est utilisé pour identifier l'enregistrement et ne peut pas être modifié.
     *
     * @param sport L'objet Sport avec le nom mis à jour. Son 'code' doit être défini.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     * @throws IllegalArgumentException si l'objet sport ou son code est null.
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
     * Supprime un sport de la base de données en utilisant l'objet fourni.
     * Appelle en interne `deleteByCode`.
     *
     * @param sport L'objet Sport à supprimer. Son 'code' doit être défini.
     * @return Le nombre de lignes affectées.
     * @throws IllegalArgumentException si l'objet sport ou son code est null.
     */
    @Override
    public int delete(Sport sport) {
        if (sport == null || sport.getCode() == null)
            throw new IllegalArgumentException("Sport to delete must not be null.");
        return deleteByCode(sport.getCode());
    }

    /**
     * Supprime un sport de la base de données par son 'code' unique.
     *
     * @param code Le code unique du sport à supprimer. Ne peut être null ou vide.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     * @throws IllegalArgumentException si le code est null ou vide.
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

    /**
     * Non supporté. La clé primaire de Sport est une chaîne de caractères ('code').
     * Utilisez `findByCode(String)`.
     */
    @Override
    public Sport findByID(Long id) {
        throw new UnsupportedOperationException("Sport ID is a String (code). Use findByCode(String).");
    }

    /**
     * Transforme une ligne d'un ResultSet en un objet Sport.
     *
     * @param rs Le ResultSet positionné sur la ligne à traiter.
     * @return Un nouvel objet Sport.
     * @throws SQLException Si une erreur se produit lors de l'accès au ResultSet.
     */
    private Sport mapResultSetToSport(ResultSet rs) throws SQLException {
        return new Sport(
                rs.getString("code"),
                rs.getString("nom"));
    }
}