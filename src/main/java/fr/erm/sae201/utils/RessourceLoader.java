package fr.erm.sae201.utils;

import javafx.scene.image.Image;

/**
 * Classe utilitaire pour centraliser le chargement des ressources.
 * <p>
 * Fournit des méthodes statiques pour charger des images et des feuilles de style
 * depuis les répertoires de ressources de l'application, en gérant les erreurs
 * de chargement de manière sécurisée.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class RessourceLoader {

    /**
     * Charge une image depuis le répertoire des ressources.
     * En cas d'échec, une image par défaut (404.png) est retournée.
     *
     * @param imagePath Le chemin vers le fichier image, relatif au répertoire `/images/`.
     * @return L'objet Image chargé, ou une image par défaut si le chargement échoue.
     */
    public static Image loadImage(String imagePath) {
        try {
            return new Image(RessourceLoader.class.getResourceAsStream("/images/" + imagePath));
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            return new Image(RessourceLoader.class.getResourceAsStream("/images/404.png"));
        }
    }

    /**
     * Charge une image à partir d'une chaîne de caractères encodée en Base64.
     * Utile pour les images stockées en base de données ou reçues via une API.
     *
     * @param base64Image La chaîne de caractères encodée en Base64 représentant l'image.
     * @return L'objet Image chargé, ou une image par défaut si la chaîne est invalide.
     */
    public static Image loadImageFromBase64(String base64Image) {
        try {
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
            return new Image(new java.io.ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            System.err.println("Error loading image from Base64: " + e.getMessage());
            return new Image(RessourceLoader.class.getResourceAsStream("/images/404.png"));
        }
    }

    /**
     * Charge un fichier CSS et retourne son chemin externe sous forme de chaîne.
     * Ce chemin est ensuite utilisable pour l'appliquer à une scène JavaFX.
     *
     * @param cssPath Le chemin vers le fichier CSS, relatif au répertoire `/css/`.
     * @return Le chemin externe du fichier CSS, ou `null` si le chargement échoue.
     */
    public static String loadCSS(String cssPath) {
        try {
            return RessourceLoader.class.getResource("/css/" + cssPath).toExternalForm();
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + cssPath + " - " + e.getMessage());
            return null;
        }
    }
}