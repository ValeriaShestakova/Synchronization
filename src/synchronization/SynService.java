/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;
import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
/**
 *
 * @author Valeria
 */
interface SynService extends Remote {
    
    public void setServer(Server server) throws RemoteException ;
     
    public void scanDir(String path, Set<FileProperties> s) throws RemoteException;
     
    public Set<FileProperties> loadFromBynaryFile(File f) throws RemoteException;
     
    public String getProperty(String key) throws RemoteException;
    
    public void Synchr(Set<FileProperties> s1, Set<FileProperties> s2,  Set<FileProperties> dir1, Set<FileProperties> dir2, Config conf)
            throws RemoteException;
    
    public boolean saveToBinaryFile(Set<FileProperties> s, File f) throws RemoteException;
    
}
