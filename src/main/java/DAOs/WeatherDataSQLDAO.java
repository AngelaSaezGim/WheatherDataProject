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

    /**
     * Cuenta el número total de registros en la tabla WeatherDataAS01.
     */
    public int countWeatherData() throws SQLException {
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

    public List<WeatherData> loadAllWeatherData() throws SQLException {
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

    public WeatherData loadWeatherDataByRecordId(String recordId) throws SQLException {
        String query = "SELECT * FROM WeatherDataAS01 WHERE recordId = ?";
        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            stmt.setString(1, recordId);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return readWeatherDataFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar los datos meteorológicos con recordId: " + recordId, e);
        }
        return null;
    }

    //CARGAMOS DATOS SEGUN LA CIUDAD EN LA QUE ESTEMOS
    //COMO PUEDE HABER VARIAS CIUDADES IGUALES ES UNA LIST
    public List<WeatherData> loadWeatherDataByCity(String ciudad) throws SQLException {
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

    //CARGAMOS DATOS SEGUN VARIAS CIUDADES
    //COMO PUEDE HABER VARIAS CIUDADES IGUALES ES UNA LIST
    public List<WeatherData> loadWeatherDataByCities(List<String> ciudades) throws SQLException {
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

    //INSERT
    public int insertWeatherData(WeatherData weatherData) {
        int filasAfectadas = 0;

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

            stmt.setInt(1, weatherData.getRecordId());
            stmt.setString(2, weatherData.getCity());
            stmt.setString(3, weatherData.getCountry());
            stmt.setDouble(4, weatherData.getLatitude());
            stmt.setDouble(5, weatherData.getLongitude());
            stmt.setDate(6, (Date) weatherData.getDate());
            stmt.setInt(7, weatherData.getTemperatureCelsius());
            stmt.setInt(8, weatherData.getHumidityPercent());
            stmt.setDouble(9, weatherData.getPrecipitationMm());
            stmt.setInt(10, weatherData.getWindSpeedKmh());
            stmt.setString(11, weatherData.getWeatherCondition());
            stmt.setString(12, weatherData.getForecast());
            stmt.setDate(13, (Date) weatherData.getUpdated());

            filasAfectadas = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error al insertar datos meteorológicos: " + e.getMessage(), e);
        }

        return filasAfectadas;
    }

    // Función para verificar si el ID ya existe en la base de datos
    public boolean weatherDataExist(String recordId) {
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

    //Borra TODOS los registros
    public int deleteAllWeatherDataSQL() throws SQLException {
        String query = "DELETE FROM WeatherDataAS01";
        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar todos los datos meteorológicos: " + e.getMessage(), e);
        }
    }

    //Borra LISTA de registros seleccionados (Filtros)
    public int deleteWeatherDataByListSQL(List<WeatherData> weatherDataList) throws SQLException {
        if (weatherDataList == null || weatherDataList.isEmpty()) {
            return 0; // No hay datos para eliminar
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
