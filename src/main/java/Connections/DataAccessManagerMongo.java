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

import static Connections.Constants.*;

/**
 *
 * @author angsaegim
 */

//SINGLETON PARA CONEXION MONGODB

//AutoCloseable cierro la conexión cuando no la use
public class DataAccessManagerMongo implements AutoCloseable {

    /**
     * ************************ PARTE ESTÁTICA ****************************
     */
    private static String mongoURI = DEFAULT_MONGO_URI;
    private static DataAccessManagerMongo singleton;
    private MongoClient mongoClient;
    private MongoDatabase database;

    // Instanciamos un único objeto DataAccessManager - SINGLETON
    private DataAccessManagerMongoDB() {
        this.database = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
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
            try {
                singleton.mongoClient = new MongoClient(new MongoClientURI(mongoURI));
            } catch (Exception e) {
                singleton = null;
                throw new RuntimeException("Error al conectar a MongoDB: " + e.getMessage(), e);
            }
        }
        return singleton;
    }

    /**
     * Carga las credenciales y URL de acceso a MongoDB desde un archivo de
     * configuración.
     */
    private static void loadMongoDBParams() {
        Properties properties = new Properties();
        try ( FileReader reader = new FileReader(DB_CONFIG__FILE_NAME)) {
            properties.load(reader);
            if (properties.getProperty(DB_CONFIG__URI_PROPERTY) != null) {
                mongoURI = properties.getProperty(DB_CONFIG__URI_PROPERTY);
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
        return this.database;
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
