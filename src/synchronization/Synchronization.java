/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;
import synchronization.ClientJFrame;
/**
 *
 * @author Валерия
 */
public class Synchronization {   
    
     
    /**
     * @param args the command line arguments
     */
    //public static ClientJFrame frame;
    public static void main(String[] args) {
       
        Config conf = new Config("config.xml");
        conf.loadFromXML();
        ClientJFrame frame = new ClientJFrame(conf);
        frame.setVisible(true);
        String host = conf.getProperty("host");
        int port = Integer.valueOf(conf.getProperty("port"));
        Thread s = new Server(port);
        // запуск потока экземпляра класса Server
        s.start(); 
    /*    Thread c = new Client(host, port, frame);
        // запуск потока экземпляра класса Client
        c.start(); */
    }
}
