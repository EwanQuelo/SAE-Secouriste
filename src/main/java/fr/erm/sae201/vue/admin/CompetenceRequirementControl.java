package fr.erm.sae201.vue.admin;

import fr.erm.sae201.metier.persistence.Competence;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

/**
 * Un contrôle JavaFX personnalisé pour afficher une compétence et permettre
 * la saisie du nombre de secouristes requis via un Spinner.
 */
public class CompetenceRequirementControl extends HBox {

    private final Competence competence;
    private final Spinner<Integer> numberSpinner;

    /**
     * Construit le contrôle.
     * @param competence La compétence à afficher.
     * @param initialValue Le nombre initial de secouristes requis (0 si aucun).
     */
    public CompetenceRequirementControl(Competence competence, int initialValue) {
        super(10); // Espacement de 10px
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("competence-requirement-control");

        this.competence = competence;

        Label competenceLabel = new Label(competence.getIntitule());
        competenceLabel.setPrefWidth(150); // Largeur fixe pour l'alignement
        competenceLabel.getStyleClass().add("competence-requirement-label");

        // Utilisation d'un Spinner pour une meilleure expérience utilisateur
        this.numberSpinner = new Spinner<>(0, 50, initialValue); // De 0 à 50 secouristes, avec la valeur initiale
        this.numberSpinner.setPrefWidth(70);
        this.numberSpinner.getStyleClass().add("competence-requirement-spinner");

        this.getChildren().addAll(competenceLabel, numberSpinner);
    }

    /**
     * @return La compétence associée à ce contrôle.
     */
    public Competence getCompetence() {
        return competence;
    }

    /**
     * @return Le nombre de secouristes requis sélectionné dans le Spinner.
     */
    public int getRequiredNumber() {
        return numberSpinner.getValue();
    }
}