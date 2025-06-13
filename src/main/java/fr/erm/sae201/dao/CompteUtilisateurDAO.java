package fr.erm.sae201.dao;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteUtilisateurDAO extends DAO<CompteUtilisateur> {

    /**
     * Trouve un compte utilisateur par son login (email).
     * @param login L'email à rechercher.
     * @return Un Optional contenant le CompteUtilisateur s'il est trouvé, sinon un Optional vide.
     */
    public Optional<CompteUtilisateur> findByLogin(String login) {
        if (login == null || login.trim().isEmpty()) return Optional.empty();
        // MODIFIÉ : 'id_secouriste' devient 'idSecouriste' pour correspondre à la BDD
        String sql = "SELECT login, motDePasseHash, role, idSecouriste FROM CompteUtilisateur WHERE login = ?";
        CompteUtilisateur compte = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    compte = new CompteUtilisateur(
                            rs.getString("login"),
                            rs.getString("motDePasseHash"),
                            Role.valueOf(rs.getString("role")),
                            // MODIFIÉ : Le nom de la colonne dans le ResultSet est maintenant 'idSecouriste'
                            (Long) rs.getObject("idSecouriste")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du CompteUtilisateur par login " + login + ": " + e.getMessage());
        }
        return Optional.ofNullable(compte);
    }

    /**
     * Crée un nouveau compte utilisateur dans la base de données.
     * @param compte L'objet CompteUtilisateur à créer.
     * @return Le nombre de lignes affectées (1 en cas de succès, -1 en cas d'erreur).
     */
    @Override
    public int create(CompteUtilisateur compte) {
        if (compte == null) throw new IllegalArgumentException("CompteUtilisateur ne peut pas être null.");
        // MODIFIÉ : 'id_secouriste' devient 'idSecouriste' pour correspondre à la BDD
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

    // Les autres méthodes de l'interface DAO ne sont pas requises pour ce cas d'usage,
    // mais doivent être présentes car la classe est abstraite.
    @Override
    public List<CompteUtilisateur> findAll() {
        return new ArrayList<>();
    }
    @Override
    public CompteUtilisateur findByID(Long id) {
        throw new UnsupportedOperationException("Utiliser findByLogin(String) pour cette classe.");
    }
    @Override
    public int update(CompteUtilisateur element) {
        return -1;
    }
    @Override
    public int delete(CompteUtilisateur element) {
        return -1;
    }

    /**
     * Met à jour le mot de passe hashé d'un utilisateur identifié par son login.
     * @param login L'email de l'utilisateur.
     * @param newPasswordHash Le nouveau mot de passe hashé.
     * @return Le nombre de lignes affectées (1 en cas de succès).
     */
    public int updatePassword(String login, String newPasswordHash) {
        if (login == null || login.trim().isEmpty() || newPasswordHash == null || newPasswordHash.isEmpty()) {
            throw new IllegalArgumentException("Login et hash du mot de passe ne peuvent pas être nuls.");
        }
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
}