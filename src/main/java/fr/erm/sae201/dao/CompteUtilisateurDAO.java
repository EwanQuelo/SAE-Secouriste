package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteUtilisateurDAO extends DAO<CompteUtilisateur> {

    public Optional<CompteUtilisateur> findByLogin(String login) {
        if (login == null || login.trim().isEmpty()) return Optional.empty();
        String sql = "SELECT login, motDePasseHash, role, idSecouriste FROM CompteUtilisateur WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CompteUtilisateur(
                            rs.getString("login"),
                            rs.getString("motDePasseHash"),
                            Role.valueOf(rs.getString("role")),
                            (Long) rs.getObject("idSecouriste")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du CompteUtilisateur par login " + login + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public int create(CompteUtilisateur compte) {
        if (compte == null) throw new IllegalArgumentException("CompteUtilisateur ne peut pas être null.");
        String sql = "INSERT INTO CompteUtilisateur (login, motDePasseHash, role, idSecouriste) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, compte.getLogin());
            pstmt.setString(2, compte.getMotDePasseHash());
            pstmt.setString(3, compte.getRole().name());
            if (compte.getIdSecouriste() != null) {
                pstmt.setLong(4, compte.getIdSecouriste());
            } else {
                pstmt.setNull(4, Types.BIGINT);
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du CompteUtilisateur " + compte.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }

    public int updatePassword(String login, String newPasswordHash) {
        String sql = "UPDATE CompteUtilisateur SET motDePasseHash = ? WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, login);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe pour " + login + ": " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int update(CompteUtilisateur compte) {
        // La seule mise à jour typique est le mot de passe, géré par updatePassword.
        // On pourrait ajouter la mise à jour du rôle ou du lien secouriste si nécessaire.
        String sql = "UPDATE CompteUtilisateur SET role = ?, idSecouriste = ? WHERE login = ?";
         try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, compte.getRole().name());
            if (compte.getIdSecouriste() != null) {
                pstmt.setLong(2, compte.getIdSecouriste());
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            pstmt.setString(3, compte.getLogin());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du CompteUtilisateur " + compte.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }
    
    @Override
    public int delete(CompteUtilisateur compte) {
         if (compte == null) return -1;
         String sql = "DELETE FROM CompteUtilisateur WHERE login = ?";
         try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, compte.getLogin());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du CompteUtilisateur " + compte.getLogin() + ": " + e.getMessage());
            return -1;
        }
    }
    
    @Override
    public List<CompteUtilisateur> findAll() {
        // Implémentation si nécessaire, souvent peu utile pour les comptes.
        return new ArrayList<>();
    }
    
    @Override
    public CompteUtilisateur findByID(Long id) {
        throw new UnsupportedOperationException("Utiliser findByLogin(String) pour cette classe.");
    }
}