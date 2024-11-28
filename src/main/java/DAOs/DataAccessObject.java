/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author angel
 */

/**
 * Clase base para todos los DAOs.
 * Proporciona métodos comunes para interactuar con la base de datos SQL.
 */

public class DataAccessObject {
    
     // Conexión a la base de datos (protegida para que las subclases puedan usarla)
    protected final Connection cnt;

    /**
     * Constructor de DataAccessObject.
     * @param connection La conexión a la base de datos proporcionada por el DataAccessManager.
     */
    protected DataAccessObject(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("La conexión no puede ser nula.");
        }
        this.cnt = connection;
    }
}
