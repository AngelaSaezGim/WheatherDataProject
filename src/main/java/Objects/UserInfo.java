/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

/**
 *
 * @author angel
 */
public class UserInfo {
    
    private String dni;
    private String name;
    private String surname;
    private String city;

    // Constructor vacío
    public UserInfo() {
    }

    // Constructor completo
    public UserInfo(String dni, String name, String surname, String city) {
        this.dni = dni;
        this.name = name;
        this.surname = surname;
        this.city = city;
    }

    // Getters y Setters
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Método toString para representación del objeto
    @Override
    public String toString() {
        return "Usuario {" +
                "DNI ='" + dni + '\'' +
                ", Nombre ='" + name + '\'' +
                ", Apellidos ='" + surname + '\'' +
                ", Ciudad ='" + city + '\'' +
                '}';
    }
}
