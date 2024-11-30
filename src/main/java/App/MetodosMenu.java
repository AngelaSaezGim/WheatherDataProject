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

    // Función para solicitar el DNI al usuario
    public static String requestDNI() {
        System.out.print("Por favor, ingrese su DNI: ");
        return readNotEmptyString();
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

        System.out.print("- Quieres ver los usuarios actuales de la base de datos?\n 1 - sí ");
        int respuesta = tcl.nextInt();
        tcl.nextLine();  // Limpiar el buffer del scanner
        if (respuesta == 1) {
            verUserInfo(managerSQL);
        }
    }

    //2 - Cargar Usuarios por DNI
    //DEVUELVE OBJETO USUARIO CON ESE DNI (MOSTRAR POR DNI...)
    public static boolean searchUsersByDNI(DataAccessManagerSQL managerSQL) throws SQLException {
        String dniUser = requestDNI();

        UserInfo userFilteredByCode = managerSQL.loadUsersByDNI(dniUser);
        if (userFilteredByCode != null) {
            // Llamar al método para mostrar el mensaje de bienvenida
            mensajeBienvenidaConTemperatura(userFilteredByCode, managerSQL);
            return true;
        } else {
            System.out.println("No se encontró usuario con el DNI especificado.");
            return false;
        }
    }
    
    public static void validarUsuario(DataAccessManagerSQL managerSQL) {
        boolean isValid = false;
        while (!isValid) {
            try {
                isValid = searchUsersByDNI(managerSQL); // Función que valida el DNI
                if (!isValid) {
                    System.out.println("DNI no válido. Inténtelo de nuevo.");
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    //***************************** SQL - WeatherDATA *****************************/
    // Método para mostrar la temperatura de la ciudad
    
    public static void mensajeBienvenidaConTemperatura(UserInfo userFilteredByCode, DataAccessManagerSQL managerSQL) throws SQLException {
    if (userFilteredByCode != null) {
        String userName = userFilteredByCode.getName();
        String userCity = userFilteredByCode.getCity();
        
        // Obtener la temperatura de la ciudad - llamamos a WeatherData
        WeatherData weatherData = managerSQL.loadWeatherDataByCity(userCity);
        if (weatherData != null) {
            double temperature = weatherData.getTemperatureCelsius();
            System.out.println("Benvingut " + userName + ", a la teua ciutat " + userCity + " hi ha una temperatura de " + temperature + " graus centígrads.");
        } else {
            System.out.println("No se pudo obtener la temperatura para " + userCity);
        }
    }
}

}
