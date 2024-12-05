/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

/**
 *
 * @author angsaegim
 */
import static App.GeneralMethodsMenu.requestDNI;
import java.util.Scanner;

//SQL Connection
import Connections.DataAccessManagerSQL;
//MongoDb Connection
import Connections.DataAccessManagerMongoDB;
import Objects.UserInfo;
import Objects.WeatherData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.OptionalDouble;

public class WeatherApp {

    /* EN INGLÉS */
    private static Scanner tcl = new Scanner(System.in);

    //Opciones del menú principal
    protected enum MainMenuOptions {
        QUERY_CHANGEBD, QUERY_MANAGERBD, EXIT
    };

    protected enum ManagerMenuOption {
        QUERY_INSERT, QUERY_DELETE, QUERY_LIST, QUERY_SYNCRONIZED, QUERY_UPSERT, QUERY_UPLOAD_XML_Mdb,
        EXIT_MANAGER
    };

    private static boolean isUsingMongoDB = false;

    private static DataAccessManagerSQL managerSQL = null;
    private static DataAccessManagerMongoDB managerMongoDB = null;

    public static void main(String[] args) throws SQLException {

        //Desactivamos logs de MongoDB
        Utilities.disableMongoLogging();

        //PRIMERO SINCRONIZO TODO
        System.out.println("Sincronizando.....");
        GeneralMethodsMenu.syncronizeBDs(managerMongoDB, managerSQL);
        GeneralMethodsMenu.waitIntro();

        userWelcome();
        //Elegimos la primera base de datos a la que nos conectamos (mongoDB o SQL)
        //variable isUsingMongoDB (es MUY importante en el menú)
        chooseDatabase();

        MainMenuOptions ChoosenOption = null;
        do {
            // Mostrar qué base de datos estamos gestionando y el número de elementos
            showDBstate();

            GeneralMethodsMenu.printMainMenu();
            ChoosenOption = GeneralMethodsMenu.readChoice();

            switch (ChoosenOption) {
                case QUERY_CHANGEBD:
                    // Cambiar entre MongoDB y SQL
                    if (isUsingMongoDB) {
                        // Cerrar la conexión actual de MongoDB y cambiar a SQL
                        System.out.println("Cambiando a la base de datos SQL...");
                        if (managerMongoDB != null) {
                            managerMongoDB.close(); // Cerrar la conexión MongoD
                            System.out.println("Conexion a MongoDB cerrada");
                        }
                        // Conectar a SQL usando el Singleton -No necesitas abrir una nueva conexión cada vez.
                        managerSQL = DataAccessManagerSQL.getInstance(); // Usamos la instancia del Singleton
                        isUsingMongoDB = false;
                    } else {
                        // Cerrar la conexión actual de SQL y cambiar a MongoDB
                        System.out.println("Cambiando a la base de datos MongoDB...");
                        if (managerSQL != null) {
                            managerSQL.close(); // Cerrar la conexión SQL
                            System.out.println("Conexión a SQL cerrada");
                        }
                        managerMongoDB = DataAccessManagerMongoDB.getInstance(); // Usamos la instancia existente del Singleton
                        isUsingMongoDB = true;
                    }
                    GeneralMethodsMenu.waitIntro();
                    break;
                case QUERY_MANAGERBD:
                    ManagerMenuOption ChoosenOption2 = null;
                    do {
                        // Mostrar qué base de datos estamos gestionando y el número de elementos
                        showDBstate();  // método que muestra el estado
                        GeneralMethodsMenu.printManagerMenu();
                        ChoosenOption2 = GeneralMethodsMenu.readChoice2();
                        switch (ChoosenOption2) {
                            case QUERY_INSERT:
                                if (isUsingMongoDB) {
                                    System.out.println("Insertar en MongoDB");
                                    WeatherDataMongoDBMenu.insertWeatherDataMongo(managerMongoDB);
                                } else {
                                    System.out.println("Insertar en SQL");
                                    WeatherDataSQLMenu.insertWeatherDataSQL(managerSQL);
                                }
                                break;
                            case QUERY_DELETE:
                                if (isUsingMongoDB) {
                                    System.out.println("Borrar en MongoDB");
                                    WeatherDataMongoDBMenu.deleteWeatherDataMenuMongo(managerMongoDB);
                                    //
                                } else {
                                    System.out.println("Borrar en SQL");
                                    WeatherDataSQLMenu.deleteWeatherDataMenuSQL(managerSQL);
                                }
                                GeneralMethodsMenu.waitIntro();
                                break;
                            case QUERY_LIST:
                                System.out.println("Listar Datos");
                                if (isUsingMongoDB) {
                                    System.out.println("Listar en MongoDB");
                                    WeatherDataMongoDBMenu.listWeatherDataMongoDB(managerMongoDB);
                                } else {
                                    System.out.println("Listar en SQL");
                                    WeatherDataSQLMenu.listWeatherDataSQL(managerSQL);
                                }
                                GeneralMethodsMenu.waitIntro();
                                break;
                            case QUERY_SYNCRONIZED:
                                System.out.println("Sincronizar");
                                GeneralMethodsMenu.syncronizeBDs(managerMongoDB, managerSQL);
                                GeneralMethodsMenu.waitIntro();
                                break;
                            case QUERY_UPSERT:
                                if (isUsingMongoDB) {
                                    System.out.println("Operación UPSERT de un elemento dado");
                                     WeatherDataMongoDBMenu.upsertWeatherDataMongo(managerMongoDB);
                                     tcl.next();
                                } else {
                                    System.out.println("La operación Upsert solo está disponible en MongoDB.");
                                }
                                break;
                            case QUERY_UPLOAD_XML_Mdb:
                                if (isUsingMongoDB) {
                                    System.out.println("Subir XML - Importar items (import.xml) \n El formato será de tu elección");
                                    managerMongoDB.uploadXMLMongoDB();
                                } else {
                                    System.out.println("La opción de subir XML solo está disponible para MongoDB.");
                                }
                                GeneralMethodsMenu.waitIntro();
                                break;
                            case EXIT_MANAGER:
                                System.out.println("Saliendo del gestor...");
                                break;
                            default:
                                System.out.println("Opción no válida.");
                                break;
                        }
                    } while (ChoosenOption2 != ManagerMenuOption.EXIT_MANAGER);
                    break;

                case EXIT:
            }
        } while (ChoosenOption != MainMenuOptions.EXIT);

        System.out.println("Salimos del programa, adios!!");
    }

    private static void chooseDatabase() {
        System.out.println("Antes de nada, ¿a qué base de datos de WeatherData quieres conectarte? (Luego podrás cambiarla)");
        System.out.println("1) MongoDB");
        System.out.println("2) SQL");

        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.print("Elige una opción: ");
                int choice = Integer.parseInt(tcl.nextLine());
                switch (choice) {
                    case 1:
                        managerMongoDB = DataAccessManagerMongoDB.getInstance();
                        isUsingMongoDB = true;
                        validInput = true;
                        break;
                    case 2:
                        managerSQL = DataAccessManagerSQL.getInstance();
                        isUsingMongoDB = false;
                        validInput = true;
                        break;
                    default:
                        System.out.println("Dato no válido, elija 1 o 2. Inténtelo nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número (1 o 2).");
            }
        }
    }

    //*** SE REPRODUCIRAN SIEMPRE Y NOS DARAN EL ESTADO DE NUESTRAS BD*//
    protected static void showDBstate() throws SQLException {

        System.out.println("Actualmente estamos gestionando la base de datos: "
                + (isUsingMongoDB ? "MongoDB" : "SQL"));
        if (isUsingMongoDB) {
            long count = managerMongoDB.countWeatherDataMongo();
            System.out.println("Número de elementos en la colección MongoDB: " + count);
        } else {
            int count = managerSQL.countWeatherDataSQL();
            System.out.println("Número de elementos en la tabla SQL: " + count);
        }
    }

    //------------------------------ USUARIOS --------------------------------//
    public static void userWelcome() {

        //INSTANCIA CONEXIÓN SQL (uso singleton) - SOLO LA ABRIMOS Y CERRAMOS PARA YSER INFO
        try ( DataAccessManagerSQL managerSQL = DataAccessManagerSQL.getInstance()) {
            System.out.println("Conexión a WeatherDataSQL exitosa");
            ////-----------------------USERINFO -------------------------------------
            // Conexión a UserInfo (Solo la usamos aqui)
            try ( Connection userInfoConnection = managerSQL.getConnection("UserInfo")) {
                System.out.println("Conexión a UserInfo exitosa.");
                solicitarUsersSQL(managerSQL);
                searchUsersByDNISQL(managerSQL); //Te valida e imprime el mensaje de Binvenida
            } catch (Exception e) {
                System.err.println("Error en UserInfo: " + e.getMessage());
            } finally {
                System.out.println("Cerrada la conexión a UserInfo SQL");
            }
        } catch (Exception e) {
            System.err.println("Error en WeatherData: " + e.getMessage());
            ////-----------------------USERINFO -------------------------------------
        } finally {
            //no se cierra explicitamente ya que usamos autocloseable pero lo  marcamos.
            System.out.println("Cerrada la conexión con weaterData SQL para usuarios");
            GeneralMethodsMenu.waitIntro();
        }
    }

    public static void verUserInfoSQL(DataAccessManagerSQL managerSQL) throws SQLException {
        List<UserInfo> allUsers = managerSQL.loadAllUsersSQL();
        printUserInfoSQL(allUsers);
    }

    //Complementario a verUserInfoSQL
    public static void printUserInfoSQL(List<UserInfo> users) {
        for (UserInfo user : users) {
            System.out.println("\t" + user);
        }
        System.out.println();
    }

    public static void solicitarUsersSQL(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.print("- Quieres ver los usuarios actuales de la base de datos?\n 1 - sí ");
        int respuesta = tcl.nextInt();
        tcl.nextLine();  // Limpiar el buffer del scanner
        if (respuesta == 1) {
            verUserInfoSQL(managerSQL);
        }
    }
    //2 - Cargar Usuarios por DNI
    //DEVUELVE OBJETO USUARIO CON ESE DNI (MOSTRAR POR DNI...)

    public static boolean searchUsersByDNISQL(DataAccessManagerSQL managerSQL) throws SQLException {
        String dniUser;
        boolean continueSearch = true;  // Variable para controlar el bucle

        while (continueSearch) {
            dniUser = requestDNI();  // Solicitar el DNI

            UserInfo userFilteredByCode = managerSQL.loadUsersByDNISQL(dniUser);

            if (userFilteredByCode != null) {
                welcomeMessageTemperatureSQL(userFilteredByCode, managerSQL);
                return true;  // Salir del método si se encuentra un usuario
            } else {
                // Si no se encuentra el usuario, mostrar mensaje y continuar buscando
                System.out.println("No se encontró usuario con el DNI especificado. ");
                continueSearch = true;  // Continuar pidiendo el DNI
            }
        }

        return false;  // Si se ha salido del bucle (esto solo ocurriría si el DNI es válido)
    }

    // Método para mostrar la temperatura de la ciudad 
    // ULTIMA TEMPERATURA REGISTRADA
    public static void welcomeMessageTemperatureSQL(UserInfo userFilteredByCode, DataAccessManagerSQL managerSQL) throws SQLException {
        if (userFilteredByCode != null) {
            String userName = userFilteredByCode.getName();
            String userCity = userFilteredByCode.getCity();

            //Com que en la BD proporcionada PODRIA donar-se el cas de tindre diversos registres per la mateixa ciutat
            // Obtener los datos meteorológicos de la ciudad, QUE PUEDEN SER VARIOS
            List<WeatherData> weatherDataList = managerSQL.loadWeatherDataByCitySQL(userCity);

            if (weatherDataList != null && !weatherDataList.isEmpty()) {
                OptionalDouble optionalAverage = weatherDataList.stream()
                        .mapToDouble(WeatherData::getTemperatureCelsius) // Extrae temperaturas como double
                        .average(); // Calcula promedio

                if (optionalAverage.isPresent()) {
                    double averageTemperature = optionalAverage.getAsDouble();
                    System.out.println("Benvingut " + userName + ", a la teua ciutat " + userCity
                            + " la temperatura mitjana registrada és de " + averageTemperature + " graus centígrads.");
                } else {
                    System.out.println("No se pudo obtener la última temperatura registrada para " + userCity);
                }
            } else {
                System.out.println("No se pudo obtener la temperatura para " + userCity);
            }
        }
    }

}
