package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AffectationDAO extends DAO<Affectation> {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

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
     * Trouve toutes les affectations pour un DPS spécifique.
     * @param dpsId L'ID du DPS.
     * @return Une liste d'affectations pour ce DPS.
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
     * Trouve toutes les affectations pour un Secouriste spécifique.
     * @param secouristeId L'ID du Secouriste.
     * @return Une liste de ses affectations.
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
    
    // Utilitaire pour mapper un ResultSet vers un Optional<Affectation>
    private java.util.Optional<Affectation> mapResultSetToAffectation(ResultSet rs) throws SQLException {
        DPS dps = dpsDAO.findByID(rs.getLong("idDPS"));
        Secouriste secouriste = secouristeDAO.findByID(rs.getLong("idSecouriste"));
        Competence competence = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
        if (dps != null && secouriste != null && competence != null) {
            return java.util.Optional.of(new Affectation(dps, secouriste, competence));
        }
        return java.util.Optional.empty();
    }

    // Le reste du DAO (create, delete, etc.) est inchangé
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