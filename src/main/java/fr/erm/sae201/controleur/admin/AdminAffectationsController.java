package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.metier.graphe.algorithme.ServiceAffectationExhaustive;
import fr.erm.sae201.metier.graphe.algorithme.ServiceAffectationGloutonne;
import fr.erm.sae201.metier.graphe.algorithme.ModelesAlgorithme.AffectationResultat;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminAffectationsView;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdminAffectationsController {

    private final AdminAffectationsView view;
    private final MainApp navigator;
    private final DPSDAO dpsDAO;
    private final AffectationDAO affectationDAO;
    private final ServiceAffectationExhaustive serviceExhaustif;
    private final ServiceAffectationGloutonne serviceGlouton;

    private DPS dpsSelectionne;
    private List<AffectationResultat> propositionActuelle;

    public AdminAffectationsController(AdminAffectationsView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator;
        this.dpsDAO = new DPSDAO();
        this.affectationDAO = new AffectationDAO();
        this.serviceExhaustif = new ServiceAffectationExhaustive();
        this.serviceGlouton = new ServiceAffectationGloutonne();

        // Lier les actions de l'interface aux méthodes du contrôleur
        view.setOnDpsSelected((observable, oldValue, newValue) -> handleDpsSelection(newValue));
        view.setRunExhaustiveAction(event -> runAlgorithm(serviceExhaustif::trouverMeilleureAffectationPourDPS, "Approche exhaustive"));
        view.setRunGreedyAction(event -> runAlgorithm(serviceGlouton::trouverAffectationPourDPS, "Approche gloutonne"));
        view.setSaveChangesAction(event -> saveChanges());
        
        // Charger les données initiales (la liste des DPS)
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
     * Méthode générique pour lancer un algorithme d'affectation.
     * @param algorithmFunction La fonction de l'algorithme à exécuter (ex: serviceExhaustif::methode).
     * @param algorithmName Le nom de l'algorithme pour les messages.
     */
    private void runAlgorithm(Function<DPS, List<AffectationResultat>> algorithmFunction, String algorithmName) {
        if (dpsSelectionne == null) {
            NotificationUtils.showError("Aucun DPS", "Veuillez d'abord sélectionner un dispositif.");
            return;
        }
        view.showLoading(true);
        
        Task<List<AffectationResultat>> task = new Task<>() {
            @Override
            protected List<AffectationResultat> call() {
                System.out.println("Lancement de l'algorithme : " + algorithmName);
                long startTime = System.currentTimeMillis();
                
                // Appelle la fonction de l'algorithme passée en paramètre
                List<AffectationResultat> result = algorithmFunction.apply(dpsSelectionne);
                
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
    
        List<Affectation> affectationsAEnregistrer = propositionActuelle.stream()
            .map(res -> new Affectation(dpsSelectionne, res.secouriste(), res.poste().competenceRequise()))
            .collect(Collectors.toList());
            
        boolean success = affectationDAO.replaceAffectationsForDps(dpsSelectionne.getId(), affectationsAEnregistrer);

        if (success) {
            NotificationUtils.showSuccess("Succès", "Les affectations ont été enregistrées en base de données.");
        } else {
            NotificationUtils.showError("Erreur", "L'enregistrement des affectations a échoué.");
        }
    }
}