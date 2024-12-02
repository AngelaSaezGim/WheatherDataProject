/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connections;

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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.UpdateOptions;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author angsaegim
 */
//SINGLETON PARA CONEXION MONGODB
//AutoCloseable cierro la conexión cuando no la use
public class DataAccessManagerMongoDB implements AutoCloseable {

    private static Scanner tcl = new Scanner(System.in);

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
        
    public long countWeatherData() {
        try {
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            return collection.countDocuments();
        } catch (Exception e) {
            System.err.println("Error al contar los documentos en la colección: " + e.getMessage());
            return 0;
        }
    }

    public List<WeatherData> loadAllWeatherDataMongoDB() throws java.text.ParseException {

        List<WeatherData> weatherDataList = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME); // Nombre de la colección

        // Usando find().into() para obtener todos los documentos en una lista
        List<Document> documents = collection.find().into(new ArrayList<>());
        // Iterar sobre los documentos obtenidos
        for (Document doc : documents) {
            Integer recordId = getIntegerField(doc, "record_id");
            String city = getStringField(doc, "city");
            String country = getStringField(doc, "country");
            Double latitude = getDoubleField(doc, "latitude");
            Double longitude = getDoubleField(doc, "longitude");

            // Verificar si la fecha es nula o vacía
            Date date = getDateField(doc, "date");

            Double temperatureCelsius = getDoubleField(doc, "temperature_celsius");
            Integer humidityPercent = getIntegerField(doc, "humidity_percent");
            Double precipitationMm = getDoubleField(doc, "precipitation_mm");
            Integer windSpeedKmh = getIntegerField(doc, "wind_speed_kmh");
            String weatherCondition = getStringField(doc, "weather_condition");
            String forecast = getStringField(doc, "forecast");

            // Verificar si la fecha de actualización es nula o vacía
            Date updated = getDateField(doc, "updated");

            // Imprimir el documento para depuración
            System.out.println(doc);

            // Crear objeto WeatherData con los valores extraídos
            WeatherData data = new WeatherData(
                    recordId,
                    city,
                    country,
                    latitude,
                    longitude,
                    date, // La fecha puede ser null sin causar NullPointerException
                    temperatureCelsius,
                    humidityPercent,
                    precipitationMm,
                    windSpeedKmh,
                    weatherCondition,
                    forecast,
                    updated // La fecha de actualización también puede ser null sin causar problemas
            );
            weatherDataList.add(data);
        }

        // Imprimir el número de elementos cargados desde MongoDB
        System.out.println("Número de elementos cargados desde MongoDB: " + weatherDataList.size());

        return weatherDataList;
    }

    // Método para obtener un valor de tipo String de un documento, manejando nulos o vacíos
    private String getStringField(Document doc, String fieldName) {
        String value = doc.getString(fieldName);
        return (value == null || value.isEmpty()) ? "Desconocido" : value; // Valor predeterminado "Desconocido"
    }

    // Método para obtener un valor de tipo Integer de un documento, manejando nulos
    private Integer getIntegerField(Document doc, String fieldName) {
        Object value = doc.get(fieldName);
        if (value == null) {
            return 0; // Valor predeterminado 0
        }
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            return ((Double) value).intValue(); // Convertir Double a Integer
        } else {
            // Si el valor no es ni Integer ni Double, puedes lanzar un error o retornar un valor predeterminado
            return 0; // O manejar el error según lo que necesites
        }
    }

    // Método para obtener un valor de tipo Double de un documento, manejando nulos
    private Double getDoubleField(Document doc, String fieldName) {
        Object value = doc.get(fieldName);
        if (value == null) {
            return 0.0; // Valor predeterminado 0.0
        }
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue(); // Convertir Integer a Double
        } else {
            // Si el valor no es ni Double ni Integer, puedes lanzar un error o retornar un valor predeterminado
            return 0.0; // O manejar el error según lo que necesites
        }
    }

    public Date parseDate(Object dateObj) throws java.text.ParseException {
        if (dateObj == null) {
            return null;
        }

        // Parseo del objeto de fecha (asegúrate de usar un formato válido)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateObj.toString());
    }

    private Date getDateField(Document doc, String fieldName) {
        if (doc.containsKey(fieldName) && doc.get(fieldName) != null) {
            try {
                return parseDate(doc.get(fieldName));
            } catch (Exception e) {
                System.err.println("Error al parsear la fecha para el campo: " + fieldName);
            }
        }
        return null;  // Devuelve null si el valor es nulo o no está presente
    }

    // Método para mostrar los datos insertados
    public void printWeatherData() {
        MongoCollection<Document> col = database.getCollection(COLLECTION_NAME);

        for (Document doc : col.find()) {
            doc.remove("_id");  // Eliminar el campo "_id" del documento antes de imprimir
            System.out.println(doc.toJson());
        }
    }

    public void solicitarWeatherDataMongoDB() throws SQLException {

        System.out.print("- Quieres ver los datos neteorológicos actuales de la base de datos?\n 1 - sí ");
        int respuesta = tcl.nextInt();
        tcl.nextLine();
        if (respuesta == 1) {
            printWeatherData();
        }
    }

    /// --------------------------------- INSERT ------------------------- //
    public void insertWeatherDataMongoDB() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        solicitarWeatherDataMongoDB();

        boolean continuar = true;
        while (continuar) {

            System.out.print("Introduce el record id: ");
            String recordId = scanner.nextLine().trim();

            // Solicitar el nombre de la ciudad
            System.out.print("Introduce el nombre de la ciudad (0 para salir): ");
            String city = scanner.nextLine().trim();

            // Salir del bucle si la ciudad es "0"
            if (city.equals("0")) {
                break;
            }

            // Solicitar los datos
            //trim me permite espacios en blanco
            //para los doubles he hecho una función a parte para validarlos
            System.out.print("Introduce el país: ");
            String country = scanner.nextLine().trim();

            System.out.print("Introduce la latitud: ");
            double latitude = getDoubleInput(scanner);

            System.out.print("Introduce la longitud: ");
            double longitude = getDoubleInput(scanner);

            System.out.print("Introduce la fecha (yyyy-MM-dd): ");
            String date = scanner.nextLine().trim();

            System.out.print("Introduce la temperatura en grados Celsius: ");
            double temperatureCelsius = getDoubleInput(scanner);

            System.out.print("Introduce la humedad en porcentaje: ");
            double humidityPercent = getDoubleInput(scanner);

            System.out.print("Introduce la precipitación en mm: ");
            double precipitationMm = getDoubleInput(scanner);

            System.out.print("Introduce la velocidad del viento en km/h: ");
            double windSpeedKmh = getDoubleInput(scanner);

            System.out.print("Introduce la condición del tiempo: ");
            String weatherCondition = scanner.nextLine().trim();

            System.out.print("Introduce la previsión del tiempo: ");
            String forecast = scanner.nextLine().trim();

            String updated = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            System.out.println("Predicción actualizada con fecha actual " + updated);

            // Crear el objeto WeatherData
            Document newWeatherData = new Document("recordId", recordId)
                    .append("city", city)
                    .append("country", country.isEmpty() ? null : country)
                    .append("latitude", latitude)
                    .append("longitude", longitude)
                    .append("date", date.isEmpty() ? null : date)
                    .append("temperature_celsius", temperatureCelsius)
                    .append("humidity_percent", humidityPercent)
                    .append("precipitation_mm", precipitationMm)
                    .append("wind_speed_kmh", windSpeedKmh)
                    .append("weather_condition", weatherCondition.isEmpty() ? null : weatherCondition)
                    .append("forecast", forecast.isEmpty() ? null : forecast)
                    //UPDATED SERÁ LA FECHA ACTUAL
                    .append("updated", updated);

            // Insertar el documento en la base de datos
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            collection.insertOne(newWeatherData);

            System.out.println("Datos insertados correctamente.");
            System.out.println(newWeatherData);

            // Preguntar si desea continuar agregando datos
            System.out.print("¿Desea agregar más datos? (sí/no): ");
            scanner.nextLine(); // Consume el salto de línea residual
            String continueInput = tcl.nextLine();
            continuar = continueInput.equalsIgnoreCase("sí");
        }
    }

    // PARA LOS DOUBLES - Permitir que esten en BLANCO
    private double getDoubleInput(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return 0.0; // Si está en blanco, asigna un valor por defecto
                }
                double value = Double.parseDouble(input);
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Por favor, introduce un número válido: ");
            }
        }
    }

    /// --------------------------------- FIN INSERT ------------------------- //
    //-------------------- LISTAR ---------------------------//
    // Mostrar datos paginados o en pantalla
    private static void mostrarDatosCompletosPaginados(List<Document> weatherData) {
        final int ITEMS_POR_PAGINA = 3; // Cambia esto según tus necesidades
        int totalPaginas = (int) Math.ceil((double) weatherData.size() / ITEMS_POR_PAGINA);

        Scanner scanner = new Scanner(System.in);
        int paginaActual = 0;

        while (paginaActual < totalPaginas) {
            int inicio = paginaActual * ITEMS_POR_PAGINA;
            int fin = Math.min(inicio + ITEMS_POR_PAGINA, weatherData.size());

            // Mostrar los datos de la página actual
            for (int i = inicio; i < fin; i++) {
                Document doc = weatherData.get(i);
                imprimirDatosDocumento(doc);
            }

            System.out.println("\nPágina " + (paginaActual + 1) + " de " + totalPaginas);
            System.out.println("Presiona 'Enter' para ver más o escribe 'salir' para salir.");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) {
                break;
            } else {
                paginaActual++;
            }
        }
    }

    private static void imprimirDatosDocumento(Document doc) {

        System.out.println("Ciudad: " + doc.getString("city"));
        System.out.println("País: " + doc.getString("country"));
        System.out.println("Latitud: " + obtenerDoubleSeguro(doc, "latitude"));
        System.out.println("Longitud: " + obtenerDoubleSeguro(doc, "longitude"));
        System.out.println("Fecha: " + doc.getString("date"));
        System.out.println("Temperatura: " + obtenerDoubleSeguro(doc, "temperature_celsius") + " °C");
        System.out.println("Humedad: " + obtenerDoubleSeguro(doc, "humidity_percent") + " %");
        System.out.println("Precipitación: " + obtenerDoubleSeguro(doc, "precipitation_mm") + " mm");
        System.out.println("Viento: " + obtenerDoubleSeguro(doc, "wind_speed_kmh") + " km/h");
        System.out.println("Condición del tiempo: " + doc.getString("weather_condition"));
        System.out.println("Previsión: " + doc.getString("forecast"));
        System.out.println("Fecha de actualización: " + doc.getString("updated"));
        System.out.println("-------------------------------------");
    }

    //Arreglar problemas conversión double*****
    private static double obtenerDoubleSeguro(Document doc, String key) {
        Object value = doc.get(key);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else {
            return 0.0; // O algún valor por defecto que prefieras
        }
    }

    public void listarWeatherDataMongoDB() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("¿Cómo deseas listar los datos meteorológicos?");
        System.out.println("1 - Por ciudad");
        System.out.println("2 - Por varias ciudades (separadas por coma)");
        System.out.println("3 - ALL - Mostrar todos los datos (ordenados alfabéticamente por ciudad)");
        System.out.println("4 - Atrás");
        System.out.print("Elige una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();  // Consumir el salto de línea

        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        switch (opcion) {
            case 1:
                listarPorCiudad(collection);
                break;
            case 2:
                listarPorVariasCiudades(collection);
                break;
            case 3:
                listarTodosAlfabeticamente(collection);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo.");
                break;
        }
    }

    // Listar por ciudad
    private static void listarPorCiudad(MongoCollection<Document> collection) {
        System.out.println("Filtramos por UNA ciudad");
        System.out.print("Ingrese el nombre de la ciudad: ");
        String ciudad = tcl.nextLine().trim();

        Document query = new Document("city", ciudad);
        List<Document> results = collection.find(query).into(new ArrayList<>());

        if (!results.isEmpty()) {
            mostrarDatosCompletosPaginados(results);
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad: " + ciudad);
        }
    }

    // Listar por varias ciudades
    private static void listarPorVariasCiudades(MongoCollection<Document> collection) {
        System.out.println("Filtramos por VARIAS ciudades");
        System.out.print("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid): ");
        String ciudadesInput = tcl.nextLine().trim();
        String[] ciudades = ciudadesInput.split(",");

        boolean found = false;
        for (String ciudad : ciudades) {
            ciudad = ciudad.trim();
            Document query = new Document("city", ciudad);
            List<Document> results = collection.find(query).into(new ArrayList<>());

            if (!results.isEmpty()) {
                mostrarDatosCompletosPaginados(results);
                found = true;
            } else {
                System.out.println("No se encontraron datos para la ciudad: " + ciudad);
            }
        }

        if (!found) {
            System.out.println("No se encontraron datos meteorológicos para ninguna de las ciudades.");
        }
    }

    // Listar todos los ítems ordenados por ciudad
    private static void listarTodosAlfabeticamente(MongoCollection<Document> collection) {
        System.out.println("Filtramos todas las ciudades Alfabéticamente");

        List<Document> results = collection.find().into(new ArrayList<>());
        if (!results.isEmpty()) {
            results.sort((doc1, doc2) -> doc1.getString("city").compareToIgnoreCase(doc2.getString("city")));
            mostrarDatosCompletosPaginados(results);
        } else {
            System.out.println("No hay datos meteorológicos disponibles.");
        }
    }

    //-------------------------- FIN LISTAR ---------------------------------//
    //-------------------------- METODO DELETE  ---------------------------------//
    public void deleteWeatherData() throws SQLException {

        System.out.println("Seleccione una opción para borrar datos:");
        System.out.println("1 - Borrar TODOS los datos (sin confirmación) *PELIGRO*");
        System.out.println("2 - Borrar usando FILTROS");
        System.out.println("3 - Atrás");
        System.out.print("Elige una opción: ");
        int opcion = tcl.nextInt();
        tcl.nextLine();

        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        switch (opcion) {
            case 1:
                // Borrar todos sin confirmación
                long totalBorrados = collection.deleteMany(new Document()).getDeletedCount();
                System.out.println("Todos los datos han sido borrados.");
                System.out.println("Se borraron " + totalBorrados + " registros.");
                break;
            case 2:
                System.out.println("Borramos usando filtros; ");
                System.out.println("1 - Borrar por ciudad");
                System.out.println("2 - Borrar por varias ciudades (separadas por coma)");
                System.out.println("3 - ALL - CON CONFIRMACIÓN");
                System.out.println("4 - Atrás");
                System.out.print("Elige una opción: ");
                int opcionFiltros = tcl.nextInt();
                tcl.nextLine();

                switch (opcionFiltros) {
                    case 1:
                        System.out.println("Borraremos uno/varios registros de UNA Ciudad ");
                        solicitarWeatherDataMongoDB();
                        filtreDeleteByCiudad(collection);
                        break;
                    case 2:
                        System.out.println("Borraremos registros de VARIAS Ciudades");
                        solicitarWeatherDataMongoDB();
                        filtreDeleteVariasCiudades(collection);
                        break;
                    case 3:
                        filtreConfirmarYBorrarALL(collection);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
                break;
            case 3:
                return;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo.");
                break;
        }
    }

    private static void filtreDeleteByCiudad(MongoCollection<Document> collection) throws SQLException {

        System.out.println("Dime la ciudad de la que borraremos sus registros:");
        String ciudad = tcl.nextLine().trim();

        long totalBorrados = collection.deleteMany(new Document("city", ciudad)).getDeletedCount();
        if (totalBorrados > 0) {
            System.out.println("Todos los datos de la ciudad " + ciudad + " han sido borrados.");
            System.out.println("Se borraron " + totalBorrados + " registros.");
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad " + ciudad);
        }
    }

    private static void filtreDeleteVariasCiudades(MongoCollection<Document> collection) throws SQLException {

        System.out.println("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid):");
        String ciudadesInput = tcl.nextLine().trim();
        List<String> ciudades = Arrays.asList(ciudadesInput.split("\\s*,\\s*"));

        long totalBorrados = collection.deleteMany(new Document("city", new Document("$in", ciudades))).getDeletedCount();
        if (totalBorrados > 0) {
            System.out.println("Todos los datos de las ciudades " + String.join(", ", ciudades) + " han sido borrados.");
            System.out.println("Se borraron " + totalBorrados + " registros.");
        } else {
            System.out.println("No se encontraron datos meteorológicos para las ciudades proporcionadas.");
        }
    }

    private static void filtreConfirmarYBorrarALL(MongoCollection<Document> collection) throws SQLException {

        System.out.println("¿Está seguro de que desea borrar TODOS los datos? (sí/no):");
        String confirmacion = tcl.nextLine().trim().toLowerCase();

        if (confirmacion.equals("sí") || confirmacion.equals("si")) {
            long totalBorrados = collection.deleteMany(new Document()).getDeletedCount();
            System.out.println("Todos los datos han sido borrados.");
            System.out.println("Se borraron " + totalBorrados + " registros.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    // ------------------------ FIN METODO DELETE --------------------------------//
    /*--------------------------SYNCRONIZED-------------------------------------*/
    //
    //Elimina todos los datos existentes en la tabla MongoDB.
    //Inserta los nuevos datos proporcionados (POR LA OTRA BD - SQL) como una lista de objetos WeatherData.
    public synchronized void replaceWeatherDataMongoDB(List<WeatherData> weatherDataList) {

        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // ELIMINA TODOS LOS DOCUMENTOS (todo lo de dentro)
        collection.deleteMany(new Document());

        // CONVIERTE Y AGREGA LOS NUEVOS DATOS DE SQL
        //ITERANDO EN WEATHER DATA LIST
        List<Document> documents = new ArrayList<>();
        for (WeatherData data : weatherDataList) {
            Document doc = new Document("recordId", data.getRecordId())
                    .append("city", data.getCity())
                    .append("country", data.getCountry())
                    .append("latitude", data.getLatitude())
                    .append("longitude", data.getLongitude())
                    .append("date", data.getDate())
                    .append("temperature_celsius", data.getTemperatureCelsius())
                    .append("humidity_percent", data.getHumidityPercent())
                    .append("precipitation_mm", data.getPrecipitationMm())
                    .append("wind_speed_kmh", data.getWindSpeedKmh())
                    .append("weather_condition", data.getWeatherCondition())
                    .append("forecast", data.getForecast())
                    .append("updated", data.getUpdated());
            documents.add(doc);
        }

        // INSERTA LOS DOCUEMNTOS CON LAS NUEVAS ACTUALIZACIONES DE SQL EN LA COLECCIÓN
        collection.insertMany(documents);
    }
    /*--------------------------SYNCRONIZED-------------------------------------*/

 /*-------------------------- UPSERT -------------------------------------*/
    
     public void upsertWeatherRecord(WeatherData data) {
         
       // Obténemos colección weatherdata
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // Crear el filtro para buscar el documento por recordId (campo único)
        Document filter = new Document("record_id", data.getRecordId());

        // Crear el documento con los datos que queremos insertar o actualizar
        // preparando los nuevos valores para los campos. 
        //Si el record_id ya existe, MongoDB actualizará esos campos. 
        //Si no existe, insertará un nuevo documento con estos valores.
        Document updateDocument = new Document()
                .append("city", data.getCity())
                .append("country", data.getCountry())
                .append("latitude", data.getLatitude())
                .append("longitude", data.getLongitude())
                .append("date", data.getDate())
                .append("temperature_celsius", data.getTemperatureCelsius())
                .append("humidity_percent", data.getHumidityPercent())
                .append("precipitation_mm", data.getPrecipitationMm())
                .append("wind_speed_kmh", data.getWindSpeedKmh())
                .append("weather_condition", data.getWeatherCondition())
                .append("forecast", data.getForecast())
                .append("updated", data.getUpdated());

        // Realizar el upsert (insertar si no existe o actualizar si existe)
        UpdateOptions options = new UpdateOptions().upsert(true);
        //updateOne con la opción upsert = true. Si el documento con ese record_id no existe, se inserte uno nuevo
        collection.updateOne(filter, new Document("$set", updateDocument), options);

        System.out.println("Upsert completado.");
    }
 /*-------------------------- UPSERT -------------------------------------*/
 /*-------------------------- SUBIR XML -------------------------------------*/
 /*-------------------------- SUBIR XML -------------------------------------*/
}
