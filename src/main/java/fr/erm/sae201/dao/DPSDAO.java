package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data Access Object (DAO) for managing {@link DPS} (Dispositif Prévisionnel de
 * Secours) entities.
 * A {@link DPS} represents a planned rescue and first aid deployment, detailing
 * schedules,
 * location, associated sport, and the day of operation. This DAO handles CRUD
 * operations
 * for DPS records and manages the relationship with required
 * {@link Competence}s
 * through the 'ABesoin' join table.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.1
 */
public class DPSDAO extends DAO<DPS> {

    // DAOs for related entities, used to reconstruct DPS objects
    // or manage related data.
    private final SiteDAO siteDAO = new SiteDAO();
    private final SportDAO sportDAO = new SportDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

    /**
     * Retrieves all {@link DPS} records from the database.
     * Each DPS object is constructed by mapping a row from the 'DPS' table and
     * resolving its associated {@link Site}, {@link Sport}, and {@link Journee}
     * objects.
     *
     * @return A {@link List} of all {@link DPS} objects found. The list may be
     *         empty
     *         if no DPS records exist or if an error occurs during retrieval.
     */
    @Override
    public List<DPS> findAll() {
        String sql = "SELECT id, horaire_depart_heure, horaire_depart_minute, horaire_fin_heure, horaire_fin_minute, lieu, sport, jour FROM DPS";
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

    /**
     * Finds a specific {@link DPS} by its unique ID.
     * The DPS object is constructed by mapping the corresponding row and resolving
     * its associated {@link Site}, {@link Sport}, and {@link Journee} objects.
     *
     * @param id The unique ID of the {@link DPS} to find. Can be 'null'.
     * @return The {@link DPS} object if found; 'null' if no DPS with the given ID
     *         exists, if the provided 'id' is 'null', or if an error occurs.
     */
    @Override
    public DPS findByID(Long id) {
        if (id == null)
            return null;
        String sql = "SELECT id, horaire_depart_heure, horaire_depart_minute, horaire_fin_heure, horaire_fin_minute, lieu, sport, jour FROM DPS WHERE id = ?";
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

    /**
     * Creates a new {@link DPS} record in the database.
     * The start and end times are taken directly from the 'int[] {HH, MM}' format.
     * After successful insertion, the generated ID from the database is set
     * back into the provided {@link DPS} object.
     *
     * @param dps The {@link DPS} object to persist. Must not be 'null'.
     *            Its {@link Site}, {@link Sport}, and {@link Journee} must be set
     *            and valid.
     * @return The number of rows affected (typically 1 on success). Returns -1 if
     *         an
     *         {@link SQLException} occurs or if the 'dps' object is 'null'.
     * @throws IllegalArgumentException if 'dps' is 'null'.
     */
    @Override
    public int create(DPS dps) {
        if (dps == null)
            throw new IllegalArgumentException("DPS cannot be null.");
        String sql = "INSERT INTO DPS (horaire_depart_heure, horaire_depart_minute, horaire_fin_heure, horaire_fin_minute, lieu, sport, jour) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, dps.getHoraireDepart()[0]);
            pstmt.setInt(2, dps.getHoraireDepart()[1]);
            pstmt.setInt(3, dps.getHoraireFin()[0]);
            pstmt.setInt(4, dps.getHoraireFin()[1]);
            pstmt.setString(5, dps.getSite().getCode());
            pstmt.setString(6, dps.getSport().getCode());
            pstmt.setDate(7, Date.valueOf(dps.getJournee().getDate()));

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
    

    /**
     * NOUVEAU: Récupère tous les DPS dans une plage de dates donnée.
     * @param startDate La date de début.
     * @param endDate La date de fin.
     * @return Une liste de DPS.
     */
    public List<DPS> findAllBetweenDates(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT id, horaire_depart_heure, horaire_depart_minute, horaire_fin_heure, horaire_fin_minute, lieu, sport, jour FROM DPS WHERE jour BETWEEN ? AND ?";
        List<DPS> dpsList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dpsList.add(mapResultSetToDPS(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding DPS between dates: " + e.getMessage());
        }
        return dpsList;
    }

    /**
     * Updates an existing {@link DPS} record in the database.
     * The start and end times are taken directly from the 'int[] {HH, MM}' format.
     *
     * @param dps The {@link DPS} object with updated information. Its ID must be
     *            set
     *            to identify the record to update. Must not be 'null'.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         ID was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'dps' is 'null'.
     * @throws IllegalArgumentException if 'dps' is 'null'.
     */
    @Override
    public int update(DPS dps) {
        if (dps == null)
            throw new IllegalArgumentException("DPS to update cannot be null.");
        String sql = "UPDATE DPS SET horaire_depart_heure = ?, horaire_depart_minute = ?, horaire_fin_heure = ?, horaire_fin_minute = ?, lieu = ?, sport = ?, jour = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dps.getHoraireDepart()[0]);
            pstmt.setInt(2, dps.getHoraireDepart()[1]);
            pstmt.setInt(3, dps.getHoraireFin()[0]);
            pstmt.setInt(4, dps.getHoraireFin()[1]);
            pstmt.setString(5, dps.getSite().getCode());
            pstmt.setString(6, dps.getSport().getCode());
            pstmt.setDate(7, Date.valueOf(dps.getJournee().getDate()));
            pstmt.setLong(8, dps.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating DPS " + dps.getId() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Deletes a {@link DPS} record from the database based on its ID.
     *
     * @param dps The {@link DPS} object to delete. Its ID must be set. Must not be
     *            'null'.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         ID was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'dps' is 'null'.
     * @throws IllegalArgumentException if 'dps' is 'null'.
     */
    @Override
    public int delete(DPS dps) {
        if (dps == null)
            throw new IllegalArgumentException("DPS to delete cannot be null.");
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

    /**
     * Maps a row from a {@link ResultSet} to a {@link DPS} object.
     * This method retrieves related {@link Site}, {@link Sport}, and
     * {@link Journee} objects
     * using their respective DAOs based on codes/dates found in the result set.
     * It also reconstructs the 'int[] {HH, MM}' format from separate DB columns.
     * If any critical related entity (Site, Sport, Journee) is not found, this
     * method
     * prints an error and returns 'null', indicating the DPS object could not be
     * fully constructed.
     *
     * @param rs The {@link ResultSet} positioned at the row to map.
     * @return A new {@link DPS} object, or 'null' if essential related data is
     *         missing
     *         or an {@link SQLException} occurs.
     * @throws SQLException If an error occurs while accessing the
     *                      {@link ResultSet}.
     */
    private DPS mapResultSetToDPS(ResultSet rs) throws SQLException {
        Site site = siteDAO.findByCode(rs.getString("lieu"));
        Sport sport = sportDAO.findByCode(rs.getString("sport"));
        Journee journee = journeeDAO.findByDate(rs.getDate("jour").toLocalDate());

        if (site == null || sport == null || journee == null) {
            System.err.println("Could not fully construct DPS ID " + rs.getLong("id")
                    + " due to missing Site, Sport, or Journee.");
            return null; // ou lancer une exception
        }

        return new DPS(
                rs.getLong("id"),
                new int[]{rs.getInt("horaire_depart_heure"), rs.getInt("horaire_depart_minute")},
                new int[]{rs.getInt("horaire_fin_heure"), rs.getInt("horaire_fin_minute")},
                site,
                journee,
                sport);
    }

    // --- Relationship Management: ABesoin (DPS <-> Competence) ---

    /**
     * Finds all required {@link Competence}s and their respective numbers needed
     * for a specific DPS.
     * This queries the 'ABesoin' join table.
     *
     * @param dpsId The ID of the {@link DPS} for which to find requirements.
     * @return A {@link Map} where keys are {@link Competence} objects and values
     *         are
     *         the 'Integer' number of personnel with that competence required for
     *         the DPS.
     *         Returns an empty map if no requirements are found or if an error
     *         occurs.
     */
    public Map<Competence, Integer> findRequiredCompetencesForDps(long dpsId) {
        Map<Competence, Integer> requirements = new HashMap<>();
        String sql = "SELECT intituleCompetence, nombre FROM ABesoin WHERE idDPS = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dpsId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
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

    /**
     * Sets or updates the required number for a specific {@link Competence} for a
     * given DPS.
     * This uses an 'INSERT ... ON DUPLICATE KEY UPDATE' SQL statement to either
     * create
     * a new requirement entry in 'ABesoin' or update the 'nombre' (number) if the
     * DPS-Competence pair already exists.
     *
     * @param dpsId              The ID of the {@link DPS}.
     * @param intituleCompetence The 'intitule' (title) of the {@link Competence}.
     * @param nombre             The required number of personnel with this
     *                           competence.
     * @return The number of rows affected (typically 1 for insert, 2 for update due
     *         to how
     *         'ON DUPLICATE KEY UPDATE' is counted by some drivers, or -1 if an
     *         error occurs).
     */
    public int setRequiredCompetence(long dpsId, String intituleCompetence, int nombre) {
        // Utilise INSERT ... ON DUPLICATE KEY UPDATE pour créer ou mettre à jour la
        // ligne
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

    /**
     * Removes a required {@link Competence} from a specific DPS in the 'ABesoin'
     * table.
     *
     * @param dpsId              The ID of the {@link DPS}.
     * @param intituleCompetence The 'intitule' (title) of the {@link Competence} to
     *                           remove.
     * @return The number of rows affected (1 if the requirement was removed, 0 if
     *         it didn't exist).
     *         Returns -1 if an {@link SQLException} occurs.
     */
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

    /**
     * Private helper method to map a {@link ResultSet} row to a {@link DPS} object,
     * with an option to fetch related 'ABesoin' competence requirements.
     * Note: This method is currently not used directly in the provided snippet but
     * is available
     * for internal use if needed for more complex fetching logic.
     *
     * @param rs             The {@link ResultSet} positioned at the row to map.
     * @param fetchRelations If 'true', competence requirements from 'ABesoin' are
     *                       fetched and set.
     * @return A new {@link DPS} object. Returns 'null' if essential base data
     *         (Site, Sport, Journee) is missing.
     * @throws SQLException If an error occurs while accessing the
     *                      {@link ResultSet}.
     */
    private DPS mapResultSetToDPS(ResultSet rs, boolean fetchRelations) throws SQLException {
        Site site = siteDAO.findByCode(rs.getString("lieu"));
        Sport sport = sportDAO.findByCode(rs.getString("sport"));
        Journee journee = journeeDAO.findByDate(rs.getDate("jour").toLocalDate());

        DPS dps = new DPS(rs.getLong("id"), new int[]{rs.getInt("horaire_depart_heure"), rs.getInt("horaire_depart_minute")},
                new int[]{rs.getInt("horaire_fin_heure"), rs.getInt("horaire_fin_minute")}, site, journee, sport);

        if (fetchRelations) {
            dps.setCompetencesRequises(findRequiredCompetencesForDps(dps.getId()));
        }
        return dps;
    }
}