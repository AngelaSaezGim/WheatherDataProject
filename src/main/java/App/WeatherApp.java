/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

/**
 *
 * @author angsaegim
 */
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

//SQL Connection
import Connections.DataAccessManagerSQL;
//MongoDb Connection
import Connections.DataAccessManagerMongoDB;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;

public class WeatherApp {

    /* EN INGLÉS */
    private static Scanner tcl = new Scanner(System.in);

    //Opciones del menú principal
    private enum MainMenuOptions {
        QUERY_CHANGEBD, QUERY_MANAGERBD, EXIT
    };

    private enum ManagerMenuOption {
        QUERY_INSERT, QUERY_DELETE, QUERY_LIST, QUERY_SYNCRONIZED, QUERY_UPSERT, QUERY_UPLOAD_XML_Mdb,
        EXIT_MANAGER
    };

    // Por defecto iniciamos con SQL al inicio (ya que es el que quiero usar para gestionar UserInfo)
    private static boolean isUsingMongoDB = false;

    private static DataAccessManagerSQL managerSQL = null;
    private static DataAccessManagerMongoDB managerMongoDB = null;

    public static void main(String[] args) {

        //Desactivamos logs de MongoDB
        Utilities.disableMongoLogging();

        //Luego la implementaré
        //userWelcome();
        //Elegimos la primera base de datos a la que nos conectamos (mongoDB o SQL)
        //Usaremos variable isUsingMongoDB (es MUY importante en el menú)
        //Si nos conectamos a SQL se quedará en false
        //Si nos conectamos a MongoDB se quedará en true
        chooseDatabase();

        MainMenuOptions opcionElegida = null;
        do {
            // Mostrar qué base de datos estamos gestionando
            // Mostrar qué base de datos estamos gestionando y el número de elementos
            mostrarEstadoBaseDeDatos();  // Aquí llamas al método que muestra el estado

            printMainMenu();
            opcionElegida = readChoice();

            switch (opcionElegida) {
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
                        managerSQL = DataAccessManagerSQL.getInstance(); // Usamos la instancia existente del Singleton
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
                    break;
                case QUERY_MANAGERBD:
                    ManagerMenuOption opcionElegida2 = null;
                    do {
                        // Mostrar qué base de datos estamos gestionando y el número de elementos
                        mostrarEstadoBaseDeDatos();  // Aquí llamas al método que muestra el estado
                        printManagerMenu();
                        opcionElegida2 = readChoice2();
                        switch (opcionElegida2) {
                            case QUERY_INSERT:
                                break;
                            case QUERY_DELETE:
                                break;
                            case QUERY_LIST:
                                break;
                            case QUERY_SYNCRONIZED:
                                break;
                            case QUERY_UPSERT:
                                if (isUsingMongoDB) {
                                    //realizarUpsert(mongoDatabase);
                                } else {
                                    System.out.println("La operación Upsert solo está disponible en MongoDB.");
                                }
                                break;
                            case QUERY_UPLOAD_XML_Mdb:
                                // Opción de subir XML
                                if (isUsingMongoDB) {
                                    System.out.println("Subiendo archivo XML a MongoDB...");
                                    //subirXMLAMongoDB(managerMongoDB);
                                } else {
                                    System.out.println("La opción de subir XML solo está disponible para MongoDB.");
                                }
                                break;
                            case EXIT_MANAGER:
                        }
                    } while (opcionElegida2 != ManagerMenuOption.EXIT_MANAGER);
                    break;
                case EXIT:
            }
        } while (opcionElegida != MainMenuOptions.EXIT);

        System.out.println("Salimos del programa, adios!!");
    }

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

    private static MainMenuOptions readChoice() {
        try {
            int choiceInt = Integer.valueOf(tcl.nextLine());
            return MainMenuOptions.values()[choiceInt - 1];
        } catch (RuntimeException re) {
            System.out.println("Opción inválida... Inténtelo otra vez.");
            return readChoice();
        }
    }

    private static ManagerMenuOption readChoice2() {
        try {
            int choiceInt = Integer.valueOf(tcl.nextLine());
            return ManagerMenuOption.values()[choiceInt - 1];
        } catch (RuntimeException re) {
            System.out.println("Opción inválida... Inténtelo otra vez.");
            return readChoice2();
        }
    }
 // Método para cambiar a MongoDB (sin abrir nueva conexión, ya gestionado por Singleton)
    private static DataAccessManagerMongoDB changeConnectionMongoDb() {
        // Usar la instancia singleton para MongoDB
        return DataAccessManagerMongoDB.getInstance();
    }

    // Método para cambiar a SQL (sin abrir nueva conexión, ya gestionado por Singleton)
    private static DataAccessManagerSQL changeConnectionSQL() {
        // Usar la instancia singleton para SQL
        return DataAccessManagerSQL.getInstance();
    }


    private static void chooseDatabase() {
        System.out.println("Antes de nada, ¿a qué base de datos de WeatherData quieres conectarte? (Luego podrás cambiarla)");
        System.out.println("1) MongoDB");
        System.out.println("2) SQL");
        int choice = Integer.parseInt(tcl.nextLine());

        switch (choice) {
            case 1:
                changeConnectionMongoDb();
                isUsingMongoDB = true;
                break;
            case 2:
                changeConnectionSQL();
                isUsingMongoDB = false;
                break;
            default:
                System.out.println("Dato no válido, elija 1 o 2. Vuelve a intentar.");
                chooseDatabase(); // Recursividad para volver a pedir la elección
                break;
        }
    }

    //*** SE REPRODUCIRAN SIEMPRE Y NOS DARAN EL ESTADO DE NUESTRAS BD*//
    private static void mostrarEstadoBaseDeDatos() {

        System.out.println("Actualmente estamos gestionando la base de datos: "
                + (isUsingMongoDB ? "MongoDB" : "SQL"));
        /*
    if (isUsingMongoDB) {
        // Contamos los elementos en MongoDB
        long countMongoDB = contarElementosMongoDB(managerMongoDB.getDatabase());
        System.out.println("Número de elementos en MongoDB: " + countMongoDB);
    } else {
        try {
            // Contamos los elementos en SQL
            int countSQL = contarElementosSQL(weatherConnection);
            System.out.println("Número de elementos en SQL: " + countSQL);
        } catch (SQLException e) {
            System.err.println("Error al contar los elementos en SQL: " + e.getMessage());
        }*/
    }

    public static void userWelcome() {

        //INSTANCIA CONEXIÓN SQL (uso singleton) - SOLO LA ABRIMOS Y CERRAMOS PARA YSER INFO
        try ( DataAccessManagerSQL managerSQL = DataAccessManagerSQL.getInstance()) {
            // Conexión a WeatherData (por defecto SQL)
            System.out.println("Conexión a WeatherData con SQL  exitosa.");

            ////-----------------------USERINFO -------------------------------------
            // Conexión a UserInfo (Solo la usamos aqui)
            try ( Connection userInfoConnection = managerSQL.getConnection("UserInfo")) {
                System.out.println("Conexión a UserInfo exitosa.");
                MetodosMenu.solicitarUsers(managerSQL);
                MetodosMenu.validarUsuario(managerSQL); //Te valida e imprime el mensaje de Binvenida
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
        }
        MetodosMenu.esperarIntro();
    }
}
