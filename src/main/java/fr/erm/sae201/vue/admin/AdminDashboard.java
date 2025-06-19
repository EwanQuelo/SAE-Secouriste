package fr.erm.sae201.vue.admin;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.service.DashboardService;
import fr.erm.sae201.vue.MainApp;
import fr.erm.sae201.vue.base.BaseView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Vue du tableau de bord spécifique pour un administrateur.
 * 
 * Affiche des graphiques de statistiques, tels que la répartition des
 * compétences et des tranches d'âge parmi les secouristes.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AdminDashboard extends BaseView {

    private final DashboardService dashboardService;
    /** Graphique en barres pour la répartition des compétences. */
    private BarChart<String, Number> skillChart;
    /** Diagramme circulaire pour la répartition des âges. */
    private PieChart ageChart;

    /**
     * Constructeur du tableau de bord administrateur.
     * Initialise les composants et charge les données des graphiques.
     *
     * @param navigator L'instance de MainApp pour la navigation.
     * @param compte    L'utilisateur administrateur connecté.
     */
    public AdminDashboard(MainApp navigator, CompteUtilisateur compte) {
        super(navigator, compte, "Accueil");
        
        this.dashboardService = new DashboardService();
        loadChartData();
    }

    /**
     * Crée le contenu central de la vue, composé d'une grille de graphiques.
     *
     * @return Un HBox contenant les panneaux des graphiques.
     */
    @Override
    protected Node createCenterContent() {
        HBox chartsContainer = new HBox(20);
        chartsContainer.setPadding(new Insets(10));

        Node skillChartPanel = createSkillChartPanel();
        Node ageChartPanel = createAgeChartPanel();

        HBox.setHgrow(skillChartPanel, Priority.ALWAYS);
        HBox.setHgrow(ageChartPanel, Priority.ALWAYS);

        chartsContainer.getChildren().addAll(skillChartPanel, ageChartPanel);
        
        return chartsContainer;
    }

    /**
     * Crée le panneau contenant le graphique des compétences.
     *
     * @return Un Node contenant le graphique en barres.
     */
    private Node createSkillChartPanel() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        skillChart = new BarChart<>(xAxis, yAxis);
        skillChart.setTitle("Compétences les plus maîtrisées");
        skillChart.setLegendVisible(false);
        yAxis.setLabel("Nombre de secouristes");
        xAxis.setLabel("Compétence");
        
        VBox panel = new VBox(10, skillChart);
        panel.getStyleClass().add("dashboard-chart-panel");
        VBox.setVgrow(skillChart, Priority.ALWAYS);
        return panel;
    }

    /**
     * Crée le panneau contenant le graphique des tranches d'âge.
     *
     * @return Un Node contenant le diagramme circulaire.
     */
    private Node createAgeChartPanel() {
        ageChart = new PieChart();
        ageChart.setTitle("Répartition par tranche d'âge");
        ageChart.setLegendSide(Side.LEFT);
        ageChart.setLabelsVisible(true);

        VBox panel = new VBox(10, ageChart);
        panel.getStyleClass().add("dashboard-chart-panel");
        VBox.setVgrow(ageChart, Priority.ALWAYS);
        return panel;
    }

    /**
     * Récupère les données via le DashboardService et peuple les graphiques.
     * Les données sont triées et filtrées pour un affichage plus clair.
     */
    private void loadChartData() {
        // Peuplement du graphique des compétences
        Map<String, Integer> skillData = dashboardService.getSkillDistribution();
        XYChart.Series<String, Number> skillSeries = new XYChart.Series<>();
        
        // Trie les compétences par popularité et ne garde que le top 10.
        skillData.entrySet().stream()
                 .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                 .limit(10) 
                 .forEach(entry -> skillSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        
        skillChart.getData().add(skillSeries);

        // Applique un style aux barres du graphique.
        for (Node n : skillChart.lookupAll(".chart-bar")) {
            n.setStyle("-fx-bar-fill: #1E88E5;");
        }

        // Peuplement du graphique des âges
        Map<String, Integer> ageData = dashboardService.getAgeDistribution();
        
        // Filtre les tranches d'âge vides pour ne pas polluer le graphique.
        ObservableList<PieChart.Data> pieChartData = ageData.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        
        ageChart.setData(pieChartData);
    }
}