package fr.erm.sae201.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstract Data Access Object (DAO) class.
 * This class provides a foundational template for common database Create, Read,
 * Update, and Delete (CRUD) operations. It also manages the establishment
 * of database connections. Concrete DAO implementations for specific entities
 * should extend this class and implement its abstract methods.
 *
 * @param <T> The type of the entity (Plain Old Java Object - POJO) that a
 *            concrete
 *            DAO will manage.
 * @author Abdelbadie, Ewan QUELO, Raphael MILLE, Matheo BIET
 * @version 1.1
 */
public abstract class DAO<T> {

    // Database connection parameters
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/secours2030?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "snowad1234";

    /**
     * Establishes and returns a connection to the database.
     * This method first attempts to load the JDBC driver specified by
     * 'DRIVER_CLASS_NAME'.
     * Then, it uses 'DriverManager' to obtain a connection using the predefined
     * 'URL', 'USERNAME', and 'PASSWORD'.
     *
     * @return A {@link Connection} object to the database.
     * @throws SQLException if a database access error occurs during connection
     *                      establishment (e.g., incorrect URL, credentials, or
     *                      server not available).
     *                      The method may also print a stack trace to standard
     *                      error and return 'null'
     *                      if the JDBC driver class cannot be found.
     */
    protected Connection getConnection() throws SQLException {
        // Load the driver class
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
        // Obtain the connection
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Retrieves all elements of type T from the database.
     * 
     * @return A list of all elements.
     */
    public abstract List<T> findAll();

    /**
     * Finds an element by its primary key, assuming the key is a Long.
     * DAOs for entities with non-Long or composite primary keys (e.g., String code,
     * LocalDate)
     * should throw an UnsupportedOperationException and provide their own specific
     * finders
     * (e.g., findByCode(String code)).
     * 
     * @param id The Long ID of the element.
     * @return The element if found, otherwise null.
     */
    public abstract T findByID(Long id);

    /**
     * Creates a new element in the database.
     * 
     * @param element The element to create.
     * @return The number of rows affected (typically 1 on success), or -1 on error.
     */
    public abstract int create(T element);

    /**
     * Updates an existing element in the database.
     * 
     * @param element The element with updated information.
     * @return The number of rows affected, or -1 on error.
     */
    public abstract int update(T element);

    /**
     * Deletes an element from the database.
     * 
     * @param element The element to delete.
     * @return The number of rows affected, or -1 on error.
     */
    public abstract int delete(T element);

    /**
     * Utility method to close an {@link AutoCloseable} resource (like {@link Connection},
     * {@link java.sql.Statement}, {@link java.sql.ResultSet}) without throwing an exception
     * if the close operation itself fails.
     * Any exception during closing is caught and its message is printed to standard error.
     * 
     * @param resource The {@link AutoCloseable} resource to close. Can be 'null'.
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