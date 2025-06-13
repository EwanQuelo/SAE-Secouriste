package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Competence;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompetenceDAO extends DAO<Competence> {

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

    public Competence findByIntitule(String intitule) {
        if (intitule == null || intitule.trim().isEmpty()) return null;
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
    
    // --- GESTION DES RELATIONS (Table Necessite) ---

    /**
     * Trouve tous les prérequis pour une compétence donnée.
     * @param intituleCompetence L'intitulé de la compétence.
     * @return Un ensemble de compétences prérequises.
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
     * Ajoute un prérequis à une compétence.
     * @param intituleCompetence L'intitulé de la compétence principale.
     * @param intitulePrerequis L'intitulé de la compétence requise.
     * @return Le nombre de lignes affectées.
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
     * Supprime un prérequis d'une compétence.
     * @param intituleCompetence L'intitulé de la compétence principale.
     * @param intitulePrerequis L'intitulé de la compétence requise.
     * @return Le nombre de lignes affectées.
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
    
    // --- Méthodes CRUD de base (inchangées mais présentées pour la complétude) ---
    
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

    @Override
    public int delete(Competence competence) {
        // ON DELETE CASCADE dans le SQL s'occupera de nettoyer la table Necessite.
        return deleteByIntitule(competence.getIntitule());
    }

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

    // --- Méthodes utilitaires et non supportées ---
    
    /**
     * Version "simple" de findByIntitule qui ne charge pas récursivement les prérequis.
     * Utile pour éviter les boucles infinies.
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

    private Competence mapResultSetToCompetence(ResultSet rs) throws SQLException {
        return new Competence(rs.getString("intitule"));
    }

    @Override
    public Competence findByID(Long id) {
        throw new UnsupportedOperationException("Competence ID is String. Use findByIntitule(String).");
    }

    @Override
    public int update(Competence element) {
        throw new UnsupportedOperationException("Updating Competence PK is not supported. Delete and re-create if needed, or implement a specific method.");
    }
}