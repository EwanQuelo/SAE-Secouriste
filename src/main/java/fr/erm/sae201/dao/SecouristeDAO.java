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

public class SecouristeDAO extends DAO<Secouriste> {

    // DAOs pour les dépendances
    private final CompetenceDAO competenceDAO = new CompetenceDAO();
    private final JourneeDAO journeeDAO = new JourneeDAO();

    @Override
    public Secouriste findByID(Long id) {
        if (id == null) return null;
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
    
    @Override
    public List<Secouriste> findAll() {
        List<Secouriste> secouristes = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                secouristes.add(mapResultSetToSecouriste(rs, true)); // Hydrate avec les relations
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Secouristes: " + e.getMessage());
        }
        return secouristes;
    }

    // --- GESTION RELATION : Possede (Secouriste <-> Competence) ---
    public Set<Competence> findCompetencesForSecouriste(long secouristeId) {
        Set<Competence> competences = new HashSet<>();
        String sql = "SELECT intituleCompetence FROM Possede WHERE idSecouriste = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Competence comp = competenceDAO.findByIntitule(rs.getString("intituleCompetence"));
                    if (comp != null) competences.add(comp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding competences for secouriste " + secouristeId + ": " + e.getMessage());
        }
        return competences;
    }

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

    // --- GESTION RELATION : EstDisponible (Secouriste <-> Journee) ---
    public Set<Journee> findAvailabilitiesForSecouriste(long secouristeId) {
        Set<Journee> journees = new HashSet<>();
        String sql = "SELECT jour FROM EstDisponible WHERE idSecouriste = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, secouristeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    journees.add(new Journee(rs.getDate("jour").toLocalDate()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding availabilities for secouriste " + secouristeId + ": " + e.getMessage());
        }
        return journees;
    }

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

    // --- Méthodes CRUD de base et utilitaires ---
    private Secouriste mapResultSetToSecouriste(ResultSet rs, boolean fetchRelations) throws SQLException {
        Secouriste secouriste = new Secouriste(
            rs.getLong("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getDate("dateNaissance"),
            rs.getString("email"),
            rs.getString("tel"),
            rs.getString("adresse")
        );
        if (fetchRelations) {
            secouriste.setCompetences(findCompetencesForSecouriste(secouriste.getId()));
            secouriste.setDisponibilites(findAvailabilitiesForSecouriste(secouriste.getId()));
        }
        return secouriste;
    }
    
    @Override
    public int create(Secouriste secouriste) {
        String sql = "INSERT INTO Secouriste (nom, prenom, dateNaissance, email, tel, adresse) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

    @Override
    public int delete(Secouriste secouriste) {
        if (secouriste == null) return -1;
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