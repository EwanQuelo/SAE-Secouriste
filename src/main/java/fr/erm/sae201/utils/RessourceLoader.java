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
     * Loads an image from a Base64 encoded string.
     * @param base64Image the Base64 encoded string representing the image
     * @return the loaded Image object, or a default image if the Base64 string is invalid
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
