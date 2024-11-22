/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connections;

import java.sql.Connection;

/**
 *
 * @author angsaegim
 */
public class DataAccessSQLObject {
    
    protected Connection cnt;

    DataAccessSQLObject(Connection cnt) {
        if (cnt == null) {
            throw new IllegalArgumentException("Conexión obligatoria");
        }
        this.cnt = cnt;
    }
    
}
