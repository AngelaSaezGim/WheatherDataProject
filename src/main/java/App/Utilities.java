/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

//prevenir error MongoDB
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 *
 * @author angel
 */
public class Utilities {

    public static void disableMongoLogging() {
        /*
* Static method: Disable annoying mongo log messages
* This method require add some code to POM file
* https://stackoverflow.com/questions/30137564/how-to-disable-mongodb-java-driver-logging
         */
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
    }

}
