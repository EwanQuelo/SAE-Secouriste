package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for managing {@link Affectation} entities.
 * An {@link Affectation} represents the assignment of a {@link Secouriste}
 * with a specific {@link Competence} to a {@link DPS} (Dispositif Prévisionnel de Secours).
 * This class handles database operations such as creating, retrieving, and deleting affectations.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class AffectationDAO extends DAO<Affectation> {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

    /**
     * Retrieves all {@link Affectation} records from the database.
     * For each record, it fetches the associated {@link DPS}, {@link Secouriste},
     * and {@link Competence} objects using their respective DAOs.
     * If any related entity cannot be found for a given record, that affectation
     * will not be included in the returned list.
     *
     * @return A {@link List} of all {@link Affectation} objects. The list may be empty
     *         if no affectations are found or if an error occurs during retrieval.
     */
    @Override
    public List<Affectation> findAll() {
        String sql = "SELECT idDPS, idSecouriste, intituleCompetence FROM Affectation";
        List<Affectation> affectations = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DPS dps = dpsDAO.findByID(rs.getLong("idDPS"));
                Secouriste secouriste = secouristeDAO.findByID(rs.getLong("idSecouriste"));
                Competence competence = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
                if (dps != null && secouriste != null && competence != null) {
                    affectations.add(new Affectation(dps, secouriste, competence));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Affectations: " + e.getMessage());
        }
        return affectations;
    }
    
    /**
     * Finds all {@link Affectation} records associated with a specific {@link DPS}.
     *
     * @param dpsId The ID of the {@link DPS} for which to find affectations.
     * @return A {@link List} of {@link Affectation} objects for the given DPS ID.
     *         The list may be empty if no affectations are found for this DPS or if an error occurs.
     */
    public List<Affectation> findAffectationsByDpsId(long dpsId) {
        List<Affectation> affectations = new ArrayList<>();
        String sql = "SELECT idDPS, idSecouriste, intituleCompetence FROM Affectation WHERE idDPS = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dpsId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mapResultSetToAffectation(rs).ifPresent(affectations::add);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding affectations for DPS " + dpsId + ": " + e.getMessage());
        }
        return affectations;
    }

    /**
     * Finds all {@link Affectation} records associated with a specific {@link Secouriste}.
     *
     * @param secouristeId The ID of the {@link Secouriste} for which to find affectations.
     * @return A {@link List} of {@link Affectation} objects for the given Secouriste ID.
     *         The list may be empty if no affectations are found for this secouriste or if an error occurs.
     */
    public List<Affectation> findAffectationsBySecouristeId(long secouristeId) {
        List<Affectation> affectations = new ArrayList<>();
        String sql = "SELECT idDPS, idSecouriste, intituleCompetence FROM Affectation WHERE idSecouriste = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mapResultSetToAffectation(rs).ifPresent(affectations::add);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding affectations for Secouriste " + secouristeId + ": " + e.getMessage());
        }
        return affectations;
    }
    
    
    /**
     * Helper method to map a row from a {@link ResultSet} to an {@link Affectation} object.
     * It retrieves IDs and intitule from the current row, then uses {@link DPSDAO},
     * {@link SecouristeDAO}, and {@link CompetenceDAO} to fetch the full related objects.
     *
     * @param rs The {@link ResultSet} positioned at the row to map.
     * @return An {@link Optional} containing the mapped {@link Affectation} if all related
     *         entities are found and valid; otherwise, an empty {@link Optional}.
     * @throws SQLException If an error occurs while accessing the {@link ResultSet}.
     */
    private java.util.Optional<Affectation> mapResultSetToAffectation(ResultSet rs) throws SQLException {
        DPS dps = dpsDAO.findByID(rs.getLong("idDPS"));
        Secouriste secouriste = secouristeDAO.findByID(rs.getLong("idSecouriste"));
        Competence competence = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
        if (dps != null && secouriste != null && competence != null) {
            return java.util.Optional.of(new Affectation(dps, secouriste, competence));
        }
        return java.util.Optional.empty();
    }

    /**
     * Creates a new {@link Affectation} record in the database.
     * The {@link Affectation} object must have its {@link DPS}, {@link Secouriste},
     * and {@link Competence} fields properly set with valid entities.
     *
     * @param affectation The {@link Affectation} object to persist. Must not be 'null'.
     * @return The number of rows affected (typically 1 on success, or -1 if an SQLException occurs).
     * @throws IllegalArgumentException if 'affectation' is 'null' or its internal components are not set.
     */
    @Override
    public int create(Affectation affectation) {
        if (affectation == null) throw new IllegalArgumentException("Affectation cannot be null.");
        String sql = "INSERT INTO Affectation (idDPS, idSecouriste, intituleCompetence) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, affectation.getDps().getId());
            pstmt.setLong(2, affectation.getSecouriste().getId());
            pstmt.setString(3, affectation.getCompetence().getIntitule());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Affectation: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Deletes an existing {@link Affectation} record from the database.
     * The deletion is based on the composite key (DPS ID, Secouriste ID, Competence intitule)
     * derived from the provided {@link Affectation} object.
     *
     * @param affectation The {@link Affectation} object to delete. Must not be 'null'.
     * @return The number of rows affected (typically 1 on success, 0 if no matching record was found,
     *         or -1 if an SQLException occurs).
     * @throws IllegalArgumentException if 'affectation' is 'null' or its internal components are not set.
     */
    @Override
    public int delete(Affectation affectation) {
        if (affectation == null) throw new IllegalArgumentException("Affectation cannot be null.");
        String sql = "DELETE FROM Affectation WHERE idDPS = ? AND idSecouriste = ? AND intituleCompetence = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, affectation.getDps().getId());
            pstmt.setLong(2, affectation.getSecouriste().getId());
            pstmt.setString(3, affectation.getCompetence().getIntitule());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Affectation: " + e.getMessage());
            return -1;
        }
    }

    
    
    @Override
    public Affectation findByID(Long id) {
        throw new UnsupportedOperationException("Affectation has a composite PK. Use findByCompositeKey().");
    }

    @Override
    public int update(Affectation element) {
        throw new UnsupportedOperationException("Updating an Affectation (join table record) is typically done by deleting and creating a new one.");
    }
}