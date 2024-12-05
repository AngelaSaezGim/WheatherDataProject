/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import Connections.DataAccessManagerMongoDB;
import Objects.WeatherData;
import java.sql.SQLException;
import java.util.List;
import static App.GeneralMethodsMenu.tcl;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import java.sql.Date;
import java.util.Scanner;

/**
 *
 * @author angel
 */
public class WeatherDataMongoDBMenu {

    //METODOS ESTATICOS QUE PUEDO USAR EN MIS MENUS
    /**
     * *************************** FUNCIONES LANZADAS - DATA ACCESS MANAGER
     * ****************************
     */
    //***************************** WEATHER DATA OPERATIONS *****************************/
    //***** SELECT *//////
    //2 - Ver Los Datos Meteorológicos //
    public static void viewWeatherDataMongoDB(DataAccessManagerMongoDB managerMongoDB) {
        List<WeatherData> allWeatherDatas = managerMongoDB.loadAllWeatherDataMongo();
        WeatherDataSQLMenu.displayWeatherData(allWeatherDatas);
    }

    public static void requestWeatherDataMongo(DataAccessManagerMongoDB managerMongoDB) {

        System.out.print("- Quieres ver los datos neteorológicos actuales de la base de datos?\n 1 - sí ");
        int input = tcl.nextInt();

        tcl.nextLine();
        if (input == 1) {
            viewWeatherDataMongoDB(managerMongoDB);
        }
    }
    //**************************** LISTAR  *******************************//////

    public static void listWeatherDataMongoDB(DataAccessManagerMongoDB managerMongoDB) throws SQLException {

        int option = WeatherDataSQLMenu.printFilterListMenu();

        switch (option) {
            case 1:
                listByCityMongo(managerMongoDB);
                break;
            case 2:
                listByMultipleCitiesMongo(managerMongoDB);
                break;
            case 3:
                listAllAlphabeticallyMongo(managerMongoDB);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo.");
                break;
        }
    }

    private static void listByCityMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {
        // Filtrar por city
        System.out.println("Filtramos por UNA ciudad");
        System.out.print("Ingrese el nombre de la ciudad: ");
        String city = tcl.nextLine();
        List<WeatherData> weatherDataByCity = managerMongoDB.loadWeatherDataByCityMongo(city);
        if (weatherDataByCity != null && !weatherDataByCity.isEmpty()) {
            GeneralMethodsMenu.displayPaginatedData(weatherDataByCity);
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
        }
    }

    private static void listByMultipleCitiesMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {
        // Filtrar por varias citiesList
        System.out.println("Filtramos por VARIAS ciudades");
        System.out.print("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid): ");
        String cityInput = tcl.nextLine();
        String[] citiesList = cityInput.split(",");
        for (String city : citiesList) {
            city = city.trim(); // POR CADA CIUDAD - FIltro concreto de ciudad
            List<WeatherData> weatherDataByCity = managerMongoDB.loadWeatherDataByCityMongo(city);
            if (weatherDataByCity != null && !weatherDataByCity.isEmpty()) {
                GeneralMethodsMenu.displayPaginatedData(weatherDataByCity);
            } else {
                System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
            }
        }
    }

    private static void listAllAlphabeticallyMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {
        System.out.println("Filtramos todas las ciudades Alfabéticamente");
        // Mostrar todos los datos ordenados alfabéticamente por city
        List<WeatherData> allWeatherData = managerMongoDB.loadAllWeatherDataMongo();
        if (allWeatherData != null && !allWeatherData.isEmpty()) {
            // SE ORDENA POR CIUDAD ALFABETICAMENTE
            allWeatherData.sort((wd1, wd2) -> wd1.getCity().compareToIgnoreCase(wd2.getCity()));
            GeneralMethodsMenu.displayPaginatedData(allWeatherData);
        } else {
            System.out.println("No hay datos meteorológicos disponibles.");
        }
    }

    //**************************** LISTAR  *******************************//////
    //**************************** DELETE  *******************************//////
    public static void deleteWeatherDataMenuMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {

        int option = WeatherDataSQLMenu.printFilterDeleteMenu();

        switch (option) {
            case 1:
                // Borrar todos sin confirmación
                int deletedRecords = managerMongoDB.deleteAllWeatherDataMongo();
                System.out.println("Todos los datos han sido borrados.");
                System.out.println("Se borraron " + deletedRecords + " registros");
                break;
            case 2:
                int optionFilters = WeatherDataSQLMenu.printFilterDeleteMenu2();
                switch (optionFilters) {
                    case 1:
                        System.out.println("Borraremos uno/varios registros de UNA Ciudad ");
                        requestWeatherDataMongo(managerMongoDB);
                        filtreDeleteByCityMongo(managerMongoDB);
                        break;
                    case 2:
                        System.out.println("Borraremos registros de VARIAS Ciudades");
                        requestWeatherDataMongo(managerMongoDB);
                        filtreDeleteByMultipleCitiesMongo(managerMongoDB);
                        break;
                    case 3:
                        confirmAndDeleteAllMongo(managerMongoDB);
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

    private static void filtreDeleteByCityMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {

        System.out.println("Dime la ciudad de la que borraremos sus registros");
        String city = tcl.nextLine();

        List<WeatherData> weatherDataByCity = managerMongoDB.loadWeatherDataByCityMongo(city);
        System.out.println("Datos que borraremos;");
        WeatherDataSQLMenu.displayWeatherData(weatherDataByCity);

        if (weatherDataByCity != null && !weatherDataByCity.isEmpty()) {
            int deletedRecords = managerMongoDB.deleteWeatherDataByListMongo(weatherDataByCity);
            System.out.println("Todos los datos han sido borrados.");
            System.out.println("Se borraron " + deletedRecords + " registros");
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
        }
    }

    private static void filtreDeleteByMultipleCitiesMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {

        System.out.println("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid): ");

        String citiesInput = tcl.nextLine();
        String[] citiesList = citiesInput.split(",");

        for (String city : citiesList) {
            city = city.trim();
            List<WeatherData> weatherDataByCities = managerMongoDB.loadWeatherDataByCityMongo(city);
            System.out.println("Datos que borraremos;");
            WeatherDataSQLMenu.displayWeatherData(weatherDataByCities);

            if (weatherDataByCities != null && !weatherDataByCities.isEmpty()) {
                int deleteRecords = managerMongoDB.deleteWeatherDataByListMongo(weatherDataByCities);
                System.out.println("Todos los datos han sido borrados.");
                System.out.println("Se borraron " + deleteRecords + " registros");
            } else {
                System.out.println("No se encontraron datos meteorológicos para la ciudad " + city);
            }
        }

    }

    private static void confirmAndDeleteAllMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {

        System.out.println("¿Está seguro de que desea borrar TODOS los datos? (sí/no): ");
        String confirmation = tcl.nextLine().trim().toLowerCase();

        if (confirmation.equals("sí") || confirmation.equals("si")) {
            int deletedRecords = managerMongoDB.deleteAllWeatherDataMongo();
            System.out.println("Todos los datos han sido borrados.");
            System.out.println("Se borraron " + deletedRecords + " registros");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
    //**************************** DELETE  *******************************//////

    //************************** INSERTAR ************************************/
    public static void insertWeatherDataMongo(DataAccessManagerMongoDB managerMongoDB) throws SQLException {

        System.out.println("Inserción de datos meteorológicos. Escriba '0' como ciudad para terminar.");
        boolean continueInsertion = true;
        requestWeatherDataMongo(managerMongoDB);

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
            if (managerMongoDB.existsWeatherDataMongo(recordIdInput)) {
                System.out.println("Error: El identificador de registro " + recordIdInput + " ya existe. Intente con otro.");
                continue;
            }

            try {
                // Intentar convertir el recordIdInput a un int
                int recordId = Integer.parseInt(recordIdInput);
                newWeatherData.setRecordId(recordId);  // Suponiendo que recordId en WeatherData es de tipo int
            } catch (NumberFormatException e) {
                System.out.println("Error: El identificador debe ser un número entero. Intente nuevamente.");
                continue;
            }

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
                managerMongoDB.insertarWeatherDataMongo(newWeatherData);
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

    //************************** INSERTAR ************************************/
    //* UPSERT *////
    public static void upsertWeatherDataMongo(DataAccessManagerMongoDB managerMongoDB) {

        try {
            WeatherData newWeatherData = new WeatherData();

            // Solicitar al usuario los datos necesarios
            System.out.print("Introduce el ID del registro: ");
            int recordId = tcl.nextInt();
            tcl.nextLine();
            newWeatherData.setRecordId(recordId);

            System.out.print("Ingrese la ciudad: ");
            String city = tcl.nextLine();
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

            tcl.nextLine();

            System.out.print("Ingrese la fecha (YYYY-MM-DD): ");
            try {
                String dateInput = tcl.nextLine();
                newWeatherData.setDate(dateInput.isBlank() ? null : Date.valueOf(dateInput));
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Fecha inválida. Se requiere el formato YYYY-MM-DD.");
                newWeatherData.setDate(null);
            }
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

            // Usar la fecha actual como la última actualización
            Date updated = new Date(System.currentTimeMillis());
            System.out.println("Predicción actualizada con fecha actual: " + updated);
            newWeatherData.setUpdated(updated);
            tcl.nextLine();

            // Llamar al método upsert del DAO
            UpdateResult result = managerMongoDB.upsertWeatherDataMongo(newWeatherData);

            if (result != null) {
                if (result.getUpsertedId() != null) {
                    System.out.println("Nuevo documento insertado con ID: " + result.getUpsertedId());
                } else if (result.getModifiedCount() > 0) {
                    System.out.println("Documento actualizado.");
                } else {
                    System.out.println("No se realizaron cambios.");
                }
            } else {
                System.out.println("Error durante la operación.");
            }

        } catch (Exception e) {
            System.err.println("Error al capturar los datos: " + e.getMessage());
        } finally {
            tcl.close();
        }
    }
}
