package fr.erm.sae201.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstract Data Access Object (DAO) class.
 * Provides a template for CRUD operations and database connection management.
 * @param <T> The type of the entity this DAO manages.
 * @author Abdelbadie
 * @version 1.1 
 */
public abstract class DAO<T> {
    
    // Adjusted to match your SQL DDL for 'secours2030' but using example user/pass
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/secours2030?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root"; // From your example
    private static final String PASSWORD = "snowad1234";   // From your example

    /**
     * Gets a connection to the database.
     * @return A database connection.
     * @throws SQLException if a database access error occurs.
     */
    protected Connection getConnection() throws SQLException {
        // Charger la classe du pilote
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace ();
            return null;
        }
        // Obtenir la connection
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    /**
     * Retrieves all elements of type T from the database.
     * @return A list of all elements.
     */
    public abstract List<T> findAll();

    /**
     * Finds an element by its primary key, assuming the key is a Long.
     * DAOs for entities with non-Long or composite primary keys (e.g., String code, LocalDate)
     * should throw an UnsupportedOperationException and provide their own specific finders
     * (e.g., findByCode(String code)).
     * @param id The Long ID of the element.
     * @return The element if found, otherwise null.
     */
    public abstract T findByID(Long id);

    /**
     * Creates a new element in the database.
     * @param element The element to create.
     * @return The number of rows affected (typically 1 on success), or -1 on error.
     */
    public abstract int create(T element);

    /**
     * Updates an existing element in the database.
     * @param element The element with updated information.
     * @return The number of rows affected, or -1 on error.
     */
    public abstract int update(T element);

    /**
     * Deletes an element from the database.
     * @param element The element to delete.
     * @return The number of rows affected, or -1 on error.
     */
    public abstract int delete(T element);


    // Utility to close resources quietly (optional, but good practice if not using try-with-resources fully)
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