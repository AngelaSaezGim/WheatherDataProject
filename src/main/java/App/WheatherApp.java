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
import Connections.DataAccessManagerMongo;

import java.sql.SQLException;

public class WheatherApp {

    private static Scanner tcl = new Scanner(System.in);

    //Opciones del menú principal
    private enum MenuOption {
        QUERY_ALL, QUERY_BY_CODE, QUERY_CLIENTES_INSERT, QUERY_CLIENTES_UPDATE, QUERY_CLIENTES_DELETE, EXIT
    };

    public static void main(String[] args) {

        MenuOption opcionElegida = null;

        try ( DataAccessManagerSQL dam = DataAccessManagerSQL.getInstance()) {
            
            System.out.println("Benvingut XXXXXX, a la teua ciutat ZZZZZZZ hi ha una temperatura de X graus centígrads.");

            do {
                System.out.println("Menu de Opciones");
                System.out.println("Usando: " + "ejmongoDB");
                System.out.println("Elementos en la base de datos: " + "ejmongoDB");
                printOptions();
                opcionElegida = readChoice();

                switch (opcionElegida) {
                    case QUERY_ALL:
                        selectConnectionSQL(dam);
                        DataAccessManagerMongo.connectToMongoClient();
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

        } catch (SQLException sqe) {
            System.out.println("Error de acceso a datos: " + sqe.getMessage());
        }
        System.out.println("\n\n  ADIOS !!!! \n\n");
        tcl.close();
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

    public static void selectConnectionSQL(DataAccessManagerSQL dam) throws SQLException {
        System.out.println("a");
    }

    public static String doLogin() throws SQLException {
        
        System.out.println("Procedemos al login");
        System.out.println("Dime tu DNI;");
        tcl.nextLine();
        
        return
    }
}
