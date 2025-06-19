package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.DPS;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service dédié à l'exportation de données, comme la génération de fichiers CSV.
 */
public class ExportService {

    private final DPSDAO dpsDAO = new DPSDAO();

    /**
     * MODIFIÉ : Génère une chaîne CSV contenant tous les dispositifs,
     * y compris leurs besoins en compétences, avec la virgule comme séparateur.
     *
     * @return Une chaîne de caractères représentant les données en CSV.
     */
    public String exportDpsToCsvString() {
        List<DPS> allDps = dpsDAO.findAll();
        StringBuilder csvBuilder = new StringBuilder();

        // 1. MODIFIÉ : L'en-tête utilise maintenant des virgules.
        csvBuilder.append("ID,Date,Sport,Site,Heure Debut,Heure Fin,Besoins\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (DPS dps : allDps) {
            Map<Competence, Integer> requirements = dpsDAO.findRequiredCompetencesForDps(dps.getId());

            String requirementsString = requirements.entrySet().stream()
                    .map(entry -> entry.getKey().getIntitule() + ": " + entry.getValue())
                    .collect(Collectors.joining(" | ")); // Utilisation d'un séparateur interne différent pour plus de clarté

            String sportNom = sanitizeCsvField(dps.getSport().getNom());
            String siteNom = sanitizeCsvField(dps.getSite().getNom());
            String date = dps.getJournee().getDate().format(dateFormatter);
            String heureDebut = String.format("%02d:%02d", dps.getHoraireDepart()[0], dps.getHoraireDepart()[1]);
            String heureFin = String.format("%02d:%02d", dps.getHoraireFin()[0], dps.getHoraireFin()[1]);
            String sanitizedRequirements = sanitizeCsvField(requirementsString);

            // 2. MODIFIÉ : La chaîne de formatage utilise maintenant des virgules.
            csvBuilder.append(String.format("%d,%s,%s,%s,%s,%s,%s\n",
                    dps.getId(),
                    date,
                    sportNom,
                    siteNom,
                    heureDebut,
                    heureFin,
                    sanitizedRequirements
            ));
        }
        return csvBuilder.toString();
    }

    /**
     * MODIFIÉ : Assainit un champ de texte pour le format CSV avec virgule comme séparateur.
     * Si le champ contient une virgule, des guillemets ou un retour à la ligne,
     * il est entouré de guillemets. Les guillemets existants sont doublés.
     *
     * @param field La chaîne de caractères à assainir.
     * @return La chaîne de caractères assainie.
     */
    private String sanitizeCsvField(String field) {
        if (field == null) {
            return "";
        }
        String sanitized = field.replace("\"", "\"\"");
        // 3. MODIFIÉ : La condition vérifie la présence de virgules au lieu de points-virgules.
        if (sanitized.contains(",") || sanitized.contains("\"") || sanitized.contains("\n")) {
            return "\"" + sanitized + "\"";
        }
        return sanitized;
    }
}