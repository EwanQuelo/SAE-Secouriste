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

    /**
     * Crée un nouveau secouriste dans la base de données.
     * L'ID est auto-généré par la base de données.
     * @param secouriste L'objet Secouriste à créer (sans ID défini).
     * @return L'ID généré par la base de données en cas de succès, ou -1 en cas d'erreur.
     */
    public int create(Secouriste secouriste) { // MODIFIÉ : le type de retour est maintenant long
        if (secouriste == null) {
            throw new IllegalArgumentException("Secouriste to create cannot be null.");
        }
        // MODIFIÉ : La colonne 'id' n'est plus dans la requête INSERT
        String sql = "INSERT INTO Secouriste (nom, prenom, dateNaissance, email, tel, adresse) VALUES (?, ?, ?, ?, ?, ?)";
        
        // MODIFIÉ : On ajoute Statement.RETURN_GENERATED_KEYS pour récupérer l'ID
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Les index sont décalés car on n'insère plus l'ID
            pstmt.setString(1, secouriste.getNom());
            pstmt.setString(2, secouriste.getPrenom());
            if (secouriste.getDateNaissance() != null) {
                // Pour le constructeur, j'ai mis "new Date()", mais si le POJO
                // stocke null, il faut gérer ce cas.
                pstmt.setDate(3, new java.sql.Date(secouriste.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setString(4, secouriste.getEmail());
            pstmt.setString(5, secouriste.getTel());
            pstmt.setString(6, secouriste.getAddresse());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                // Aucun enregistrement n'a été créé, c'est une erreur.
                return -1;
            }

            // NOUVEAU : Bloc pour récupérer l'ID auto-généré
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1); // On retourne le nouvel ID
                } else {
                    // L'insertion a fonctionné mais on n'a pas pu récupérer l'ID, erreur grave.
                    throw new SQLException("La création a échoué, impossible d'obtenir l'ID.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating Secouriste: " + e.getMessage());
            return -1;
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