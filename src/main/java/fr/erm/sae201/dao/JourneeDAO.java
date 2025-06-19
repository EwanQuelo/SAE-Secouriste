package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Journee;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des entités Journee.
 * <p>
 * Une Journee représente une date spécifique, qui sert de clé primaire.
 * Cette classe gère les opérations CRUD pour les enregistrements de Journee.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class JourneeDAO extends DAO<Journee> {

    /**
     * Récupère toutes les journées de la base de données.
     * Chaque journée est identifiée par une date unique.
     *
     * @return Une liste de tous les objets Journee trouvés.
     */
    @Override
    public List<Journee> findAll() {
        String sql = "SELECT jour FROM Journee";
        List<Journee> journees = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                journees.add(new Journee(rs.getDate("jour").toLocalDate()));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Journees: " + e.getMessage());
        }
        return journees;
    }

    /**
     * Recherche une journée spécifique par sa date.
     * La date sert de clé primaire pour une journée.
     *
     * @param date La date à rechercher.
     * @return L'objet Journee si trouvé ; sinon `null`.
     */
    public Journee findByDate(LocalDate date) {
        if (date == null) return null;
        String sql = "SELECT jour FROM Journee WHERE jour = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Journee(rs.getDate("jour").toLocalDate());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Journee by date " + date + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Crée un nouvel enregistrement de Journee dans la base de données.
     * La date de la journée sert de clé primaire.
     *
     * @param journee L'objet Journee à persister. Ne doit pas être null.
     * @return Le nombre de lignes affectées (1 en cas de succès, -1 si erreur).
     * @throws IllegalArgumentException si l'objet journee est null.
     */
    @Override
    public int create(Journee journee) {
        if (journee == null) throw new IllegalArgumentException("Journee to create cannot be null.");
        String sql = "INSERT INTO Journee (jour) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(journee.getDate()));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            // L'erreur se produit souvent si la date existe déjà (violant la contrainte de clé primaire).
            System.err.println("Error creating Journee " + journee + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Supprime un enregistrement de Journee de la base de données en fonction de sa date.
     *
     * @param journee L'objet Journee à supprimer. Ne doit pas être null.
     * @return Le nombre de lignes affectées (1 si succès, 0 si non trouvé, -1 si erreur).
     * @throws IllegalArgumentException si l'objet journee est null.
     */
    @Override
    public int delete(Journee journee) {
        if (journee == null) throw new IllegalArgumentException("Journee to delete cannot be null.");
        String sql = "DELETE FROM Journee WHERE jour = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(journee.getDate()));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Journee " + journee + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Non supporté. La clé primaire de Journee est une LocalDate.
     * Utilisez `findByDate(LocalDate)`.
     */
    @Override
    public Journee findByID(Long id) {
        throw new UnsupportedOperationException("Journee ID is a LocalDate, not a Long. Use findByDate(LocalDate).");
    }

    /**
     * Non supporté. La mise à jour d'une clé primaire n'est pas recommandée.
     * Utilisez `delete` puis `create`.
     */
    @Override
    public int update(Journee element) {
        throw new UnsupportedOperationException("Updating a primary key (jour) is not recommended. Use delete then create.");
    }
}