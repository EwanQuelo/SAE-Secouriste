package fr.erm.sae201.metier.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Représente un secouriste.
 *
 * @author Raphael MILLE
 * @author Ewan QUELO
 * @author Matheo BIET
 * @version 1.0
 */
public class Secouriste {
    
    /** L'identifiant unique du secouriste. */
    private long id;
    /** Le nom de famille du secouriste. */
    private String nom;
    /** Le prénom du secouriste. */
    private String prenom;
    /** La date de naissance du secouriste. */
    private Date dateNaissance;
    /** L'adresse email du secouriste. */
    private String email;
    /** Le numéro de téléphone du secouriste. */
    private String tel;
    /** L'adresse postale du secouriste. */
    private String addresse;
    /** L'ensemble des journées où le secouriste est disponible. */
    private Set<Journee> disponibilites;
    /** L'ensemble des compétences que le secouriste possède. */
    private Set<Competence> competences;

    /**
     * Construit un nouveau Secouriste avec un ID.
     *
     * @param id L'ID unique du secouriste.
     * @param nom Le nom de famille.
     * @param prenom Le prénom.
     * @param dateNaissance La date de naissance.
     * @param email L'adresse email.
     * @param tel Le numéro de téléphone.
     * @param addresse L'adresse postale.
     * @throws IllegalArgumentException si des paramètres obligatoires sont invalides.
     */
    public Secouriste(long id, String nom, String prenom, Date dateNaissance, String email, String tel, String addresse) {
        // La validation est faite par les setters, qui peuvent aussi cloner les objets mutables.
        setId(id);
        setNom(nom);
        setPrenom(prenom);
        setDateNaissance(dateNaissance);
        setEmail(email);
        setTel(tel);
        setAddresse(addresse);
        this.disponibilites = new HashSet<>();
        this.competences = new HashSet<>();
    }

    /**
     * Construit un nouveau Secouriste sans ID (typiquement avant son insertion en base de données).
     *
     * @param nom Le nom de famille.
     * @param prenom Le prénom.
     * @param dateNaissance La date de naissance.
     * @param email L'adresse email.
     * @param tel Le numéro de téléphone.
     * @param addresse L'adresse postale.
     */
    public Secouriste(String nom, String prenom, Date dateNaissance, String email, String tel, String addresse) {
        setNom(nom);
        setPrenom(prenom);
        setDateNaissance(dateNaissance);
        setEmail(email);
        setTel(tel);
        setAddresse(addresse);
        this.disponibilites = new HashSet<>();
        this.competences = new HashSet<>();
    }

    /**
     * Retourne l'ID du secouriste.
     * @return L'ID.
     */
    public long getId() {
        return id;
    }
    
    /**
     * Définit l'ID du secouriste.
     * @param id Le nouvel ID.
     * @throws IllegalArgumentException si l'ID n'est pas un nombre positif.
     */
    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number.");
        }
        this.id = id;
    }

    /**
     * Retourne le nom de famille.
     * @return Le nom.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de famille.
     * @param nom Le nouveau nom. Ne doit pas être null ou vide.
     * @throws IllegalArgumentException si le nom est null ou vide.
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom cannot be null or empty.");
        }
        this.nom = nom;
    }

    /**
     * Retourne le prénom.
     * @return Le prénom.
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom.
     * @param prenom Le nouveau prénom. Ne doit pas être null ou vide.
     * @throws IllegalArgumentException si le prénom est null ou vide.
     */
    public void setPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Prenom cannot be null or empty.");
        }
        this.prenom = prenom;
    }

    /**
     * Retourne une copie de la date de naissance.
     * @return Une copie de la date de naissance.
     */
    public Date getDateNaissance() {
        return new Date(dateNaissance.getTime());
    }

    /**
     * Définit la date de naissance.
     * @param dateNaissance La nouvelle date de naissance. Ne doit pas être null.
     * @throws IllegalArgumentException si la date est null.
     */
    public void setDateNaissance(Date dateNaissance) {
        if (dateNaissance == null) {
            throw new IllegalArgumentException("Date de naissance cannot be null.");
        }
        // Stocke une copie pour protéger l'objet original de modifications externes.
        this.dateNaissance = new Date(dateNaissance.getTime());
    }

    /**
     * Retourne l'email.
     * @return L'email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit l'email.
     * @param email Le nouvel email. Doit être valide.
     * @throws IllegalArgumentException si l'email est invalide.
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email must contain '@' and '.' characters.");
        }
        this.email = email;
    }

    /**
     * Retourne le numéro de téléphone.
     * @return Le téléphone.
     */
    public String getTel() {
        return tel;
    }

    /**
     * Définit le numéro de téléphone.
     * @param tel Le nouveau numéro. Peut être null ou vide.
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * Retourne l'adresse.
     * @return L'adresse.
     */
    public String getAddresse() {
        return addresse;
    }

    /**
     * Définit l'adresse.
     * @param addresse La nouvelle adresse. Peut être null ou vide.
     */
    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    /**
     * Retourne une copie de l'ensemble des disponibilités.
     * @return Un ensemble de Journee.
     */
    public Set<Journee> getDisponibilites() {
        return new HashSet<>(disponibilites);
    }

    /**
     * Définit l'ensemble des disponibilités.
     * @param disponibilites Le nouvel ensemble de disponibilités.
     * @throws IllegalArgumentException si l'ensemble ou un de ses éléments est null.
     */
    public void setDisponibilites(Set<Journee> disponibilites) {
        if (disponibilites == null) {
            throw new IllegalArgumentException("Disponibilites set cannot be null.");
        }
        for (Journee j : disponibilites) {
            if (j == null) {
                throw new IllegalArgumentException("Journee in disponibilites cannot be null.");
            }
        }
        this.disponibilites = new HashSet<>(disponibilites);
    }

    /**
     * Ajoute une disponibilité.
     * @param journee La journée à ajouter.
     * @throws IllegalArgumentException si la journée est null.
     */
    public void addDisponibilite(Journee journee) {
        if (journee == null) {
            throw new IllegalArgumentException("Journee to add cannot be null.");
        }
        this.disponibilites.add(journee);
    }
    
    /**
     * Supprime une disponibilité.
     * @param journee La journée à supprimer.
     * @return `true` si la journée a été supprimée.
     */
    public boolean removeDisponibilite(Journee journee) {
        if (journee == null) return false;
        return this.disponibilites.remove(journee);
    }

    /**
     * Retourne une copie de l'ensemble des compétences.
     * @return Un ensemble de Competence.
     */
    public Set<Competence> getCompetences() {
        return new HashSet<>(competences);
    }

    /**
     * Définit l'ensemble des compétences.
     * @param competences Le nouvel ensemble de compétences.
     * @throws IllegalArgumentException si l'ensemble ou un de ses éléments est null.
     */
    public void setCompetences(Set<Competence> competences) {
        if (competences == null) {
            throw new IllegalArgumentException("Competences set cannot be null.");
        }
        for (Competence c : competences) {
            if (c == null) {
                throw new IllegalArgumentException("Competence in set cannot be null.");
            }
        }
        this.competences = new HashSet<>(competences);
    }

    /**
     * Ajoute une compétence.
     * @param competence La compétence à ajouter.
     * @throws IllegalArgumentException si la compétence est null.
     */
    public void addCompetence(Competence competence) {
        if (competence == null) {
            throw new IllegalArgumentException("Competence to add cannot be null.");
        }
        this.competences.add(competence);
    }
    
    /**
     * Supprime une compétence.
     * @param competence La compétence à supprimer.
     * @return `true` si la compétence a été supprimée.
     */
    public boolean removeCompetence(Competence competence) {
        if (competence == null) return false;
        return this.competences.remove(competence);
    }

    /**
     * Compare ce secouriste à un autre objet.
     * Deux secouristes sont égaux si leurs ID sont identiques.
     * @param o L'objet à comparer.
     * @return `true` si les objets sont égaux.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Secouriste that = (Secouriste) o;
        return id == that.id;
    }

    /**
     * Génère un code de hachage basé sur l'ID.
     * @return Le code de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retourne une représentation textuelle du secouriste.
     * @return Une chaîne de caractères décrivant le secouriste.
     */
    @Override
    public String toString() {
        return "Secouriste{" +
               "id=" + id +
               ", nom='" + nom + '\'' +
               ", prenom='" + prenom + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}