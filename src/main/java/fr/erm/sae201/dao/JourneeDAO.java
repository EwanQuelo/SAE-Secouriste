package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.Journee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Journee entity, extending the generic DAO.
 * @author Raphael Mille, Ewan Quelo, Matheo Biet
 * @version 1.2
 */
public class JourneeDAO extends DAO<Journee> {

    @Override
    public List<Journee> findAll() {
        String sql = "SELECT jour, mois, annee FROM Journee";
        List<Journee> journees = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                journees.add(new Journee(
                        rs.getInt("jour"),
                        rs.getInt("mois"),
                        rs.getInt("annee")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Journees: " + e.getMessage());
        }
        return journees;
    }

    /**
     * {@inheritDoc}
     * Not applicable for Journee as its ID is composite (jour, mois, annee).
     * Use findByDate(int jour, int mois, int annee) instead.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public Journee findByID(Long id) {
        throw new UnsupportedOperationException("Journee ID is composite (jour, mois, annee). Use findByDate().");
    }

    /**
     * Finds a Journee by its composite primary key (jour, mois, annee).
     * @param jour The day of the month.
     * @param mois The month.
     * @param annee The year.
     * @return The Journee object if found, otherwise null.
     */
    public Journee findByDate(int jour, int mois, int annee) {
        String sql = "SELECT jour, mois, annee FROM Journee WHERE jour = ? AND mois = ? AND annee = ?";
        Journee journee = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jour);
            pstmt.setInt(2, mois);
            pstmt.setInt(3, annee);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    journee = new Journee(
                            rs.getInt("jour"),
                            rs.getInt("mois"),
                            rs.getInt("annee")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding Journee by date " + jour + "/" + mois + "/" + annee + ": " + e.getMessage());
        }
        return journee;
    }
    
    public Journee findByJournee(Journee journeeObj) {
        if (journeeObj == null) return null;
        return findByDate(journeeObj.getJour(), journeeObj.getMois(), journeeObj.getAnnee());
    }


    @Override
    public int create(Journee journee) {
        if (journee == null) {
            throw new IllegalArgumentException("Journee to create cannot be null.");
        }
        String sql = "INSERT INTO Journee (jour, mois, annee) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, journee.getJour());
            pstmt.setInt(2, journee.getMois());
            pstmt.setInt(3, journee.getAnnee());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating Journee " + journee + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     * Updating a Journee is problematic as all its fields are part of the primary key.
     * This typically involves deleting the old record and inserting a new one.
     * A specific method updateJournee(Journee oldJournee, Journee newJournee) would be more appropriate.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public int update(Journee element) {
        throw new UnsupportedOperationException("Updating Journee's primary key via generic update(T) is not directly supported. " +
                                                "Use delete then create, or a custom update method providing the old key.");
    }

    /**
     * Updates a Journee by deleting the old one and inserting the new one.
     * This is because all fields of Journee are part of its primary key.
     * @param oldJournee The Journee to be replaced.
     * @param newJournee The new Journee data.
     * @return 1 if successful (delete and create successful), 0 if only one part succeeded, -1 on error.
     *         More granular error handling might be needed.
     */
    public int updateByReplacing(Journee oldJournee, Journee newJournee) {
        if (oldJournee == null || newJournee == null) {
            throw new IllegalArgumentException("Old or new Journee cannot be null for updateByReplacing.");
        }
        // This should ideally be in a transaction
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            int deleteResult;
            String deleteSql = "DELETE FROM Journee WHERE jour = ? AND mois = ? AND annee = ?";
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setInt(1, oldJournee.getJour());
                deletePstmt.setInt(2, oldJournee.getMois());
                deletePstmt.setInt(3, oldJournee.getAnnee());
                deleteResult = deletePstmt.executeUpdate();
            }

            if (deleteResult <= 0 && findByJournee(oldJournee) != null) { // If delete failed but old one still exists
                conn.rollback();
                System.err.println("Failed to delete old Journee " + oldJournee + " during update.");
                return -1;
            }
            
            int createResult;
            String createSql = "INSERT INTO Journee (jour, mois, annee) VALUES (?, ?, ?)";
            try (PreparedStatement createPstmt = conn.prepareStatement(createSql)) {
                createPstmt.setInt(1, newJournee.getJour());
                createPstmt.setInt(2, newJournee.getMois());
                createPstmt.setInt(3, newJournee.getAnnee());
                createResult = createPstmt.executeUpdate();
            }

            if (createResult > 0) {
                conn.commit();
                return createResult; // Return result of create (usually 1)
            } else {
                conn.rollback();
                System.err.println("Failed to create new Journee " + newJournee + " after deleting old one.");
                return -1;
            }

        } catch (SQLException e) {
            System.err.println("Error updating Journee by replacing " + oldJournee + " with " + newJournee + ": " + e.getMessage());
            // Attempt to rollback, though connection might be closed by try-with-resources before this if error is in getConnection
            return -1;
        }
    }


    @Override
    public int delete(Journee journee) {
        if (journee == null) {
            throw new IllegalArgumentException("Journee to delete cannot be null.");
        }
        String sql = "DELETE FROM Journee WHERE jour = ? AND mois = ? AND annee = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, journee.getJour());
            pstmt.setInt(2, journee.getMois());
            pstmt.setInt(3, journee.getAnnee());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Journee " + journee + ": " + e.getMessage());
            return -1;
        }
    }
}