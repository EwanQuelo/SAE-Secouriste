package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.dao.AffectationDAO;
import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.Affectation;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.ServiceAffectationExhaustive;
import fr.erm.sae201.metier.service.ServiceAffectationExhaustive.AffectationResultat;
import fr.erm.sae201.utils.NotificationUtils;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminAffectationsView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import java.util.List;
import java.util.stream.Collectors;

public class AdminAffectationsController {

    private final AdminAffectationsView view;
    private final MainApp navigator; // CORRIGÉ : Ajout de la déclaration
    private final DPSDAO dpsDAO;
    private final AffectationDAO affectationDAO; // CORRIGÉ : Ajout de la déclaration
    private final ServiceAffectationExhaustive serviceExhaustif;

    private DPS dpsSelectionne;
    private List<AffectationResultat> propositionActuelle;

    public AdminAffectationsController(AdminAffectationsView view, MainApp navigator) {
        this.view = view;
        this.navigator = navigator; // CORRIGÉ : Initialisation
        this.dpsDAO = new DPSDAO();
        this.affectationDAO = new AffectationDAO(); // CORRIGÉ : Initialisation
        this.serviceExhaustif = new ServiceAffectationExhaustive();

        // Utilisation de la syntaxe lambda correcte pour le ChangeListener
        view.setOnDpsSelected((observable, oldValue, newValue) -> handleDpsSelection(newValue));
        
        view.setRunExhaustiveAction(e -> runExhaustiveAlgorithm());
        // view.setRunGreedyAction(e -> runGreedyAlgorithm()); // Pour plus tard
        view.setSaveChangesAction(e -> saveChanges());
        
        loadInitialData();
    }

    private void loadInitialData() {
        List<DPS> allDps = dpsDAO.findAll();
        view.populateDpsList(allDps);
    }
    
    private void handleDpsSelection(DPS dps) {
        if (dps == null) {
            view.setAlgoButtonsDisabled(true);
            return;
        }
        this.dpsSelectionne = dps;
        this.propositionActuelle = null;
        view.displayDpsDetails(dps);
        view.clearProposition();
        view.setAlgoButtonsDisabled(false);
    }

    private void runExhaustiveAlgorithm() {
        if (dpsSelectionne == null) {
            NotificationUtils.showError("Aucun DPS", "Veuillez d'abord sélectionner un dispositif.");
            return;
        }
        view.showLoading(true);
        
        Task<List<AffectationResultat>> task = new Task<>() {
            @Override
            protected List<AffectationResultat> call() throws Exception {
                // On appelle la méthode du service qui prend un DPS en paramètre
                return serviceExhaustif.trouverMeilleureAffectationPourDPS(dpsSelectionne);
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
            // CORRIGÉ : Utilisation de showError ou showSuccess, pas showInfo
            NotificationUtils.showSuccess("Aucune affectation", "La proposition était vide, rien n'a été enregistré.");
            return;
        }
    
        System.out.println("Enregistrement de " + propositionActuelle.size() + " affectations pour le DPS " + dpsSelectionne.getId());
        
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