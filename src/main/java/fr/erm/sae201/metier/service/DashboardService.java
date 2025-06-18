package fr.erm.sae201.metier.service;

import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Secouriste;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    private final SecouristeMngt secouristeMngt = new SecouristeMngt();

    /**
     * Calcule la répartition des compétences parmi tous les secouristes.
     * @return Une Map avec l'intitulé de la compétence et le nombre de fois où elle est possédée.
     */
    public Map<String, Integer> getSkillDistribution() {
        List<Secouriste> secouristes = secouristeMngt.getAllSecouristes();
        Map<String, Integer> counts = new HashMap<>();
        for (Secouriste secouriste : secouristes) {
            for (Competence competence : secouriste.getCompetences()) {
                counts.merge(competence.getIntitule(), 1, Integer::sum);
            }
        }
        return counts;
    }

    /**
     * Calcule la répartition des secouristes par tranche d'âge.
     * @return Une Map avec la tranche d'âge et le nombre de secouristes correspondants.
     */
    public Map<String, Integer> getAgeDistribution() {
        List<Secouriste> secouristes = secouristeMngt.getAllSecouristes();
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("18-25", 0);
        counts.put("26-35", 0);
        counts.put("36-45", 0);
        counts.put("46-55", 0);
        counts.put("56+", 0);

        for (Secouriste secouriste : secouristes) {
            if (secouriste.getDateNaissance() == null) continue;
            LocalDate birthDate = secouriste.getDateNaissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int age = Period.between(birthDate, LocalDate.now()).getYears();

            if (age <= 25) counts.merge("18-25", 1, Integer::sum);
            else if (age <= 35) counts.merge("26-35", 1, Integer::sum);
            else if (age <= 45) counts.merge("36-45", 1, Integer::sum);
            else if (age <= 55) counts.merge("46-55", 1, Integer::sum);
            else counts.merge("56+", 1, Integer::sum);
        }
        return counts;
    }
}