package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Affectation entity (join table: DPS, Secouriste, Competence).
 * Table: Affectation (idDPS INT, idSecouriste INT, intituleCompetence VARCHAR(100))
 */
public class AffectationDAO extends DAO<Affectation> {

    private DPSDAO dpsDAO;
    private SecouristeDAO secouristeDAO;
    private CompetenceDAO competenceDAO;

    public AffectationDAO() {
        this.dpsDAO = new DPSDAO();
        this.secouristeDAO = new SecouristeDAO();
        this.competenceDAO = new CompetenceDAO();
    }

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
                } else {
                    System.err.println("Warning: Could not fully construct Affectation, missing related entity for row: "
                                       + "idDPS=" + rs.getLong("idDPS")
                                       + ", idSecouriste=" + rs.getLong("idSecouriste")
                                       + ", intituleCompetence=" + rs.getString("intituleCompetence"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Affectations: " + e.getMessage());
        }
        return affectations;
    }

    @Override
    public Affectation findByID(Long id) {
        throw new UnsupportedOperationException("Affectation has a composite primary key. Use findByCompositeKey or specific finders.");
    }

    public Affectation findByCompositeKey(long idDPS, long idSecouriste, String intituleCompetence) {
        String sql = "SELECT idDPS, idSecouriste, intituleCompetence FROM Affectation " +
                     "WHERE idDPS = ? AND idSecouriste = ? AND intituleCompetence = ?";
        Affectation affectation = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, idDPS);
            pstmt.setLong(2, idSecouriste);
            pstmt.setString(3, intituleCompetence);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DPS dps = dpsDAO.findByID(rs.getLong("idDPS"));
                    Secouriste secouriste = secouristeDAO.findByID(rs.getLong("idSecouriste"));
                    Competence competence = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
                    if (dps != null && secouriste != null && competence != null) {
                        affectation = new Affectation(dps, secouriste, competence);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Affectation by composite key: " + e.getMessage());
        }
        return affectation;
    }


    @Override
    public int create(Affectation affectation) {
        if (affectation == null || affectation.getDps() == null || affectation.getSecouriste() == null || affectation.getCompetence() == null) {
            throw new IllegalArgumentException("Affectation or its linked entities cannot be null for creation.");
        }
        String sql = "INSERT INTO Affectation (idDPS, idSecouriste, intituleCompetence) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, affectation.getDps().getId());
            pstmt.setLong(2, affectation.getSecouriste().getId());
            pstmt.setString(3, affectation.getCompetence().getCode());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Affectation: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int update(Affectation element) {
        throw new UnsupportedOperationException("Updating an Affectation (join table record) is typically done by delete and create due to PK nature.");
    }

    @Override
    public int delete(Affectation affectation) {
        if (affectation == null || affectation.getDps() == null || affectation.getSecouriste() == null || affectation.getCompetence() == null) {
            throw new IllegalArgumentException("Affectation or its linked entities cannot be null for deletion.");
        }
        String sql = "DELETE FROM Affectation WHERE idDPS = ? AND idSecouriste = ? AND intituleCompetence = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, affectation.getDps().getId());
            pstmt.setLong(2, affectation.getSecouriste().getId());
            pstmt.setString(3, affectation.getCompetence().getCode());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Affectation: " + e.getMessage());
            return -1;
        }
    }

    public List<Affectation> findAffectationsByDpsId(long idDPS) {
        String sql = "SELECT idDPS, idSecouriste, intituleCompetence FROM Affectation WHERE idDPS = ?";
        List<Affectation> affectations = new ArrayList<>();
         try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, idDPS);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DPS dps = dpsDAO.findByID(rs.getLong("idDPS")); // Could be the one passed in
                    Secouriste secouriste = secouristeDAO.findByID(rs.getLong("idSecouriste"));
                    Competence competence = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
                    if (dps != null && secouriste != null && competence != null) {
                        affectations.add(new Affectation(dps, secouriste, competence));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Affectations by DPS ID " + idDPS +": " + e.getMessage());
        }
        return affectations;
    }

}