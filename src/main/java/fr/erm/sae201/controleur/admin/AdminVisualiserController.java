package fr.erm.sae201.controleur.admin;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.DPS;
import fr.erm.sae201.metier.service.AffectationMngt;
import fr.erm.sae201.metier.service.DPSMngt;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.admin.AdminVisualiserView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur pour la vue de visualisation du planning des DPS.
 * 
 * Cette classe gère l'affichage d'un calendrier hebdomadaire. Elle récupère
 * les Dispositifs Prévisionnels de Secours (DPS) pour la semaine sélectionnée,
 * calcule leur état d'avancement (nombre d'affectés vs. requis) et demande
 * à la vue de les afficher.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminVisualiserController {

    /** La vue gérée par ce contrôleur. */
    private final AdminVisualiserView view;

    /** Le navigateur principal de l'application. */
    private final MainApp navigator;

    /** Le compte de l'utilisateur actuellement connecté. */
    private final CompteUtilisateur compte;

    /** Le service métier pour la gestion des DPS. */
    private final DPSMngt dpsMngt;

    /** Le service métier pour la gestion des affectations. */
    private final AffectationMngt affectationMngt;

    /** Le premier jour (lundi) de la semaine actuellement affichée. */
    private LocalDate currentWeekStart;

    /**
     * Structure de données pour transporter les informations d'un DPS
     * enrichies de son état d'affectation.
     *
     * @param dps      L'objet DPS concerné.
     * @param assigned Le nombre de secouristes actuellement affectés.
     * @param required Le nombre total de postes à pourvoir.
     */
    public record DpsStatusInfo(DPS dps, int assigned, int required) {}

    /**
     * Constructeur du contrôleur de visualisation.
     * Initialise les services, définit la semaine courante et lance le
     * premier affichage des données.
     *
     * @param view      La vue à contrôler.
     * @param navigator Le navigateur principal de l'application.
     * @param compte    Le compte de l'utilisateur connecté.
     */
    public AdminVisualiserController(AdminVisualiserView view, MainApp navigator, CompteUtilisateur compte) {
        this.view = view;
        this.navigator = navigator;
        this.compte = compte;
        this.dpsMngt = new DPSMngt();
        this.affectationMngt = new AffectationMngt();
        this.currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);

        this.view.setWeekChangeHandler(weeksToAdd -> {
            currentWeekStart = currentWeekStart.plusWeeks(weeksToAdd);
            refreshView();
        });

        refreshView();
    }

    /**
     * Rafraîchit la vue en chargeant et affichant les DPS pour la semaine courante.
     * Calcule la date de fin de semaine, récupère tous les DPS dans cet intervalle,
     * puis pour chaque DPS, compte le nombre de secouristes affectés par rapport
     * au nombre requis avant de transmettre ces données à la vue.
     */
    public void refreshView() {
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        List<DPS> allDpsInWeek = dpsMngt.getAllDpsBetweenDates(currentWeekStart, weekEnd);

        List<DpsStatusInfo> dpsStatusList = new ArrayList<>();
        for (DPS dps : allDpsInWeek) {
            int assignedCount = affectationMngt.getAssignmentCountForDps(dps.getId());
            int requiredCount = dpsMngt.getTotalPersonnelRequired(dps.getId());
            dpsStatusList.add(new DpsStatusInfo(dps, assignedCount, requiredCount));
        }

        view.populateCalendar(currentWeekStart, dpsStatusList);
    }
}