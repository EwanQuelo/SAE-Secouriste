package fr.erm.sae201.vue;

/**
 * Classe de lancement principale de l'application.
 * Son unique rôle est de servir de point d'entrée et d'appeler la méthode `main`
 * de la classe MainApp, qui gère le démarrage effectif de l'application JavaFX.
 * Cette structure est couramment utilisée pour découpler le point d'entrée de la
 * classe principale de l'interface graphique.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class Launcher {
    /**
     * Point d'entrée de l'application.
     * Délègue l'exécution à la classe MainApp.
     *
     * @param args Les arguments de la ligne de commande (non utilisés dans cette application).
     */
    public static void main(String[] args) {
        MainApp.main(args);
    }
}