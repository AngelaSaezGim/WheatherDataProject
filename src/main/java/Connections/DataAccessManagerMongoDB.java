/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connections;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author angsaegim
 */
//SINGLETON PARA CONEXION MONGODB
//AutoCloseable cierro la conexión cuando no la use
public class DataAccessManagerMongoDB implements AutoCloseable {

    /**
     * ************************ PARTE ESTÁTICA ****************************
     */
    // Constantes para configuración de MongoDB
    private static final String DB_CONFIG_FILE_NAME = "src/resources/db.properties";
    private static final String DB_CONFIG_URI_PROPERTY = "mongodb.uri";
    private static final String DB_CONFIG_DATABASE_PROPERTY = "mongodb.database";
    private static final String DEFAULT_MONGO_URI = "mongodb://localhost:27017";
    private static final String DEFAULT_DATABASE_NAME = "WeatherDataAS01";

    private static String mongoURI = DEFAULT_MONGO_URI;

    private static DataAccessManagerMongoDB singleton;
    private MongoClient mongoClient;
    private MongoDatabase database;

    // Instanciamos un único objeto DataAccessManager - SINGLETON
    private DataAccessManagerMongoDB() {
        try {
            // Intentamos conectar al cliente MongoDB
            this.mongoClient = new MongoClient(new MongoClientURI(mongoURI));
            this.database = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar a MongoDB: " + e.getMessage(), e);
        }

    }

    /**
     * Obtiene la instancia de la clase DataAccessManagerMongoDB.
     *
     * @return La instancia única de DataAccessManagerMongoDB.
     */
    public static DataAccessManagerMongoDB getInstance() {
         if (singleton == null) {
            loadMongoDBParams();
            singleton = new DataAccessManagerMongoDB();
        }
        System.out.println("Conexión a MongoDB realizada (instancia)");
        return singleton;
    }

    /**
     * Carga las credenciales y URL de acceso a MongoDB desde un archivo de
     * configuración.
     */
    private static void loadMongoDBParams() {
        Properties properties = new Properties();
        try ( FileReader reader = new FileReader(DB_CONFIG_FILE_NAME)) {
            properties.load(reader);
            if (properties.getProperty(DB_CONFIG_URI_PROPERTY) != null) {
                mongoURI = properties.getProperty(DB_CONFIG_URI_PROPERTY);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar la configuración de MongoDB. Usando valores por defecto: " + e.getMessage());
        }
    }

    /**
     * Devuelve la base de datos a la que se puede acceder.
     *
     * @return La base de datos de MongoDB.
     */
    public MongoDatabase getDatabase() {
        try {
            if (this.database == null) {
                throw new RuntimeException("No se ha establecido la conexión con MongoDB.");
            }
            return this.database;
        } catch (Exception e) {
            System.err.println("Error al obtener la base de datos: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void close() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                mongoClient = null;
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar la conexión con MongoDB: " + e.getMessage());
        } finally {
            singleton = null;
        }
    }
}
