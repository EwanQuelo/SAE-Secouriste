package fr.erm.sae201.metier.persistence;

/**
 * Définit les rôles possibles pour un utilisateur dans l'application.
 * 
 * L'utilisation d'une énumération garantit la sécurité des types et évite les
 * erreurs de saisie qui pourraient survenir avec des chaînes de caractères.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public enum Role {
    /**
     * Un utilisateur avec des droits de secouriste, typiquement lié à un
     * profil Secouriste.
     */
    SECOURISTE,

    /**
     * Un utilisateur avec des droits d'administration, qui peut gérer
     * l'ensemble du système et qui n'est pas nécessairement un secouriste.
     */
    ADMINISTRATEUR
}