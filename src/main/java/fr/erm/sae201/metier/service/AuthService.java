package fr.erm.sae201.metier.service;

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

/**
 * Service class for handling authentication-related logic,
 * such as login, registration, and password reset.
 */
public class AuthService {

    private final CompteUtilisateurDAO compteDAO = new CompteUtilisateurDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param login    The user's email.
     * @param password The user's plain text password.
     * @return An {@link Optional} containing the {@link CompteUtilisateur} if login is successful,
     *         otherwise an empty Optional.
     */
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

    /**
     * Registers a new first aider (Secouriste).
     *
     * @param firstName   The user's first name.
     * @param lastName    The user's last name.
     * @param email       The user's email, which will be their login.
     * @param password    The user's plain text password.
     * @param dateOfBirth The user's date of birth.
     * @return {@code true} if the registration was successful, {@code false} otherwise.
     * @throws Exception if the email is already in use or if a critical database error occurs.
     */
    public boolean registerSecouriste(String firstName, String lastName, String email, String password, Date dateOfBirth) throws Exception {
        if (compteDAO.findByLogin(email).isPresent()) {
            throw new Exception("This email is already used by another account.");
        }

        // MODIFIED: Use the provided dateOfBirth instead of a placeholder
        Secouriste nouveauSecouriste = new Secouriste(lastName, firstName, dateOfBirth, email, "", "");
        
        long secouristeId = secouristeDAO.create(nouveauSecouriste);
        
        if (secouristeId == -1) {
            throw new Exception("Critical error while creating the first aider profile.");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        CompteUtilisateur nouveauCompte = new CompteUtilisateur(email, hashedPassword, Role.SECOURISTE, secouristeId);
        
        int result = compteDAO.create(nouveauCompte);
        return result > 0;
    }


    /**
     * Generates a reset code, sends it via email using Mailjet, and returns it.
     *
     * @param recipientEmail The recipient's email address.
     * @return The generated code on success, or {@code null} on failure.
     * @throws MailjetException If an error occurs while communicating with the API.
     */
    public String sendResetCode(String recipientEmail) throws MailjetException {
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
                        .put("Email", "taboulakidoum@gmail.com") // IMPORTANT: Use a verified sender email in your Mailjet account
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