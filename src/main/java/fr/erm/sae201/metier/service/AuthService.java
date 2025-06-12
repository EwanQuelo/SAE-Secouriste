package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.CompteUtilisateurDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;
import fr.erm.sae201.metier.persistence.Secouriste;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;
import java.util.Optional;

public class AuthService {

    private final CompteUtilisateurDAO compteDAO = new CompteUtilisateurDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    /**
     * Tente de connecter un utilisateur avec son email et son mot de passe en clair.
     * @param login L'email de l'utilisateur.
     * @param password Le mot de passe en clair.
     * @return Un Optional contenant le CompteUtilisateur en cas de succès, sinon un Optional vide.
     */
    public Optional<CompteUtilisateur> login(String login, String password) {
        Optional<CompteUtilisateur> compteOpt = compteDAO.findByLogin(login);
        if (compteOpt.isPresent()) {
            CompteUtilisateur compte = compteOpt.get();
            // On vérifie que le mot de passe fourni correspond au hash stocké en base
            if (BCrypt.checkpw(password, compte.getMotDePasseHash())) {
                return Optional.of(compte); // Succès
            }
        }
        return Optional.empty(); // Échec (login ou mot de passe incorrect)
    }

    /**
     * Inscrit un nouveau secouriste. Crée un profil Secouriste ET un CompteUtilisateur associé.
     * Idéalement, cette méthode devrait être transactionnelle pour garantir la cohérence des données.
     * @return true si l'inscription est réussie.
     * @throws Exception si l'email est déjà utilisé ou si une erreur de base de données survient.
     */
    public boolean registerSecouriste(String firstName, String lastName, String email, String password) throws Exception {
        if (compteDAO.findByLogin(email).isPresent()) {
            throw new Exception("Cet email est déjà utilisé par un autre compte.");
        }

        // MODIFIÉ : On utilise le nouveau constructeur sans ID.
        Secouriste nouveauSecouriste = new Secouriste(lastName, firstName, new Date(), email, "", "");

        // La suite ne change pas...
        long secouristeId = secouristeDAO.create(nouveauSecouriste);
        if (secouristeId == -1) {
            throw new Exception("Erreur critique lors de la création du profil secouriste.");
        }
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        CompteUtilisateur nouveauCompte = new CompteUtilisateur(email, hashedPassword, Role.SECOURISTE, secouristeId);
        int result = compteDAO.create(nouveauCompte);

        return result > 0;
    }
}