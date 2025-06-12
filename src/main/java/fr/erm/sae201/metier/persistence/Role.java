package fr.erm.sae201.metier.persistence;

/**
 * Définit les rôles possibles pour un utilisateur dans l'application.
 * L'utilisation d'un enum garantit la sécurité des types et évite les erreurs.
 */
public enum Role {
    /**
     * Un utilisateur avec des droits de secouriste, lié à un profil Secouriste.
     */
    SECOURISTE,

    /**
     * Un utilisateur avec des droits d'administration, qui n'est pas nécessairement un secouriste.
     */
    ADMINISTRATEUR
}