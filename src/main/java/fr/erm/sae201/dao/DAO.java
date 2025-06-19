package fr.erm.sae201.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe abstraite DAO (Data Access Object).
 * <p>
 * Fournit un modèle de base pour les opérations CRUD (Create, Read, Update, Delete)
 * communes à la base de données. Elle gère également l'établissement des connexions.
 * Les DAO concrets pour des entités spécifiques doivent hériter de cette classe.
 * </p>
 *
 * @param <T> Le type de l'entité (POJO) que le DAO concret gérera.
 * @author Abdelbadie
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.1
 */
public abstract class DAO<T> {

    // Paramètres de connexion à la base de données
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/secours2030?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "snowad1234";

    /**
     * Établit et retourne une connexion à la base de données.
     * <p>
     * Cette méthode tente d'abord de charger le pilote JDBC. Ensuite, elle utilise
     * le DriverManager pour obtenir une connexion en utilisant l'URL, le nom
     * d'utilisateur et le mot de passe prédéfinis.
     * </p>
     *
     * @return Un objet Connection vers la base de données.
     * @throws SQLException si une erreur d'accès à la base de données se produit
     *                      (ex: URL incorrecte, identifiants faux, serveur non disponible).
     */
    protected Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Récupère tous les éléments de type T de la base de données.
     *
     * @return Une liste de tous les éléments.
     */
    public abstract List<T> findAll();

    /**
     * Recherche un élément par sa clé primaire, en supposant que la clé est un Long.
     * <p>
     * Les DAOs pour des entités avec des clés non-Long ou composites (ex: String)
     * doivent lever une UnsupportedOperationException et fournir leurs propres
     * méthodes de recherche spécifiques (ex: findByCode(String)).
     * </p>
     *
     * @param id L'ID de type Long de l'élément.
     * @return L'élément si trouvé, sinon null.
     */
    public abstract T findByID(Long id);

    /**
     * Crée un nouvel élément dans la base de données.
     *
     * @param element L'élément à créer.
     * @return Le nombre de lignes affectées (typiquement 1), ou -1 en cas d'erreur.
     */
    public abstract int create(T element);

    /**
     * Met à jour un élément existant dans la base de données.
     *
     * @param element L'élément avec les informations mises à jour.
     * @return Le nombre de lignes affectées, ou -1 en cas d'erreur.
     */
    public abstract int update(T element);

    /**
     * Supprime un élément de la base de données.
     *
     * @param element L'élément à supprimer.
     * @return Le nombre de lignes affectées, ou -1 en cas d'erreur.
     */
    public abstract int delete(T element);

    /**
     * Méthode utilitaire pour fermer une ressource (Connection, Statement, ResultSet)
     * sans lever d'exception si la fermeture elle-même échoue.
     *
     * @param resource La ressource AutoCloseable à fermer. Peut être null.
     */
    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }
}