package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminCreateUserView;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour l'interface de création d'un nouvel utilisateur (secouriste).
 * 
 * Cette classe orchestre la création d'un secouriste en collectant les informations
 * de la vue, en validant les données saisies et en appelant le service d'authentification
 * pour finaliser l'inscription en base de données.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminCreateUserController {

    /** La vue de création d'utilisateur associée. */
    private final AdminCreateUserView view;

    /** Le navigateur principal de l'application pour gérer les changements de vue. */
    private final MainApp navigator;

    /** Le service d'authentification qui gère la logique d'inscription. */
    private final AuthService authService;

    /**
     * Constructeur du contrôleur de création d'utilisateur.
     * Initialise les dépendances et connecte les actions des boutons de la vue
     * aux méthodes de gestion correspondantes.
     *
     * @param view La vue à contrôler.
     * @param navigator Le navigateur principal de l'application.
     */
    public AdminCreateUserController(AdminCreateUserView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.authService = new AuthService();

        view.setSaveButtonAction(e -> handleSave());
        view.setCancelButtonAction(e -> handleCancel());

        loadData();
    }

    /**
     * Charge les données nécessaires à l'affichage de la vue,
     * notamment la liste de toutes les compétences disponibles.
     */
    private void loadData() {
        Platform.runLater(() -> view.populateCompetences(authService.getAllCompetences()));
    }

    /**
     * Gère la sauvegarde du nouvel utilisateur.
     * Cette méthode récupère l'ensemble des données saisies dans la vue, effectue
     * une validation de base (champs requis, format de l'email), puis délègue
     * la création à l'AuthService. Elle gère également l'affichage des notifications
     * de succès ou d'erreur et la redirection.
     */
    private void handleSave() {
        String prenom = view.getPrenom();
        String nom = view.getNom();
        String email = view.getEmail();
        String password = view.getPassword();
        LocalDate dateNaissance = view.getDateNaissance();

        List<Competence> selectedCompetences = new ArrayList<>();
        Map<Competence, CheckBox> checkBoxes = view.getCompetenceCheckBoxes();
        for (Map.Entry<Competence, CheckBox> entry : checkBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedCompetences.add(entry.getKey());
            }
        }

        if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || password.isEmpty() || dateNaissance == null) {
            NotificationUtils.showError("Champs manquants", "Veuillez remplir tous les champs personnels et le mot de passe.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
             NotificationUtils.showError("Format invalide", "L'adresse email n'est pas valide.");
            return;
        }

        try {
            Date dob = Date.from(dateNaissance.atStartOfDay(ZoneId.systemDefault()).toInstant());
            boolean success = authService.registerSecouriste(prenom, nom, email, password, dob, selectedCompetences);

            if (success) {
                NotificationUtils.showSuccess("Utilisateur créé", "Le secouriste " + prenom + " " + nom + " a été ajouté avec succès.");
                navigator.showAdminUtilisateursView(view.getCompte());
            } else {
                // Cette branche est une sécurité ; une erreur (ex: email dupliqué)
                // devrait normalement lever une exception depuis le service.
                NotificationUtils.showError("Erreur", "La création de l'utilisateur a échoué. L'email existe peut-être déjà.");
            }
        } catch (Exception e) {
            NotificationUtils.showError("Erreur de création", e.getMessage());
        }
    }

    /**
     * Gère l'annulation de la création de l'utilisateur.
     * Redirige l'administrateur vers la vue listant tous les utilisateurs.
     */
    private void handleCancel() {
        navigator.showAdminUtilisateursView(view.getCompte());
    }
}