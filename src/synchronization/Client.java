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
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс реализует Client Socket.
 * Класс создан на основе класса Thread и для работы с ним необходимо перегрузить
 * метод run(), который выполняется при запуске потока.
 */
public class Client extends WebTransfer {
    
    private String host;
    private int port;
    
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
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
           boolean authorizationPassed = false;           
        //ISyncService serverService = null;
         Config conf = new Config("config.xml");
         conf.loadFromXML();
         String log = conf.getProperty("login");
         String pswd = conf.getProperty("password");
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
                deinitFileTransferring();
            
        /* Locale.setDefault(Locale.ENGLISH);
        ApplicationUser user = new ApplicationUser(conf.getProperty("login"),conf.getProperty("password"));
        System.out.println(conf.getProperty("login")+" "+conf.getProperty("password"));
        try {
            authorizationPassed = authorization(user);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } */
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
            
        try {
            Synchr(currentdir1, currentdir2, dir1, dir2, conf);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       if (!(SendFrom1.isEmpty() && SendFrom2.isEmpty())){
            initFileTransferring();
            if (SendFrom2.isEmpty()){
            sendFiles(dataOutput,SendFrom1,"1","2");
            } else
            if (SendFrom1.isEmpty()){
            receiveFiles(dataInput,SendFrom2,"1","2");
            } else {
                sendFiles(dataOutput,SendFrom1,"1","2");
                receiveFiles(dataInput,SendFrom2,"1","2");
            }
            
             deinitFileTransferring();
        
            }
        f1.delete();
        f1 = new File(t1); 
        currentdir1 = new TreeSet<>();
        compare.scanDir(d1, currentdir1);
        compare.saveToBinaryFile(currentdir1, f1); 
           }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
    
    public void initFileTransferring() {
        try {
            //dataSocket = new Socket(config.getProperty("host"),Integer.valueOf(config.getProperty("port"))+2);
            System.out.println("Connecting to " + host + ":" + port);
            dataSocket = new Socket(host, port);
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
