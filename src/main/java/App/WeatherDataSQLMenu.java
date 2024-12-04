/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import static App.GeneralMethodsMenu.requestDNI;
import static App.GeneralMethodsMenu.tcl;
import Connections.DataAccessManagerMongoDB;
import Connections.DataAccessManagerSQL;
import Objects.UserInfo;
import Objects.WeatherData;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Scanner;

/**
 * Menu de Manejo Weather Data en SQL
 *
 * @author angel
 */
public class WeatherDataSQLMenu {

    //***************************** FUNCIONES LANZADAS - DATA ACCESS MANAGER *****************************/
    //***************************** WEATHER DATA OPERATIONS *****************************/
    //***** SELECT *//////
    //2 - Ver Los Datos Meteorológicos //
    public static void viewWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {
        List<WeatherData> allWeatherDatas = managerSQL.loadAllWeatherDataSQL();
        displayWeatherData(allWeatherDatas);
    }

    //Complementario a viewWeatherDataSQL y viewWeatherDataMongoDB
    /////PONER EN OTRO SITIO
    public static void displayWeatherData(List<WeatherData> weatherDataList) {
        if (weatherDataList == null || weatherDataList.isEmpty()) {
            System.out.println("No hay registros...");
            return;
        }

        for (WeatherData weatherData : weatherDataList) {
            System.out.println("\t" + weatherData);
        }
        System.out.println();
    }
     /////PONER EN OTRO SITIO

    /*SOLICITAR*/
    public static void requestWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.print("- Quieres ver los datos neteorológicos actuales de la base de datos?\n 1 - sí ");
        int input = tcl.nextInt();

        tcl.nextLine();
        if (input == 1) {
            viewWeatherDataSQL(managerSQL);
        }
    }

    //**************************** LISTAR  *******************************//////
    
    //Poner en otro sitio
    public static int printFilterListMenu(){
        System.out.println("¿Cómo deseas listar los datos meteorológicos?");
        System.out.println("1 - Por ciudad");
        System.out.println("2 - Por varias ciudades (separadas por coma)");
        System.out.println("3 - ALL - Mostrar todos los datos (ordenados alfabéticamente por ciudad)");
        System.out.println("4 - Atrás");
        System.out.print("Elige una opción: ");

        int option = tcl.nextInt();
        tcl.nextLine();
        
        return option;
    }
    //Pooner en otro sitio

    public static void listWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        int option = printFilterListMenu();

        switch (option) {
            case 1:
                listByCitySQL(managerSQL);
                break;
            case 2:
                listByMultipleCitiesSQl(managerSQL);
                break;
            case 3:
                listAllAlphabeticallySQL(managerSQL);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo.");
                break;
        }
    }

    private static void listByCitySQL(DataAccessManagerSQL managerSQL) throws SQLException {

        // Filtrar por city
        System.out.println("Filtramos por UNA ciudad");
        System.out.print("Ingrese el nombre de la ciudad: ");
        String city = tcl.nextLine();
        List<WeatherData> weatherDataByCity = managerSQL.loadWeatherDataByCitySQL(city);
        if (weatherDataByCity != null && !weatherDataByCity.isEmpty()) {
            GeneralMethodsMenu.displayPaginatedData(weatherDataByCity);
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
        }
    }

    private static void listByMultipleCitiesSQl(DataAccessManagerSQL managerSQL) throws SQLException {
        // Filtrar por varias citiesList
        System.out.println("Filtramos por VARIAS ciudades");
        System.out.print("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid): ");
        String cityInput = tcl.nextLine();
        String[] citiesList = cityInput.split(",");
        for (String city : citiesList) {
            city = city.trim();
            List<WeatherData> weatherDataByCity = managerSQL.loadWeatherDataByCitySQL(city);
            if (weatherDataByCity != null && !weatherDataByCity.isEmpty()) {
                GeneralMethodsMenu.displayPaginatedData(weatherDataByCity);
            } else {
                System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
            }
        }
    }

    private static void listAllAlphabeticallySQL(DataAccessManagerSQL managerSQL) throws SQLException {
        System.out.println("Filtramos todas las ciudades Alfabéticamente");
        // Mostrar todos los datos ordenados alfabéticamente por city
        List<WeatherData> allWeatherData = managerSQL.loadAllWeatherDataSQL();
        if (allWeatherData != null && !allWeatherData.isEmpty()) {
            // SE ORDENA POR CIUDAD ALFABETICAMENTE
            allWeatherData.sort((wd1, wd2) -> wd1.getCity().compareToIgnoreCase(wd2.getCity()));
            GeneralMethodsMenu.displayPaginatedData(allWeatherData);
        } else {
            System.out.println("No hay datos meteorológicos disponibles.");
        }
    }

    //**************************** DELETE  *******************************//////
        //Poner en otro sitio
    public static int printFilterDeleteMenu(){
        System.out.println("Seleccione una opción para borrar datos:");
        System.out.println("1 - Borrar TODOS los datos (sin confirmación) *PELIGRO*");
        System.out.println("2 - Borrar usando FILTROS");
        System.out.println("3 - Atrás");
        System.out.print("Elige una opción: ");

        int option = tcl.nextInt();
        tcl.nextLine();
        
        return option;
    }
    //Pooner en otro sitio
    
    public static int printFilterDeleteMenu2() {
        System.out.println("Borramos usando filtros; ");
        System.out.println("1 - Borrar por ciudad");
        System.out.println("2 - Borrar por varias ciudades (separadas por coma)");
        System.out.println("3 - ALL - CON CONFIRMACIÓN");
        System.out.println("4 - Atrás");
        System.out.print("Elige una opción: ");

        int option = tcl.nextInt();
        tcl.nextLine();
        
        return option;
    }
    
    public static void deleteWeatherDataMenuSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        int option = printFilterDeleteMenu();

        switch (option) {
            case 1:
                // Borrar todos sin confirmación
                int deletedRecords = managerSQL.deleteAllWeatherDataSQL();
                System.out.println("Todos los datos han sido borrados.");
                System.out.println("Se borraron " + deletedRecords + " registros");
                break;
            case 2:
                int optionFilters = printFilterDeleteMenu2();
                
                switch (optionFilters) {
                    case 1:
                        System.out.println("Borraremos uno/varios registros de UNA Ciudad ");
                        requestWeatherDataSQL(managerSQL);
                        filtreDeleteByCitySQL(managerSQL);
                        break;
                    case 2:
                        System.out.println("Borraremos registros de VARIAS Ciudades");
                        requestWeatherDataSQL(managerSQL);
                        filtreDeleteByMultipleCitiesSQL(managerSQL);
                        break;
                    case 3:
                        confirmAndDeleteAllSQL(managerSQL);
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

    private static void filtreDeleteByCitySQL(DataAccessManagerSQL managerSQL) throws SQLException {

        int deletedRecords;
        System.out.println("Dime la ciudad de la que borraremos sus registros");
        String city = tcl.nextLine();

        List<WeatherData> weatherDataByCity = managerSQL.loadWeatherDataByCitySQL(city);
        System.out.println("Datos que borraremos;");
        displayWeatherData(weatherDataByCity);

        if (weatherDataByCity != null && !weatherDataByCity.isEmpty()) {
            deletedRecords = managerSQL.deleteWeatherDataByListSQL(weatherDataByCity);
            System.out.println("Todos los datos han sido borrados.");
            System.out.println("Se borraron " + deletedRecords + " registros");
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
        }
    }

    private static void filtreDeleteByMultipleCitiesSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.println("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid): ");

        String citiesInput = tcl.nextLine();
        String[] citiesList = citiesInput.split(",");

        for (String city : citiesList) {
            city = city.trim();
            List<WeatherData> weatherDataByCities = managerSQL.loadWeatherDataByCitySQL(city);
            System.out.println("Datos que borraremos;");
            displayWeatherData(weatherDataByCities);

            if (weatherDataByCities != null && !weatherDataByCities.isEmpty()) {
                int deleteRecords = managerSQL.deleteWeatherDataByListSQL(weatherDataByCities);
                System.out.println("Todos los datos han sido borrados.");
                System.out.println("Se borraron " + deleteRecords + " registros");
            } else {
                System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
            }
        }

    }

    private static void confirmAndDeleteAllSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.println("¿Está seguro de que desea borrar TODOS los datos? (sí/no): ");
        String confirmation = tcl.nextLine().trim().toLowerCase();

        if (confirmation.equals("sí") || confirmation.equals("si")) {
            int deletedRecords = managerSQL.deleteAllWeatherDataSQL();
            System.out.println("Todos los datos han sido borrados.");
            System.out.println("Se borraron " + deletedRecords + " registros");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    //************************** INSERTAR ************************************/
    public static void insertarWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.println("Inserción de datos meteorológicos. Escriba '0' como ciudad para terminar.");
        boolean continueInsertion = true;
        requestWeatherDataSQL(managerSQL);

        while (continueInsertion) {
            // Crear un nuevo objeto WeatherData
            WeatherData newWeatherData = new WeatherData();

            System.out.print("Ingrese el identificador del registro: ");
            String recordIdInput = tcl.nextLine();
            if (recordIdInput.isBlank()) {
                System.out.println("El identificador es obligatorio. Intente nuevamente.");
                continue;
            }
            // Verificar si el recordId ya existe
            if (managerSQL.existsWeatherDataSQL(recordIdInput)) {
                System.out.println("Error: El identificador de registro " + recordIdInput + " ya existe. Intente con otro.");
                continue;
            }

            newWeatherData.setRecordId(Integer.parseInt(recordIdInput));

            System.out.print("Ingrese la ciudad: ");
            String city = tcl.nextLine();
            if (city.equals("0")) {
                System.out.println("Finalizando inserción.");
                continueInsertion = false;
                continue;
            }
            newWeatherData.setCity(city.isBlank() ? null : city);

            System.out.print("Ingrese el país: ");
            String country = tcl.nextLine();
            newWeatherData.setCountry(country.isBlank() ? null : country);

            System.out.print("Ingrese la latitud: ");
            String latitudeInput = tcl.nextLine();
            newWeatherData.setLatitude(latitudeInput.isBlank() ? 0.0 : Double.parseDouble(latitudeInput));

            System.out.print("Ingrese la longitud: ");
            String longitudeInput = tcl.nextLine();
            newWeatherData.setLongitude(longitudeInput.isBlank() ? 0.0 : Double.parseDouble(longitudeInput));

            System.out.print("Ingrese la fecha (YYYY-MM-DD): ");
            String dateInput = tcl.nextLine();
            newWeatherData.setDate(dateInput.isBlank() ? null : Date.valueOf(dateInput));

            System.out.print("Ingrese la temperatura (°C): ");
            String tempInput = tcl.nextLine();
            newWeatherData.setTemperatureCelsius(tempInput.isBlank() ? 0 : Integer.parseInt(tempInput));

            System.out.print("Ingrese el porcentaje de humedad: ");
            String humidityInput = tcl.nextLine();
            newWeatherData.setHumidityPercent(humidityInput.isBlank() ? 0 : Integer.parseInt(humidityInput));

            System.out.print("Ingrese la precipitación (mm): ");
            String precipitationInput = tcl.nextLine();
            newWeatherData.setPrecipitationMm(precipitationInput.isBlank() ? 0.0 : Double.parseDouble(precipitationInput));

            System.out.print("Ingrese la velocidad del viento (km/h): ");
            String windSpeedInput = tcl.nextLine();
            newWeatherData.setWindSpeedKmh(windSpeedInput.isBlank() ? 0 : Integer.parseInt(windSpeedInput));

            System.out.print("Ingrese la condición del clima: ");
            String weatherCondition = tcl.nextLine();
            newWeatherData.setWeatherCondition(weatherCondition.isBlank() ? null : weatherCondition);

            System.out.print("Ingrese el pronóstico: ");
            String forecast = tcl.nextLine();
            newWeatherData.setForecast(forecast.isBlank() ? null : forecast);

            // HE HECHO QUE SE UTILIZE LA FECHA ACTUAL COMO UPDATED (Tanto en MongoDB como en SQL)
            Date updated = new Date(System.currentTimeMillis());
            System.out.println("Predicción actualizada con fecha actual: " + updated);
            newWeatherData.setUpdated(updated);

            //insertar en la base de datos
            try {
                managerSQL.insertarWeatherDataSQL(newWeatherData);
                System.out.println("Datos agregados con éxito:");
                System.out.println(newWeatherData);
            } catch (SQLException e) {
                System.out.println("Error al insertar los datos: " + e.getMessage());
                throw e;
            }

            System.out.print("¿Desea agregar más datos? (si/no): ");
            String continueInput = tcl.nextLine();
            continueInsertion = continueInput.equalsIgnoreCase("si");
        }
    }
}
