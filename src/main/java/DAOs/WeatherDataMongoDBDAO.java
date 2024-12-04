/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import com.mongodb.client.*;
import org.bson.Document;
import Objects.WeatherData;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.DeleteResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author angel
 */
public class WeatherDataMongoDBDAO {

    private final MongoCollection<Document> collection;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    //En lugar de recibir un Connection como en SQL, 
    //el DAO para MongoDB recibe un MongoDatabase (o se inicializa a partir de un cliente).
    public WeatherDataMongoDBDAO(MongoDatabase database) {
        this.collection = database.getCollection("WeatherDataAS01"); // Nombre de la colección
    }

    public class WeatherDataMongoFields {

        public static final String FIELD_RECORD_ID = "record_id";  // MongoDB suele utilizar _id como identificador único
        public static final String FIELD_CITY = "city";
        public static final String FIELD_COUNTRY = "country";
        public static final String FIELD_LATITUDE = "latitude";
        public static final String FIELD_LONGITUDE = "longitude";
        public static final String FIELD_DATE = "date";
        public static final String FIELD_TEMPERATURE_CELSIUS = "temperature_celsius";
        public static final String FIELD_HUMIDITY_PERCENT = "humidity_percent";
        public static final String FIELD_PRECIPITATION_MM = "precipitation_mm";
        public static final String FIELD_WIND_SPEED_KMH = "wind_speed_kmh";
        public static final String FIELD_WEATHER_CONDITION = "weather_condition";
        public static final String FIELD_FORECAST = "forecast";
        public static final String FIELD_UPDATED = "updated";
    }

    //
    private static WeatherData readWeatherDataFromDocument(Document doc) throws java.text.ParseException {

        // Extraemos los campos del documento de forma genérica con el método readValueFromDocument
        Object recordIdObj = processRecordId(doc);
        int recordId = (recordIdObj instanceof Integer) ? (Integer) recordIdObj
                : (recordIdObj instanceof Double) ? ((Double) recordIdObj).intValue()
                        : 0; // Valor predeterminado en caso de que no sea Integer ni Double, o si es null

        String city = (doc.containsKey(WeatherDataMongoFields.FIELD_CITY) && doc.getString(WeatherDataMongoFields.FIELD_CITY) != null && !doc.getString(WeatherDataMongoFields.FIELD_CITY).isEmpty())
                ? doc.getString(WeatherDataMongoFields.FIELD_CITY)
                : null; // Si está vacío o es nulo, asignamos null

        String country = (doc.containsKey(WeatherDataMongoFields.FIELD_COUNTRY) && doc.getString(WeatherDataMongoFields.FIELD_COUNTRY) != null && !doc.getString(WeatherDataMongoFields.FIELD_COUNTRY).isEmpty())
                ? doc.getString(WeatherDataMongoFields.FIELD_COUNTRY)
                : null; // Lo mismo que city

        double latitude = (doc.containsKey(WeatherDataMongoFields.FIELD_LATITUDE) && doc.get(WeatherDataMongoFields.FIELD_LATITUDE) != null)
                ? (Double) readValueFromDocument(doc, WeatherDataMongoFields.FIELD_LATITUDE)
                : 0.0; // Valor predeterminado en caso de null

        double longitude = (doc.containsKey(WeatherDataMongoFields.FIELD_LONGITUDE) && doc.get(WeatherDataMongoFields.FIELD_LONGITUDE) != null)
                ? (Double) readValueFromDocument(doc, WeatherDataMongoFields.FIELD_LONGITUDE)
                : 0.0; // Valor predeterminado en caso de null

        Date date = convertToDate((doc.containsKey(WeatherDataMongoFields.FIELD_DATE) && doc.get(WeatherDataMongoFields.FIELD_DATE) != null)
                ? doc.get(WeatherDataMongoFields.FIELD_DATE)
                : null); // Convertimos a null si no está presente

        // Leer el campo de temperatura, permitiendo tanto Integer como Double
        double temperatureCelsius = (doc.containsKey(WeatherDataMongoFields.FIELD_TEMPERATURE_CELSIUS) && doc.get(WeatherDataMongoFields.FIELD_TEMPERATURE_CELSIUS) != null)
                ? (doc.get(WeatherDataMongoFields.FIELD_TEMPERATURE_CELSIUS) instanceof Double
                ? (Double) doc.get(WeatherDataMongoFields.FIELD_TEMPERATURE_CELSIUS)
                : (double) (Integer) doc.get(WeatherDataMongoFields.FIELD_TEMPERATURE_CELSIUS)) // Si es Integer, lo casteamos a Double
                : 0; // Valor predeterminado en caso de nul

        int humidityPercent = (doc.containsKey(WeatherDataMongoFields.FIELD_HUMIDITY_PERCENT) && doc.get(WeatherDataMongoFields.FIELD_HUMIDITY_PERCENT) != null)
                ? (doc.get(WeatherDataMongoFields.FIELD_HUMIDITY_PERCENT) instanceof Integer
                ? (Integer) doc.get(WeatherDataMongoFields.FIELD_HUMIDITY_PERCENT)
                : 0) // Verifica si el valor es un Integer, sino usa 0
                : 0; // Valor predeterminado en caso de null

        double precipitationMm = (doc.containsKey(WeatherDataMongoFields.FIELD_PRECIPITATION_MM) && doc.get(WeatherDataMongoFields.FIELD_PRECIPITATION_MM) != null)
                ? (Double) readValueFromDocument(doc, WeatherDataMongoFields.FIELD_PRECIPITATION_MM)
                : 0.0; // Valor predeterminado en caso de null

        int windSpeedKmh = (doc.containsKey(WeatherDataMongoFields.FIELD_WIND_SPEED_KMH) && doc.get(WeatherDataMongoFields.FIELD_WIND_SPEED_KMH) != null)
                ? (doc.get(WeatherDataMongoFields.FIELD_WIND_SPEED_KMH) instanceof Integer
                ? (Integer) doc.get(WeatherDataMongoFields.FIELD_WIND_SPEED_KMH)
                : 0) // Verifica si el valor es un Integer, sino usa 0
                : 0; // Valor predeterminado en caso de null

        String weatherCondition = (doc.containsKey(WeatherDataMongoFields.FIELD_WEATHER_CONDITION) && doc.getString(WeatherDataMongoFields.FIELD_WEATHER_CONDITION) != null && !doc.getString(WeatherDataMongoFields.FIELD_WEATHER_CONDITION).isEmpty())
                ? doc.getString(WeatherDataMongoFields.FIELD_WEATHER_CONDITION)
                : null; // Lo mismo que city

        String forecast = (doc.containsKey(WeatherDataMongoFields.FIELD_FORECAST) && doc.getString(WeatherDataMongoFields.FIELD_FORECAST) != null && !doc.getString(WeatherDataMongoFields.FIELD_FORECAST).isEmpty())
                ? doc.getString(WeatherDataMongoFields.FIELD_FORECAST)
                : null; // Lo mismo que city

        Date updated = convertToDate((doc.containsKey(WeatherDataMongoFields.FIELD_UPDATED) && doc.get(WeatherDataMongoFields.FIELD_UPDATED) != null)
                ? doc.get(WeatherDataMongoFields.FIELD_UPDATED)
                : null); // Convertimos a null si no está presente

        // Creamos un objeto WeatherData con los datos obtenidos
        return new WeatherData(recordId, city, country, latitude, longitude, date, temperatureCelsius, humidityPercent, precipitationMm, windSpeedKmh, weatherCondition, forecast, updated);
    }

    // Método auxiliar para convertir de String a Date si es necesario
    private static Date convertToDate(Object field) throws java.text.ParseException {
        if (field instanceof Date) {
            // Si ya es un Date, lo devolvemos directamente
            return (Date) field;
        } else if (field instanceof String) {
            // Cambiar el formato para manejar solo la fecha
            return new SimpleDateFormat("yyyy-MM-dd").parse((String) field);
        }
        return null; // Si no es ni Date ni String, devolvemos null o lanzamos una excepción según lo que prefieras
    }

    // Función genérica para leer el valor del campo y convertir a Integer o Double según el tipo
    private static Object readValueFromDocument(Document document, String fieldName) {
        // Obtén el valor de forma genérica
        Object value = document.get(fieldName);

        // Verifica si el valor es un Integer o un Double y realiza la conversión correspondiente
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            throw new IllegalArgumentException("El campo " + fieldName + " no contiene un valor de tipo Integer o Double.");
        }
    }

    public static Object processRecordId(Document doc) {
        Object recordId = doc.get("record_id");

        if (recordId instanceof Integer) {
            // Procesar como Integer
            return (Integer) recordId;
        } else if (recordId instanceof Double) {
            // Procesar como Double
            return ((Double) recordId).intValue();  // Convertir a int
        } else if (recordId instanceof String) {
            // Procesar como String
            return (String) recordId;
        } else if (recordId == null) {
            // Si el valor es null, asignamos un valor predeterminado, por ejemplo, 0
            return 0;  // Valor predeterminado cuando record_id es null
        } else {
            // Manejar otros tipos o lanzar excepción personalizada
            throw new IllegalArgumentException("El campo record_id tiene un tipo inesperado: " + recordId.getClass().getName());
        }
    }

    /*
    Document doc = collection.find(Filters.eq(WeatherDataMongoFields.FIELD_CITY, "Madrid")).first();
    WeatherData weatherData = readWeatherDataFromDocument(doc);
     */
    //Equivalente SELECT COUNT(*) en SQL 
    public long countWeatherDataMongo() {
        try {
            return collection.countDocuments(); // Cuenta todos los documentos en la colección
        } catch (Exception e) {
            throw new RuntimeException("Error al contar los registros en WeatherDataAS01: " + e.getMessage(), e);
        }
    }

    public boolean weatherDataExistMongo(String recordId) {
        try {
            // filtro para buscar el `recordId`
            Document query = new Document("recordId", recordId);
            // Ejecutamos la consulta de búsqueda
            long count = collection.countDocuments(query);
            // Si el contador es mayor que 0 = ID ya existe
            return count > 0;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar la existencia del recordId: " + e.getMessage(), e);
        }
    }

    //Cargamos list WeatherData con TODOS los datos de Mongo
    public List<WeatherData> loadAllWeatherDataMongo() {
        List<WeatherData> weatherDataList = new ArrayList<>();
        try {
            // Consultamos todos los documentos en la colección
            FindIterable<Document> documents = collection.find();
            MongoCursor<Document> cursor = documents.iterator();

            // Con cursor iteramos resultados y RELLENAMOS LISTA
            while (cursor.hasNext()) {
                Document document = cursor.next();
                // Aquí debes mapear el documento de MongoDB a un objeto WeatherData
                weatherDataList.add(readWeatherDataFromDocument(document)); //readWeatherDataFromDocument()-reutilizada
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar los datos meteorológicos: " + e.getMessage(), e);
        }
        return weatherDataList;
    }

    //FILTROS - 1
    //Saca lista de WeatherDatas por city
    public List<WeatherData> loadWeatherDataByCityMongo(String city) {
        List<WeatherData> weatherDataList = new ArrayList<>();

        try {
            // Creamos el filtro para la city
            Document filter = new Document("city", city);

            // Consultamos los documentos que COINCIDEN con el filtro
            FindIterable<Document> documents = collection.find(filter); //FindIterable - filter
            MongoCursor<Document> cursor = documents.iterator(); //Recorremos para consultarlos

            // Iteramos sobre los resultados y los agregamos a la lista
            while (cursor.hasNext()) {
                Document document = cursor.next();
                // Mapear el documento a un objeto WeatherData
                weatherDataList.add(readWeatherDataFromDocument(document)); //readWeatherDataFromDocument()-reutilizada
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar los datos meteorológicos para la ciudad: " + city, e);
        }
        return weatherDataList.isEmpty() ? null : weatherDataList;
    }

    //FILTROS - 2
    //Sacar lista de WeatherDatas por CITIES - DEVUELVE LIST<WEATHERDATA>
    public List<WeatherData> loadWeatherDataByCitiesMongo(List<String> cities) {
        List<WeatherData> weatherDataList = new ArrayList<>();

        try {
            // Creamos el filtro - OPERADOR $in para la LISTA DE CIUDADES
            Document filter = new Document("city", new Document("$in", cities));

            // Consultamos los documentos que coinciden con filtro
            FindIterable<Document> documents = collection.find(filter);
            MongoCursor<Document> cursor = documents.iterator();

            // Iteramos sobre los resultados y los AGREGAMOS A LA LISTA
            while (cursor.hasNext()) {
                Document document = cursor.next();
                // Mapear el documento a un objeto WeatherData
                weatherDataList.add(readWeatherDataFromDocument(document)); //readWeatherDataFromDocument()-reutilizada
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar los datos meteorológicos para las ciudades: " + cities, e);
        }
        return weatherDataList.isEmpty() ? null : weatherDataList; // Retorna null si la lista está vacía
    }

    ///////////////////INSERT MONGODB ///////////////////////////////////////
    //Imp-en MongoDB no exiten las claves primarias
    //verificar si recordID existe para insertar
    //Le pasamos OBJETO WEATHER DATA E INSERTAMOS EN LA BD
    public int insertWeatherDataMongo(WeatherData weatherData) {
        try {
            // Verificar si el recordId ya existe con Filters.eq
            Document existingData = collection.find(Filters.eq("recordId", weatherData.getRecordId())).first();

            //si existe - excepción
            if (existingData != null) {
                // Si el recordId ya existe, lanzar una excepción
                throw new RuntimeException("El recordId ya existe en la base de datos.");
            }

            // Crear el documento para insertar
            Document newWeatherData = new Document()
                    .append("record_id", weatherData.getRecordId())
                    .append("city", weatherData.getCity())
                    .append("country", weatherData.getCountry())
                    .append("latitude", weatherData.getLatitude())
                    .append("longitude", weatherData.getLongitude())
                    .append("date", weatherData.getDate()) // Asegúrate de que el tipo sea adecuado
                    .append("temperatureCelsius", weatherData.getTemperatureCelsius())
                    .append("humidityPercent", weatherData.getHumidityPercent())
                    .append("precipitationMm", weatherData.getPrecipitationMm())
                    .append("windSpeedKmh", weatherData.getWindSpeedKmh())
                    .append("weatherCondition", weatherData.getWeatherCondition())
                    .append("forecast", weatherData.getForecast())
                    .append("updated", weatherData.getUpdated()); // Asegúrate de que el tipo sea adecuado

            // Insertar el documento en la colección
            collection.insertOne(newWeatherData, new InsertOneOptions());

            return 1; // 1 = se insertó un documento
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar los datos meteorológicos: " + e.getMessage(), e);
        }
    }

    public int insertWeatherDataMongoDB(List<WeatherData> weatherDataList) {
        try {
            List<Document> documents = new ArrayList<>();

            // Convertir cada WeatherData en un documento de MongoDB
            for (WeatherData weatherData : weatherDataList) {
                Document newWeatherData = new Document()
                        .append("record_id", weatherData.getRecordId())
                        .append("city", weatherData.getCity())
                        .append("country", weatherData.getCountry())
                        .append("latitude", weatherData.getLatitude())
                        .append("longitude", weatherData.getLongitude())
                        .append("date", weatherData.getDate()) // Asegúrate de que el tipo sea adecuado
                        .append("temperatureCelsius", weatherData.getTemperatureCelsius())
                        .append("humidityPercent", weatherData.getHumidityPercent())
                        .append("precipitationMm", weatherData.getPrecipitationMm())
                        .append("windSpeedKmh", weatherData.getWindSpeedKmh())
                        .append("weatherCondition", weatherData.getWeatherCondition())
                        .append("forecast", weatherData.getForecast())
                        .append("updated", weatherData.getUpdated()); // Asegúrate de que el tipo sea adecuado

                documents.add(newWeatherData);
            }

            // Realizar una sola inserción en MongoDB
            collection.insertMany(documents);

            return weatherDataList.size(); // Devuelve el número de elementos insertados
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar los datos meteorológicos: " + e.getMessage(), e);
        }
    }
    ///////////// DELETE ///////////////////////////////////////////////////////

    //uso deleteMany y DeleteResult
    public int deleteAllWeatherDataMongo() {
        try {
            // Usamos deleteMany con un FILTRO VACIO para eliminar todos los documentos
            DeleteResult result = collection.deleteMany(new Document());
            return (int) result.getDeletedCount(); // DEVUELVE NUMERO DE ELEMNTOS ELIMINADOS (como en SQL)
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar todos los datos meteorológicos: " + e.getMessage(), e);
        }
    }

    //BORRAR CON FILTROS - LE PASAMOS UNA LISTA Y LA BORRA
    //Uso deleteMany y OPERADOR $in 
    public int deleteWeatherDataByListMongo(List<WeatherData> weatherDataList) {
        if (weatherDataList == null || weatherDataList.isEmpty()) {
            return 0; // Si la lista está vacía, no hay nada que eliminar
        }

        // Crear una lista de LOS RECORDS ID QUE SE ELIMINARAN
        List<Integer> recordIds = weatherDataList.stream()
                .map(WeatherData::getRecordId)
                .collect(Collectors.toList());

        // Usamos el operador $in para eliminar los documentos que tengan un recordId en la lista
        //lOS QUE TENGAN RECORD ID Y SALGAN SE ELIMINAN (FILTRO)
        Document filter = new Document("record_id", new Document("$in", recordIds));

        try {
            // Eliminar los documentos que coinciden con el filtro
            DeleteResult result = collection.deleteMany(filter);
            return (int) result.getDeletedCount(); // Devuelve numero de elementos eliminados
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar los datos meteorológicos: " + e.getMessage(), e);
        }
    }

    /// UPSERT ///
    public UpdateResult upsertWeatherDataMongo(WeatherData weatherData) {
        // Crear el filtro para buscar el documento basado en el recordId
        Document filter = new Document("recordId", weatherData.getRecordId());

        // Crear el documento con los datos actualizados o nuevos
        Document updatedDocument = new Document()
                .append("record_id", weatherData.getRecordId())
                .append("city", weatherData.getCity())
                .append("country", weatherData.getCountry())
                .append("latitude", weatherData.getLatitude())
                .append("longitude", weatherData.getLongitude())
                .append("date", weatherData.getDate())
                .append("temperatureCelsius", weatherData.getTemperatureCelsius())
                .append("humidityPercent", weatherData.getHumidityPercent())
                .append("precipitationMm", weatherData.getPrecipitationMm())
                .append("windSpeedKmh", weatherData.getWindSpeedKmh())
                .append("weatherCondition", weatherData.getWeatherCondition())
                .append("forecast", weatherData.getForecast())
                .append("updated", weatherData.getUpdated());

        // Opciones para el upsert
        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            // Realizar el upsert: Si el documento existe, lo actualiza; si no, lo inserta
            UpdateResult result = collection.updateOne(filter, new Document("$set", updatedDocument), options);

            // Verifica si se insertó un nuevo documento
            if (result.getUpsertedId() != null) {
                // Se ha insertado un nuevo documento
                System.out.println("Nuevo documento insertado con ID: " + result.getUpsertedId());
            } else if (result.getModifiedCount() > 0) {
                // Se ha actualizado un documento existente
                System.out.println("Documento actualizado.");
            } else {
                // No se realizaron cambios
                System.out.println("No se realizaron cambios.");
            }

            return result; // Retorna el UpdateResult para más detalles

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retorna null si ocurrió un error
        }
    }

}
