/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Objects.WeatherData;
import java.sql.Date;
import java.util.stream.Collectors;

/**
 *
 * @author angel
 */
public class WeatherDataSQLDAO extends DataAccessObject {

    public WeatherDataSQLDAO(Connection connection) {
        super(connection);
    }

    private class WeatherDataTableColumns {

        private static final String COLUMN_RECORD_ID = "record_id";
        private static final String COLUMN_CITY = "city";
        private static final String COLUMN_COUNTRY = "country";
        private static final String COLUMN_LATITUDE = "latitude";
        private static final String COLUMN_LONGITUDE = "longitude";
        private static final String COLUMN_DATE = "date";
        private static final String COLUMN_TEMPERATURE_CELSIUS = "temperature_celsius";
        private static final String COLUMN_HUMIDITY_PERCENT = "humidity_percent";  // Cambié la mayúscula 'P' por minúscula
        private static final String COLUMN_PRECIPITATION_MM = "precipitation_mm";
        private static final String COLUMN_WIND_SPEED_KMH = "wind_speed_kmh";
        private static final String COLUMN_WEATHER_CONDITION = "weather_condition";
        private static final String COLUMN_FORECAST = "forecast";
        private static final String COLUMN_UPDATED = "updated";
    }

    private static WeatherData readWeatherDataFromResultSet(ResultSet rs) throws SQLException {

        int recordId = rs.getInt(WeatherDataTableColumns.COLUMN_RECORD_ID);
        String city = rs.getString(WeatherDataTableColumns.COLUMN_CITY);
        String country = rs.getString(WeatherDataTableColumns.COLUMN_COUNTRY);
        double latitude = rs.getDouble(WeatherDataTableColumns.COLUMN_LATITUDE);
        double longitude = rs.getDouble(WeatherDataTableColumns.COLUMN_LONGITUDE);
        Date date = rs.getDate(WeatherDataTableColumns.COLUMN_DATE);
        int temperatureCelsius = rs.getInt(WeatherDataTableColumns.COLUMN_TEMPERATURE_CELSIUS);
        int humidityPercent = rs.getInt(WeatherDataTableColumns.COLUMN_HUMIDITY_PERCENT);
        double precipitationMm = rs.getDouble(WeatherDataTableColumns.COLUMN_PRECIPITATION_MM);
        int windSpeedKmh = rs.getInt(WeatherDataTableColumns.COLUMN_WIND_SPEED_KMH);
        String weatherCondition = rs.getString(WeatherDataTableColumns.COLUMN_WEATHER_CONDITION);
        String forecast = rs.getString(WeatherDataTableColumns.COLUMN_FORECAST);
        Date updated = rs.getDate(WeatherDataTableColumns.COLUMN_UPDATED);

        return new WeatherData(recordId, city, country, latitude, longitude, date, temperatureCelsius, humidityPercent, precipitationMm, windSpeedKmh, weatherCondition, forecast, updated);
    }

    /////////////////////////////SELECT////////////////////////////////////////
    // Cuenta registros en weatherData
    public int countWeatherDataSQL() throws SQLException {
        String query = "SELECT COUNT(*) FROM WeatherDataAS01";
        try ( PreparedStatement stmt = cnt.prepareStatement(query);  ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Devuelve el primer valor de la primera fila (la cuenta).
            }
        } catch (SQLException e) {
            throw new SQLException("Error al contar los registros en WeatherDataAS01: " + e.getMessage(), e);
        }
        return 0;
    }

    // Saber si id ya existe en BD
    public boolean weatherDataExistSQL(String recordId) {
        String query = "SELECT COUNT(*) FROM WeatherDataAS01 WHERE " + WeatherDataTableColumns.COLUMN_RECORD_ID + " = ?";

        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            stmt.setString(1, recordId);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el contador es mayor que 0, el ID ya existe
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error al verificar la existencia del ID: " + e.getMessage(), e);
        }
        return false; // Si no hay registros, el ID no existe
    }

    //Devuelve objeto list que devuelve todos los datos (Sincronizar)
    public List<WeatherData> loadAllWeatherDataSQL() throws SQLException {
        List<WeatherData> weatherDataList = new ArrayList<>();
        String query = "SELECT * FROM WeatherDataAS01";

        try ( PreparedStatement stmt = cnt.prepareStatement(query);  ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                weatherDataList.add(readWeatherDataFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar los datos meteorológicos: " + e.getMessage(), e);
        }
        return weatherDataList;
    }

    //Filtro 1 - (String ciudad)
    //CARGAMOS DATOS SEGUN LA CIUDAD EN LA QUE ESTEMOS
    //COMO PUEDE HABER VARIAS CIUDADES IGUALES ES UNA LIST
    public List<WeatherData> loadWeatherDataByCitySQL(String ciudad) throws SQLException {
        String query = "SELECT * FROM WeatherDataAS01 WHERE city = ?";
        List<WeatherData> weatherDataList = new ArrayList<>();

        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            stmt.setString(1, ciudad);
            try ( ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Leer datos y agregar a la lista
                    WeatherData weatherData = readWeatherDataFromResultSet(rs);
                    weatherDataList.add(weatherData);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar los datos meteorológicos para la ciudad: " + ciudad, e);
        }
        return weatherDataList.isEmpty() ? null : weatherDataList; // Retorna null si la lista está vacía
    }

    //Filtro 2 - (list ciudades)
    //CARGAMOS DATOS SEGUN VARIAS CIUDADES
    //COMO PUEDE HABER VARIAS CIUDADES IGUALES ES UNA LIST
    public List<WeatherData> loadWeatherDataByCitiesSQL(List<String> ciudades) throws SQLException {

        String query = "SELECT * FROM WeatherDataAS01 WHERE city "
                + "IN (" + String.join(",", ciudades.stream().map(c -> "?").toArray(String[]::new)) + ")";

        List<WeatherData> weatherDataList = new ArrayList<>();

        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            // Establecer los parámetros para cada ciudad
            for (int i = 0; i < ciudades.size(); i++) {
                stmt.setString(i + 1, ciudades.get(i));
            }

            try ( ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WeatherData weatherData = readWeatherDataFromResultSet(rs);
                    weatherDataList.add(weatherData);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar los datos meteorológicos para las ciudades: " + ciudades, e);
        }
        return weatherDataList.isEmpty() ? null : weatherDataList; // Retorna null si la lista está vacía
    }

    /////////////////////////////INSERT////////////////////////////////////////
    public int insertWeatherDataSQL(WeatherData weatherData) throws SQLException {
        int filasAfectadas = 0;

        // Verificar si la clave primaria (record_id) ya existe (para insertar o no)
        String checkQuery = "SELECT COUNT(*) FROM WeatherDataAS01 WHERE record_id = ?";
        try ( PreparedStatement checkStmt = cnt.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, weatherData.getRecordId());

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Si ya existe un registro con el mismo record_id, lanzamos una excepción
                throw new SQLException("El record_id ya existe en la base de datos.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error al verificar si el record_id ya existe: " + e.getMessage());
        }

        // Sentencia SQL para insertar los datos meteorológicos
        String sentenciaSQL = "INSERT INTO WeatherDataAS01 ("
                + WeatherDataTableColumns.COLUMN_RECORD_ID + ", "
                + WeatherDataTableColumns.COLUMN_CITY + ", "
                + WeatherDataTableColumns.COLUMN_COUNTRY + ", "
                + WeatherDataTableColumns.COLUMN_LATITUDE + ", "
                + WeatherDataTableColumns.COLUMN_LONGITUDE + ", "
                + WeatherDataTableColumns.COLUMN_DATE + ", "
                + WeatherDataTableColumns.COLUMN_TEMPERATURE_CELSIUS + ", "
                + WeatherDataTableColumns.COLUMN_HUMIDITY_PERCENT + ", "
                + WeatherDataTableColumns.COLUMN_PRECIPITATION_MM + ", "
                + WeatherDataTableColumns.COLUMN_WIND_SPEED_KMH + ", "
                + WeatherDataTableColumns.COLUMN_WEATHER_CONDITION + ", "
                + WeatherDataTableColumns.COLUMN_FORECAST + ", "
                + WeatherDataTableColumns.COLUMN_UPDATED
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( PreparedStatement stmt = cnt.prepareStatement(sentenciaSQL)) {
            // Asignar los valores del objeto WeatherData a los parámetros de la sentencia
            stmt.setInt(1, weatherData.getRecordId());
            stmt.setString(2, weatherData.getCity());
            stmt.setString(3, weatherData.getCountry());
            stmt.setDouble(4, weatherData.getLatitude());
            stmt.setDouble(5, weatherData.getLongitude());
            stmt.setDate(6, (Date) weatherData.getDate());
            stmt.setDouble(7, weatherData.getTemperatureCelsius());
            stmt.setDouble(8, weatherData.getHumidityPercent());
            stmt.setDouble(9, weatherData.getPrecipitationMm());
            stmt.setDouble(10, weatherData.getWindSpeedKmh());
            stmt.setString(11, weatherData.getWeatherCondition());
            stmt.setString(12, weatherData.getForecast());
            stmt.setDate(13, (Date) weatherData.getUpdated());

            // Ejecutar la sentencia de inserción
            filasAfectadas = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al insertar los datos meteorológicos: " + e.getMessage());
        }

        return filasAfectadas;
    }

    public int insertWeatherDataSQL(List<WeatherData> weatherDataList) throws SQLException {
        String insertSQL = "INSERT INTO WeatherDataAS01 ("
                + WeatherDataTableColumns.COLUMN_RECORD_ID + ", "
                + WeatherDataTableColumns.COLUMN_CITY + ", "
                + WeatherDataTableColumns.COLUMN_COUNTRY + ", "
                + WeatherDataTableColumns.COLUMN_LATITUDE + ", "
                + WeatherDataTableColumns.COLUMN_LONGITUDE + ", "
                + WeatherDataTableColumns.COLUMN_DATE + ", "
                + WeatherDataTableColumns.COLUMN_TEMPERATURE_CELSIUS + ", "
                + WeatherDataTableColumns.COLUMN_HUMIDITY_PERCENT + ", "
                + WeatherDataTableColumns.COLUMN_PRECIPITATION_MM + ", "
                + WeatherDataTableColumns.COLUMN_WIND_SPEED_KMH + ", "
                + WeatherDataTableColumns.COLUMN_WEATHER_CONDITION + ", "
                + WeatherDataTableColumns.COLUMN_FORECAST + ", "
                + WeatherDataTableColumns.COLUMN_UPDATED
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( PreparedStatement stmt = cnt.prepareStatement(insertSQL)) {
            // Comenzamos una transacción para insertar todos los registros
            cnt.setAutoCommit(false);

            for (WeatherData weatherData : weatherDataList) {
                // Asignar los valores del objeto WeatherData a los parámetros de la sentencia
                stmt.setInt(1, weatherData.getRecordId());
                stmt.setString(2, weatherData.getCity());
                stmt.setString(3, weatherData.getCountry());
                stmt.setDouble(4, weatherData.getLatitude());
                stmt.setDouble(5, weatherData.getLongitude());
                stmt.setDate(6, (Date) weatherData.getDate());
                stmt.setDouble(7, weatherData.getTemperatureCelsius());
                stmt.setDouble(8, weatherData.getHumidityPercent());
                stmt.setDouble(9, weatherData.getPrecipitationMm());
                stmt.setDouble(10, weatherData.getWindSpeedKmh());
                stmt.setString(11, weatherData.getWeatherCondition());
                stmt.setString(12, weatherData.getForecast());
                stmt.setDate(13, (Date) weatherData.getUpdated());

                // Añadir la sentencia a batch
                stmt.addBatch();
            }

            // Ejecutar el batch de inserciones
            int[] results = stmt.executeBatch();

            // Hacer commit
            cnt.commit();

            return results.length; // Devuelve el número de registros insertados
        } catch (SQLException e) {
            // Si ocurre un error, hacer rollback
            try {
                cnt.rollback();
            } catch (SQLException rollbackEx) {
                throw new SQLException("Error durante rollback: " + rollbackEx.getMessage(), rollbackEx);
            }
            throw new SQLException("Error durante la inserción en SQL: " + e.getMessage(), e);
        } finally {
            // Asegurarse de que el autocommit esté habilitado nuevamente
            try {
                cnt.setAutoCommit(true);
            } catch (SQLException e) {
                throw new SQLException("Error al restaurar el autoCommit: " + e.getMessage(), e);
            }
        }
    }

    //********************* DELETE ***************************************/
    //Borra TODOS los registros
    public int deleteAllWeatherDataSQL() throws SQLException {
        String query = "DELETE FROM WeatherDataAS01";
        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar todos los datos meteorológicos: " + e.getMessage(), e);
        }
    }

    public int deleteWeatherDataByListSQL(List<WeatherData> weatherDataList) throws SQLException {
        if (weatherDataList == null || weatherDataList.isEmpty()) {
            return 0; // Si la lista está vacia - no hay datos para eliminar
        }

        String query = "DELETE FROM WeatherDataAS01 WHERE "
                + WeatherDataTableColumns.COLUMN_RECORD_ID + " IN ("
                + weatherDataList.stream().map(w -> "?").collect(Collectors.joining(", ")) + ")";

        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            for (int i = 0; i < weatherDataList.size(); i++) {
                stmt.setInt(i + 1, weatherDataList.get(i).getRecordId());
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar datos meteorológicos: " + e.getMessage(), e);
        }
    }

}
