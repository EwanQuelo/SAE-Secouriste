package fr.erm.sae201.dao; // DAO package

import fr.erm.sae201.metier.persistence.Competence; // POJO package

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Competence entity, extending the generic DAO.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.2
 */
public class CompetenceDAO extends DAO<Competence> {

    @Override
    public List<Competence> findAll() {
        String sql = "SELECT intitule FROM Competence";
        List<Competence> competences = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Assuming Competence constructor: new Competence(code, nom)
                // and SQL intitule maps to both code and nom for simplicity here.
                competences.add(new Competence(rs.getString("intitule"), rs.getString("intitule")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Competences: " + e.getMessage());
        }
        return competences;
    }

    /**
     * {@inheritDoc}
     * This method is not applicable for Competence as its ID is a String (intitule), not Long.
     * Use findByIntitule(String intitule) instead.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public Competence findByID(Long id) {
        System.err.println("Warning: findByID(Long) called on CompetenceDAO. Use findByIntitule(String) instead.");
        throw new UnsupportedOperationException("Competence ID is String (intitule), not Long. Use findByIntitule(String).");
    }

    /**
     * Finds a Competence by its intitule (which serves as its code/ID).
     * @param intitule The intitule (code) of the competence.
     * @return The Competence object if found, otherwise null.
     */
    public Competence findByIntitule(String intitule) {
        if (intitule == null || intitule.trim().isEmpty()) return null;
        String sql = "SELECT intitule FROM Competence WHERE intitule = ?";
        Competence competence = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, intitule);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    competence = new Competence(rs.getString("intitule"), rs.getString("intitule"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Competence by intitule " + intitule + ": " + e.getMessage());
        }
        return competence;
    }

    @Override
    public int create(Competence competence) {
        if (competence == null || competence.getCode() == null || competence.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Competence or its code cannot be null or empty for creating.");
        }
        String sql = "INSERT INTO Competence (intitule) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, competence.getCode()); // SQL uses 'intitule'
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Competence with intitule " + competence.getCode() + ": " + e.getMessage());
            return -1;
        }
    }
    
    @Override
    public int update(Competence element) {
        System.err.println("Warning: update(Competence) is problematic for PK changes. " +
                           "Use a custom method like updateIntitule(String oldIntitule, String newIntitule).");
        throw new UnsupportedOperationException("Updating Competence's primary key (intitule) via generic update(T) is not directly supported. " +
                                                "Provide old intitule separately or use delete then create.");
    }
    
    public int updateIntitule(String oldIntitule, Competence newCompetence) {
        if (oldIntitule == null || oldIntitule.trim().isEmpty() ||
            newCompetence == null || newCompetence.getCode() == null || newCompetence.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Old or new competence intitule cannot be null or empty.");
        }
        // Assuming the 'nom' field in Competence POJO should also be updated if it conceptually changes with code.
        // But the DB table Competence only has 'intitule'. If 'nom' was a separate DB column:
        // String sql = "UPDATE Competence SET intitule = ?, nom = ? WHERE intitule = ?";
        // pstmt.setString(1, newCompetence.getCode());
        // pstmt.setString(2, newCompetence.getNom()); // If 'nom' is a separate field in DB
        // pstmt.setString(3, oldIntitule);

        String sql = "UPDATE Competence SET intitule = ? WHERE intitule = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newCompetence.getCode()); // The new intitule
            pstmt.setString(2, oldIntitule);             // The intitule to find the row
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Competence intitule from " + oldIntitule + " to " + newCompetence.getCode() + ": " + e.getMessage());
            return -1;
        }
    }


    @Override
    public int delete(Competence competence) {
        if (competence == null || competence.getCode() == null || competence.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Competence or its code cannot be null for deleting.");
        }
        String sql = "DELETE FROM Competence WHERE intitule = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, competence.getCode());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Competence with intitule " + competence.getCode() + ": " + e.getMessage());
            return -1;
        }
    }
    
    public int deleteByIntitule(String intitule) {
        if (intitule == null || intitule.trim().isEmpty()) {
            throw new IllegalArgumentException("Intitule cannot be null or empty for deleting.");
        }
        // Create a dummy object for the generic delete, or just execute directly
        String sql = "DELETE FROM Competence WHERE intitule = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intitule);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Competence with intitule " + intitule + ": " + e.getMessage());
            return -1;
        }
    }
}