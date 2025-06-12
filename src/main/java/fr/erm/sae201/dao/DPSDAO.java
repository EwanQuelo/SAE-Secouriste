package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.persistence.Site;    // For creating Site objects
import fr.erm.sae201.metier.persistence.Sport;   // For creating Sport objects
import fr.erm.sae201.metier.persistence.Journee; // For creating Journee objects

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the DPS entity, extending the generic DAO.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.2
 */
public class DPSDAO extends DAO<DPS> {

    // Helper to convert int[2] time to DB integer format (e.g., HHMM)
    private int timeArrayToInt(int[] timeArray) {
        if (timeArray == null || timeArray.length != 2) {
            throw new IllegalArgumentException("Time array must have 2 elements {hour, minute}.");
        }
        if (timeArray[0] < 0 || timeArray[0] > 23 || timeArray[1] < 0 || timeArray[1] > 59) {
            throw new IllegalArgumentException("Invalid hour/minute in time array.");
        }
        return timeArray[0] * 100 + timeArray[1];
    }

    // Helper to convert DB integer time (HHMM) to int[2] format
    private int[] intToTimeArray(int timeInt) {
        if (timeInt < 0 || timeInt > 2359 || (timeInt % 100) > 59) {
             // Basic validation for common errors, more robust validation might be needed
            throw new IllegalArgumentException("Invalid integer time format from DB: " + timeInt);
        }
        return new int[]{timeInt / 100, timeInt % 100};
    }

    @Override
    public List<DPS> findAll() {
        String sql = "SELECT D.id, D.horaire_depart, D.horaire_fin, D.lieu AS site_code, D.sport AS sport_code " +
                     // ", J.jour AS journee_jour, J.mois AS journee_mois, J.annee AS journee_annee " + // If Journee FKs were in DPS table
                     "FROM DPS D ";
                     // "LEFT JOIN Site S ON D.lieu = S.code " + // To get Site name directly (optional join)
                     // "LEFT JOIN Sport SP ON D.sport = SP.code " + // To get Sport name directly (optional join)
                     // "LEFT JOIN Journee J ON D.journee_jour = J.jour AND D.journee_mois = J.mois AND D.journee_annee = J.annee"; // If FKs existed

        List<DPS> dpsList = new ArrayList<>();
        SiteDAO siteDAO = new SiteDAO(); // To fetch full Site objects
        SportDAO sportDAO = new SportDAO(); // To fetch full Sport objects
        JourneeDAO journeeDAO = new JourneeDAO(); // To fetch full Journee objects (if linked)

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Site site = siteDAO.findByCode(rs.getString("site_code"));
                Sport sport = sportDAO.findByCode(rs.getString("sport_code"));
                
                // === Journee Handling ===
                // The SQL for DPS table doesn't have a direct FK to Journee.
                // The UML 'EstProgramme (Journee 1 - DPS 0..*)' implies a DPS MUST have ONE Journee.
                // This needs to be resolved in the DB schema:
                // Option A: Add journee_jour, journee_mois, journee_annee FKs to DPS table.
                // Option B: An association table DPS_Journee (idDPS, jour, mois, annee) - less likely for 1-to-many.
                // For now, as the schema stands, we cannot reliably fetch the associated Journee.
                // A placeholder is used, but this is a critical point.
                Journee journee;
                // if (rs.getString("journee_jour") != null) { // If FKs were present and joined
                //    journee = journeeDAO.findByDate(rs.getInt("journee_jour"), rs.getInt("journee_mois"), rs.getInt("journee_annee"));
                // } else {
                    // This is a temporary, WRONG placeholder. The application logic
                    // would need to ensure every DPS has a Journee linked somehow.
                    System.err.println("WARNING (DPSDAO.findAll): Journee for DPS ID " + rs.getLong("id") + 
                                       " cannot be reliably fetched with current schema. Using placeholder.");
                    journee = new Journee(1, 1, 1900); // Invalid placeholder
                // }
                if (site == null || sport == null || journee == null) {
                     System.err.println("Skipping DPS ID " + rs.getLong("id") + " due to missing Site, Sport, or Journee.");
                     continue;
                }

                dpsList.add(new DPS(
                        rs.getLong("id"),
                        intToTimeArray(rs.getInt("horaire_depart")),
                        intToTimeArray(rs.getInt("horaire_fin")),
                        site,
                        journee, // This Journee is problematic
                        sport
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all DPS: " + e.getMessage());
        }
        return dpsList;
    }

    @Override
    public DPS findByID(Long id) {
        if (id == null) return null;
        String sql = "SELECT D.id, D.horaire_depart, D.horaire_fin, D.lieu AS site_code, D.sport AS sport_code " +
                     // Add Journee columns if they exist in DPS table or are joined
                     "FROM DPS D WHERE D.id = ?";
        DPS dps = null;
        SiteDAO siteDAO = new SiteDAO();
        SportDAO sportDAO = new SportDAO();
        JourneeDAO journeeDAO = new JourneeDAO();


        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Site site = siteDAO.findByCode(rs.getString("site_code"));
                    Sport sport = sportDAO.findByCode(rs.getString("sport_code"));
                    
                    // Journee: Same issue as in findAll().
                    Journee journee;
                    System.err.println("WARNING (DPSDAO.findByID): Journee for DPS ID " + rs.getLong("id") + 
                                       " cannot be reliably fetched. Using placeholder.");
                    journee = new Journee(1, 1, 1900); // Invalid placeholder

                    if (site == null || sport == null || journee == null) {
                        System.err.println("Could not fully construct DPS ID " + id + " due to missing Site, Sport, or Journee.");
                        return null;
                    }

                    dps = new DPS(
                            rs.getLong("id"),
                            intToTimeArray(rs.getInt("horaire_depart")),
                            intToTimeArray(rs.getInt("horaire_fin")),
                            site,
                            journee, // Problematic
                            sport
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding DPS by ID " + id + ": " + e.getMessage());
        }
        return dps;
    }

    @Override
    public int create(DPS dps) {
        if (dps == null || dps.getSite() == null || dps.getSport() == null || dps.getJournee() == null) {
            throw new IllegalArgumentException("DPS or its required associations (Site, Sport, Journee) cannot be null for creating.");
        }
        // How is dps.getJournee() persisted? The DPS table SQL does not have columns for it.
        // This implies 'EstProgramme' might be an association table OR Journee FKs are missing in DPS table.
        // For now, I will only insert data present in the current DPS table structure.
        // IF Journee FKs were in DPS table (e.g., journee_jour, journee_mois, journee_annee):
        // String sql = "INSERT INTO DPS (id, horaire_depart, horaire_fin, lieu, sport, journee_jour, journee_mois, journee_annee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        String sql = "INSERT INTO DPS (id, horaire_depart, horaire_fin, lieu, sport) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, dps.getId());
            pstmt.setInt(2, timeArrayToInt(dps.getHoraireDepart()));
            pstmt.setInt(3, timeArrayToInt(dps.getHoraireFin()));
            pstmt.setString(4, dps.getSite().getCode());
            pstmt.setString(5, dps.getSport().getCode());
            // If Journee FKs were in DPS table:
            // pstmt.setInt(6, dps.getJournee().getJour());
            // pstmt.setInt(7, dps.getJournee().getMois());
            // pstmt.setInt(8, dps.getJournee().getAnnee());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating DPS " + dps.getId() + ": " + e.getMessage());
             if (e.getMessage().toLowerCase().contains("unique constraint") || e.getErrorCode() == 1062) { // MySQL duplicate
                 System.err.println("Potential UNIQUE constraint violation on horaire_depart or horaire_fin for DPS " + dps.getId());
            }
            return -1;
        }
    }

    @Override
    public int update(DPS dps) {
        if (dps == null || dps.getSite() == null || dps.getSport() == null || dps.getJournee() == null) {
            throw new IllegalArgumentException("DPS or its required associations cannot be null for updating.");
        }
        // String sql = "UPDATE DPS SET horaire_depart = ?, horaire_fin = ?, lieu = ?, sport = ?, journee_jour = ?, journee_mois = ?, journee_annee = ? WHERE id = ?";
        String sql = "UPDATE DPS SET horaire_depart = ?, horaire_fin = ?, lieu = ?, sport = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, timeArrayToInt(dps.getHoraireDepart()));
            pstmt.setInt(2, timeArrayToInt(dps.getHoraireFin()));
            pstmt.setString(3, dps.getSite().getCode());
            pstmt.setString(4, dps.getSport().getCode());
            // If Journee FKs were in DPS table:
            // pstmt.setInt(5, dps.getJournee().getJour());
            // pstmt.setInt(6, dps.getJournee().getMois());
            // pstmt.setInt(7, dps.getJournee().getAnnee());
            // pstmt.setLong(8, dps.getId());
            pstmt.setLong(5, dps.getId());


            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating DPS " + dps.getId() + ": " + e.getMessage());
            if (e.getMessage().toLowerCase().contains("unique constraint") || e.getErrorCode() == 1062) {
                 System.err.println("Potential UNIQUE constraint violation on horaire_depart or horaire_fin for DPS " + dps.getId());
            }
            return -1;
        }
    }

    @Override
    public int delete(DPS dps) {
        if (dps == null) {
            throw new IllegalArgumentException("DPS to delete cannot be null.");
        }
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
}