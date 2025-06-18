package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * Data Access Object (DAO) for managing {@link Affectation} entities.
 * An {@link Affectation} represents the assignment of a {@link Secouriste}
 * with a specific {@link Competence} to a {@link DPS} (Dispositif Prévisionnel
 * de Secours).
 * This class handles database operations such as creating, retrieving, and
 * deleting affectations.
 *
 * @author Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.1
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
     * @return A {@link List} of all {@link Affectation} objects. The list may be
     *         empty
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
     *         The list may be empty if no affectations are found for this DPS or if
     *         an error occurs.
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
     * Finds all {@link Affectation} records associated with a specific
     * {@link Secouriste}.
     *
     * @param secouristeId The ID of the {@link Secouriste} for which to find
     *                     affectations.
     * @return A {@link List} of {@link Affectation} objects for the given
     *         Secouriste ID.
     *         The list may be empty if no affectations are found for this
     *         secouriste or if an error occurs.
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
     * Helper method to map a row from a {@link ResultSet} to an {@link Affectation}
     * object.
     * It retrieves IDs and intitule from the current row, then uses {@link DPSDAO},
     * {@link SecouristeDAO}, and {@link CompetenceDAO} to fetch the full related
     * objects.
     *
     * @param rs The {@link ResultSet} positioned at the row to map.
     * @return An {@link Optional} containing the mapped {@link Affectation} if all
     *         related
     *         entities are found and valid; otherwise, an empty {@link Optional}.
     * @throws SQLException If an error occurs while accessing the
     *                      {@link ResultSet}.
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
     * @param affectation The {@link Affectation} object to persist. Must not be
     *                    'null'.
     * @return The number of rows affected (typically 1 on success, or -1 if an
     *         SQLException occurs).
     * @throws IllegalArgumentException if 'affectation' is 'null' or its internal
     *                                  components are not set.
     */
    @Override
    public int create(Affectation affectation) {
        if (affectation == null)
            throw new IllegalArgumentException("Affectation cannot be null.");
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
     * The deletion is based on the composite key (DPS ID, Secouriste ID, Competence
     * intitule)
     * derived from the provided {@link Affectation} object.
     *
     * @param affectation The {@link Affectation} object to delete. Must not be
     *                    'null'.
     * @return The number of rows affected (typically 1 on success, 0 if no matching
     *         record was found,
     *         or -1 if an SQLException occurs).
     * @throws IllegalArgumentException if 'affectation' is 'null' or its internal
     *                                  components are not set.
     */
    @Override
    public int delete(Affectation affectation) {
        if (affectation == null)
            throw new IllegalArgumentException("Affectation cannot be null.");
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

    /**
     * Récupère toutes les affectations pour un secouriste donné entre deux dates.
     * C'est une version optimisée qui ne fait qu'une seule requête complexe.
     * CORRIGÉ : Utilisation d'alias SQL (AS) pour éviter les conflits de noms de colonnes.
     *
     * @param secouristeId L'ID du secouriste.
     * @param startDate    La date de début de la période.
     * @param endDate      La date de fin de la période.
     * @return Une liste d'objets Affectation.
     */
    public List<Affectation> findAffectationsForSecouristeBetweenDates(long secouristeId, LocalDate startDate,
            LocalDate endDate) {
        List<Affectation> affectations = new ArrayList<>();
        // CORRIGÉ : Ajout des alias (AS) pour les colonnes portant le même nom
        String sql = "SELECT " +
            "  Affectation.idSecouriste, Affectation.intituleCompetence, Affectation.idDPS, " +
            "  DPS.horaire_depart_heure, DPS.horaire_depart_minute, DPS.horaire_fin_heure, DPS.horaire_fin_minute, DPS.jour AS dps_jour, " +
            "  Secouriste.nom AS secouriste_nom, Secouriste.prenom, Secouriste.dateNaissance, Secouriste.email, Secouriste.tel, Secouriste.adresse, " +
            "  Site.code AS site_code, Site.nom AS site_nom, Site.longitude, Site.latitude, " +
            "  Sport.code AS sport_code, Sport.nom AS sport_nom " +
            "FROM " +
            "  Affectation " +
            "JOIN Secouriste ON Affectation.idSecouriste = Secouriste.id " +
            "JOIN DPS ON Affectation.idDPS = DPS.id " +
            "JOIN Site ON DPS.lieu = Site.code " +
            "JOIN Sport ON DPS.sport = Sport.code " +
            "WHERE Affectation.idSecouriste = ? AND DPS.jour BETWEEN ? AND ?";


        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, secouristeId);
            pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            pstmt.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // CORRIGÉ : Utilisation des alias pour lire les données du ResultSet
                    Secouriste secouriste = new Secouriste(rs.getLong("idSecouriste"), rs.getString("secouriste_nom"),
                            rs.getString("prenom"), rs.getDate("dateNaissance"), rs.getString("email"),
                            rs.getString("tel"), rs.getString("adresse"));
                    
                    Site site = new Site(rs.getString("site_code"), rs.getString("site_nom"), rs.getFloat("longitude"),
                            rs.getFloat("latitude"));

                    Sport sport = new Sport(rs.getString("sport_code"), rs.getString("sport_nom"));
                    
                    Competence competence = new Competence(rs.getString("intituleCompetence"));

                    Journee journee = new Journee(rs.getDate("dps_jour").toLocalDate());

                    int[] horaireDepart = { rs.getInt("horaire_depart_heure"), rs.getInt("horaire_depart_minute") };
                    int[] horaireFin = { rs.getInt("horaire_fin_heure"), rs.getInt("horaire_fin_minute") };

                    DPS dps = new DPS(rs.getLong("idDPS"), horaireDepart, horaireFin, site, journee, sport);

                    affectations.add(new Affectation(dps, secouriste, competence));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des affectations pour le secouriste " + secouristeId);
            e.printStackTrace();
        }
        return affectations;
    }

    @Override
    public Affectation findByID(Long id) {
        throw new UnsupportedOperationException("Affectation has a composite PK. Use findByCompositeKey().");
    }

    @Override
    public int update(Affectation element) {
        throw new UnsupportedOperationException(
                "Updating an Affectation (join table record) is typically done by deleting and creating a new one.");
    }
}