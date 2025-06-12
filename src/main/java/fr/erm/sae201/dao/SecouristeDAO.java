package fr.erm.sae201.dao; // DAO package

import fr.erm.sae201.metier.persistence.Secouriste; // POJO package

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// No need to import Date from java.util if Secouriste POJO handles it internally

/**
 * DAO for the Secouriste entity, extending the generic DAO.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.2
 */
public class SecouristeDAO extends DAO<Secouriste> {

    @Override
    public List<Secouriste> findAll() {
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste";
        List<Secouriste> secouristes = new ArrayList<>();

        try (Connection conn = getConnection(); // Inherited method
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                secouristes.add(new Secouriste(
                        rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        // Assuming Secouriste constructor handles null java.sql.Date correctly
                        // and expects java.util.Date
                        rs.getDate("dateNaissance") != null ? new java.util.Date(rs.getDate("dateNaissance").getTime()) : null,
                        rs.getString("email"),
                        rs.getString("tel"),
                        rs.getString("adresse")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Secouristes: " + e.getMessage());
            // Consider throwing a custom DAOException or logging
        }
        return secouristes;
    }

    @Override
    public Secouriste findByID(Long id) {
        if (id == null) return null;
        String sql = "SELECT id, nom, prenom, dateNaissance, email, tel, adresse FROM Secouriste WHERE id = ?";
        Secouriste secouriste = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    secouriste = new Secouriste(
                            rs.getLong("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getDate("dateNaissance") != null ? new java.util.Date(rs.getDate("dateNaissance").getTime()) : null,
                            rs.getString("email"),
                            rs.getString("tel"),
                            rs.getString("adresse")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Secouriste by ID " + id + ": " + e.getMessage());
        }
        return secouriste;
    }

    @Override
    public int create(Secouriste secouriste) {
        if (secouriste == null) {
            throw new IllegalArgumentException("Secouriste to create cannot be null.");
        }
        String sql = "INSERT INTO Secouriste (id, nom, prenom, dateNaissance, email, tel, adresse) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, secouriste.getId());
            pstmt.setString(2, secouriste.getNom());
            pstmt.setString(3, secouriste.getPrenom());
            if (secouriste.getDateNaissance() != null) {
                pstmt.setDate(4, new java.sql.Date(secouriste.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            pstmt.setString(5, secouriste.getEmail());
            pstmt.setString(6, secouriste.getTel());
            pstmt.setString(7, secouriste.getAddresse());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Secouriste: " + e.getMessage());
            return -1; // Error indicator as per example
        }
    }
    
    @Override
    public int update(Secouriste secouriste) {
        if (secouriste == null) {
            throw new IllegalArgumentException("Secouriste to update cannot be null.");
        }
        String sql = "UPDATE Secouriste SET nom = ?, prenom = ?, dateNaissance = ?, email = ?, tel = ?, adresse = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, secouriste.getNom());
            pstmt.setString(2, secouriste.getPrenom());
            if (secouriste.getDateNaissance() != null) {
                pstmt.setDate(3, new java.sql.Date(secouriste.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
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
        if (secouriste == null) {
            throw new IllegalArgumentException("Secouriste to delete cannot be null.");
        }
        String sql = "DELETE FROM Secouriste WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, secouriste.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Secouriste with ID " + secouriste.getId() + ": " + e.getMessage());
            return -1;
        }
    }
}