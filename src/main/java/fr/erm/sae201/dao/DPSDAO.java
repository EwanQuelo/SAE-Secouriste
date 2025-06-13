package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DPSDAO extends DAO<DPS> {

    private final SiteDAO siteDAO = new SiteDAO();
    private final SportDAO sportDAO = new SportDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

    @Override
    public List<DPS> findAll() {
        String sql = "SELECT id, horaire_depart, horaire_fin, lieu, sport, jour FROM DPS";
        List<DPS> dpsList = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dpsList.add(mapResultSetToDPS(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all DPS: " + e.getMessage());
        }
        return dpsList;
    }

    @Override
    public DPS findByID(Long id) {
        if (id == null) return null;
        String sql = "SELECT id, horaire_depart, horaire_fin, lieu, sport, jour FROM DPS WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDPS(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding DPS by ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public int create(DPS dps) {
        if (dps == null) throw new IllegalArgumentException("DPS cannot be null.");
        String sql = "INSERT INTO DPS (horaire_depart, horaire_fin, lieu, sport, jour) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, timeArrayToInt(dps.getHoraireDepart()));
            pstmt.setInt(2, timeArrayToInt(dps.getHoraireFin()));
            pstmt.setString(3, dps.getSite().getCode());
            pstmt.setString(4, dps.getSport().getCode());
            pstmt.setDate(5, Date.valueOf(dps.getJournee().getDate()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        dps.setId(generatedKeys.getLong(1)); // Met à jour l'ID de l'objet
                    }
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            System.err.println("Error creating DPS: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int update(DPS dps) {
        if (dps == null) throw new IllegalArgumentException("DPS to update cannot be null.");
        String sql = "UPDATE DPS SET horaire_depart = ?, horaire_fin = ?, lieu = ?, sport = ?, jour = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, timeArrayToInt(dps.getHoraireDepart()));
            pstmt.setInt(2, timeArrayToInt(dps.getHoraireFin()));
            pstmt.setString(3, dps.getSite().getCode());
            pstmt.setString(4, dps.getSport().getCode());
            pstmt.setDate(5, Date.valueOf(dps.getJournee().getDate()));
            pstmt.setLong(6, dps.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating DPS " + dps.getId() + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int delete(DPS dps) {
        if (dps == null) throw new IllegalArgumentException("DPS to delete cannot be null.");
        String sql = "DELETE FROM DPS WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dps.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting DPS " + dps.getId() + ": " + e.getMessage());
            return -1;
        }
    }

    private DPS mapResultSetToDPS(ResultSet rs) throws SQLException {
        Site site = siteDAO.findByCode(rs.getString("lieu"));
        Sport sport = sportDAO.findByCode(rs.getString("sport"));
        Journee journee = journeeDAO.findByDate(rs.getDate("jour").toLocalDate());

        if (site == null || sport == null || journee == null) {
            System.err.println("Could not fully construct DPS ID " + rs.getLong("id") + " due to missing Site, Sport, or Journee.");
            return null; // ou lancer une exception
        }

        return new DPS(
                rs.getLong("id"),
                intToTimeArray(rs.getInt("horaire_depart")),
                intToTimeArray(rs.getInt("horaire_fin")),
                site,
                journee,
                sport
        );
    }

        // --- GESTION RELATION : ABesoin (DPS <-> Competence) ---
    public Map<Competence, Integer> findRequiredCompetencesForDps(long dpsId) {
        Map<Competence, Integer> requirements = new HashMap<>();
        String sql = "SELECT intituleCompetence, nombre FROM ABesoin WHERE idDPS = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dpsId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    Competence comp = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
                    if (comp != null) {
                        requirements.put(comp, rs.getInt("nombre"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding required competences for DPS " + dpsId + ": " + e.getMessage());
        }
        return requirements;
    }

    public int setRequiredCompetence(long dpsId, String intituleCompetence, int nombre) {
        // Utilise INSERT ... ON DUPLICATE KEY UPDATE pour créer ou mettre à jour la ligne
        String sql = "INSERT INTO ABesoin (idDPS, intituleCompetence, nombre) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE nombre = VALUES(nombre)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dpsId);
            pstmt.setString(2, intituleCompetence);
            pstmt.setInt(3, nombre);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error setting required competence: " + e.getMessage());
            return -1;
        }
    }

    public int removeRequiredCompetence(long dpsId, String intituleCompetence) {
        String sql = "DELETE FROM ABesoin WHERE idDPS = ? AND intituleCompetence = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dpsId);
            pstmt.setString(2, intituleCompetence);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing required competence: " + e.getMessage());
            return -1;
        }
    }

    // --- Méthodes CRUD et utilitaires ---
    private DPS mapResultSetToDPS(ResultSet rs, boolean fetchRelations) throws SQLException {
        Site site = siteDAO.findByCode(rs.getString("lieu"));
        Sport sport = sportDAO.findByCode(rs.getString("sport"));
        Journee journee = journeeDAO.findByDate(rs.getDate("jour").toLocalDate());

        DPS dps = new DPS(rs.getLong("id"), intToTimeArray(rs.getInt("horaire_depart")), intToTimeArray(rs.getInt("horaire_fin")), site, journee, sport);
        
        if (fetchRelations) {
            dps.setCompetencesRequises(findRequiredCompetencesForDps(dps.getId()));
        }
        return dps;
    }
    
    // --- Helpers pour la conversion des horaires ---
    private int timeArrayToInt(int[] timeArray) {
        return timeArray[0] * 100 + timeArray[1];
    }
    private int[] intToTimeArray(int timeInt) {
        return new int[]{timeInt / 100, timeInt % 100};
    }
}