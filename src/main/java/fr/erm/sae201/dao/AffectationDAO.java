package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) pour la gestion des entités Affectation.
 * 
 * Une affectation représente l'assignation d'un Secouriste avec une Compétence
 * spécifique à un DPS (Dispositif Prévisionnel de Secours). Cette classe gère
 * les opérations de base de données comme la création, la recherche et la suppression.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.2
 */
public class AffectationDAO extends DAO<Affectation> {

    private final DPSDAO dpsDAO = new DPSDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

    /**
     * Récupère toutes les affectations de la base de données.
     * Pour chaque enregistrement, elle reconstruit l'objet Affectation complet
     * en utilisant les DAOs correspondants.
     *
     * @return Une liste de toutes les affectations. La liste peut être vide.
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
     * Compte le nombre d'affectations pour un DPS donné.
     *
     * @param dpsId L'ID du DPS.
     * @return Le nombre d'affectations.
     */
    public int countAffectationsForDps(long dpsId) {
        String sql = "SELECT COUNT(*) FROM Affectation WHERE idDPS = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dpsId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting affectations for DPS " + dpsId + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Recherche toutes les affectations associées à un DPS spécifique.
     *
     * @param dpsId L'ID du DPS pour lequel trouver les affectations.
     * @return Une liste d'objets Affectation. Peut être vide.
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
     * Recherche toutes les affectations associées à un Secouriste spécifique.
     *
     * @param secouristeId L'ID du secouriste pour lequel trouver les affectations.
     * @return Une liste d'objets Affectation. Peut être vide.
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
     * Méthode utilitaire pour transformer une ligne d'un ResultSet en un objet Affectation.
     * Elle récupère les objets complets DPS, Secouriste et Competence via leurs DAOs respectifs.
     *
     * @param rs Le ResultSet positionné sur la ligne à traiter.
     * @return Un Optional contenant l'objet Affectation si toutes les entités sont trouvées, sinon un Optional vide.
     * @throws SQLException Si une erreur se produit lors de l'accès au ResultSet.
     */
    private Optional<Affectation> mapResultSetToAffectation(ResultSet rs) throws SQLException {
        DPS dps = dpsDAO.findByID(rs.getLong("idDPS"));
        Secouriste secouriste = secouristeDAO.findByID(rs.getLong("idSecouriste"));
        Competence competence = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
        if (dps != null && secouriste != null && competence != null) {
            return Optional.of(new Affectation(dps, secouriste, competence));
        }
        return Optional.empty();
    }

    /**
     * Crée un nouvel enregistrement d'affectation dans la base de données.
     *
     * @param affectation L'objet Affectation à persister. Ne doit pas être null.
     * @return Le nombre de lignes affectées (1 en cas de succès, -1 en cas d'erreur).
     * @throws IllegalArgumentException si l'objet affectation est null.
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
     * Supprime une affectation de la base de données en se basant sur sa clé primaire composite.
     *
     * @param affectation L'objet Affectation à supprimer. Ne doit pas être null.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvée, -1 si erreur).
     * @throws IllegalArgumentException si l'objet affectation est null.
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
     * Cette méthode utilise une seule requête optimisée avec des jointures pour
     * construire les objets complets et éviter de multiples appels à la base de données.
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
                "  DPS.horaire_depart_heure, DPS.horaire_depart_minute, DPS.horaire_fin_heure, DPS.horaire_fin_minute, DPS.jour AS dps_jour, "
                +
                "  Secouriste.nom AS secouriste_nom, Secouriste.prenom, Secouriste.dateNaissance, Secouriste.email, Secouriste.tel, Secouriste.adresse, "
                +
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

    /**
     * Remplace toutes les affectations pour un DPS donné par une nouvelle liste.
     * Cette opération est transactionnelle : soit toutes les modifications sont appliquées, soit aucune ne l'est.
     *
     * @param dpsId L'ID du DPS pour lequel remplacer les affectations.
     * @param nouvellesAffectations La nouvelle liste d'affectations à enregistrer.
     * @return `true` si la transaction a réussi, `false` sinon.
     */
    public boolean replaceAffectationsForDps(long dpsId, List<Affectation> nouvellesAffectations) {
        Connection conn = null;
        try {
            conn = getConnection();
            // Démarre une transaction pour garantir l'intégrité des données :
            // soit la suppression et les insertions réussissent, soit tout est annulé.
            conn.setAutoCommit(false);

            String deleteSql = "DELETE FROM Affectation WHERE idDPS = ?";
            try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteSql)) {
                pstmtDelete.setLong(1, dpsId);
                pstmtDelete.executeUpdate();
            }

            String insertSql = "INSERT INTO Affectation (idDPS, idSecouriste, intituleCompetence) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertSql)) {
                for (Affectation affectation : nouvellesAffectations) {
                    pstmtInsert.setLong(1, affectation.getDps().getId());
                    pstmtInsert.setLong(2, affectation.getSecouriste().getId());
                    pstmtInsert.setString(3, affectation.getCompetence().getIntitule());
                    pstmtInsert.addBatch();
                }
                pstmtInsert.executeBatch();
            }

            // Valide la transaction si tout s'est bien passé
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction error during affectation replacement: " + e.getMessage());
            if (conn != null) {
                try {
                    // Annule la transaction en cas d'erreur
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    // Rétablit le mode d'auto-commit par défaut
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Récupère toutes les affectations pour une date spécifique.
     *
     * @param date La date pour laquelle rechercher les affectations.
     * @return Une liste d'objets Affectation.
     */
    public List<Affectation> findAllByDate(LocalDate date) {
    List<Affectation> affectations = new ArrayList<>();
    String sql = "SELECT a.idDPS, a.idSecouriste, a.intituleCompetence " +
                 "FROM Affectation a JOIN DPS d ON a.idDPS = d.id " +
                 "WHERE d.jour = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setDate(1, java.sql.Date.valueOf(date));
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                mapResultSetToAffectation(rs).ifPresent(affectations::add);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return affectations;
}

    /**
     * Supprime toutes les affectations pour un ID de DPS donné.
     *
     * @param dpsId L'ID du DPS.
     * @return Le nombre de lignes affectées, ou -1 en cas d'erreur.
     */
    public int deleteAllAffectationsForDps(long dpsId) {
        String sql = "DELETE FROM Affectation WHERE idDPS = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dpsId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting all affectations for DPS ID " + dpsId + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Non supporté. L'entité Affectation a une clé primaire composite.
     * Utilisez une méthode de recherche plus spécifique.
     */
    @Override
    public Affectation findByID(Long id) {
        throw new UnsupportedOperationException("Affectation has a composite PK. Use findByCompositeKey().");
    }

    /**
     * Non supporté. La mise à jour d'une affectation (table de jointure)
     * se fait généralement en supprimant l'ancienne et en créant une nouvelle.
     */
    @Override
    public int update(Affectation element) {
        throw new UnsupportedOperationException(
                "Updating an Affectation (join table record) is typically done by deleting and creating a new one.");
    }
}