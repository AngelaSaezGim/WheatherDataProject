/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

/**
 *
 * @author angsaegim
 */
import java.util.Scanner;

//SQL Connection
import Connections.DataAccessManagerSQL;
//MongoDb Connection
import Connections.DataAccessManagerMongoDB;
import java.sql.Connection;
import java.sql.SQLException;

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

        //Luego la implementaré
        //userWelcome();
        //Elegimos la primera base de datos a la que nos conectamos (mongoDB o SQL)
        //variable isUsingMongoDB (es MUY importante en el menú)
        chooseDatabase();

        MainMenuOptions opcionElegida = null;
        do {
            // Mostrar qué base de datos estamos gestionando y el número de elementos
            mostrarEstadoBaseDeDatos(); 

            MetodosMenu.printMainMenu();
            opcionElegida = MetodosMenu.readChoice();

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
                    MetodosMenu.esperarIntro();
                    break;
                case QUERY_MANAGERBD:
                    ManagerMenuOption opcionElegida2 = null;
                    do {
                        // Mostrar qué base de datos estamos gestionando y el número de elementos
                        mostrarEstadoBaseDeDatos();  // método que muestra el estado
                        MetodosMenu.printManagerMenu();
                        opcionElegida2 = MetodosMenu.readChoice2();
                        switch (opcionElegida2) {
                            case QUERY_INSERT:
                                if (isUsingMongoDB) {
                                    System.out.println("Insertar en MongoDB");
                                    managerMongoDB.insertWeatherDataMongoDB();
                                } else {
                                    System.out.println("Insertar en SQL");
                                    MetodosBDMenu.insertarWeatherDataSQL(managerSQL);
                                }
                                break;
                            case QUERY_DELETE:
                                if (isUsingMongoDB) {
                                    System.out.println("Borrar en MongoDB");
                                    managerMongoDB.deleteWeatherData();
                                    //
                                } else {
                                    System.out.println("Borrar en SQL");
                                    MetodosBDMenu.deleteWeatherData(managerSQL);
                                }
                                MetodosMenu.esperarIntro();
                                break;
                            case QUERY_LIST:
                                System.out.println("Listar Datos");
                                if (isUsingMongoDB) {
                                    System.out.println("Listar en MongoDB");
                                    managerMongoDB.listarWeatherDataMongoDB();
                                } else {
                                    System.out.println("Listar en SQL");
                                    MetodosBDMenu.listarWeatherDataSQL(managerSQL);
                                }
                                MetodosMenu.esperarIntro();
                                break;
                            case QUERY_SYNCRONIZED:
                                System.out.println("Sincronizar");
                                MetodosBDMenu.sincronizarBDs(managerMongoDB, managerSQL);
                                MetodosMenu.esperarIntro();
                                break;
                            case QUERY_UPSERT:
                                if (isUsingMongoDB) {
                                    System.out.println("Operación UPSERT de un elemento dado");
                                } else {
                                    System.out.println("La operación Upsert solo está disponible en MongoDB.");
                                }
                                MetodosMenu.esperarIntro();
                                break;
                            case QUERY_UPLOAD_XML_Mdb:
                                if (isUsingMongoDB) {
                                    System.out.println("Subir XML - Importar items (import.xml) \n El formato será de tu elección");
                                    //subirXMLAMongoDB(managerMongoDB);
                                } else {
                                    System.out.println("La opción de subir XML solo está disponible para MongoDB.");
                                }
                                MetodosMenu.esperarIntro();
                                break;
                            case EXIT_MANAGER:
                                System.out.println("Saliendo del gestor...");
                                break;
                            default:
                                System.out.println("Opción no válida.");
                                break;
                        }
                    } while (opcionElegida2 != ManagerMenuOption.EXIT_MANAGER);
                    break;
                case EXIT:
            }
        } while (opcionElegida != MainMenuOptions.EXIT);

        System.out.println("Salimos del programa, adios!!");
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
                MetodosBDMenu.solicitarUsersSQL(managerSQL);
                MetodosBDMenu.validarUsuarioSQL(managerSQL); //Te valida e imprime el mensaje de Binvenida
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
            MetodosMenu.esperarIntro();
        }
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
    protected static void mostrarEstadoBaseDeDatos() throws SQLException {

        System.out.println("Actualmente estamos gestionando la base de datos: "
                + (isUsingMongoDB ? "MongoDB" : "SQL"));
        if (isUsingMongoDB) {
            long count = managerMongoDB.countWeatherData();
            System.out.println("Número de elementos en la colección MongoDB: " + count);
        } else {
            int count = managerSQL.countWeatherDataSQL();
            System.out.println("Número de elementos en la tabla SQL: " + count);
        }
    }
}
