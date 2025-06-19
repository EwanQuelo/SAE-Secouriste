package fr.erm.sae201.utils;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Classe utilitaire pour afficher des notifications standardisées dans l'application.
 * 
 * Elle centralise la configuration et l'affichage des notifications de succès,
 * d'avertissement et d'erreur en utilisant la bibliothèque ControlsFX.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class NotificationUtils {

    /**
     * Affiche une notification de succès de type "information".
     *
     * @param title   Titre de la notification.
     * @param message Message à afficher dans la notification.
     */
    public static void showSuccess(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.BOTTOM_LEFT)
                .hideAfter(Duration.seconds(4))
                .showInformation();
    }

    /**
     * Affiche une notification de type "avertissement".
     *
     * @param title   Titre de la notification.
     * @param message Message à afficher dans la notification.
     */
    public static void showWarning(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.BOTTOM_LEFT)
                .hideAfter(Duration.seconds(4))
                .showWarning();
    }

    /**
     * Affiche une notification de type "erreur".
     *
     * @param title   Titre de la notification.
     * @param message Message à afficher dans la notification.
     */
    public static void showError(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.BOTTOM_LEFT)
                .hideAfter(Duration.seconds(4))
                .showError();
    }
}