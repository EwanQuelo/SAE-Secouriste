package fr.erm.sae201.metier.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import fr.erm.sae201.dao.CompteUtilisateurDAO;
import fr.erm.sae201.dao.CompetenceDAO;
import fr.erm.sae201.dao.SecouristeDAO;
import fr.erm.sae201.exception.AuthenticationException;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role;
import fr.erm.sae201.metier.persistence.Secouriste;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Service class for handling authentication-related logic.
 */
public class AuthService {

    private final CompteUtilisateurDAO compteDAO = new CompteUtilisateurDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param login    The user's email.
     * @param password The user's plain text password.
     * @return The {@link CompteUtilisateur} if login is successful.
     * @throws EntityNotFoundException if the user does not exist.
     * @throws AuthenticationException if the password is incorrect.
     */
    public CompteUtilisateur login(String login, String password) {
        // La méthode findByLogin lève EntityNotFoundException si l'utilisateur n'est pas trouvé.
        // Cette exception va "remonter" jusqu'au contrôleur.
        CompteUtilisateur compte = compteDAO.findByLogin(login);

        // Si l'utilisateur est trouvé, on vérifie le mot de passe.
        if (BCrypt.checkpw(password, compte.getMotDePasseHash())) {
            return compte;
        } else {
            // Si le mot de passe est incorrect, on lève une exception spécifique.
            throw new AuthenticationException("Le mot de passe est incorrect.");
        }
    }

    // Ancienne méthode registerSecouriste (ne change pas, utilisée par l'inscription publique)
    public boolean registerSecouriste(String firstName, String lastName, String email, String password, Date dateOfBirth) throws Exception {
        return registerSecouriste(firstName, lastName, email, password, dateOfBirth, null);
    }

    public boolean registerSecouriste(String firstName, String lastName, String email, String password, Date dateOfBirth, List<Competence> competences) throws Exception {
        // Cette vérification est correcte, mais on pourrait la rendre plus spécifique.
        try {
            compteDAO.findByLogin(email);
            // Si on arrive ici, le compte existe déjà.
            throw new Exception("Cet email est déjà utilisé par un autre compte.");
        } catch (EntityNotFoundException e) {
            // C'est le comportement attendu, on peut continuer.
        }

        Secouriste nouveauSecouriste = new Secouriste(lastName, firstName, dateOfBirth, email, "", "");
        
        long secouristeId = secouristeDAO.create(nouveauSecouriste);
        
        if (secouristeId == -1L) {
            throw new Exception("Erreur critique lors de la création du profil secouriste.");
        }
        
        if (competences != null) {
            for (Competence comp : competences) {
                secouristeDAO.addCompetenceToSecouriste(secouristeId, comp.getIntitule());
            }
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        CompteUtilisateur nouveauCompte = new CompteUtilisateur(email, hashedPassword, Role.SECOURISTE, secouristeId);
        
        int result = compteDAO.create(nouveauCompte);
        return result > 0;
    }

    public List<Competence> getAllCompetences() {
        return competenceDAO.findAll();
    }


    /**
     * Generates a reset code, sends it via email using Mailjet, and returns it.
     *
     * @param recipientEmail The recipient's email address.
     * @return The generated code on success, or {@code null} on failure.
     * @throws MailjetException If an error occurs while communicating with the API.
     */
    public String sendResetCode(String recipientEmail) throws MailjetException {
        // On vérifie que le compte existe avant d'envoyer un email
        compteDAO.findByLogin(recipientEmail); // Lèvera une EntityNotFoundException si le compte n'existe pas

        String code = String.format("%06d", new Random().nextInt(999999));

        ClientOptions options = ClientOptions.builder()
                .apiKey("8122fbfcf9aa6f8c3ad3d5230df3b5a8")
                .apiSecretKey("a86b3cc785667ec7a2613400d6e4250e")
                .build();

        MailjetClient client = new MailjetClient(options);

        String htmlContent = "<h1>Password Reset</h1>"
                           + "<p>Hello,</p>"
                           + "<p>You have requested to reset your password. Use the code below to proceed:</p>"
                           + "<h2 style='color: #1a73e8;'>" + code + "</h2>"
                           + "<p>If you did not make this request, you can ignore this email.</p>"
                           + "<p>The SECOURS Team</p>";

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
            .property(Emailv31.MESSAGES, new JSONArray()
                .put(new JSONObject()
                    .put(Emailv31.Message.FROM, new JSONObject()
                        .put("Email", "taboulakidoum@gmail.com")
                        .put("Name", "SECOURS App"))
                    .put(Emailv31.Message.TO, new JSONArray()
                        .put(new JSONObject()
                            .put("Email", recipientEmail)
                            .put("Name", "User")))
                    .put(Emailv31.Message.SUBJECT, "Your password reset code")
                    .put(Emailv31.Message.HTMLPART, htmlContent)
            ));

        MailjetResponse response = client.post(request);

        if (response.getStatus() == 200) {
            System.out.println("Email sent successfully via Mailjet. Response: " + response.getData());
            return code;
        } else {
            System.err.println("Failed to send email via Mailjet. Status: " + response.getStatus() + ", Response: " + response.getData());
            return null;
        }
    }

    /**
     * Resets the password for a given user.
     *
     * @param email       The user's email.
     * @param newPassword The new password in plain text.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    public boolean resetPassword(String email, String newPassword) {
        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        int affectedRows = compteDAO.updatePassword(email, newPasswordHash);
        return affectedRows > 0;
    }
}