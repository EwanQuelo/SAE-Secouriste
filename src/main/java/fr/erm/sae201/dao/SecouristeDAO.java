package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Journee;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object (DAO) for managing {@link Secouriste} (Rescuer) entities.
 * A {@link Secouriste} has personal details, a set of {@link Competence}s they
 * possess,
 * and a set of {@link Journee}s (days) on which they are available.
 * This class handles CRUD operations for Secouriste records and manages their
 * relationships with Competences (via 'Possede' table) and Availabilities
 * (via 'EstDisponible' table).
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class SecouristeDAO extends DAO<Secouriste> {

    // DAOs for related entities, used when managing relationships
    private final CompetenceDAO competenceDAO = new CompetenceDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();

    /**
     * Finds a specific {@link Secouriste} by their unique ID.
     * The retrieved Secouriste object is "hydrated" with its associated
     * {@link Competence}s and availabilities ({@link Journee}s).
     *
     * @param id The unique ID of the {@link Secouriste} to find. Can be 'null'.
     * @return The {@link Secouriste} object if found, fully populated with their
     *         competences and availabilities; 'null' if no Secouriste with the
     *         given ID
     *         exists, if 'id' is 'null', or if an error occurs.
     */
    @Override
    public Secouriste findByID(Long id) {
        if (id == null)
            return null;
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSecouriste(rs, true); // Hydrate avec les relations
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Secouriste by ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all {@link Secouriste} records from the database.
     * Each Secouriste object in the returned list is "hydrated" with its associated
     * {@link Competence}s and availabilities ({@link Journee}s).
     *
     * @return A {@link List} of all {@link Secouriste} objects found, fully
     *         populated.
     *         The list may be empty if no Secouristes exist or if an error occurs.
     */
    @Override
    public List<Secouriste> findAll() {
        List<Secouriste> secouristes = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                secouristes.add(mapResultSetToSecouriste(rs, true)); // Hydrate avec les relations
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Secouristes: " + e.getMessage());
        }
        return secouristes;
    }

    // --- Relationship Management: Possede (Secouriste <-> Competence) ---

    /**
     * Finds all {@link Competence}s possessed by a specific {@link Secouriste}.
     * This queries the 'Possede' join table.
     *
     * @param secouristeId The ID of the {@link Secouriste}.
     * @return A {@link Set} of {@link Competence} objects possessed by the
     *         Secouriste.
     *         The set may be empty if the Secouriste has no competences or if an
     *         error occurs.
     */
    public Set<Competence> findCompetencesForSecouriste(long secouristeId) {
        Set<Competence> competences = new HashSet<>();
        String sql = "SELECT intituleCompetence FROM Possede WHERE idSecouriste = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Competence comp = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
                    if (comp != null)
                        competences.add(comp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding competences for secouriste " + secouristeId + ": " + e.getMessage());
        }
        return competences;
    }

    /**
     * Adds a {@link Competence} to a {@link Secouriste} in the 'Possede' table.
     *
     * @param secouristeId       The ID of the {@link Secouriste}.
     * @param intituleCompetence The 'intitule' (title) of the {@link Competence} to
     *                           add.
     * @return The number of rows affected (typically 1 on success, or -1 if an
     *         SQLException occurs,
     *         e.g., if the relationship already exists and there's a unique
     *         constraint).
     */
    public int addCompetenceToSecouriste(long secouristeId, String intituleCompetence) {
        String sql = "INSERT INTO Possede (idSecouriste, intituleCompetence) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            pstmt.setString(2, intituleCompetence);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding competence to secouriste: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Removes a {@link Competence} from a {@link Secouriste} in the 'Possede'
     * table.
     *
     * @param secouristeId       The ID of the {@link Secouriste}.
     * @param intituleCompetence The 'intitule' (title) of the {@link Competence} to
     *                           remove.
     * @return The number of rows affected (1 if the competence was removed, 0 if it
     *         wasn't possessed).
     *         Returns -1 if an SQLException occurs.
     */
    public int removeCompetenceFromSecouriste(long secouristeId, String intituleCompetence) {
        String sql = "DELETE FROM Possede WHERE idSecouriste = ? AND intituleCompetence = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            pstmt.setString(2, intituleCompetence);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing competence from secouriste: " + e.getMessage());
            return -1;
        }
    }

    // --- Relationship Management: EstDisponible (Secouriste <-> Journee) ---

    /**
     * Finds all {@link Journee}s (days) on which a specific {@link Secouriste} is
     * available.
     * This queries the 'EstDisponible' join table.
     *
     * @param secouristeId The ID of the {@link Secouriste}.
     * @return A {@link Set} of {@link Journee} objects representing the
     *         Secouriste's availabilities.
     *         The set may be empty if the Secouriste has no availabilities or if an
     *         error occurs.
     */
    public Set<Journee> findAvailabilitiesForSecouriste(long secouristeId) {
        Set<Journee> journees = new HashSet<>();
        String sql = "SELECT jour FROM EstDisponible WHERE idSecouriste = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    journees.add(new Journee(rs.getDate("jour").toLocalDate()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding availabilities for secouriste " + secouristeId + ": " + e.getMessage());
        }
        return journees;
    }

    /**
     * Adds an availability (a specific {@link Journee}) for a {@link Secouriste}
     * in the 'EstDisponible' table.
     *
     * @param secouristeId The ID of the {@link Secouriste}.
     * @param date         The {@link LocalDate} representing the day of
     *                     availability.
     * @return The number of rows affected (typically 1 on success, or -1 if an
     *         SQLException occurs,
     *         e.g., if the availability already exists and there's a unique
     *         constraint).
     */
    public int addAvailability(long secouristeId, LocalDate date) {
        String sql = "INSERT INTO EstDisponible (idSecouriste, jour) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            pstmt.setDate(2, Date.valueOf(date));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding availability: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Removes an availability (a specific {@link Journee}) for a {@link Secouriste}
     * from the 'EstDisponible' table.
     *
     * @param secouristeId The ID of the {@link Secouriste}.
     * @param date         The {@link LocalDate} of the availability to remove.
     * @return The number of rows affected (1 if availability was removed, 0 if it
     *         didn't exist).
     *         Returns -1 if an SQLException occurs.
     */
    public int removeAvailability(long secouristeId, LocalDate date) {
        String sql = "DELETE FROM EstDisponible WHERE idSecouriste = ? AND jour = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            pstmt.setDate(2, Date.valueOf(date));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing availability: " + e.getMessage());
            return -1;
        }
    }

    // --- Basic CRUD Methods and Utility ---

    /**
     * Maps a row from a {@link ResultSet} to a {@link Secouriste} object.
     * If 'fetchRelations' is true, it also populates the Secouriste's competences
     * and availabilities by calling respective finder methods.
     *
     * @param rs             The {@link ResultSet} positioned at the row to map.
     * @param fetchRelations If 'true', related competences and availabilities are
     *                       fetched.
     * @return A new {@link Secouriste} object, potentially with its relations
     *         populated.
     * @throws SQLException If an error occurs while accessing the
     *                      {@link ResultSet}.
     */
    private Secouriste mapResultSetToSecouriste(ResultSet rs, boolean fetchRelations) throws SQLException {
        Secouriste secouriste = new Secouriste(
                rs.getLong("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getDate("dateNaissance"),
                rs.getString("email"),
                rs.getString("tel"),
                rs.getString("adresse"));
        if (fetchRelations) {
            secouriste.setCompetences(findCompetencesForSecouriste(secouriste.getId()));
            secouriste.setDisponibilites(findAvailabilitiesForSecouriste(secouriste.getId()));
        }
        return secouriste;
    }

    /**
     * Creates a new {@link Secouriste} record in the database.
     * After successful insertion, the generated ID from the database is set
     * back into the provided {@link Secouriste} object.
     * This method returns the generated ID of the new Secouriste.
     * Note: Competences and availabilities must be added separately using methods
     * like
     * 'addCompetenceToSecouriste' and 'addAvailability'.
     *
     * @param secouriste The {@link Secouriste} object to persist. Must not be
     *                   'null'.
     * @return The ID of the newly created Secouriste if successful; -1 if an error
     *         occurs
     *         or if 'secouriste' is 'null'.
     * @throws IllegalArgumentException if 'secouriste' is 'null'.
     */
    @Override
    public int create(Secouriste secouriste) {
        String sql = "INSERT INTO Secouriste (nom, prenom, dateNaissance, email, tel, adresse) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, secouriste.getNom());
            pstmt.setString(2, secouriste.getPrenom());
            pstmt.setDate(3, new java.sql.Date(secouriste.getDateNaissance().getTime()));
            pstmt.setString(4, secouriste.getEmail());
            pstmt.setString(5, secouriste.getTel());
            pstmt.setString(6, secouriste.getAddresse());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        secouriste.setId(generatedKeys.getLong(1));
                    }
                }
            }
            return (int) secouriste.getId(); // Retourne l'ID du nouveau secouriste
        } catch (SQLException e) {
            System.err.println("Error creating Secouriste: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Updates an existing {@link Secouriste}'s personal details in the database.
     * Note: This method does not update competences or availabilities. Those must
     * be
     * managed through their specific add/remove methods.
     *
     * @param secouriste The {@link Secouriste} object with updated information.
     *                   Its ID must be set to identify the record to update. Must
     *                   not be 'null'.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         ID was found).
     *         Returns -1 if an {@link SQLException} occurs or if 'secouriste' is
     *         'null'.
     * @throws IllegalArgumentException if 'secouriste' is 'null'.
     */
    @Override
    public int update(Secouriste secouriste) {
        String sql = "UPDATE Secouriste SET nom = ?, prenom = ?, dateNaissance = ?, email = ?, tel = ?, adresse = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, secouriste.getNom());
            pstmt.setString(2, secouriste.getPrenom());
            pstmt.setDate(3, new java.sql.Date(secouriste.getDateNaissance().getTime()));
            pstmt.setString(4, secouriste.getEmail());
            pstmt.setString(5, secouriste.getTel());
            pstmt.setString(6, secouriste.getAddresse());
            pstmt.setLong(7, secouriste.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Secouriste with ID " + secouriste.getId() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Deletes a {@link Secouriste} from the database based on their ID.
     * Note: This does not automatically handle related records in 'Possede',
     * 'EstDisponible',
     * or 'Affectation' unless 'ON DELETE CASCADE' is set up in the database schema.
     *
     * @param secouriste The {@link Secouriste} object to delete. Its ID must be
     *                   set.
     *                   Can be 'null', in which case -1 is returned.
     * @return The number of rows affected (1 if successful, 0 if no record with the
     *         ID was found).
     *         Returns -1 if 'secouriste' is 'null', its ID is not set, or an
     *         {@link SQLException} occurs.
     */
    @Override
    public int delete(Secouriste secouriste) {
        if (secouriste == null)
            return -1;
        String sql = "DELETE FROM Secouriste WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouriste.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Secouriste with ID " + secouriste.getId() + ": " + e.getMessage());
            return -1;
        }
    }
}