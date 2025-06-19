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
 * DAO (Data Access Object) pour la gestion des entités Secouriste.
 * 
 * Un Secouriste possède des informations personnelles, un ensemble de compétences
 * et un ensemble de journées où il est disponible. Ce DAO gère les opérations CRUD
 * pour les secouristes et leurs relations avec les Compétences (table 'Possede')
 * et les disponibilités (table 'EstDisponible').
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SecouristeDAO extends DAO<Secouriste> {

    private final CompetenceDAO competenceDAO = new CompetenceDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();

    /**
     * Compte le nombre total de secouristes dans la base de données.
     *
     * @return Le nombre total de secouristes.
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Secouriste";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting secouristes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Compte le nombre de secouristes correspondant à une recherche.
     * Si la recherche est vide, compte tous les secouristes.
     *
     * @param query Le terme de recherche.
     * @return Le nombre de secouristes correspondants.
     */
    public int countFiltered(String query) {
        if (query == null || query.trim().isEmpty()) {
            return countAll();
        }
        String sql = "SELECT COUNT(*) FROM Secouriste WHERE CONCAT(nom, ' ', prenom, email) LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting filtered secouristes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Récupère une "page" de secouristes filtrés par un terme de recherche.
     *
     * @param query Le terme de recherche.
     * @param offset Le point de départ (nombre d'éléments à sauter).
     * @param limit Le nombre d'éléments à récupérer.
     * @return Une liste de secouristes filtrés et paginés.
     */
    public List<Secouriste> findFilteredAndPaginated(String query, int offset, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return findPaginated(offset, limit);
        }

        List<Secouriste> secouristes = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste " +
                     "WHERE CONCAT(nom, ' ', prenom, email) LIKE ? " +
                     "ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + query + "%");
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    secouristes.add(mapResultSetToSecouriste(rs, true));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding filtered and paginated Secouristes: " + e.getMessage());
        }
        return secouristes;
    }

    /**
     * Récupère une "page" de secouristes depuis la base de données.
     *
     * @param offset Le point de départ (nombre d'éléments à sauter).
     * @param limit Le nombre maximum d'éléments à récupérer.
     * @return Une liste de secouristes pour la page demandée.
     */
    public List<Secouriste> findPaginated(int offset, int limit) {
        List<Secouriste> secouristes = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // "Hydrate" l'objet avec ses relations (compétences, disponibilités).
                    secouristes.add(mapResultSetToSecouriste(rs, true));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding paginated Secouristes: " + e.getMessage());
        }
        return secouristes;
    }

    /**
     * Recherche un secouriste spécifique par son ID unique.
     * L'objet Secouriste est "hydraté" avec ses compétences et disponibilités.
     *
     * @param id L'ID unique du secouriste à trouver.
     * @return L'objet Secouriste si trouvé ; sinon `null`.
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
                    return mapResultSetToSecouriste(rs, true);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Secouriste by ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère tous les secouristes de la base de données.
     * Chaque objet Secouriste est "hydraté" avec ses compétences et disponibilités.
     *
     * @return Une liste de tous les objets Secouriste trouvés.
     */
    @Override
    public List<Secouriste> findAll() {
        List<Secouriste> secouristes = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                secouristes.add(mapResultSetToSecouriste(rs, true));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Secouristes: " + e.getMessage());
        }
        return secouristes;
    }

    /**
     * Recherche toutes les compétences possédées par un secouriste.
     *
     * @param secouristeId L'ID du secouriste.
     * @return Un ensemble d'objets Competence.
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
     * Ajoute une compétence à un secouriste dans la table 'Possede'.
     *
     * @param secouristeId L'ID du secouriste.
     * @param intituleCompetence L'intitulé de la compétence à ajouter.
     * @return Le nombre de lignes affectées (1 si succès, -1 si erreur).
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
     * Supprime une compétence d'un secouriste dans la table 'Possede'.
     *
     * @param secouristeId L'ID du secouriste.
     * @param intituleCompetence L'intitulé de la compétence à supprimer.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvée, -1 si erreur).
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

    /**
     * Recherche toutes les journées où un secouriste est disponible.
     *
     * @param secouristeId L'ID du secouriste.
     * @return Un ensemble d'objets Journee représentant les disponibilités.
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
     * Ajoute une disponibilité (une journée) pour un secouriste.
     * S'assure d'abord que la journée existe dans la table 'Journee'.
     *
     * @param secouristeId L'ID du secouriste.
     * @param date La date de la disponibilité.
     * @return Le nombre de lignes affectées (1 si succès, -1 si erreur).
     */
    public int addAvailability(long secouristeId, LocalDate date) {
        // S'assure que la journée existe dans la table Journee pour éviter une erreur de clé étrangère.
        String sqlEnsureJournee = "INSERT IGNORE INTO Journee (jour) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmtEnsure = conn.prepareStatement(sqlEnsureJournee)) {
            pstmtEnsure.setDate(1, java.sql.Date.valueOf(date));
            pstmtEnsure.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error ensuring Journee exists for date " + date + ": " + e.getMessage());
            return -1;
        }

        String sqlInsertDispo = "INSERT INTO EstDisponible (idSecouriste, jour) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmtDispo = conn.prepareStatement(sqlInsertDispo)) {
            pstmtDispo.setLong(1, secouristeId);
            pstmtDispo.setDate(2, java.sql.Date.valueOf(date));
            return pstmtDispo.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding availability: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Supprime une disponibilité pour un secouriste.
     *
     * @param secouristeId L'ID du secouriste.
     * @param date La date de la disponibilité à supprimer.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvée, -1 si erreur).
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

    /**
     * Transforme une ligne d'un ResultSet en un objet Secouriste.
     *
     * @param rs Le ResultSet positionné sur la ligne à traiter.
     * @param fetchRelations Si `true`, les compétences et disponibilités sont aussi chargées.
     * @return Un nouvel objet Secouriste.
     * @throws SQLException Si une erreur survient lors de l'accès au ResultSet.
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
     * Crée un nouvel enregistrement de Secouriste dans la base de données.
     * Renvoie l'ID généré pour le nouveau secouriste.
     *
     * @param secouriste L'objet Secouriste à persister.
     * @return L'ID du secouriste nouvellement créé, ou -1 en cas d'erreur.
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
            return (int) secouriste.getId();
        } catch (SQLException e) {
            System.err.println("Error creating Secouriste: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Met à jour les informations personnelles d'un secouriste.
     * Ne modifie ni les compétences, ni les disponibilités.
     *
     * @param secouriste L'objet Secouriste avec les informations mises à jour.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
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
     * Supprime un secouriste de la base de données en fonction de son ID.
     *
     * @param secouriste L'objet Secouriste à supprimer.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
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