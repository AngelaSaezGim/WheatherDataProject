/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import Connections.DataAccessManagerMongoDB;
import Connections.DataAccessManagerSQL;
import Objects.WeatherData;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author angel
 */
public class GeneralMethodsMenu {

    static Scanner tcl = new Scanner(System.in);

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

    //SINCRONIZAR AMBAS BASES DE DATOS
    public static void syncronizeBDs(DataAccessManagerMongoDB managerMongoDB, DataAccessManagerSQL managerSQL) {
        try {
            // Inicializar AMBOS gestores (para que no de null al coger los datos)
            if (managerMongoDB == null) {
                managerMongoDB = DataAccessManagerMongoDB.getInstance();
            }
            if (managerSQL == null) {
                managerSQL = DataAccessManagerSQL.getInstance();
            }

            // Número de elementos que tienen cada BD
            long mongoCount = managerMongoDB.countWeatherDataMongo();
            int sqlCount = managerSQL.countWeatherDataSQL();
            System.out.println("Nº Elementos Mongo: " + mongoCount);
            System.out.println("Nº Elementos SQL: " + sqlCount);

            // Comprobación de cuál tiene más
            if (mongoCount == sqlCount) {
                System.out.println("Ambas bases de datos tienen el mismo número de elementos. No es necesario sincronizar.");
            } else {
                String userInput = "";

                do {
                    System.out.println("¿Qué base de datos quieres actualizar ? (Sincronizar) M = MongoDB / S = SQL");
                    userInput = tcl.nextLine().toUpperCase();  // Convertimos la entrada a mayúsculas para evitar problemas con la comparación

                    //CARGAMOS TODOS LOS DATOS DE LA OTRA
                    //BORRAMOS TODO
                    //INSERTAMOS TODOS
                    if (userInput.equals("M")) {
                        System.out.println("Actualizando MongoDB con los datos de SQL...");
                        var dataSQL = managerSQL.loadAllWeatherDataSQL(); // Obtener todos los datos de SQL
                        long deletedMongo = managerMongoDB.deleteAllWeatherDataMongo();  // Eliminar los datos actuales en MongoDB
                        System.out.println("Datos eliminados de MongoDB: " + deletedMongo);
                        
                        List<WeatherData> dataMongo = new ArrayList<>();
                        for (WeatherData weatherDataSQL : dataSQL) {
                            // Convertir cada objeto SQL en un objeto WeatherData
                            WeatherData weatherData = new WeatherData(
                                    weatherDataSQL.getRecordId(),
                                    weatherDataSQL.getCity(),
                                    weatherDataSQL.getCountry(),
                                    weatherDataSQL.getLatitude(),
                                    weatherDataSQL.getLongitude(),
                                    weatherDataSQL.getDate(),
                                    weatherDataSQL.getTemperatureCelsius(),
                                    weatherDataSQL.getHumidityPercent(),
                                    weatherDataSQL.getPrecipitationMm(),
                                    weatherDataSQL.getWindSpeedKmh(),
                                    weatherDataSQL.getWeatherCondition(),
                                    weatherDataSQL.getForecast(),
                                    weatherDataSQL.getUpdated()
                            );
                            dataMongo.add(weatherData);
                        }

                        // Insertar todos los datos de una vez en MongoDB
                        managerMongoDB.insertarWeatherDataMongo(dataMongo);

                        System.out.println("Base de datos MongoDB ahora sincronizada.");
                        WeatherDataMongoDBMenu.viewWeatherDataMongoDB(managerMongoDB);

                    } else if (userInput.equals("S")) {
                        System.out.println("Actualizando SQL con los datos de MongoDB...");
                        var dataMongo = managerMongoDB.loadAllWeatherDataMongo(); // Obtener todos los datos de MongoDB
                        List<WeatherData> dataSQL = new ArrayList<>();

                        // Convertir los datos de MongoDB a SQL
                        for (WeatherData weatherData : dataMongo) {
                            WeatherData weatherDataSQL = new WeatherData(
                                    weatherData.getRecordId(),
                                    weatherData.getCity(),
                                    weatherData.getCountry(),
                                    weatherData.getLatitude(),
                                    weatherData.getLongitude(),
                                    weatherData.getDate(),
                                    weatherData.getTemperatureCelsius(),
                                    weatherData.getHumidityPercent(),
                                    weatherData.getPrecipitationMm(),
                                    weatherData.getWindSpeedKmh(),
                                    weatherData.getWeatherCondition(),
                                    weatherData.getForecast(),
                                    weatherData.getUpdated()
                            );
                            dataSQL.add(weatherDataSQL);
                        }

                        // Insertar todos los datos en SQL
                        managerSQL.insertarWeatherDataSQL(dataSQL);
                        WeatherDataSQLMenu.viewWeatherDataSQL(managerSQL);
                    } else {
                        System.out.println("Entrada no válida. Por favor, elija 'M' para MongoDB o 'S' para SQL.");
                    }
                } while (!userInput.equals("M") && !userInput.equals("S"));

                System.out.println("Sincronización completada.");
                mongoCount = managerMongoDB.countWeatherDataMongo();
                sqlCount = managerSQL.countWeatherDataSQL();
                System.out.println("Nº Elementos Mongo tras sincronización: " + mongoCount);
                System.out.println("Nº Elementos SQL tras sincronización: " + sqlCount);

                GeneralMethodsMenu.waitIntro();
            }
        } catch (Exception e) {
            System.err.println("Error durante la sincronización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //PRINTS MENU
    public static void printMainMenu() {
        System.out.println("Elija una opción:");
        System.out.println("\t1) Cambiar Base de datos (SQL/MongoDB)");
        System.out.println("\t2) Gestionar BD");
        System.out.println("\t3) Salir del programa");
    }

    public static void printManagerMenu() {
        System.out.println("Selecciona una opción para gestionar la base de datos:");
        System.out.println("\t1. Insertar datos a la BD");
        System.out.println("\t2. Borrar elementos de la BD");
        System.out.println("\t3. Listar elementos");
        System.out.println("\t4. Sincronizar Bases de Datos WeatherData");
        System.out.println("\t5. Realizar Upsert (SOLO MONGODB)");
        System.out.println("\t6. Subir XML (SOLO MONGODB)");
        System.out.println("\t7. Salir del menú de gestión");
    }

    protected static WeatherApp.MainMenuOptions readChoice() {
        try {
            int choiceInt = Integer.valueOf(tcl.nextLine());
            return WeatherApp.MainMenuOptions.values()[choiceInt - 1];
        } catch (RuntimeException re) {
            System.out.println("Opción inválida... Inténtelo otra vez.");
            return readChoice();
        }
    }

    protected static WeatherApp.ManagerMenuOption readChoice2() {
        try {
            int choiceInt = Integer.valueOf(tcl.nextLine());
            return WeatherApp.ManagerMenuOption.values()[choiceInt - 1];
        } catch (RuntimeException re) {
            System.out.println("Opción inválida... Inténtelo otra vez.");
            return readChoice2();
        }
    }

    //REQUESTS Y UTILIDADES
    public static void waitIntro() {
        System.out.println("Presione Enter para continuar...");
        tcl.nextLine();
    }

    // Función para solicitar el DNI al usuario
    public static String requestDNI() {
        System.out.print("Por favor, ingrese su DNI: ");
        return readNotEmptyString();
    }

    private static String readNotEmptyString() {
        String input = null;
        //prevenir texto vacío
        while (input == null || input.length() == 0) {
            input = tcl.nextLine();
            if (input.length() == 0) {
                System.out.println("escriba algo...");
            }
        }
        return input;
    }

    protected static void displayWeatherDataDetailed(WeatherData weatherData) {
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

    public static void displayPaginatedData(List<WeatherData> weatherDataList) {
        final int ITEMS_PER_PAGE = 3;
        int totalPages = (int) Math.ceil((double) weatherDataList.size() / ITEMS_PER_PAGE);

        Scanner scanner = new Scanner(System.in);
        int currentPage = 0;

        while (currentPage < totalPages) {
            int start = currentPage * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, weatherDataList.size());

            // Mostrar los datos de la página (datos weatherDataList)
            for (int i = start; i < end; i++) {
                GeneralMethodsMenu.displayWeatherDataDetailed(weatherDataList.get(i)); // Método para imprimir los datos de un solo WeatherData
            }

            System.out.println("\nPágina " + (currentPage + 1) + " de " + totalPages);
            System.out.println("Presiona 'Enter' para ver más o escribe 'salir' para salir.");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) {
                break;
            } else {
                currentPage++;
            }
        }
    }

}
