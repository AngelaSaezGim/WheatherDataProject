/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import Objects.UserInfo;
import Objects.WeatherData;

import Connections.DataAccessManagerSQL;

/**
 *
 * @author angel
 */
public class MetodosMenu {

    static Scanner tcl = new Scanner(System.in);

    public static void esperarIntro() {
        System.out.println("Presione Enter para continuar...");
        tcl.nextLine();
    }
    
    private static String requestContentLike() {
        System.out.print("Escriba el codigo a buscar; ");
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
     
    //***************************** FUNCIONES LANZADAS - DATA ACCESS MANAGER *****************************/
    //***************************** SQL *****************************/
    
    //1 - Ver Los Usuarios//
    public static void verUserInfo(DataAccessManagerSQL managerSQL) throws SQLException {
        List<UserInfo> allUsers = managerSQL.loadAllUsers();
        printUserInfo(allUsers);
    }

    //Complementario a verUserInfo
    public static void printUserInfo(List<UserInfo> users) {
        if (users == null || users.isEmpty()) {
            System.out.println("No hay registros...");
            return;
        }

        for (UserInfo user : users) {
            System.out.println("\t" + user);
        }
        System.out.println();
    }

    /*SOLICITAR*/
    public static void solicitarUsers(DataAccessManagerSQL managerSQL) throws SQLException {

        System.out.print("- Quieres ver los usuarios actuales de la base de datos?\n 1 - sí / 0 - no ");
        int respuesta = tcl.nextInt();
        if (respuesta == 1) {
            verUserInfo(managerSQL);
        }
    }
    
    //2 - Cargar Usuarios por DNI
    
    //DEVUELVE OBJETO USUARIO CON ESE DNI (MOSTRAR POR DNI...)
    public static void searchUsersByDNI(DataAccessManagerSQL managerSQL) throws SQLException {
        System.out.println("Vamos a filtar un usuario concreto por su DNI");
        solicitarUsers(managerSQL);
        String dniUser = requestContentLike();
        //List<UserInfo> usersFilteredByCode = managerSQL.loadUsersContaining(dniUser);
        //if (usersFilteredByCode != null) {
            //printUserInfo(usersFilteredByCode);
        //} else {
            System.out.println("No se encontró usuario con el DNI especificado.");
        }
    }

