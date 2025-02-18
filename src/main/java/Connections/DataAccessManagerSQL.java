/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connections;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import DAOs.UserInfoSQLDAO;
import DAOs.WeatherDataSQLDAO;
import java.util.List;

import Objects.UserInfo;
import Objects.WeatherData;

/**
 *
 * @author angsaegim
 */
public class DataAccessManagerSQL implements AutoCloseable {

    /**
     * ************************ PARTE ESTÁTICA ****************************
     */
    //Por default usamos wheater data pero podemos cambiar a userInfo
    private static final String MYSQL_DB_DRIVER__CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String DB_CONFIG_FILE_NAME = "src/resources/db.properties";

    // Claves de las bases de datos
    private static final String USER_INFO_DB = "UserInfo";
    private static final String WEATHER_DATA_DB = "WeatherData";

    //COMO TENGO 2 BASES DE DATOS USO UN HASHMAP PARA PODER CONECTARME A AMBAS
    //ASI PODRE USARLAS CUANDO LAS NECESITE Y NO TENDRÉ QUE DUPLICAR CODIGO
    private static Map<String, String> databaseUsers = new HashMap<>();
    private static Map<String, String> databasePasswords = new HashMap<>();
    private static Map<String, String> databaseURLs = new HashMap<>();

    // Almacena las conexiones activas a las bases de datos
    private final Map<String, Connection> connections = new HashMap<>();

    private static DataAccessManagerSQL singleton;

    private UserInfoSQLDAO userInfoDAO;
    private WeatherDataSQLDAO weatherDataDAO;

    // Instanciamos una UNICA CONEXIÓN (SINGLETON)
    private DataAccessManagerSQL() {
        loadDataBaseParams(); //para saber si es userInfo o wheaterData
        this.userInfoDAO = new UserInfoSQLDAO(getConnection(USER_INFO_DB));
        this.weatherDataDAO = new WeatherDataSQLDAO(getConnection(WEATHER_DATA_DB));
    }

    // Obtiene la instancia única de DataAccessManagerSQL
    public static synchronized DataAccessManagerSQL getInstance() {
        if (singleton == null) {
            singleton = new DataAccessManagerSQL();
        }
        System.out.println("Conexión a SQL realizada (instancia)");
        return singleton;
    }

    /**
     * Carga las credenciales y URL de acceso a datos del fichero de
     * configuración (db.properties) EN ESTE CASO ITERAMOS EN EL HASMAP YA QUE
     * TENEMOS 2 CONEXIONES QUE CARGAR
     */
    private void loadDataBaseParams() {
        Properties databaseConfig = new Properties();
        try ( FileReader dbReaderStream = new FileReader(DB_CONFIG_FILE_NAME)) {
            databaseConfig.load(dbReaderStream);

            // Cargar configuraciones para WeatherData
            databaseUsers.put("WeatherData", databaseConfig.getProperty("weather.user", "root"));
            databasePasswords.put("WeatherData", databaseConfig.getProperty("weather.pass", "serpis"));
            databaseURLs.put("WeatherData", databaseConfig.getProperty("weather.url"));

            // Cargar configuraciones para UserInfo
            databaseUsers.put("UserInfo", databaseConfig.getProperty("userinfo.user", "root"));
            databasePasswords.put("UserInfo", databaseConfig.getProperty("userinfo.pass", "serpis"));
            databaseURLs.put("UserInfo", databaseConfig.getProperty("userinfo.url"));

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar configuración de bases de datos: " + e.getMessage(), e);
        }
    }

    //Creamos la conexión según la base de datos a la que nos estemos conectando
    private static Connection createConnection(String dbName) {
        String url = databaseURLs.get(dbName);
        String user = databaseUsers.get(dbName);
        String password = databasePasswords.get(dbName);

        if (url == null || user == null || password == null) {
            throw new RuntimeException("Faltan configuraciones para la base de datos: '" + dbName + ", ve a db.resoruces'.");
        }
        try {
            Class.forName(MYSQL_DB_DRIVER__CLASS_NAME);
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(true);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos '" + dbName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Devuelve una conexión a la base de datos especificada. Si no existe una
     * conexión, se crea automáticamente.
     */
    public Connection getConnection(String dbName) {
        return connections.computeIfAbsent(dbName, DataAccessManagerSQL::createConnection);
    }

    /**
     * Cierra todas las conexiones activas.
     */
    @Override
    public void close() {
        for (Connection connection : connections.values()) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        connections.clear();
        singleton = null;
    }

    /* FUNCIONES CON DATOS */
    //para conectar y ejecutar las SQL en la bbdd

    ///---------------------- USERS ------------------------------------
    public List<UserInfo> loadAllUsersSQL() throws SQLException {

        return this.userInfoDAO.loadAllUsers();
    }

    public UserInfo loadUsersByDNISQL(String dni) throws SQLException {
        if (dni == null || dni.length() == 0) {
            throw new IllegalArgumentException("Debe indicar el filtro de búsqueda");
        }
        return this.userInfoDAO.loadUserByDni(dni);
    }

    public boolean userExistSQL(String dni) throws SQLException {
        return this.userInfoDAO.userExist(dni);
    }

    ///---------------------- USERS ------------------------------------
    /**
     * *****************************************************************
     */
    //                        WEATHER DATA SQL                          *
    /**
     * *****************************************************************
     * @return 
     */
    // SELECT ALL
    public int countWeatherDataSQL() throws SQLException {
        return this.weatherDataDAO.countWeatherDataSQL();
    }
    
    public boolean existsWeatherDataSQL(String recordId) {
        return this.weatherDataDAO.weatherDataExistSQL(recordId);
    }
      
    public List<WeatherData> loadAllWeatherDataSQL() throws SQLException {
        return this.weatherDataDAO.loadAllWeatherDataSQL();
    }

  /*----------------------------------------------------------*/

 /*-------------- SELECT - CONTAINING *------------------------*/
                                                      //city
    public List<WeatherData> loadWeatherDataByCitySQL(String city) throws SQLException {
        if (city == null || city.length() == 0) {
            throw new IllegalArgumentException("Debe indicar el filtro de búsqueda");
        }
        return this.weatherDataDAO.loadWeatherDataByCitySQL(city);
    }
                                                        //list citiesList
    public List<WeatherData> loadWeatherDataByCitiesSQL(List<String> citiesList) throws SQLException {
        if (citiesList == null || citiesList.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el filtro de búsqueda");
        }
        return this.weatherDataDAO.loadWeatherDataByCitiesSQL(citiesList);
    }

 /*------------------------------------------------------------*/
 /*-------------- INSERT - (WeatherData) *------------------------*/
    
    public int insertarWeatherDataSQL(WeatherData newweatherdata) throws SQLException {
        return this.weatherDataDAO.insertWeatherDataSQL(newweatherdata);
    }
    
    public int insertarWeatherDataSQL(List<WeatherData> weatherDataList) throws SQLException {
        return this.weatherDataDAO.insertWeatherDataSQL(weatherDataList);
    }

/*---------------------------------------------------------------*/

 /*---------------------------------------------------------------*/
 /*-------------- DELETE - (WeatherData) *------------------------*/
    public int deleteAllWeatherDataSQL() throws SQLException {
        return this.weatherDataDAO.deleteAllWeatherDataSQL();
    }

    public int deleteWeatherDataByListSQL(List<WeatherData> weatherDataList) throws SQLException {
        if (weatherDataList == null || weatherDataList.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el filtro de búsqueda");
        }
        return this.weatherDataDAO.deleteWeatherDataByListSQL(weatherDataList);
    }
}

    /*---------------------------------------------------------------*/
