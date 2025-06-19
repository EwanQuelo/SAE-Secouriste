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
 * Service gérant la logique métier liée à l'authentification.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AuthService {

    private final CompteUtilisateurDAO compteDAO = new CompteUtilisateurDAO();
    private final SecouristeDAO secouristeDAO = new SecouristeDAO();
    private final CompetenceDAO competenceDAO = new CompetenceDAO();

    /**
     * Tente de connecter un utilisateur avec ses identifiants.
     *
     * @param login    L'email de l'utilisateur.
     * @param password Le mot de passe en clair de l'utilisateur.
     * @return Le CompteUtilisateur si la connexion réussit.
     * @throws EntityNotFoundException si l'utilisateur n'existe pas.
     * @throws AuthenticationException si le mot de passe est incorrect.
     */
    public CompteUtilisateur login(String login, String password) {
        // La méthode findByLogin lève une EntityNotFoundException si l'utilisateur est introuvable.
        // Cette exception remontera jusqu'au contrôleur qui pourra l'interpréter.
        CompteUtilisateur compte = compteDAO.findByLogin(login);

        if (BCrypt.checkpw(password, compte.getMotDePasseHash())) {
            return compte;
        } else {
            // Le mot de passe est incorrect, on lève une exception spécifique.
            throw new AuthenticationException("Le mot de passe est incorrect.");
        }
    }

    /**
     * Inscrit un nouveau secouriste sans compétences initiales.
     *
     * @param firstName   Le prénom.
     * @param lastName    Le nom de famille.
     * @param email       L'email, qui servira de login.
     * @param password    Le mot de passe en clair.
     * @param dateOfBirth La date de naissance.
     * @return `true` si l'inscription a réussi.
     * @throws Exception si l'email est déjà utilisé ou en cas d'erreur critique.
     */
    public boolean registerSecouriste(String firstName, String lastName, String email, String password, Date dateOfBirth) throws Exception {
        return registerSecouriste(firstName, lastName, email, password, dateOfBirth, null);
    }

    /**
     * Inscrit un nouveau secouriste avec une liste optionnelle de compétences.
     *
     * @param firstName   Le prénom.
     * @param lastName    Le nom de famille.
     * @param email       L'email, qui servira de login.
     * @param password    Le mot de passe en clair.
     * @param dateOfBirth La date de naissance.
     * @param competences La liste des compétences initiales (peut être null).
     * @return `true` si l'inscription a réussi.
     * @throws Exception si l'email est déjà utilisé ou en cas d'erreur critique.
     */
    public boolean registerSecouriste(String firstName, String lastName, String email, String password, Date dateOfBirth, List<Competence> competences) throws Exception {
        try {
            compteDAO.findByLogin(email);
            // Si on arrive ici sans exception, c'est que le compte existe déjà.
            throw new Exception("Cet email est déjà utilisé par un autre compte.");
        } catch (EntityNotFoundException e) {
            // C'est le comportement attendu pour une nouvelle inscription, on peut continuer.
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

    /**
     * Récupère la liste de toutes les compétences disponibles dans le système.
     *
     * @return Une liste d'objets Competence.
     */
    public List<Competence> getAllCompetences() {
        return competenceDAO.findAll();
    }

    /**
     * Génère un code de réinitialisation, l'envoie par email via Mailjet et le retourne.
     *
     * @param recipientEmail L'adresse email du destinataire.
     * @return Le code généré en cas de succès, sinon `null`.
     * @throws MailjetException Si une erreur survient lors de la communication avec l'API.
     * @throws EntityNotFoundException Si l'email ne correspond à aucun compte.
     */
    public String sendResetCode(String recipientEmail) throws MailjetException {
        // Vérifie que le compte existe avant d'envoyer un email, ce qui peut coûter de l'argent.
        compteDAO.findByLogin(recipientEmail);

        String code = String.format("%06d", new Random().nextInt(999999));

        ClientOptions options = ClientOptions.builder()
                .apiKey("8122fbfcf9aa6f8c3ad3d5230df3b5a8")
                .apiSecretKey("a86b3cc785667ec7a2613400d6e4250e")
                .build();
        MailjetClient client = new MailjetClient(options);

        String htmlContent = "<h1>Réinitialisation de mot de passe</h1>"
                           + "Bonjour,"
                           + "Vous avez demandé à réinitialiser votre mot de passe. Utilisez le code ci-dessous pour continuer :"
                           + "<h2 style='color: #1a73e8;'>" + code + "</h2>"
                           + "Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email."
                           + "L'équipe SECOURS";

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
            .property(Emailv31.MESSAGES, new JSONArray()
                .put(new JSONObject()
                    .put(Emailv31.Message.FROM, new JSONObject()
                        .put("Email", "taboulakidoum@gmail.com")
                        .put("Name", "Application SECOURS"))
                    .put(Emailv31.Message.TO, new JSONArray()
                        .put(new JSONObject()
                            .put("Email", recipientEmail)
                            .put("Name", "Utilisateur")))
                    .put(Emailv31.Message.SUBJECT, "Votre code de réinitialisation de mot de passe")
                    .put(Emailv31.Message.HTMLPART, htmlContent)
            ));

        MailjetResponse response = client.post(request);

        if (response.getStatus() == 200) {
            System.out.println("Email envoyé avec succès via Mailjet. Réponse : " + response.getData());
            return code;
        } else {
            System.err.println("Échec de l'envoi de l'email via Mailjet. Statut : " + response.getStatus() + ", Réponse : " + response.getData());
            return null;
        }
    }

    /**
     * Réinitialise le mot de passe pour un utilisateur donné.
     *
     * @param email       L'email de l'utilisateur.
     * @param newPassword Le nouveau mot de passe en clair.
     * @return `true` si la mise à jour a réussi, `false` sinon.
     */
    public boolean resetPassword(String email, String newPassword) {
        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        int affectedRows = compteDAO.updatePassword(email, newPasswordHash);
        return affectedRows > 0;
    }
}