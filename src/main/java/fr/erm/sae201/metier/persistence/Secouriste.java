package fr.erm.sae201.metier.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a first aid responder (Secouriste).
 * @author Raphael Mille, Ewan Quelo, Matheo Biet 
 * @version 1.0
 */
public class Secouriste {
    
    private long id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String email;
    private String tel;
    private String addresse;
    private Set<Journee> disponibilites; // Relation EstDisponible
    private Set<Competence> competences;   // Relation Possede

    /**
     * Constructs a new Secouriste.
     * @param id The unique ID of the secouriste.
     * @param nom The last name. Must not be null or empty.
     * @param prenom The first name. Must not be null or empty.
     * @param dateNaissance The date of birth. Must not be null. A copy is stored.
     * @param email The email address. Must contain '@' and '.'.
     * @param tel The telephone number. Can be null or empty.
     * @param addresse The address. Can be null or empty.
     * @throws IllegalArgumentException if id is invalid, nom, prenom, dateNaissance or email are invalid.
     */
    public Secouriste(long id, String nom, String prenom, Date dateNaissance, String email, String tel, String addresse) {
        setId(id); // Though ID is usually immutable after creation, setter can do validation
        setNom(nom);
        setPrenom(prenom);
        setDateNaissance(dateNaissance); // Uses setter which clones
        setEmail(email);
        setTel(tel);
        setAddresse(addresse);
        this.disponibilites = new HashSet<>();
        this.competences = new HashSet<>();
    }

    /**
     * Gets the ID of the secouriste.
     * @return The ID.
     */
    public long getId() {
        return id;
    }
    
    /**
     * Sets the ID of the secouriste.
     * @param id The new ID.
     */
    public void setId(long id) {
        // Basic validation, e.g., positive ID
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number.");
        }
        this.id = id;
    }


    /**
     * Gets the last name of the secouriste.
     * @return The last name.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Sets the last name of the secouriste.
     * @param nom The new last name. Must not be null or empty.
     * @throws IllegalArgumentException if nom is null or empty.
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom cannot be null or empty.");
        }
        this.nom = nom;
    }

    /**
     * Gets the first name of the secouriste.
     * @return The first name.
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Sets the first name of the secouriste.
     * @param prenom The new first name. Must not be null or empty.
     * @throws IllegalArgumentException if prenom is null or empty.
     */
    public void setPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Prenom cannot be null or empty.");
        }
        this.prenom = prenom;
    }

    /**
     * Gets a copy of the date of birth of the secouriste.
     * @return A copy of the date of birth.
     */
    public Date getDateNaissance() {
        return new Date(dateNaissance.getTime()); // Return a copy
    }

    /**
     * Sets the date of birth of the secouriste.
     * A copy of the provided date is stored.
     * @param dateNaissance The new date of birth. Must not be null.
     * @throws IllegalArgumentException if dateNaissance is null.
     */
    public void setDateNaissance(Date dateNaissance) {
        if (dateNaissance == null) {
            throw new IllegalArgumentException("Date de naissance cannot be null.");
        }
        this.dateNaissance = new Date(dateNaissance.getTime()); // Store a copy
    }

    /**
     * Gets the email of the secouriste.
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the secouriste.
     * @param email The new email. Must contain '@' and '.'.
     * @throws IllegalArgumentException if email is null, empty, or invalid format.
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
     * Gets the telephone number of the secouriste.
     * @return The telephone number.
     */
    public String getTel() {
        return tel;
    }

    /**
     * Sets the telephone number of the secouriste.
     * @param tel The new telephone number. Can be null or empty.
     */
    public void setTel(String tel) {
        this.tel = tel; // No strict validation, can be null/empty as per typical requirements
    }

    /**
     * Gets the address of the secouriste.
     * @return The address.
     */
    public String getAddresse() {
        return addresse;
    }

    /**
     * Sets the address of the secouriste.
     * @param addresse The new address. Can be null or empty.
     */
    public void setAddresse(String addresse) {
        this.addresse = addresse; // No strict validation, can be null/empty
    }

    /**
     * Gets a copy of the set of availabilities (Journee).
     * @return A new set containing the availabilities.
     */
    public Set<Journee> getDisponibilites() {
        return new HashSet<>(disponibilites); // Return a copy
    }

    /**
     * Sets the availabilities (Journee) for the secouriste.
     * The provided set is copied.
     * @param disponibilites The set of availabilities. Must not be null. Elements cannot be null.
     * @throws IllegalArgumentException if disponibilites set is null or contains null elements.
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
        this.disponibilites = new HashSet<>(disponibilites); // Store a copy
    }

    /**
     * Adds an availability (Journee).
     * @param journee The availability to add. Must not be null.
     * @throws IllegalArgumentException if journee is null.
     */
    public void addDisponibilite(Journee journee) {
        if (journee == null) {
            throw new IllegalArgumentException("Journee to add cannot be null.");
        }
        this.disponibilites.add(journee);
    }
    
    /**
     * Removes an availability (Journee).
     * @param journee The availability to remove.
     * @return true if the availability was removed, false otherwise.
     */
    public boolean removeDisponibilite(Journee journee) {
        if (journee == null) return false;
        return this.disponibilites.remove(journee);
    }

    /**
     * Gets a copy of the set of competences possessed by the secouriste.
     * @return A new set containing the competences.
     */
    public Set<Competence> getCompetences() {
        return new HashSet<>(competences); // Return a copy
    }

    /**
     * Sets the competences for the secouriste.
     * The provided set is copied.
     * @param competences The set of competences. Must not be null. Elements cannot be null.
     * @throws IllegalArgumentException if competences set is null or contains null elements.
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
        this.competences = new HashSet<>(competences); // Store a copy
    }

    /**
     * Adds a competence to the secouriste.
     * @param competence The competence to add. Must not be null.
     * @throws IllegalArgumentException if competence is null.
     */
    public void addCompetence(Competence competence) {
        if (competence == null) {
            throw new IllegalArgumentException("Competence to add cannot be null.");
        }
        this.competences.add(competence);
    }
    
    /**
     * Removes a competence from the secouriste.
     * @param competence The competence to remove.
     * @return true if the competence was removed, false otherwise.
     */
    public boolean removeCompetence(Competence competence) {
        if (competence == null) return false;
        return this.competences.remove(competence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Secouriste that = (Secouriste) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

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