/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Objects.UserInfo;

/**
 *
 * @author angel
 */
public class UserInfoSQLDAO extends DataAccessObject {

    public UserInfoSQLDAO(Connection connection) {
        super(connection);
    }

    private class UserInfoTableColumns {

        private static final String COLUMN_DNI = "dni";
        private static final String COLUMN_NAME = "name";
        private static final String COLUMN_SURNAME = "surname";
        private static final String COLUMN_CITY = "city";
    }

    private static UserInfo readUserInfoFromResultSet(ResultSet rs) throws SQLException {
        String dni = rs.getString(UserInfoTableColumns.COLUMN_DNI);
        String name = rs.getString(UserInfoTableColumns.COLUMN_NAME);
        String surname = rs.getString(UserInfoTableColumns.COLUMN_SURNAME);
        String city = rs.getString(UserInfoTableColumns.COLUMN_CITY);
        return new UserInfo(dni, name, surname, city);
    }

    public List<UserInfo> loadAllUsers() throws SQLException {
        List<UserInfo> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        
        try ( PreparedStatement stmt = cnt.prepareStatement(query);  ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(readUserInfoFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar los usuarios: " + e.getMessage(), e);
        }
        return users;
    }
    
    public UserInfo loadUserByDni(String dni) throws SQLException {
        String query = "SELECT * FROM users WHERE dni = ?";
        try (PreparedStatement stmt = cnt.prepareStatement(query)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return readUserInfoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar el usuario con DNI: " + dni, e);
        }
        return null;
    }

}
