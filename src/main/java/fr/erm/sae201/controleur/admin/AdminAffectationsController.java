package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.graphe.algorithme.ModelesAlgorithme.AffectationResultat;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.ServiceAffectation;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminAffectationsView;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur pour l'interface de gestion des affectations par l'administrateur.
 * <p>
 * Cette classe fait le lien entre la vue (AdminAffectationsView) et les modèles de données.
 * Elle gère la sélection d'un Dispositif Prévisionnel de Secours (DPS), le lancement
 * des algorithmes d'affectation et l'enregistrement des résultats en base de données.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminAffectationsController {

    /** La vue associée à ce contrôleur. */
    private final AdminAffectationsView view;

    /** Le navigateur principal de l'application pour changer de vue. */
    private final MainApp navigator;

    /** Le DAO pour accéder aux données des DPS. */
    private final DPSDAO dpsDAO;

    /** Le DAO pour accéder aux données des affectations. */
    private final AffectationDAO affectationDAO;

    /** Le service métier contenant la logique des algorithmes d'affectation. */
    private final ServiceAffectation serviceAffectation;

    /** Le DPS actuellement sélectionné par l'utilisateur dans l'interface. */
    private DPS dpsSelectionne;

    /** La dernière proposition d'affectation générée par un algorithme. */
    private List<AffectationResultat> propositionActuelle;

    /**
     * Constructeur du contrôleur des affectations.
     * Initialise les dépendances et lie les actions de l'interface graphique
     * aux méthodes correspondantes de ce contrôleur.
     *
     * @param view La vue (interface) à contrôler.
     * @param navigator Le navigateur principal de l'application.
     */
    public AdminAffectationsController(AdminAffectationsView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsDAO = new DPSDAO();
        this.affectationDAO = new AffectationDAO();
        this.serviceAffectation = new ServiceAffectation();

        // Lie les actions de l'interface aux méthodes du contrôleur
        view.setOnDpsSelected((observable, oldValue, newValue) -> handleDpsSelection(newValue));
        view.setRunExhaustiveAction(event -> runAlgorithm("exhaustive"));
        view.setRunGreedyAction(event -> runAlgorithm("glouton"));
        view.setSaveChangesAction(event -> saveChanges());

        loadInitialData();
    }

    /**
     * Charge les données initiales nécessaires à la vue, notamment la liste de tous les DPS.
     */
    private void loadInitialData() {
        List<DPS> allDps = dpsDAO.findAll();
        view.populateDpsList(allDps);
    }

    /**
     * Gère l'événement de sélection d'un DPS dans la liste.
     * Met à jour l'état du contrôleur et de la vue avec les informations du DPS choisi.
     *
     * @param dps Le DPS qui a été sélectionné. Peut être null si la sélection est effacée.
     */
    private void handleDpsSelection(DPS dps) {
        if (dps == null) {
            view.setRightPanelDisabled(true);
            return;
        }
        this.dpsSelectionne = dps;
        this.propositionActuelle = null; // Réinitialise la proposition en attente
        view.displayDpsDetails(dps);
        view.clearProposition();
        view.setRightPanelDisabled(false);
    }

    /**
     * Lance un algorithme d'affectation de manière asynchrone pour ne pas bloquer l'interface.
     * Affiche un indicateur de chargement pendant l'exécution et met à jour la vue
     * avec le résultat une fois terminé.
     *
     * @param algorithmType La chaîne identifiant l'algorithme à utiliser ("exhaustive" ou "glouton").
     */
    private void runAlgorithm(String algorithmType) {
        if (dpsSelectionne == null) {
            NotificationUtils.showError("Aucun DPS", "Veuillez d'abord sélectionner un dispositif.");
            return;
        }
        view.showLoading(true);

        // Utilisation d'une Tâche (Task) pour exécuter l'algorithme en arrière-plan.
        Task<List<AffectationResultat>> task = new Task<>() {
            @Override
            protected List<AffectationResultat> call() {
                long startTime = System.currentTimeMillis();
                List<AffectationResultat> result;
                final String algorithmName;

                if ("exhaustive".equals(algorithmType)) {
                    algorithmName = "Approche exhaustive";
                    result = serviceAffectation.trouverAffectationExhaustive(dpsSelectionne);
                } else {
                    algorithmName = "Approche gloutonne";
                    result = serviceAffectation.trouverAffectationGloutonne(dpsSelectionne);
                }

                long endTime = System.currentTimeMillis();
                System.out.println("Fin de l'algorithme : " + algorithmName + ". Temps : " + (endTime - startTime) + " ms.");
                return result;
            }
        };

        task.setOnSucceeded(e -> {
            propositionActuelle = task.getValue();
            view.displayProposition(propositionActuelle);
            view.showLoading(false);
        });

        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            // Affiche la trace complète de l'erreur dans la console pour faciliter le débogage.
            System.err.println("ERREUR DANS LE THREAD DE L'ALGORITHME :");
            exception.printStackTrace();
            NotificationUtils.showError("Erreur Algorithme", "Une erreur est survenue : " + exception.getMessage());
            view.showLoading(false);
        });

        new Thread(task).start();
    }

    /**
     * Enregistre en base de données la proposition d'affectation actuellement affichée.
     * Remplace toutes les affectations existantes pour le DPS sélectionné par les nouvelles.
     */
    private void saveChanges() {
        if (propositionActuelle == null || dpsSelectionne == null) {
            NotificationUtils.showError("Rien à enregistrer", "Veuillez d'abord générer une proposition pour un DPS.");
            return;
        }

        if (propositionActuelle.isEmpty()) {
            NotificationUtils.showSuccess("Aucune affectation", "La proposition était vide, rien n'a été enregistré.");
            return;
        }

        List<Affectation> affectationsAEnregistrer = new ArrayList<>();
        for (AffectationResultat res : propositionActuelle) {
            affectationsAEnregistrer.add(
                new Affectation(dpsSelectionne, res.secouriste(), res.poste().competenceRequise())
            );
        }

        boolean success = affectationDAO.replaceAffectationsForDps(dpsSelectionne.getId(), affectationsAEnregistrer);

        if (success) {
            NotificationUtils.showSuccess("Succès", "Les affectations ont été enregistrées en base de données.");
        } else {
            NotificationUtils.showError("Erreur", "L'enregistrement des affectations a échoué.");
        }
    }
}