package fr.erm.sae201.metier.graphe.modele;

import fr.erm.sae201.metier.persistence.Competence;
import java.util.Objects;

/**
 * Représente un poste de travail unique à pourvoir dans le cadre d'un algorithme d'affectation.
 * Un poste est défini par le DPS auquel il appartient et la compétence spécifique qu'il requiert.
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public final class Poste {

    private final long idDps;
    private final Competence competenceRequise;

    /**
     * Construit un nouveau Poste.
     *
     * @param idDps L'identifiant du DPS. Doit être un nombre positif.
     * @param competenceRequise La compétence requise pour ce poste. Ne peut pas être nulle.
     * @throws IllegalArgumentException si les arguments sont invalides.
     */
    public Poste(long idDps, Competence competenceRequise) {
        if (idDps <= 0) {
            throw new IllegalArgumentException("L'ID du DPS doit être un nombre positif.");
        }
        if (competenceRequise == null) {
            throw new IllegalArgumentException("La compétence requise ne peut pas être nulle.");
        }
        this.idDps = idDps;
        this.competenceRequise = competenceRequise;
    }

    public long getIdDps() {
        return idDps;
    }

    public Competence getCompetenceRequise() {
        return competenceRequise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poste poste = (Poste) o;
        return idDps == poste.idDps && Objects.equals(competenceRequise, poste.competenceRequise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDps, competenceRequise);
    }

    @Override
    public String toString() {
        return "Poste[" +
               "idDps=" + idDps + ", " +
               "competenceRequise=" + competenceRequise + ']';
    }
}