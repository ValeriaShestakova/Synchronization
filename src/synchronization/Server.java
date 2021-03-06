/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

/**
 *
 * @author Valeria
 */
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс реализует Server Socket.
 * Класс создан на основе класса Thread и для работы с ним необходимо перегрузить
 * метод run(), который выполняется при запуске потока.
 */
public class Server extends WebTransfer {
    
    private int port;
    
    public Server(int port) {
        this.port = port;
        dataSocket = null;
        dataOutput = null;
        dataInput = null;      
        server = null;            
        out = null;
        in = null; 
    }
    
    @Override
    public void run() {
       ObjectInputStream inn = null;
        ObjectOutputStream oos = null;
        String serviceName = "mySyncService";
        Registry reg = null;
        try {
            reg = LocateRegistry.createRegistry(port);
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean authorizationPassed = false;
        SynService serverService = null;
        System.out.println("Waiting for client...");
            try {                   
                serverService = new SynchrService();
                serverService.setServer(this);
                //serverService.setServerDir(serverDir);
                reg.rebind(serviceName, serverService);
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
           try {            
            Locale.setDefault(Locale.ENGLISH);
            String log = null;
            String passwd = null;
            String[] loginAndPas= null;
            initFileTransferring();
            inn = new ObjectInputStream(dataSocket.getInputStream());
                //String[] loginAndPas;
           try {
               loginAndPas = (String[]) inn.readObject();
           } catch (ClassNotFoundException ex) {
               Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
           }
                log = loginAndPas[0];
                passwd = loginAndPas[1];
                ApplicationUser user = new ApplicationUser(log,passwd);
                authorizationPassed = authorization(user);
                oos = new ObjectOutputStream(dataSocket.getOutputStream());
                if (authorizationPassed) {
                    oos.writeObject("Connect");              
                deinitFileTransferring();
            String t1 = "fout1.tmp";
            File f1 = new File(t1);            
            String t2 = "fout2.tmp";
            File f2 = new File(t2); 
            Set<FileProperties> dir1;
            Set<FileProperties> dir2;
            Set<FileProperties> currentdir2;
            Set<FileProperties> currentdir1;
            Config conf = new Config("config.xml");
            conf.loadFromXML();
            String d2=conf.getProperty("dir2");
            if (f2.exists()) {
            dir2 = compare.loadFromBynaryFile(f2);
            } else { 
                dir2 = new TreeSet<>();
                compare.scanDir(d2, dir2);            
            }
            currentdir2 = new TreeSet<>();
            compare.scanDir(d2, currentdir2);            
            String d1=conf.getProperty("dir1");
            if (f1.exists()) {
            dir1 = compare.loadFromBynaryFile(f1);
            } else { 
                dir1 = new TreeSet<>();
                compare.scanDir(d1, dir1);          
            } 
            currentdir1 = new TreeSet<>();
            compare.scanDir(d1, currentdir1); 
            Synchr(currentdir1, currentdir2, dir1, dir2, conf);
            
            if (!(SendFrom1.isEmpty() && SendFrom2.isEmpty())){                
                System.out.println("Есть изменения, начинаем синхронизацию");
                initFileTransferring();
                if (SendFrom1.isEmpty()){                
                sendFiles(dataOutput,SendFrom2,"2","1"); }
                else if (SendFrom2.isEmpty()){  
                receiveFiles(dataInput,SendFrom1,"2","1"); }
                else {
                    sendFiles(dataOutput,SendFrom2,"2","1");
                    receiveFiles(dataInput,SendFrom1,"2","1");
                } 
                
                deinitFileTransferring();
            } else System.out.println("Изменений нет"); 
                f2.delete();
                f2 = new File(t2); 
                currentdir2 = new TreeSet<>();
                compare.scanDir(d2, currentdir2);
                compare.saveToBinaryFile(currentdir2, f2); 
           }
           } catch (IOException ex) {
               Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // ignore
                }
    
    }
    
    public void initFileTransferring() {
        try {
            server = new ServerSocket(port+2);
            dataSocket = server.accept();
            out = dataSocket.getOutputStream();
            in = dataSocket.getInputStream();                            
            dataOutput = new DataOutputStream(out);
            dataInput = new DataInputStream(in);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deinitFileTransferring() {
        try {
            server.close();
            dataSocket.close();
            out.close();
            in.close();                           
            dataOutput.close();
            dataInput.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            //ignore
        }
    }

}

