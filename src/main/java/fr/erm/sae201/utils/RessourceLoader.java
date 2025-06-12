package fr.erm.sae201.utils;

import javafx.scene.image.Image;

public class RessourceLoader {

    /**
     * Loads an image from the resources directory.
     *
     * @param imagePath the path to the image file relative to the resources directory
     * @return the loaded Image object, or null if the image could not be loaded
     */
    public static Image loadImage(String imagePath) {
            // Image olympicRingsImage = new Image(getClass().getResource("/resources/images/olympic_rings.png").toExternalForm());
        // try to load image, if fail load image 404.png and send error in console
        try {
            return new Image(RessourceLoader.class.getResourceAsStream("/images/" + imagePath));
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            return new Image(RessourceLoader.class.getResourceAsStream("/images/404.png"));
        }
    }

    /**
     * Loads a CSS file from the resources directory.
     *
     * @param cssPath the path to the CSS file relative to the resources directory
     * @return the loaded CSS content as a String, or null if the file could not be loaded
     */
    public static String loadCSS(String cssPath) {
        try {
            return RessourceLoader.class.getResource("/css/" + cssPath).toExternalForm();
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + e.getMessage());
            return null;
        }
    }

    
}
