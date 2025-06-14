package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Competence;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object (DAO) for managing {@link Competence} entities.
 * A {@link Competence} represents a skill or qualification. Competencies can
 * have prerequisites,
 * which are other competencies that must be acquired first. These relationships
 * are managed
 * through the 'Necessite' table.
 * This class handles CRUD operations for competencies and manages their
 * prerequisite relationships.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.0
 */
public class CompetenceDAO extends DAO<Competence> {

    /**
     * Retrieves all {@link Competence} records from the database.
     * For each competence, its prerequisites are also loaded and set.
     *
     * @return A {@link List} of all {@link Competence} objects, each potentially
     *         with its set of prerequisites populated. The list may be empty
     *         if no competencies are found or if an error occurs.
     */
    @Override
    public List<Competence> findAll() {
        String sql = "SELECT intitule FROM Competence";
        List<Competence> competences = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Competence comp = mapResultSetToCompetence(rs);
                // On "hydrate" l'objet avec ses prérequis
                comp.setPrerequisites(findPrerequisitesFor(comp.getIntitule()));
                competences.add(comp);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Competences: " + e.getMessage());
        }
        return competences;
    }

    /**
     * Finds a specific {@link Competence} by its 'intitule' (name).
     * If found, its prerequisites are also loaded and set.
     *
     * @param intitule The unique title of the competence to find.
     *                 Should not be null or empty.
     * @return The {@link Competence} object if found, with its prerequisites
     *         populated;
     *         'null' if no competence with the given 'intitule' exists, if
     *         'intitule' is invalid,
     *         or if an error occurs.
     */
    public Competence findByIntitule(String intitule) {
        if (intitule == null || intitule.trim().isEmpty())
            return null;
        String sql = "SELECT intitule FROM Competence WHERE intitule = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intitule);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Competence comp = mapResultSetToCompetence(rs);
                    // On "hydrate" l'objet avec ses prérequis
                    comp.setPrerequisites(findPrerequisitesFor(intitule));
                    return comp;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Competence by intitule " + intitule + ": " + e.getMessage());
        }
        return null;
    }

    // --- Prerequisite Relationship Management (Necessite Table) ---

    /**
     * Finds all prerequisite competencies for a given competence.
     * This method queries the 'Necessite' join table.
     * To avoid potential infinite loops in prerequisite loading (e.g., A requires
     * B, B requires A),
     * this method uses 'findByIntituleSimple' to load prerequisite objects, which
     * does not recursively load their own prerequisites.
     *
     * @param intituleCompetence The 'intitule' of the competence for which to find
     *                           prerequisites.
     * @return A {@link Set} of {@link Competence} objects that are prerequisites
     *         for the specified competence.
     *         The set may be empty if there are no prerequisites or if an error
     *         occurs.
     */
    public Set<Competence> findPrerequisitesFor(String intituleCompetence) {
        String sql = "SELECT competenceRequise FROM Necessite WHERE intituleCompetence = ?";
        Set<Competence> prerequisites = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intituleCompetence);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Pour chaque prérequis trouvé, on charge l'objet Competence complet
                    // Attention à ne pas créer de boucle infinie si A requiert B et B requiert A.
                    // findByIntituleSimple évite de recharger les prérequis des prérequis.
                    Competence prereq = findByIntituleSimple(rs.getString("competenceRequise"));
                    if (prereq != null) {
                        prerequisites.add(prereq);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding prerequisites for " + intituleCompetence + ": " + e.getMessage());
        }
        return prerequisites;
    }

    /**
     * Adds a prerequisite relationship between two competencies in the 'Necessite'
     * table.
     *
     * @param intituleCompetence The 'intitule' of the main competence.
     * @param intitulePrerequis  The 'intitule' of the competence that is a
     *                           prerequisite.
     * @return The number of rows affected (typically 1 on success, or -1 if an
     *         SQLException occurs).
     */
    public int addPrerequisite(String intituleCompetence, String intitulePrerequis) {
        String sql = "INSERT INTO Necessite (intituleCompetence, competenceRequise) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intituleCompetence);
            pstmt.setString(2, intitulePrerequis);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding prerequisite: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Removes a prerequisite relationship between two competencies from the
     * 'Necessite' table.
     *
     * @param intituleCompetence The 'intitule' of the main competence.
     * @param intitulePrerequis  The 'intitule' of the prerequisite competence to
     *                           remove.
     * @return The number of rows affected (typically 1 on success, 0 if no such
     *         relationship existed,
     *         or -1 if an SQLException occurs).
     */
    public int removePrerequisite(String intituleCompetence, String intitulePrerequis) {
        String sql = "DELETE FROM Necessite WHERE intituleCompetence = ? AND competenceRequise = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intituleCompetence);
            pstmt.setString(2, intitulePrerequis);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing prerequisite: " + e.getMessage());
            return -1;
        }
    }

    // --- Basic CRUD Methods ---

    /**
     * Creates a new {@link Competence} record in the 'Competence' table.
     * This method only creates the competence itself; prerequisite relationships
     * must be added separately using 'addPrerequisite'.
     *
     * @param competence The {@link Competence} object to persist. Its 'intitule'
     *                   must be set.
     * @return The number of rows affected (typically 1 on success, or -1 if an
     *         SQLException occurs).
     */
    @Override
    public int create(Competence competence) {
        // La création ne concerne que la table Competence, pas les relations.
        // Les relations sont ajoutées après via addPrerequisite.
        String sql = "INSERT INTO Competence (intitule) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, competence.getIntitule());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Competence " + competence.getIntitule() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Deletes a {@link Competence} from the database using its object.
     * This internally calls 'deleteByIntitule'.
     * If 'ON DELETE CASCADE' is set up in the database for the 'Necessite' table's
     * foreign keys
     * referencing 'Competence', related prerequisite entries will also be deleted.
     *
     * @param competence The {@link Competence} object to delete. Its 'intitule'
     *                   must be set.
     * @return The number of rows affected in the 'Competence' table.
     */
    @Override
    public int delete(Competence competence) {
        return deleteByIntitule(competence.getIntitule());
    }

    /**
     * Deletes a {@link Competence} from the database by its 'intitule'.
     * If 'ON DELETE CASCADE' is set up in the database for the 'Necessite' table's
     * foreign keys
     * referencing 'Competence', related prerequisite entries will also be deleted.
     *
     * @param intitule The 'intitule' of the competence to delete.
     * @return The number of rows affected in the 'Competence' table (typically 1 on
     *         success,
     *         0 if no competence with that 'intitule' was found, or -1 if an
     *         SQLException occurs).
     */
    public int deleteByIntitule(String intitule) {
        String sql = "DELETE FROM Competence WHERE intitule = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intitule);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Competence " + intitule + ": " + e.getMessage());
            return -1;
        }
    }

    // --- Utility and Unsupported Methods ---

    /**
     * A "simple" version of 'findByIntitule' that retrieves a {@link Competence}
     * without recursively loading its prerequisites.
     * This is primarily used internally by 'findPrerequisitesFor' to prevent
     * potential infinite loops when competencies have circular dependencies.
     *
     * @param intitule The 'intitule' of the competence to find.
     * @return The {@link Competence} object if found; 'null' otherwise or if an
     *         error occurs.
     */
    private Competence findByIntituleSimple(String intitule) {
        String sql = "SELECT intitule FROM Competence WHERE intitule = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intitule);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompetence(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding simple Competence by intitule " + intitule + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Maps a row from a {@link ResultSet} to a {@link Competence} object.
     * Assumes the {@link ResultSet} contains an 'intitule' column.
     *
     * @param rs The {@link ResultSet} currently positioned at the row to map.
     * @return A new {@link Competence} object.
     * @throws SQLException If an error occurs while accessing the
     *                      {@link ResultSet}.
     */
    private Competence mapResultSetToCompetence(ResultSet rs) throws SQLException {
        return new Competence(rs.getString("intitule"));
    }

    /**
     * This method is not supported for {@link Competence} as its primary key is a
     * String ('intitule').
     * Use {@link #findByIntitule(String)} instead.
     *
     * @param id The ID (not used for {@link Competence}, which uses a String PK).
     * @return Always throws {@link UnsupportedOperationException}.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public Competence findByID(Long id) {
        throw new UnsupportedOperationException("Competence ID is String. Use findByIntitule(String).");
    }

    /**
     * Updating the primary key ('intitule') of a {@link Competence} is generally
     * not supported.
     * If the 'intitule' needs to be changed, it's often better to delete the old
     * competence
     * and create a new one with the new 'intitule', then re-establish any
     * prerequisite relationships.
     * A specific method for renaming (which would involve updating related
     * 'Necessite' entries)
     * could be implemented if required.
     *
     * @param element The {@link Competence} to update (not directly supported for
     *                PK change).
     * @return Always throws {@link UnsupportedOperationException}.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public int update(Competence element) {
        throw new UnsupportedOperationException(
                "Updating Competence PK is not supported. Delete and re-create if needed, or implement a specific method.");
    }
}