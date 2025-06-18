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
 * Le tableau de bord spécifique pour un administrateur, affichant des graphiques.
 */
public class AdminDashboard extends BaseView {

    private final DashboardService dashboardService;
    private BarChart<String, Number> skillChart;
    private PieChart ageChart;

    /**
     * Constructeur du tableau de bord administrateur.
     * @param navigator L'instance de MainApp pour la navigation.
     * @param compte L'utilisateur connecté.
     */
    public AdminDashboard(MainApp navigator, CompteUtilisateur compte) {
        // Appelle le constructeur parent qui, à son tour, appellera createCenterContent().
        // Les graphiques seront donc créés mais vides.
        super(navigator, compte, "Accueil");
        
        // On initialise le service
        this.dashboardService = new DashboardService();

        // On charge les données dans les graphiques qui existent déjà.
        loadChartData();
    }

    /**
     * Crée et retourne le contenu spécifique à ce tableau de bord,
     * c'est-à-dire une grille contenant les deux graphiques.
     * @return Un Node contenant les éléments du tableau de bord.
     */
    @Override
    protected Node createCenterContent() {
        // Un HBox pour placer les deux panneaux de graphiques côte à côte
        HBox chartsContainer = new HBox(20);
        chartsContainer.setPadding(new Insets(10));

        Node skillChartPanel = createSkillChartPanel();
        Node ageChartPanel = createAgeChartPanel();

        // On donne la même largeur aux deux panneaux
        HBox.setHgrow(skillChartPanel, Priority.ALWAYS);
        HBox.setHgrow(ageChartPanel, Priority.ALWAYS);

        chartsContainer.getChildren().addAll(skillChartPanel, ageChartPanel);
        
        return chartsContainer;
    }

    /**
     * Crée le panneau contenant le graphique des compétences.
     * @return Un Node contenant le titre et le graphique en barres.
     */
    private Node createSkillChartPanel() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        skillChart = new BarChart<>(xAxis, yAxis);
        skillChart.setTitle("Compétences les plus maîtrisées");
        skillChart.setLegendVisible(false); // Pas besoin de légende pour une seule série
        yAxis.setLabel("Nombre de secouristes");
        xAxis.setLabel("Compétence");
        
        VBox panel = new VBox(10, skillChart);
        panel.getStyleClass().add("dashboard-chart-panel");
        VBox.setVgrow(skillChart, Priority.ALWAYS);
        return panel;
    }

    /**
     * Crée le panneau contenant le graphique des tranches d'âge.
     * @return Un Node contenant le titre et le diagramme circulaire.
     */
    private Node createAgeChartPanel() {
        ageChart = new PieChart();
        ageChart.setTitle("Répartition par tranche d'âge");
        ageChart.setLegendSide(Side.LEFT); // Légende à gauche
        ageChart.setLabelsVisible(true); // Affiche les labels sur les parts

        VBox panel = new VBox(10, ageChart);
        panel.getStyleClass().add("dashboard-chart-panel");
        VBox.setVgrow(ageChart, Priority.ALWAYS);
        return panel;
    }

    /**
     * Récupère les données via le service et peuple les graphiques.
     */
    private void loadChartData() {
        // --- Peuplement du graphique des compétences ---
        Map<String, Integer> skillData = dashboardService.getSkillDistribution();
        XYChart.Series<String, Number> skillSeries = new XYChart.Series<>();
        
        // Trie les compétences par nombre de possesseurs et ne garde que les 10 premières
        skillData.entrySet().stream()
                 .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                 .limit(10) 
                 .forEach(entry -> skillSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        
        skillChart.getData().add(skillSeries);

        // Appliquer un style aux barres
        for (Node n : skillChart.lookupAll(".chart-bar")) {
            n.setStyle("-fx-bar-fill: #1E88E5;");
        }

        // --- Peuplement du graphique des âges ---
        Map<String, Integer> ageData = dashboardService.getAgeDistribution();
        
        // Filtre les tranches d'âge vides pour ne pas polluer le graphique
        ObservableList<PieChart.Data> pieChartData = ageData.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        
        ageChart.setData(pieChartData);
    }
}