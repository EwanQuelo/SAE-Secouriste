package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.AffectationDAO;
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
// L'import de "Function" n'est plus nécessaire
// import java.util.function.Function; 

public class AdminAffectationsController {

    private final AdminAffectationsView view;
    private final MainApp navigator;
    private final DPSDAO dpsDAO;
    private final AffectationDAO affectationDAO;
    private final ServiceAffectation serviceAffectation;

    private DPS dpsSelectionne;
    private List<AffectationResultat> propositionActuelle;

    public AdminAffectationsController(AdminAffectationsView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsDAO = new DPSDAO();
        this.affectationDAO = new AffectationDAO();
        this.serviceAffectation = new ServiceAffectation();

        // Lier les actions de l'interface aux méthodes du contrôleur
        view.setOnDpsSelected((observable, oldValue, newValue) -> handleDpsSelection(newValue));
        
        // MODIFIÉ : L'appel est simplifié. On passe une simple chaîne de caractères.
        view.setRunExhaustiveAction(event -> runAlgorithm("exhaustive"));
        view.setRunGreedyAction(event -> runAlgorithm("glouton"));
        
        view.setSaveChangesAction(event -> saveChanges());
        
        loadInitialData();
    }

    private void loadInitialData() {
        List<DPS> allDps = dpsDAO.findAll();
        view.populateDpsList(allDps);
    }
    
    private void handleDpsSelection(DPS dps) {
        if (dps == null) {
            view.setRightPanelDisabled(true);
            return;
        }
        this.dpsSelectionne = dps;
        this.propositionActuelle = null;
        view.displayDpsDetails(dps);
        view.clearProposition();
        view.setRightPanelDisabled(false);
    }

    /**
     * MODIFIÉ : Méthode simplifiée pour lancer un algorithme d'affectation.
     * Elle ne prend plus de fonction en argument, mais une chaîne identifiant l'algorithme.
     * @param algorithmType La chaîne identifiant l'algorithme ("exhaustive" ou "glouton").
     */
    private void runAlgorithm(String algorithmType) {
        if (dpsSelectionne == null) {
            NotificationUtils.showError("Aucun DPS", "Veuillez d'abord sélectionner un dispositif.");
            return;
        }
        view.showLoading(true);
        
        Task<List<AffectationResultat>> task = new Task<>() {
            @Override
            protected List<AffectationResultat> call() {
                long startTime = System.currentTimeMillis();
                
                List<AffectationResultat> result;
                final String algorithmName;

                // Un simple if/else pour choisir quelle méthode appeler.
                // C'est beaucoup plus simple à comprendre qu'un objet Function.
                if ("exhaustive".equals(algorithmType)) {
                    algorithmName = "Approche exhaustive";
                    System.out.println("Lancement de l'algorithme : " + algorithmName);
                    result = serviceAffectation.trouverAffectationExhaustive(dpsSelectionne);
                } else {
                    algorithmName = "Approche gloutonne";
                    System.out.println("Lancement de l'algorithme : " + algorithmName);
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
            System.err.println("ERREUR DANS LE THREAD DE L'ALGORITHME :");
            exception.printStackTrace();
            NotificationUtils.showError("Erreur Algorithme", "Une erreur est survenue : " + exception.getMessage());
            view.showLoading(false);
        });
        
        new Thread(task).start();
    }

    private void saveChanges() {
        if (propositionActuelle == null || dpsSelectionne == null) {
            NotificationUtils.showError("Rien à enregistrer", "Veuillez d'abord générer une proposition pour un DPS.");
            return;
        }

        if (propositionActuelle.isEmpty()) {
            NotificationUtils.showSuccess("Aucune affectation", "La proposition était vide, rien n'a été enregistré.");
            return;
        }
    
        // Conversion du résultat en liste d'objets Affectation (sans stream)
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