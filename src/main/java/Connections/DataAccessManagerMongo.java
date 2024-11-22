/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connections;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

/**
 *
 * @author angsaegim
 */
public class DataAccessManagerMongo {

    public static MongoClient connectToMongoClient() {
        /*
    * Static method that connects to client
         */
        try {

            MongoClient dbClient = new MongoClient();
            return dbClient;
        } catch (Exception ex) {
            System.out.println("Something wrong connecting!");
            ex.printStackTrace(System.out);
        }
        return null;
    }

    public static void closeMongoClient(MongoClient c) {
        /*
    * Static method that closes connnection with client
         */
        try {
            c.close();
        } catch (Exception ex) {
            System.out.println("Unable to close!");
            ex.printStackTrace(System.out);
        }
    }

    public static MongoDatabase useDBMongo(MongoClient conn, String database, String collectionSelected) {
        // Getting a connection
        System.out.println("Connecting with MongoDB");

        conn = connectToMongoClient();
        MongoDatabase db = null;

        try {
            db = conn.getDatabase(database);
            createCollectionIfNotExists(db);
        } catch (Exception ex) {
            System.out.println("Something wrong accesing!");
            ex.printStackTrace(System.out);
        }
        return db;
    }

    public static MongoDatabase selectDBMongo(MongoClient conn, String database) {
        // Getting a connection
        conn = connectToMongoClient();
        MongoDatabase db = null;

        try {
            db = conn.getDatabase(database);
        } catch (Exception ex) {
            System.out.println("Something wrong accesing!");
            ex.printStackTrace(System.out);
        }
        return db;
    }

    public static boolean collectionExists(String collectionName, MongoDatabase database) {
        /*
        * Static method: Check if collection exists
         */
        MongoIterable<String> collection = database.listCollectionNames();
        for (String s : collection) {
            if (s.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

    public static void createCollectionIfNotExists(MongoDatabase database) {
        /*
        * Static method: Create collection if not exists
         */
        try {
            if (!(collectionExists("wheatherData", database))) {
                System.out.println("Collection does not exist");
                database.createCollection("books");
                System.out.println("Created collection wheatherData ...");
            }
        } catch (Exception e) {
            System.out.println("Something wrong creating collection!");
            e.printStackTrace(System.out);
        }
    }
}
