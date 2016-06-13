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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import synchronization.ClientJFrame;

/**
 * Класс реализует Client Socket.
 * Класс создан на основе класса Thread и для работы с ним необходимо перегрузить
 * метод run(), который выполняется при запуске потока.
 */
public class Client extends WebTransfer {
    
    private String host;
    private int port;
    private ClientJFrame frame;
    
    public Client(String host, int port, ClientJFrame frame) {
        this.host = host;
        this.port = port;
        this.frame = frame;
        dataSocket = null;
        OutputStream out = null;
        InputStream in = null;
        PrintWriter textOutput = null;
        DataOutputStream dataOutput = null;
        DataInputStream dataInput = null;
    }
    
    @Override
    public void run() {       
         //connectionIsEstablished = false,
        
        ObjectOutputStream oos = null;
        ObjectInputStream inn = null;
          boolean connectionIsEstablished  = false;           
         SynService serverService = null;
         Config conf = new Config("config.xml");
         conf.loadFromXML();
         String objectName = "rmi://" + host + "/mySyncService";
         String log = conf.getProperty("login");
         String pswd = conf.getProperty("password");
         frame.setProgress(5);
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            serverService = (SynService) registry.lookup("mySyncService");
            connectionIsEstablished = true;
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setProgress(10);
        if (connectionIsEstablished) {
        initFileTransferring();
        try {
            oos = new ObjectOutputStream(dataSocket.getOutputStream());        
            String[] loginAndPas = {log,pswd};
            oos.writeObject(loginAndPas);
            inn = new ObjectInputStream(dataSocket.getInputStream());
            } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (inn.readObject().equals("Connect")){
                System.out.println("Авторизация пройдена");
                frame.setProgress(20);
                deinitFileTransferring();
            
            String t1 = "fout1.tmp";
            File f1 = new File(t1);            
            String t2 = "fout2.tmp";
            File f2 = new File(t2); 
            Set<FileProperties> dir1;
            Set<FileProperties> dir2;
            Set<FileProperties> currentdir2;
            Set<FileProperties> currentdir1;
            
            //conf.loadFromXML();
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
            frame.setProgress(30);
            
        try {
            Synchr(currentdir1, currentdir2, dir1, dir2, conf);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setProgress(40);
       if (!(SendFrom1.isEmpty() && SendFrom2.isEmpty())){
            initFileTransferring();
            frame.setProgress(50);
            if (SendFrom2.isEmpty()){
            sendFiles(dataOutput,SendFrom1,"1","2");
            frame.setProgress(60);
            } else
            if (SendFrom1.isEmpty()){
            receiveFiles(dataInput,SendFrom2,"1","2");
            frame.setProgress(60);
            } else {
                sendFiles(dataOutput,SendFrom1,"1","2");
                frame.setProgress(70);
                receiveFiles(dataInput,SendFrom2,"1","2");
                frame.setProgress(80);
            }
            
             deinitFileTransferring();
             frame.setProgress(90);
            }
        f1.delete();
        f1 = new File(t1); 
        currentdir1 = new TreeSet<>();
        compare.scanDir(d1, currentdir1);
        compare.saveToBinaryFile(currentdir1, f1); 
        frame.setProgress(100);
        frame.setComplited();
        System.out.println("До встречи!");
           } else System.out.println("Неверный логин или пароль");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        }  else {
            System.out.println("Connection to server was failed");
            frame.connectionWasFailed();
        }
    }
         
    
    public void initFileTransferring() {
        try {
            //dataSocket = new Socket(config.getProperty("host"),Integer.valueOf(config.getProperty("port"))+2);
            System.out.println("Connecting to " + host + ":" + (port+2));
            dataSocket = new Socket(host, port+2);
            out = dataSocket.getOutputStream();
            in = dataSocket.getInputStream();
            dataOutput = new DataOutputStream(out);
            dataInput = new DataInputStream(in);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void deinitFileTransferring() {
        try {
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
