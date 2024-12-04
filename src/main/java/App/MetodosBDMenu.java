/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import static App.MetodosMenu.requestDNI;
import static App.MetodosMenu.tcl;
import Connections.DataAccessManagerMongoDB;
import Connections.DataAccessManagerSQL;
import Objects.UserInfo;
import Objects.WeatherData;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.bson.Document;

/**
 *
 * @author angel
 */
public class MetodosBDMenu {

    //TRANSICIÓN, SINCRONICACIÓN DE NUESTRAS BD
    // Método para cambiar a MongoDB (sin abrir nueva conexión, ya gestionado por Singleton)
    protected static DataAccessManagerMongoDB changeConnectionMongoDb() {
        System.out.println("Inicializando conexión MongoDB...");
        // Usar la instancia singleton para MongoDB
        return DataAccessManagerMongoDB.getInstance();
    }

    // Método para cambiar a SQL (sin abrir nueva conexión, ya gestionado por Singleton)
    protected static DataAccessManagerSQL changeConnectionSQL() {
        System.out.println("Inicializando conexión SQL...");
        // Usar la instancia singleton para SQL
        return DataAccessManagerSQL.getInstance();
    }

    public static void sincronizarBDs(DataAccessManagerMongoDB managerMongoDB, DataAccessManagerSQL managerSQL) {
        try {
            // Inicializar AMBOS gestores (para que no de null al coger los datos)
            if (managerMongoDB == null) {
                managerMongoDB = DataAccessManagerMongoDB.getInstance();
            }
            if (managerSQL == null) {
                managerSQL = DataAccessManagerSQL.getInstance();
            }

            // Número de elementos que tienen cada BD
            long mongoCount = managerMongoDB.countWeatherData();
            int sqlCount = managerSQL.countWeatherDataSQL();
            System.out.println("Nº Elementos Mongo: " + mongoCount);
            System.out.println("Nº Elementos SQL: " + sqlCount);

            // Comprobación de cuál tiene más
            if (mongoCount == sqlCount) {
                System.out.println("Ambas bases de datos tienen el mismo número de elementos. No es necesario sincronizar.");
            } else {
                String userInput = "";

                do {
                    System.out.println("¿Qué base de datos quieres actualizar? M = MongoDB / S = SQL");
                    userInput = tcl.nextLine().toUpperCase();  // Convertimos la entrada a mayúsculas para evitar problemas con la comparación

                    //CARGAMOS TODOS LOS DATOS DE LA OTRA
                    //BORRAMOS TODO
                    //INSERTAMOS TODOS
                    if (userInput.equals("M")) {
                        System.out.println("Actualizando MongoDB con los datos de SQL...");
                        var dataSQL = managerSQL.loadAllWeatherDataSQL(); // Obtener TODOS los datos de SQL
                        //Dejar limpia la BD de Mongo para poder insertar
                        long deletedMongo = managerMongoDB.deleteAllWeatherDataMongoDB();  // Eliminar datos en MongoDB
                        System.out.println("Datos eliminados de MongoDB: " + deletedMongo);
                        
                        managerMongoDB.insertWeatherDataMongoDB(dataSQL);
                        //Insertar EN MONGODB con parametro los datos que tenemos de toda la bd de SQL (dataSQL)
                        System.out.println("Base de datos ahora de MongoDB.");
                        managerMongoDB.printWeatherData();
                    } else if (userInput.equals("S")) {
                        System.out.println("Actualizando SQL  con los datos de MongoDB...");
                        var dataMongo = managerMongoDB.loadAllWeatherDataMongoDB(); // Obtener TODOS los datos de MongoDB
                        //Dejar limpia la BD de SQL para poder insertar
                        int deletedSQL = managerSQL.deleteAllWeatherDataSQL(); // Eliminar datos en SQL
                        System.out.println("Datos eliminados de SQL: " + deletedSQL);
                       //Insertar en SQL con los parametros de los datos que tenemos de toda la bd de MongoDB (dataMongo)
                       // Insertar en SQL los datos cargados desde MongoDB
                       //insertarWeatherDataSQL(WeatherData weatherData)
                        managerSQL.insertarWeatherDataSQL(dataMongo);  // Insertar en SQL
                       
                        System.out.println("Base de datos ahora de SQL.");
                        verWeatherDataSQL(managerSQL);
                    } else {
                        System.out.println("Entrada no válida. Por favor, elija 'M' para MongoDB o 'S' para SQL.");
                    }
                } while (!userInput.equals("M") && !userInput.equals("S"));

                System.out.println("Sincronización completada.");
                mongoCount = managerMongoDB.countWeatherData();
                sqlCount = managerSQL.countWeatherDataSQL();
                System.out.println("Nº Elementos Mongo tras sincronización: " + mongoCount);
                System.out.println("Nº Elementos SQL tras sincronización: " + sqlCount);

                MetodosMenu.esperarIntro();
            }
        } catch (Exception e) {
            System.err.println("Error durante la sincronización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // SQL
    //2 - Cargar Usuarios por DNI
    //DEVUELVE OBJETO USUARIO CON ESE DNI (MOSTRAR POR DNI...)
    public static boolean searchUsersByDNISQL(DataAccessManagerSQL managerSQL) throws SQLException {
        String dniUser = requestDNI();

        UserInfo userFilteredByCode = managerSQL.loadUsersByDNISQL(dniUser);
        if (userFilteredByCode != null) {
            // Llamar al método para mostrar el mensaje de bienvenida
            mensajeBienvenidaConTemperaturaSQL(userFilteredByCode, managerSQL);
            return true;
        } else {
            System.out.println("No se encontró usuario con el DNI especificado.");
            return false;
        }
    }

    public static void validarUsuarioSQL(DataAccessManagerSQL managerSQL) {
        boolean isValid = false;
        while (!isValid) {
            try {
                isValid = searchUsersByDNISQL(managerSQL); // Función que valida el DNI
                if (!isValid) {
                    System.out.println("DNI no válido. Inténtelo de nuevo.");
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    // Método para mostrar la temperatura de la ciudad (ultima temperatura registrada)
    public static void mensajeBienvenidaConTemperaturaSQL(UserInfo userFilteredByCode, DataAccessManagerSQL managerSQL) throws SQLException {
        if (userFilteredByCode != null) {
            String userName = userFilteredByCode.getName();
            String userCity = userFilteredByCode.getCity();

            //Com que en la BD proporcionada PODRIA donar-se el cas de tindre diversos registres per la mateixa ciutat
            // Obtener los datos meteorológicos de la ciudad, que pueden ser varios
            List<WeatherData> weatherDataList = managerSQL.loadWeatherDataByCitySQL(userCity);

            if (weatherDataList != null && !weatherDataList.isEmpty()) {
                // Última temperatura registrada (la más reciente)
                WeatherData lastWeatherData = weatherDataList.stream()
                        .max(Comparator.comparing(WeatherData::getDate))
                        .orElse(null); // Si no hay datos, devuelve null

                if (lastWeatherData != null) {
                    double lastTemperature = lastWeatherData.getTemperatureCelsius();
                    System.out.println("Benvingut " + userName + ", a la teua ciutat " + userCity
                            + " la última temperatura registrada va ser de " + lastTemperature + " graus centígrads.");
                } else {
                    System.out.println("No se pudo obtener la última temperatura registrada para " + userCity);
                }
            } else {
                System.out.println("No se pudo obtener la temperatura para " + userCity);
            }
        }
    }

    //***************************** FUNCIONES LANZADAS - DATA ACCESS MANAGER *****************************/
    //****************************** CRUD - SQL - WeatherDATA ++++++++++++++++*****************************/
    //***** SELECT *//////
    //1 - Ver Los Usuarios//
    public static void verUserInfoSQL(DataAccessManagerSQL managerSQL) throws SQLException {
        List<UserInfo> allUsers = managerSQL.loadAllUsersSQL();
        printUserInfoSQL(allUsers);
    }

    //Complementario a verUserInfoSQL
    public static void printUserInfoSQL(List<UserInfo> users) {
        if (users == null || users.isEmpty()) {
            System.out.println("No hay registros...");
            return;
        }

        for (UserInfo user : users) {
            System.out.println("\t" + user);
        }
        System.out.println();
    }

    //2 - Ver Los Datos Meteorológicos //
    public static void verWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {
        List<WeatherData> allWeatherDatas = managerSQL.loadAllWeatherDataSQL();
        printWeatherDataSQL(allWeatherDatas);
    }

    //Complementario a verWeatherDataSQL
    public static void printWeatherDataSQL(List<WeatherData> weatherDatas) {
        if (weatherDatas == null || weatherDatas.isEmpty()) {
            System.out.println("No hay registros...");
            return;
        }

        for (WeatherData weatherData : weatherDatas) {
            System.out.println("\t" + weatherData);
        }
        System.out.println();
    }

    /*SOLICITAR*/
    public static void solicitarUsersSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.print("- Quieres ver los usuarios actuales de la base de datos?\n 1 - sí ");
        int respuesta = tcl.nextInt();
        tcl.nextLine();  // Limpiar el buffer del scanner
        if (respuesta == 1) {
            verUserInfoSQL(managerSQL);
        }
    }

    public static void solicitarWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.print("- Quieres ver los datos neteorológicos actuales de la base de datos?\n 1 - sí ");
        int respuesta = tcl.nextInt();
        tcl.nextLine();  // Limpiar el buffer del scanner
        if (respuesta == 1) {
            verWeatherDataSQL(managerSQL);
        }
    }

    //**************************** LISTAR  *******************************//////
    private static void printWeatherDataCompleteSQL(WeatherData weatherData) {
        System.out.println("---------------------------------------------------");
        System.out.println("---  ID: " + weatherData.getRecordId() + "  ---");
        System.out.println("---------------------------------------------------");
        System.out.println("Ciudad: " + weatherData.getCity());
        System.out.println("País: " + weatherData.getCountry());
        System.out.println("Latitud: " + weatherData.getLatitude());
        System.out.println("Longitud: " + weatherData.getLongitude());
        System.out.println("Fecha: " + weatherData.getDate());
        System.out.println("Temperatura: " + weatherData.getTemperatureCelsius() + "°C");
        System.out.println("Humedad: " + weatherData.getHumidityPercent() + "%");
        System.out.println("Precipitación: " + weatherData.getPrecipitationMm() + " mm");
        System.out.println("Velocidad del Viento: " + weatherData.getWindSpeedKmh() + " km/h");
        System.out.println("Condición Climática: " + weatherData.getWeatherCondition());
        System.out.println("Pronóstico: " + weatherData.getForecast());
        System.out.println("Última Actualización: " + weatherData.getUpdated());
        System.out.println("---------------------------------------------------");
    }

    private static void mostrarDatosPaginados(List<WeatherData> weatherData) {
        final int ITEMS_POR_PAGINA = 3;
        int totalPaginas = (int) Math.ceil((double) weatherData.size() / ITEMS_POR_PAGINA);

        Scanner scanner = new Scanner(System.in);
        int paginaActual = 0;

        while (paginaActual < totalPaginas) {
            int inicio = paginaActual * ITEMS_POR_PAGINA;
            int fin = Math.min(inicio + ITEMS_POR_PAGINA, weatherData.size());

            // Mostrar los datos de la página (datos weatherData)
            for (int i = inicio; i < fin; i++) {
                WeatherData wd = weatherData.get(i);
                printWeatherDataCompleteSQL(wd); // Método para imprimir los datos de un solo WeatherData
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

    public static void listarWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.println("¿Cómo deseas listar los datos meteorológicos?");
        System.out.println("1 - Por ciudad");
        System.out.println("2 - Por varias ciudades (separadas por coma)");
        System.out.println("3 - ALL - Mostrar todos los datos (ordenados alfabéticamente por ciudad)");
        System.out.println("4 - Atrás");
        System.out.print("Elige una opción: ");

        int opcion = tcl.nextInt();
        tcl.nextLine();

        switch (opcion) {
            case 1:
                listarPorCiudad(managerSQL);
                break;
            case 2:
                listarPorVariasCiudades(managerSQL);
                break;
            case 3:
                listarTodosAlfabeticamente(managerSQL);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo.");
                break;
        }
    }

    private static void listarPorCiudad(DataAccessManagerSQL managerSQL) throws SQLException {

        // Filtrar por ciudad
        System.out.println("Filtramos por UNA ciudad");
        System.out.print("Ingrese el nombre de la ciudad: ");
        String ciudad = tcl.nextLine();
        List<WeatherData> weatherDataPorCiudad = managerSQL.loadWeatherDataByCitySQL(ciudad);
        if (weatherDataPorCiudad != null && !weatherDataPorCiudad.isEmpty()) {
            mostrarDatosPaginados(weatherDataPorCiudad);
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad " + ciudad);
        }
    }

    private static void listarPorVariasCiudades(DataAccessManagerSQL managerSQL) throws SQLException {
        // Filtrar por varias ciudades
        System.out.println("Filtramos por VARIAS ciudades");
        System.out.print("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid): ");
        String ciudadesInput = tcl.nextLine();
        String[] ciudades = ciudadesInput.split(",");
        for (String ciudadInput : ciudades) {
            ciudadInput = ciudadInput.trim();
            List<WeatherData> weatherDataPorCiudades = managerSQL.loadWeatherDataByCitySQL(ciudadInput);
            if (weatherDataPorCiudades != null && !weatherDataPorCiudades.isEmpty()) {
                mostrarDatosPaginados(weatherDataPorCiudades);
            } else {
                System.out.println("No se encontraron datos meteorológicos para la ciudad " + ciudadInput);
            }
        }
    }

    private static void listarTodosAlfabeticamente(DataAccessManagerSQL managerSQL) throws SQLException {
        System.out.println("Filtramos todas las ciudades Alfabéticamente");
        // Mostrar todos los datos ordenados alfabéticamente por ciudad
        List<WeatherData> allWeatherDatas = managerSQL.loadAllWeatherDataSQL();
        if (allWeatherDatas != null && !allWeatherDatas.isEmpty()) {
            // SE ORDENA POR CIUDAD ALFABETICAMENTE
            allWeatherDatas.sort((wd1, wd2) -> wd1.getCity().compareToIgnoreCase(wd2.getCity()));
            mostrarDatosPaginados(allWeatherDatas);
        } else {
            System.out.println("No hay datos meteorológicos disponibles.");
        }
    }

    //**************************** DELETE  *******************************//////
    public static void deleteWeatherData(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.println("Seleccione una opción para borrar datos:");
        System.out.println("1 - Borrar TODOS los datos (sin confirmación) *PELIGRO*");
        System.out.println("2 - Borrar usando FILTROS");
        System.out.println("3 - Atrás");
        System.out.print("Elige una opción: ");
        int opcion = tcl.nextInt();
        tcl.nextLine();

        switch (opcion) {

            case 1:
                // Borrar todos sin confirmación
                int deleteTables = 0;
                deleteTables = managerSQL.deleteAllWeatherDataSQL();
                System.out.println("Todos los datos han sido borrados.");
                System.out.println("Se borraron " + deleteTables + " registros");
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
                        solicitarWeatherDataSQL(managerSQL);
                        filtreDeleteByCiudad(managerSQL);
                        break;
                    case 2:
                        System.out.println("Borraremos registros de VARIAS Ciudades");
                        solicitarWeatherDataSQL(managerSQL);
                        filtreDeleteVariasCiudades(managerSQL);
                        break;
                    case 3:
                        filtreConfirmarYBorrarALL(managerSQL);
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

    private static void filtreDeleteByCiudad(DataAccessManagerSQL managerSQL) throws SQLException {

        int deleteTables;
        System.out.println("Dime la ciudad de la que borraremos sus registros");
        String ciudad = tcl.nextLine();

        List<WeatherData> weatherDataPorCiudad = managerSQL.loadWeatherDataByCitySQL(ciudad);
        System.out.println("Datos que borraremos;");
        printWeatherDataSQL(weatherDataPorCiudad);

        if (weatherDataPorCiudad != null && !weatherDataPorCiudad.isEmpty()) {
            deleteTables = managerSQL.deleteWeatherDataByListSQL(weatherDataPorCiudad);
            System.out.println("Todos los datos han sido borrados.");
            System.out.println("Se borraron " + deleteTables + " registros");
        } else {
            System.out.println("No se encontraron datos meteorológicos para la ciudad " + ciudad);
        }
    }

    private static void filtreDeleteVariasCiudades(DataAccessManagerSQL managerSQL) throws SQLException {

        int deleteTables;
        System.out.println("Ingrese las ciudades separadas por coma (por ejemplo: Barcelona, Valencia, Madrid): ");

        String ciudadesInput = tcl.nextLine();
        String[] ciudades = ciudadesInput.split(",");

        for (String ciudadInput : ciudades) {
            ciudadInput = ciudadInput.trim();
            List<WeatherData> weatherDataPorCiudades = managerSQL.loadWeatherDataByCitySQL(ciudadInput);
            System.out.println("Datos que borraremos;");
            printWeatherDataSQL(weatherDataPorCiudades);

            if (weatherDataPorCiudades != null && !weatherDataPorCiudades.isEmpty()) {
                deleteTables = managerSQL.deleteWeatherDataByListSQL(weatherDataPorCiudades);
                System.out.println("Todos los datos han sido borrados.");
                System.out.println("Se borraron " + deleteTables + " registros");
            } else {
                System.out.println("No se encontraron datos meteorológicos para la ciudad " + ciudadInput);
            }
        }

    }

    private static void filtreConfirmarYBorrarALL(DataAccessManagerSQL managerSQL) throws SQLException {

        int deleteTables;
        System.out.println("¿Está seguro de que desea borrar TODOS los datos? (sí/no): ");
        String confirmacion = tcl.nextLine().trim().toLowerCase();

        if (confirmacion.equals("sí") || confirmacion.equals("si")) {
            deleteTables = managerSQL.deleteAllWeatherDataSQL();
            System.out.println("Todos los datos han sido borrados.");
            System.out.println("Se borraron " + deleteTables + " registros");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    //************************** INSERTAR ************************************/
    public static void insertarWeatherDataSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.println("Inserción de datos meteorológicos. Escriba '0' como ciudad para terminar.");
        boolean continuar = true;
        solicitarWeatherDataSQL(managerSQL);

        while (continuar) {
            // Crear un nuevo objeto WeatherData
            WeatherData newWeatherData = new WeatherData();

            System.out.print("Ingrese el identificador del registro: ");
            String recordIdInput = tcl.nextLine();
            if (recordIdInput.isBlank()) {
                System.out.println("El identificador es obligatorio. Intente nuevamente.");
                continue;
            }
            // Verificar si el recordId ya existe
            if (managerSQL.existsWeatherData(recordIdInput)) {
                System.out.println("Error: El identificador de registro " + recordIdInput + " ya existe. Intente con otro.");
                continue;
            }

            newWeatherData.setRecordId(Integer.parseInt(recordIdInput));

            System.out.print("Ingrese la ciudad: ");
            String city = tcl.nextLine();
            if (city.equals("0")) {
                System.out.println("Finalizando inserción.");
                continuar = false;
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

            System.out.print("¿Desea agregar más datos? (sí/no): ");
            String continueInput = tcl.nextLine();
            continuar = continueInput.equalsIgnoreCase("sí");
        }
    }
    
   /////////////////INSERTAR EN MONGODB//////////////////////////////
    
    public static List<WeatherData> insertMongoDBObject(){
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

            String updated = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
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
    }
}


}
