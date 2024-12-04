/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connections;

import DAOs.WeatherDataMongoDBDAO;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.bson.Document;

import Objects.WeatherData;
import com.google.protobuf.TextFormat.ParseException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.bson.Document;

/**
 *
 * @author angsaegim
 */
//SINGLETON PARA CONEXION MONGODB
//AutoCloseable cierro la conexión cuando no la use
public class DataAccessManagerMongoDB implements AutoCloseable {

    private static Scanner tcl = new Scanner(System.in);
    
    private WeatherDataMongoDBDAO weatherDataDAO;

    /**
     * ************************ PARTE ESTÁTICA ****************************
     */
    // Constantes para configuración de MongoDB
    private static final String DB_CONFIG_FILE_NAME = "src/resources/db.properties";
    private static final String DB_CONFIG_URI_PROPERTY = "mongodb.uri";
    private static final String DB_CONFIG_DATABASE_PROPERTY = "mongodb.database";
    private static final String DEFAULT_MONGO_URI = "mongodb://localhost:27017";
    private static final String DEFAULT_DATABASE_NAME = "WeatherData";

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
            // Inicializamos el DAO pasando la base de datos
            this.weatherDataDAO = new WeatherDataMongoDBDAO(this.database);

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
    // ----------------------- METODOS DINAMICOS ----------------------- //

    private static final String COLLECTION_NAME = "WeatherDataAS01"; // Nombre de la colección

    /*----------------------------------------------------------*/
    public long countWeatherDataMongo() {
        return this.weatherDataDAO.countWeatherDataMongo();
    }
    
    public boolean existsWeatherDataMongo(String recordId) {
        return this.weatherDataDAO.weatherDataExistMongo(recordId);
    }
    
    public List<WeatherData> loadAllWeatherDataMongo() {
        return this.weatherDataDAO.loadAllWeatherDataMongo();
    }
      /*----------------------------------------------------------*/
    /*-------------------------- FILTER *------------------------*/

        public List<WeatherData> loadWeatherDataByCityMongo(String city) {
        if (city == null || city.length() == 0) {
            throw new IllegalArgumentException("Debe indicar el filtro de búsqueda");
        }
        return this.weatherDataDAO.loadWeatherDataByCityMongo(city);
    }
                                                        //list ciudades
    public List<WeatherData> loadWeatherDataByCitiesMongo(List<String> ciudades) throws SQLException {
        if (ciudades == null || ciudades.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el filtro de búsqueda");
        }
        return this.weatherDataDAO.loadWeatherDataByCitiesMongo(ciudades);
    }
    /*-------------------------- FILTER *------------------------*/
    
     /*-------------- INSERT - (WeatherData) *------------------------*/
    
    public int insertarWeatherDataMongo(WeatherData newweatherdata) throws SQLException {
        return this.weatherDataDAO.insertWeatherDataMongo(newweatherdata);
    }
    
    public int insertarWeatherDataMongo(List<WeatherData> weatherDataList) throws SQLException {
        return this.weatherDataDAO.insertWeatherDataMongoDB(weatherDataList);
    }

/*---------------------------------------------------------------*/

 /*---------------------------------------------------------------*/
 /*-------------- DELETE - (WeatherData) *------------------------*/
    public int deleteAllWeatherDataMongo() throws SQLException {
        return this.weatherDataDAO.deleteAllWeatherDataMongo();
    }

    public int deleteWeatherDataByListMongo(List<WeatherData> weatherDataList) {
        if (weatherDataList == null || weatherDataList.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el filtro de búsqueda");
        }
        return this.weatherDataDAO.deleteWeatherDataByListMongo(weatherDataList);
    }

    /*---------------------------------------------------------------*/

 /*-------------------------- UPSERT -------------------------------------*/
    public UpdateResult upsertWeatherDataMongo(WeatherData weatherData) {
        return this.weatherDataDAO.upsertWeatherDataMongo(weatherData);
}
  /*-------------------------- UPSERT -------------------------------------*/
 /*-------------------------- SUBIR XML -------------------------------------*/
    
    
    
    public void uploadXMLMongoDB() {
    try {
        // Crear el objeto para la base de datos de MongoDB
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        String filePath = "C:\\Users\\angel\\OneDrive\\Escritorio\\ProyectoADA\\src\\resources\\data.xml";
        // Solicitar el archivo XML (asumiendo que el archivo se pasa como argumento)
        System.out.print("Introduce la ruta del archivo XML: ");

        // Abrir el archivo XML
        File xmlFile = new File(filePath);
        if (!xmlFile.exists()) {
            System.out.println("El archivo no existe.");
            return;
        }

        // Crear un objeto para leer el XML
        //PROCESAMIENTO XML
        // Usa el API javax.xml.parsers para leer y procesar el archivo XML.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        //PROCESAMIENTO XML

        // Obtener todos los elementos que deseas importar, por ejemplo, "item"
        NodeList nodeList = document.getElementsByTagName("item");

        // Iterar sobre cada nodo 'item' del archivo XML
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // CREA DOCUMENTO MONGO A PARTIR DE CADA "ITEM"
                Document mongoDoc = new Document();

                mongoDoc.append("record_id", element.getElementsByTagName("recordId").item(0).getTextContent());
                mongoDoc.append("city", element.getElementsByTagName("city").item(0).getTextContent());
                mongoDoc.append("country", element.getElementsByTagName("country").item(0).getTextContent());
                mongoDoc.append("latitude", Double.parseDouble(element.getElementsByTagName("latitude").item(0).getTextContent()));
                mongoDoc.append("longitude", Double.parseDouble(element.getElementsByTagName("longitude").item(0).getTextContent()));
                mongoDoc.append("date", element.getElementsByTagName("date").item(0).getTextContent());
                mongoDoc.append("temperature_celsius", Double.parseDouble(element.getElementsByTagName("temperature_celsius").item(0).getTextContent()));
                mongoDoc.append("humidity_percent", Double.parseDouble(element.getElementsByTagName("humidity_percent").item(0).getTextContent()));
                mongoDoc.append("precipitation_mm", Double.parseDouble(element.getElementsByTagName("precipitation_mm").item(0).getTextContent()));
                mongoDoc.append("wind_speed_kmh", Double.parseDouble(element.getElementsByTagName("wind_speed_kmh").item(0).getTextContent()));
                mongoDoc.append("weather_condition", element.getElementsByTagName("weather_condition").item(0).getTextContent());
                mongoDoc.append("forecast", element.getElementsByTagName("forecast").item(0).getTextContent());

                // INSERTA DOCUMENTO EN LA COLECCIÓN
                collection.insertOne(mongoDoc);
                System.out.println("Elemento insertado: " + mongoDoc.toJson());
            }
        }
        System.out.println("Carga XML completada exitosamente.");
    } catch (Exception e) {
        System.out.println("Error al procesar el archivo XML: " + e.getMessage());
    }
}
 /*-------------------------- SUBIR XML -------------------------------------*/
}
