/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connections;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author angsaegim
 */
public class DataAccessManagerSQL implements AutoCloseable {

    /**
     * ************************ PARTE ESTÁTICA ****************************
     */
    //Por default usamos wheater data pero podemos cambiar a userInfo
    private static final String DEFAULT_DB_NAME = "WeatherData";
    private static final String MYSQL_DB_DRIVER__CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String DB_CONFIG_FILE_NAME = "src/resources/db.properties";
    
    //COMO TENGO 2 BASES DE DATOS USO UN HASHMAP PARA PODER CONECTARME A AMBAS
    //ASI PODRE USARLAS CUANDO LAS NECESITE Y NO TENDRÉ QUE DUPLICAR CODIGO
    private static Map<String, String> databaseUsers = new HashMap<>();
    private static Map<String, String> databasePasswords = new HashMap<>();
    private static Map<String, String> databaseURLs = new HashMap<>();
    
    // Almacena las conexiones activas a las bases de datos
    private final Map<String, Connection> connections = new HashMap<>();

    private static DataAccessManagerSQL singleton;

    // Instanciamos una UNICA CONEXIÓN (SINGLETON)
    private DataAccessManagerSQL() {
        loadDataBaseParams(); //para saber si es userInfo o wheaterData
    }
    
     // Obtiene la instancia única de DataAccessManagerSQL
    public static synchronized DataAccessManagerSQL getInstance() {
        if (singleton == null) {
            singleton = new DataAccessManagerSQL();
        }
        return singleton;
    }

    /**
     * Carga las credenciales y URL de acceso a datos del fichero de
     * configuración (db.properties) EN ESTE CASO ITERAMOS EN EL HASMAP YA QUE
     * TENEMOS 2 CONEXIONES QUE CARGAR
     */
    private void loadDataBaseParams() {
        Properties databaseConfig = new Properties();
        try (FileReader dbReaderStream = new FileReader(DB_CONFIG_FILE_NAME)) {
            databaseConfig.load(dbReaderStream);

            // Cargar configuraciones para WeatherData
            databaseUsers.put("WeatherData", databaseConfig.getProperty("weather.user", "root"));
            databasePasswords.put("WeatherData", databaseConfig.getProperty("weather.pass", "serpis"));
            databaseURLs.put("WeatherData", databaseConfig.getProperty("weather.url"));

            // Cargar configuraciones para UserInfo
            databaseUsers.put("UserInfo", databaseConfig.getProperty("userinfo.user", "root"));
            databasePasswords.put("UserInfo", databaseConfig.getProperty("userinfo.pass", "serpis"));
            databaseURLs.put("UserInfo", databaseConfig.getProperty("userinfo.url"));

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar configuración de bases de datos: " + e.getMessage(), e);
        }
    }
    
    //Creamos la conexión según la base de datos a la que nos estemos conectando
    private static Connection createConnection(String dbName) {
        String url = databaseURLs.get(dbName);
        String user = databaseUsers.get(dbName);
        String password = databasePasswords.get(dbName);
        
        if (url == null || user == null || password == null) {
            throw new RuntimeException("Faltan configuraciones para la base de datos: '" + dbName + ", ve a db.resoruces'.");
        }
        try {
            Class.forName(MYSQL_DB_DRIVER__CLASS_NAME);
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(true);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos '" + dbName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Devuelve una conexión a la base de datos especificada.
     * Si no existe una conexión, se crea automáticamente.
     */
    public Connection getConnection(String dbName) {
        return connections.computeIfAbsent(dbName, DataAccessManagerSQL::createConnection);
    }

   /**
     * Cierra todas las conexiones activas.
     */
    @Override
    public void close() {
        for (Connection connection : connections.values()) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        connections.clear();
        singleton = null;
    }

    /* FUNCIONES CON DATOS */
}
