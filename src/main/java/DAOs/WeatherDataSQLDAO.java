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

/**
 *
 * @author angel
 */
public class WeatherDataSQLDAO extends DataAccessObject {

    public WeatherDataSQLDAO(Connection connection) {
        super(connection);
    }

    private class WeatherDataTableColumns {

        private static final String COLUMN_RECORD_ID = "recordId";
        private static final String COLUMN_CITY = "city";
        private static final String COLUMN_COUNTRY = "country";
        private static final String COLUMN_LATITUDE = "latitude";
        private static final String COLUMN_LONGITUDE = "longitude";
        private static final String COLUMN_DATE = "date";
        private static final String COLUMN_TEMPERATURE_CELSIUS = "temperatureCelsius";
        private static final String COLUMN_HUMIDITY_PERCENT = "humidityPercent";
        private static final String COLUMN_PRECIPITATION_MM = "precipitationMm";
        private static final String COLUMN_WIND_SPEED_KMH = "windSpeedKmh";
        private static final String COLUMN_WEATHER_CONDITION = "weatherCondition";
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

    public List<WeatherData> loadAllWeatherData() throws SQLException {
        List<WeatherData> weatherDataList = new ArrayList<>();
        String query = "SELECT * FROM WeatherData";

        try ( PreparedStatement stmt = cnt.prepareStatement(query);  ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                weatherDataList.add(readWeatherDataFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar los datos meteorológicos: " + e.getMessage(), e);
        }
        return weatherDataList;
    }

    public WeatherData loadWeatherDataByRecordId(int recordId) throws SQLException {
        String query = "SELECT * FROM WeatherData WHERE recordId = ?";
        try ( PreparedStatement stmt = cnt.prepareStatement(query)) {
            stmt.setInt(1, recordId);
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

    protected int insertWeatherData(WeatherData weatherData) {
        int filasAfectadas = 0;

        String sentenciaSQL = "INSERT INTO WeatherData ("
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

    protected int updateWeatherData(int recordId, WeatherData weatherDataActualizar) {
        int filasAfectadas = 0;

        String sql = "UPDATE WeatherData SET "
                + WeatherDataTableColumns.COLUMN_CITY + " = ?, "
                + WeatherDataTableColumns.COLUMN_COUNTRY + " = ?, "
                + WeatherDataTableColumns.COLUMN_LATITUDE + " = ?, "
                + WeatherDataTableColumns.COLUMN_LONGITUDE + " = ?, "
                + WeatherDataTableColumns.COLUMN_DATE + " = ?, "
                + WeatherDataTableColumns.COLUMN_TEMPERATURE_CELSIUS + " = ?, "
                + WeatherDataTableColumns.COLUMN_HUMIDITY_PERCENT + " = ?, "
                + WeatherDataTableColumns.COLUMN_PRECIPITATION_MM + " = ?, "
                + WeatherDataTableColumns.COLUMN_WIND_SPEED_KMH + " = ?, "
                + WeatherDataTableColumns.COLUMN_WEATHER_CONDITION + " = ?, "
                + WeatherDataTableColumns.COLUMN_FORECAST + " = ?, "
                + WeatherDataTableColumns.COLUMN_UPDATED + " = ? "
                + "WHERE " + WeatherDataTableColumns.COLUMN_RECORD_ID + " = ?";

        try ( PreparedStatement stmt = cnt.prepareStatement(sql)) {
            stmt.setString(1, weatherDataActualizar.getCity());
            stmt.setString(2, weatherDataActualizar.getCountry());
            stmt.setDouble(3, weatherDataActualizar.getLatitude());
            stmt.setDouble(4, weatherDataActualizar.getLongitude());
            stmt.setDate(5, (Date) weatherDataActualizar.getDate());
            stmt.setInt(6, weatherDataActualizar.getTemperatureCelsius());
            stmt.setInt(7, weatherDataActualizar.getHumidityPercent());
            stmt.setDouble(8, weatherDataActualizar.getPrecipitationMm());
            stmt.setInt(9, weatherDataActualizar.getWindSpeedKmh());
            stmt.setString(10, weatherDataActualizar.getWeatherCondition());
            stmt.setString(11, weatherDataActualizar.getForecast());
            stmt.setDate(12, (Date) weatherDataActualizar.getUpdated());
            stmt.setInt(13, recordId);

            filasAfectadas = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error al actualizar datos meteorológicos: " + e.getMessage(), e);
        }
        return filasAfectadas;
    }

    protected int deleteWeatherData(int recordId) {
        int filasAfectadas = 0;

        String sql = "DELETE FROM WeatherData WHERE " + WeatherDataTableColumns.COLUMN_RECORD_ID + " = ?";

        try ( PreparedStatement stmt = cnt.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            filasAfectadas = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error al eliminar datos meteorológicos con recordId: " + recordId, e);
        }

        return filasAfectadas;
    }

}
