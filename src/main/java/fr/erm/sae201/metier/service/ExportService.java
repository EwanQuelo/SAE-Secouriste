package fr.erm.sae201.metier.service;

import fr.erm.sae201.dao.DPSDAO;
import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.DPS;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service dédié à l'exportation de données, notamment pour la génération de fichiers CSV.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class ExportService {

    private final DPSDAO dpsDAO = new DPSDAO();

    /**
     * Génère une chaîne de caractères au format CSV contenant tous les dispositifs.
     * Les données incluent les détails du DPS ainsi que ses besoins en compétences.
     * Le séparateur de champ utilisé est la virgule.
     *
     * @return Une chaîne de caractères représentant les données au format CSV.
     */
    public String exportDpsToCsvString() {
        List<DPS> allDps = dpsDAO.findAll();
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("ID,Date,Sport,Site,Heure Debut,Heure Fin,Besoins\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (DPS dps : allDps) {
            Map<Competence, Integer> requirements = dpsDAO.findRequiredCompetencesForDps(dps.getId());

            // Formatte la liste des besoins en une seule chaîne de caractères pour la colonne CSV.
            String requirementsString = requirements.entrySet().stream()
                    .map(entry -> entry.getKey().getIntitule() + ": " + entry.getValue())
                    .collect(Collectors.joining(" | "));

            String sportNom = sanitizeCsvField(dps.getSport().getNom());
            String siteNom = sanitizeCsvField(dps.getSite().getNom());
            String date = dps.getJournee().getDate().format(dateFormatter);
            String heureDebut = String.format("%02d:%02d", dps.getHoraireDepart()[0], dps.getHoraireDepart()[1]);
            String heureFin = String.format("%02d:%02d", dps.getHoraireFin()[0], dps.getHoraireFin()[1]);
            String sanitizedRequirements = sanitizeCsvField(requirementsString);

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
     * Assainit une chaîne de caractères pour son insertion dans un champ CSV.
     * Si le champ contient une virgule, des guillemets ou un retour à la ligne,
     * il est entouré de guillemets. Les guillemets déjà présents dans la chaîne
     * sont doublés pour être correctement échappés.
     *
     * @param field La chaîne de caractères à assainir.
     * @return La chaîne de caractères prête à être insérée dans un champ CSV.
     */
    private String sanitizeCsvField(String field) {
        if (field == null) {
            return "";
        }
        String sanitized = field.replace("\"", "\"\"");
        if (sanitized.contains(",") || sanitized.contains("\"") || sanitized.contains("\n")) {
            return "\"" + sanitized + "\"";
        }
        return sanitized;
    }
}