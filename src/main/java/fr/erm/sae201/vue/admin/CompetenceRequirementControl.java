package fr.erm.sae201.vue.admin;

import fr.erm.sae201.metier.persistence.Competence;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

/**
 * Un contrôle JavaFX personnalisé qui combine un libellé pour une compétence
 * et un sélecteur numérique (Spinner) pour définir un besoin quantitatif.
 * Ce composant est utilisé dans le formulaire d'édition de DPS pour spécifier
 * le nombre de secouristes requis pour chaque compétence.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class CompetenceRequirementControl extends HBox {

    private final Competence competence;
    private final Spinner<Integer> numberSpinner;

    /**
     * Construit le contrôle de besoin en compétence.
     *
     * @param competence La compétence à afficher.
     * @param initialValue Le nombre initial de secouristes requis (généralement 0 pour un nouveau besoin).
     */
    public CompetenceRequirementControl(Competence competence, int initialValue) {
        super(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("competence-requirement-control");

        this.competence = competence;

        Label competenceLabel = new Label(competence.getIntitule());
        competenceLabel.setPrefWidth(150);
        competenceLabel.getStyleClass().add("competence-requirement-label");

        this.numberSpinner = new Spinner<>(0, 50, initialValue);
        this.numberSpinner.setPrefWidth(70);
        this.numberSpinner.getStyleClass().add("competence-requirement-spinner");

        this.getChildren().addAll(competenceLabel, numberSpinner);
    }

    /**
     * Retourne la compétence associée à ce contrôle.
     *
     * @return L'objet Compétence.
     */
    public Competence getCompetence() {
        return competence;
    }

    /**
     * Retourne le nombre de secouristes requis, tel que défini par l'utilisateur dans le Spinner.
     *
     * @return Le nombre de secouristes requis.
     */
    public int getRequiredNumber() {
        return numberSpinner.getValue();
    }
}