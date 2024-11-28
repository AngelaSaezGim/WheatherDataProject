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

public class WheatherApp {

    private static Scanner tcl = new Scanner(System.in);

    //Opciones del menú principal
    private enum MenuOption {
        QUERY_ALL, QUERY_BY_CODE, QUERY_CLIENTES_INSERT, QUERY_CLIENTES_UPDATE, QUERY_CLIENTES_DELETE, EXIT
    };

    public static void main(String[] args) {

        //Desactivamos logs de MongoDB
        Utilities.disableMongoLogging();
        System.out.println("Logger de MongoDB deshabilitado. Continuando con la aplicación...");

        // Obtener la instancia del DataAccessManagerMongoDB (Singleton)
        DataAccessManagerMongoDB managerMongoDB = DataAccessManagerMongoDB.getInstance();
        // Obtener la base de datos de MongoDB
        MongoDatabase mongoDatabase = managerMongoDB.getDatabase();
        if (mongoDatabase != null) {
            System.out.println("Conexión exitosa a MongoDB. Base de datos: " + mongoDatabase.getName());
        } else {
            System.err.println("Error al conectar a la base de datos MongoDB.");
        }

        //INSTANCIA CONEXIÓN SQL (uso singleton)
        try ( DataAccessManagerSQL managerSQL = DataAccessManagerSQL.getInstance()) {

            // Conexión a WeatherData
            try ( Connection weatherConnection = managerSQL.getConnection("WeatherData")) {
                System.out.println("Conexión a WeatherData exitosa.");
                // Conexión a UserInfo
                try ( Connection userInfoConnection = managerSQL.getConnection("UserInfo")) {
                    System.out.println("Conexión a UserInfo exitosa.");
                    System.out.println("Benvingut XXXXXX, a la teua ciutat ZZZZZZZ hi ha una temperatura de X graus centígrads.");
                } catch (Exception e) {
                    System.err.println("Error en UserInfo: " + e.getMessage());
                }

                MenuOption opcionElegida = null;
                do {
                    System.out.println("Menu de Opciones");
                    printOptions();
                    opcionElegida = readChoice();

                    switch (opcionElegida) {
                        case QUERY_ALL:
                            DataAccessManagerMongoDB.getInstance();
                            break;
                        case QUERY_BY_CODE:
                            break;
                        case QUERY_CLIENTES_INSERT:
                            break;
                        case QUERY_CLIENTES_UPDATE:
                            break;
                        case QUERY_CLIENTES_DELETE:
                            break;
                        case EXIT:
                    }
                } while (opcionElegida != MenuOption.EXIT);

            } catch (Exception e) {
                System.err.println("Error en WeatherData: " + e.getMessage());
            }

        }
    }

    private static MenuOption readChoice() {
        try {
            int choiceInt = Integer.valueOf(tcl.nextLine());
            return MenuOption.values()[choiceInt - 1];
        } catch (RuntimeException re) {
            System.out.println("Opción inválida... Inténtelo otra vez.");
            return readChoice();
        }
    }

    private static void printOptions() {
        StringBuilder sb = new StringBuilder()
                .append("\n\n\nElija una opción:\n")
                .append("\t1) Cambiar Base de datos (SQL/MongoDB) \n")
                .append("\t2)\n")
                .append("\t3)\n")
                .append("\t4)\n")
                .append("Opción: ");
        System.out.print(sb.toString());
    }
}
