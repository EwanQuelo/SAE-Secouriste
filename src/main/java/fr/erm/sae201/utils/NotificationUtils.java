package fr.erm.sae201.utils;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationUtils {

    /**
     * Affiche une notification de succès.
     * @param title Titre de la notification.
     * @param message Message à afficher.
     */
    public static void showSuccess(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(5))
                .showInformation(); // Utilise le style d'information (.info)
    }

    /**
     * Affiche une notification d'avertissement.
     * @param title Titre de la notification.
     * @param message Message à afficher.
     */
    public static void showWarning(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(5))
                .showWarning();
    }

    /**
     * Affiche une notification d'erreur.
     * @param title Titre de la notification.
     * @param message Message à afficher.
     */
    public static void showError(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(7)) // On laisse plus de temps pour lire une erreur
                .showError();
    }
}