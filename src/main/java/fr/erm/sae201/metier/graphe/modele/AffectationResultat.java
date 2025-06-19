package fr.erm.sae201.metier.graphe.modele;

import fr.erm.sae201.metier.persistence.Secouriste;
import java.util.Objects;

/**
 * Représente le résultat d'une affectation générée par un algorithme.
 * Il s'agit d'une structure de données simple qui associe un secouriste à un poste.
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public final class AffectationResultat {

    private final Secouriste secouriste;
    private final Poste poste;

    /**
     * Construit un nouveau résultat d'affectation.
     *
     * @param secouriste Le secouriste affecté. Ne peut pas être nul.
     * @param poste Le poste pourvu. Ne peut pas être nul.
     * @throws IllegalArgumentException si les arguments sont nuls.
     */
    public AffectationResultat(Secouriste secouriste, Poste poste) {
        if (secouriste == null) {
            throw new IllegalArgumentException("Le secouriste ne peut pas être nul.");
        }
        if (poste == null) {
            throw new IllegalArgumentException("Le poste ne peut pas être nul.");
        }
        this.secouriste = secouriste;
        this.poste = poste;
    }

    public Secouriste getSecouriste() {
        return secouriste;
    }

    public Poste getPoste() {
        return poste;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AffectationResultat that = (AffectationResultat) o;
        return Objects.equals(secouriste, that.secouriste) && Objects.equals(poste, that.poste);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secouriste, poste);
    }

    @Override
    public String toString() {
        return "AffectationResultat[" +
               "secouriste=" + secouriste + ", " +
               "poste=" + poste + ']';
    }
}