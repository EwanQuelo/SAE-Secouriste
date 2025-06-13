package fr.erm.sae201.metier.service;

// NOUVEAUX IMPORTS POUR MAILJET
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;

import fr.erm.sae201.dao.CompteUtilisateurDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;
import fr.erm.sae201.metier.persistence.Secouriste;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

public class AuthService {

    private final CompteUtilisateurDAO compteDAO = new CompteUtilisateurDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    // ... les méthodes login() et registerSecouriste() restent inchangées ...
    public Optional<CompteUtilisateur> login(String login, String password) {
        Optional<CompteUtilisateur> compteOpt = compteDAO.findByLogin(login);
        if (compteOpt.isPresent()) {
            CompteUtilisateur compte = compteOpt.get();
            if (BCrypt.checkpw(password, compte.getMotDePasseHash())) {
                return Optional.of(compte);
            }
        }
        return Optional.empty();
    }

    public boolean registerSecouriste(String firstName, String lastName, String email, String password) throws Exception {
        if (compteDAO.findByLogin(email).isPresent()) {
            throw new Exception("Cet email est déjà utilisé par un autre compte.");
        }
        Secouriste nouveauSecouriste = new Secouriste(lastName, firstName, new Date(), email, "", "");
        long secouristeId = secouristeDAO.create(nouveauSecouriste);
        if (secouristeId == -1) {
            throw new Exception("Erreur critique lors de la création du profil secouriste.");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        CompteUtilisateur nouveauCompte = new CompteUtilisateur(email, hashedPassword, Role.SECOURISTE, secouristeId);
        int result = compteDAO.create(nouveauCompte);
        return result > 0;
    }


    /**
     * Génère un code de réinitialisation, l'envoie par email via Mailjet et le retourne.
     * @param recipientEmail L'email du destinataire.
     * @return Le code généré en cas de succès, ou null en cas d'échec.
     * @throws MailjetException Si une erreur se produit lors de la communication avec l'API.
     */
    public String sendResetCode(String recipientEmail) throws MailjetException {
        // La génération du code reste la même
        String code = String.format("%06d", new Random().nextInt(999999));

        // Création du client Mailjet
        ClientOptions options = ClientOptions.builder()
                .apiKey("8122fbfcf9aa6f8c3ad3d5230df3b5a8")
                .apiSecretKey("a86b3cc785667ec7a2613400d6e4250e")
                .build();

        MailjetClient client = new MailjetClient(options);

        // Construction du contenu HTML de l'email
        String htmlContent = "<h1>Réinitialisation de mot de passe</h1>"
                           + "<p>Bonjour,</p>"
                           + "<p>Vous avez demandé à réinitialiser votre mot de passe. Utilisez le code ci-dessous pour continuer :</p>"
                           + "<h2 style='color: #1a73e8;'>" + code + "</h2>"
                           + "<p>Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.</p>"
                           + "<p>L'équipe SECOURS</p>";

        // Construction de la requête Mailjet
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
            .property(Emailv31.MESSAGES, new JSONArray()
                .put(new JSONObject()
                    .put(Emailv31.Message.FROM, new JSONObject()
                        // IMPORTANT: Utilisez une adresse email vérifiée dans votre compte Mailjet
                        .put("Email", "taboulakidoum@gmail.com")
                        .put("Name", "SECOURS App"))
                    .put(Emailv31.Message.TO, new JSONArray()
                        .put(new JSONObject()
                            .put("Email", recipientEmail)
                            .put("Name", "Utilisateur")))
                    .put(Emailv31.Message.SUBJECT, "Votre code de réinitialisation de mot de passe")
                    .put(Emailv31.Message.HTMLPART, htmlContent)
            ));

        // Envoi de l'email
        MailjetResponse response = client.post(request);

        // Vérification du succès de l'envoi
        if (response.getStatus() == 200) {
            System.out.println("Email envoyé avec succès via Mailjet. Réponse: " + response.getData());
            return code;
        } else {
            System.err.println("Échec de l'envoi de l'email via Mailjet. Statut: " + response.getStatus() + ", Réponse: " + response.getData());
            return null;
        }
    }

    /**
     * Réinitialise le mot de passe pour un utilisateur donné.
     * @param email L'email de l'utilisateur.
     * @param newPassword Le nouveau mot de passe en clair.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean resetPassword(String email, String newPassword) {
        // Hasher le nouveau mot de passe
        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Appeler le DAO pour mettre à jour la base de données
        int affectedRows = compteDAO.updatePassword(email, newPasswordHash);

        return affectedRows > 0;
    }
}