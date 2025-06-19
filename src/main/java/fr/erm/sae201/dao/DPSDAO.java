package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO (Data Access Object) pour la gestion des entités DPS (Dispositif Prévisionnel de Secours).
 * 
 * Un DPS représente un déploiement planifié de secours, détaillant les horaires,
 * le lieu, le sport associé et le jour de l'opération. Ce DAO gère les opérations CRUD
 * pour les enregistrements de DPS et la relation avec les compétences requises
 * via la table de jointure 'ABesoin'.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.1
 */
public class DPSDAO extends DAO<DPS> {

    // DAOs pour les entités liées, utilisés pour reconstruire les objets DPS.
    private final SiteDAO siteDAO = new SiteDAO();
    private final SportDAO sportDAO = new SportDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

    /**
     * Récupère tous les enregistrements de DPS de la base de données.
     *
     * @return Une liste de tous les objets DPS trouvés. La liste peut être vide.
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
     * Recherche un DPS spécifique par son ID unique.
     *
     * @param id L'ID unique du DPS à trouver.
     * @return L'objet DPS si trouvé ; sinon `null`.
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
     * Crée un nouvel enregistrement de DPS dans la base de données.
     * Après une insertion réussie, l'ID généré par la base de données est
     * affecté à l'objet DPS fourni en paramètre.
     *
     * @param dps L'objet DPS à persister. Ne doit pas être null.
     * @return Le nombre de lignes affectées (1 en cas de succès, -1 en cas d'erreur).
     * @throws IllegalArgumentException si l'objet dps est null.
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
     * Récupère tous les DPS dans une plage de dates donnée.
     *
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
     * Met à jour un enregistrement de DPS existant dans la base de données.
     *
     * @param dps L'objet DPS avec les informations mises à jour. Son ID doit être défini.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     * @throws IllegalArgumentException si l'objet dps est null.
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
     * Supprime un enregistrement de DPS de la base de données en fonction de son ID.
     *
     * @param dps L'objet DPS à supprimer. Son ID doit être défini.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     * @throws IllegalArgumentException si l'objet dps est null.
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
     * Transforme une ligne d'un ResultSet en un objet DPS.
     * Cette méthode récupère les objets Site, Sport et Journee associés via
     * leurs DAOs respectifs. Si une de ces entités liées est introuvable,
     * la méthode retourne `null`.
     *
     * @param rs Le ResultSet positionné sur la ligne à traiter.
     * @return Un nouvel objet DPS, ou `null` si des données essentielles sont manquantes.
     * @throws SQLException Si une erreur survient lors de l'accès au ResultSet.
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

    /**
     * Recherche toutes les compétences requises et leur nombre pour un DPS spécifique.
     * Interroge la table de jointure 'ABesoin'.
     *
     * @param dpsId L'ID du DPS pour lequel trouver les exigences.
     * @return Une Map où les clés sont les objets Competence et les valeurs le nombre requis.
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
     * Définit ou met à jour le nombre requis pour une compétence spécifique d'un DPS.
     * @param dpsId              L'ID du DPS.
     * @param intituleCompetence L'intitulé de la compétence.
     * @param nombre             Le nombre de secouristes requis avec cette compétence.
     * @return Le nombre de lignes affectées, ou -1 en cas d'erreur.
     */
    public int setRequiredCompetence(long dpsId, String intituleCompetence, int nombre) {
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
     * Supprime une compétence requise pour un DPS spécifique de la table 'ABesoin'.
     *
     * @param dpsId              L'ID du DPS.
     * @param intituleCompetence L'intitulé de la compétence à supprimer.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvée, -1 si erreur).
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

}