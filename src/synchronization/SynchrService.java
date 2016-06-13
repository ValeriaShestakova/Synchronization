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
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.TreeSet;

/**
 * Класс, осуществляющий удаленный доступ клиента на сервер
 */
public class SynchrService extends UnicastRemoteObject implements SynService {
    
    String path, d;
    Set<FileProperties> s, dir, s1, s2, dir1, dir2;
    Config conf;
    Server server;
    boolean t;
    
    public void setServer(Server server) {
        this.server = server;
    }
    
    public void scanDir(String path, Set<FileProperties> s) {
        this.path = path;
        this.s = s;
        
    }     
    public Set<FileProperties> loadFromBynaryFile(File f) {
        return dir;
    }
     
    public String getProperty(String key) {
        return d;
    }
    
    public void Synchr(Set<FileProperties> s1, Set<FileProperties> s2,  Set<FileProperties> dir1, Set<FileProperties> dir2, Config conf){
        this.s1 = s1;
        this.s2 = s2;
        this.dir1 = dir1;
        this.dir2 = dir2;
        this.conf = conf;
    }
    
    
    public boolean saveToBinaryFile(Set<FileProperties> s, File f) {
        return t;
    }
    
    public SynchrService() throws RemoteException {
        t = false;
        s1 = new TreeSet<FileProperties>();
        s2 = new TreeSet<FileProperties>();
        dir1 = new TreeSet<FileProperties>();
        dir2 = new TreeSet<FileProperties>();
        s = new TreeSet<FileProperties>();
        dir = new TreeSet<FileProperties>();
        d = null;
        path = null;
        server = null;
        conf = new Config("config.xml");
    }
            
}
