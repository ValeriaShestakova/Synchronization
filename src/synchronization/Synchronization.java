/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Валерия
 */
public class Synchronization {   
    
     
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        System.out.println("Введите первую директорию");
        Scanner sc = new Scanner(System.in);
        String d1 = sc.nextLine();
        System.out.println("Введите вторую директорию");
        String d2 = sc.nextLine();
        args[0] = d1;
        args[1] = d2;
        Config conf = new Config("config.xml");
        
       if (args.length > 0 && args.length % 2 == 0) {
            conf.setProperty("dir1", args[0]);
            conf.setProperty("dir2", args[1]);
            conf.setProperty("host", "127.0.0.1");
            conf.setProperty("port", "1");
            conf.saveToXML();
        } else {
            conf.loadFromXML();
            //conf.printAll();
        }
                
        //myThready.start();
        String host = conf.getProperty("host");
        int port = Integer.valueOf(conf.getProperty("port"));
        Thread s = new Server(port);
        // запуск потока экземпляра класса Server
        s.start();
        Thread c = new Client(host, port);
        // запуск потока экземпляра класса Client
        c.start();
    }
}
