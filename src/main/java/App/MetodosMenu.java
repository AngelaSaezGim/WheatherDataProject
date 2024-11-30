/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import java.util.Scanner;

/**
 *
 * @author angel
 */
public class MetodosMenu {

    static Scanner tcl = new Scanner(System.in);
    
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

    public static void esperarIntro() {
        System.out.println("Presione Enter para continuar...");
        tcl.nextLine();
    }

    private static String requestContentLike() {
        System.out.print("Escriba el codigo a buscar; ");
        return readNotEmptyString();
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

}
