/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.util.Date;
/**
 *
 * @author angel
 */
public class WheatherData {
    
    private int recordId;
    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private Date date;
    private int temperatureCelsius;
    private int humidityPercent;
    private double precipitationMm;
    private int windSpeedKmh;
    private String weatherCondition;
    private String forecast;
    private Date updated;
    
    //Con id
    public WheatherData(int recordId, String city, String country, double latitude, double longitude, Date date,
                       int temperatureCelsius, int humidityPercent, double precipitationMm, int windSpeedKmh,
                       String weatherCondition, String forecast, Date updated){
        
        this.recordId = recordId;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.temperatureCelsius = temperatureCelsius;
        this.humidityPercent = humidityPercent;
        this.precipitationMm = precipitationMm;
        this.windSpeedKmh = windSpeedKmh;
        this.weatherCondition = weatherCondition;
        this.forecast = forecast;
        this.updated = updated;
    }
    
    //Sin id
    public WheatherData(String city, String country, double latitude, double longitude, Date date,
                       int temperatureCelsius, int humidityPercent, double precipitationMm, int windSpeedKmh,
                       String weatherCondition, String forecast, Date updated){
        
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.temperatureCelsius = temperatureCelsius;
        this.humidityPercent = humidityPercent;
        this.precipitationMm = precipitationMm;
        this.windSpeedKmh = windSpeedKmh;
        this.weatherCondition = weatherCondition;
        this.forecast = forecast;
        this.updated = updated;
    }

    /**
     * @return the recordId
     */
    public int getRecordId() {
        return recordId;
    }

    /**
     * @param recordId the recordId to set
     */
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the temperatureCelsius
     */
    public int getTemperatureCelsius() {
        return temperatureCelsius;
    }

    /**
     * @param temperatureCelsius the temperatureCelsius to set
     */
    public void setTemperatureCelsius(int temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    /**
     * @return the humidityPercent
     */
    public int getHumidityPercent() {
        return humidityPercent;
    }

    /**
     * @param humidityPercent the humidityPercent to set
     */
    public void setHumidityPercent(int humidityPercent) {
        this.humidityPercent = humidityPercent;
    }

    /**
     * @return the precipitationMm
     */
    public double getPrecipitationMm() {
        return precipitationMm;
    }

    /**
     * @param precipitationMm the precipitationMm to set
     */
    public void setPrecipitationMm(double precipitationMm) {
        this.precipitationMm = precipitationMm;
    }

    /**
     * @return the windSpeedKmh
     */
    public int getWindSpeedKmh() {
        return windSpeedKmh;
    }

    /**
     * @param windSpeedKmh the windSpeedKmh to set
     */
    public void setWindSpeedKmh(int windSpeedKmh) {
        this.windSpeedKmh = windSpeedKmh;
    }

    /**
     * @return the weatherCondition
     */
    public String getWeatherCondition() {
        return weatherCondition;
    }

    /**
     * @param weatherCondition the weatherCondition to set
     */
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    /**
     * @return the forecast
     */
    public String getForecast() {
        return forecast;
    }

    /**
     * @param forecast the forecast to set
     */
    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    /**
     * @return the updated
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    @Override
    public String toString() {
        return "Información Meteorológica de " + city + " {" +
                "recordId=" + recordId +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", date=" + date +
                ", temperatureCelsius=" + temperatureCelsius +
                ", humidityPercent=" + humidityPercent +
                ", precipitationMm=" + precipitationMm +
                ", windSpeedKmh=" + windSpeedKmh +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", forecast='" + forecast + '\'' +
                ", updated=" + updated +
                '}';
    }
}
