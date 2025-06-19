package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Competence;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO (Data Access Object) pour la gestion des entités Competence.
 * <p>
 * Une compétence représente un savoir-faire ou une qualification. Les compétences
 * peuvent avoir des prérequis, qui sont d'autres compétences à acquérir au préalable.
 * Ces relations sont gérées via la table 'Necessite'.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class CompetenceDAO extends DAO<Competence> {

    /**
     * Récupère toutes les compétences de la base de données.
     * Pour chaque compétence, ses prérequis sont également chargés.
     *
     * @return Une liste de toutes les compétences, chacune avec ses prérequis potentiels.
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
                // "Hydrate" l'objet en chargeant ses prérequis depuis la base de données.
                comp.setPrerequisites(findPrerequisitesFor(comp.getIntitule()));
                competences.add(comp);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Competences: " + e.getMessage());
        }
        return competences;
    }

    /**
     * Recherche une compétence spécifique par son intitulé.
     * Si elle est trouvée, ses prérequis sont également chargés.
     *
     * @param intitule L'intitulé unique de la compétence à trouver.
     * @return L'objet Competence si trouvé, avec ses prérequis ; sinon `null`.
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
                    comp.setPrerequisites(findPrerequisitesFor(intitule));
                    return comp;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Competence by intitule " + intitule + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Recherche tous les prérequis pour une compétence donnée.
     * Cette méthode interroge la table de jointure 'Necessite'. Pour éviter les
     * boucles infinies de chargement (si A requiert B et B requiert A), elle utilise
     * une méthode de recherche "simple" qui ne charge pas les prérequis des prérequis.
     *
     * @param intituleCompetence L'intitulé de la compétence pour laquelle trouver les prérequis.
     * @return Un ensemble de compétences qui sont des prérequis pour la compétence spécifiée.
     */
    public Set<Competence> findPrerequisitesFor(String intituleCompetence) {
        String sql = "SELECT competenceRequise FROM Necessite WHERE intituleCompetence = ?";
        Set<Competence> prerequisites = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, intituleCompetence);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // findByIntituleSimple est utilisé pour éviter de recharger récursivement les prérequis.
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
     * Ajoute une relation de prérequis entre deux compétences dans la table 'Necessite'.
     *
     * @param intituleCompetence L'intitulé de la compétence principale.
     * @param intitulePrerequis  L'intitulé de la compétence qui est un prérequis.
     * @return Le nombre de lignes affectées (1 en cas de succès, -1 en cas d'erreur).
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
     * Supprime une relation de prérequis entre deux compétences de la table 'Necessite'.
     *
     * @param intituleCompetence L'intitulé de la compétence principale.
     * @param intitulePrerequis  L'intitulé du prérequis à supprimer.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvée, -1 si erreur).
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

    /**
     * Crée un nouvel enregistrement dans la table 'Competence'.
     * Cette méthode ne crée que la compétence elle-même ; les relations de prérequis
     * doivent être ajoutées séparément.
     *
     * @param competence L'objet Competence à persister.
     * @return Le nombre de lignes affectées (1 en cas de succès, -1 en cas d'erreur).
     */
    @Override
    public int create(Competence competence) {
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
     * Supprime une compétence de la base de données en utilisant son objet.
     * Appelle en interne 'deleteByIntitule'.
     *
     * @param competence L'objet Competence à supprimer.
     * @return Le nombre de lignes affectées.
     */
    @Override
    public int delete(Competence competence) {
        return deleteByIntitule(competence.getIntitule());
    }

    /**
     * Supprime une compétence de la base de données par son intitulé.
     * Si la contrainte 'ON DELETE CASCADE' est configurée dans la BDD,
     * les entrées de prérequis associées seront également supprimées.
     *
     * @param intitule L'intitulé de la compétence à supprimer.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvée, -1 si erreur).
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

    /**
     * Version "simple" de `findByIntitule` qui récupère une compétence sans charger ses prérequis.
     * Principalement utilisée en interne pour éviter les boucles infinies de chargement.
     *
     * @param intitule L'intitulé de la compétence à trouver.
     * @return L'objet Competence si trouvé ; sinon `null`.
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
     * Transforme une ligne d'un ResultSet en un objet Competence.
     *
     * @param rs Le ResultSet positionné sur la ligne à traiter.
     * @return Un nouvel objet Competence.
     * @throws SQLException Si une erreur se produit lors de l'accès au ResultSet.
     */
    private Competence mapResultSetToCompetence(ResultSet rs) throws SQLException {
        return new Competence(rs.getString("intitule"));
    }

    /**
     * Non supporté. La clé primaire de Competence est une chaîne de caractères ('intitule').
     * Utilisez `findByIntitule(String)`.
     */
    @Override
    public Competence findByID(Long id) {
        throw new UnsupportedOperationException("Competence ID is String. Use findByIntitule(String).");
    }

    /**
     * Non supporté. La mise à jour de la clé primaire ('intitule') n'est pas une
     * pratique standard. Il est préférable de supprimer l'ancienne compétence et
     * d'en créer une nouvelle.
     */
    @Override
    public int update(Competence element) {
        throw new UnsupportedOperationException(
                "Updating Competence PK is not supported. Delete and re-create if needed, or implement a specific method.");
    }
}