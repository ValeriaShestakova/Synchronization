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
        /*String t1 = "fout1.tmp";
        String t2 = "fout2.tmp";
        File f1 = new File(t1);
        File f2 = new File(t2); 
        
        Set<FileProperties> dir1;
        Set<FileProperties> dir2;
        Set<FileProperties> currentdir1;
        Set<FileProperties> currentdir2;*/
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
        
        /*if (f1.exists()) {
            // Загрузить коллекцию из файла
            dir1 = compare.loadFromBynaryFile(f1);
        } else { 
            dir1 = new TreeSet<>();
            // Рекурсивный поиск
            compare.scanDir(d1, dir1);          
        } 
        if (f2.exists()) {
            // Загрузить коллекцию из файла
            dir2 = compare.loadFromBynaryFile(f2);
        } else { 
            dir2 = new TreeSet<>();
            // Рекурсивный поиск
            compare.scanDir(d2, dir2);            
        }  
        currentdir1 = new TreeSet<>();
        compare.scanDir(d1, currentdir1);  
        currentdir2 = new TreeSet<>();
        compare.scanDir(d2, currentdir2); */
       /*for (FileProperties fi: currentdir1) {
            System.out.println(fi.getPath()+" "+fi.getModifiedTime()+" "+fi.isDirectory());
        } 
        System.out.println("Second");
        for (FileProperties fi: currentdir2) {
            System.out.println(fi.getPath()+" "+fi.getModifiedTime()+" "+fi.isDirectory());
        } */
       /* Thread myThready = new Thread(new Runnable()
        {
            @Override
            public void run(){
               try {
                        compare.Synchr(currentdir1, currentdir2, dir1, dir2, conf);
                    } catch (IOException ex) {
                        Logger.getLogger(Synchronization.class.getName()).log(Level.SEVERE, null, ex);
                    }  
            }
        }); 
        
        f1.delete();
        f2.delete();
        compare.saveToBinaryFile(currentdir1, f1);
        compare.saveToBinaryFile(currentdir2, f2); */
        
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
