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

public class AdminCreateUserController {

    private final AdminCreateUserView view;
    private final MainApp navigator;
    private final AuthService authService;
    

    public AdminCreateUserController(AdminCreateUserView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.authService = new AuthService(); // Instance locale pour l'inscription

        // Lier les actions des boutons
        view.setSaveButtonAction(e -> handleSave());
        view.setCancelButtonAction(e -> handleCancel());

        // Charger les données (compétences) dans la vue
        loadData();
    }

    private void loadData() {
        // Dans ce cas, on a juste besoin de charger les compétences disponibles
        Platform.runLater(() -> view.populateCompetences(authService.getAllCompetences()));
    }

    private void handleSave() {
        // 1. Récupérer toutes les données de la vue
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
        // 2. Valider les champs obligatoires
        if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || password.isEmpty() || dateNaissance == null) {
            NotificationUtils.showError("Champs manquants", "Veuillez remplir tous les champs personnels et le mot de passe.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
             NotificationUtils.showError("Format invalide", "L'adresse email n'est pas valide.");
            return;
        }

        // 3. Appeler le service d'authentification pour créer le secouriste et son compte
        try {
            Date dob = Date.from(dateNaissance.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            // On utilise la méthode registerSecouriste étendue
            boolean success = authService.registerSecouriste(prenom, nom, email, password, dob, selectedCompetences);

            if (success) {
                NotificationUtils.showSuccess("Utilisateur créé", "Le secouriste " + prenom + " " + nom + " a été ajouté avec succès.");
                navigator.showAdminUtilisateursView(view.getCompte()); // Rediriger vers la liste des utilisateurs
            } else {
                // Le service devrait avoir levé une exception en cas d'email dupliqué,
                // mais on garde une sécurité.
                NotificationUtils.showError("Erreur", "La création de l'utilisateur a échoué. L'email existe peut-être déjà.");
            }
        } catch (Exception e) {
            // Capturer les exceptions spécifiques comme l'email dupliqué
            NotificationUtils.showError("Erreur de création", e.getMessage());
        }
    }

    private void handleCancel() {
        navigator.showAdminUtilisateursView(view.getCompte());
    }
}