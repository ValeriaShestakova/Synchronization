/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 *
 * @author Valeria
 */
abstract class WebTransfer extends Thread {
    protected Config config;
    boolean authorizationPassed = false;
    protected Socket dataSocket;
    protected OutputStream out;
    protected InputStream in;
    protected DataOutputStream dataOutput;
    protected DataInputStream dataInput;
    protected ServerSocket server;
    Set<FileProperties> SendFrom1 = new TreeSet<>();
    Set<FileProperties> SendFrom2 = new TreeSet<>();


    
    /**
     * Отправление файлов
     * @param os выходной поток
     * @param pth путь файла
     */
    
    
    public boolean authorization(ApplicationUser user) throws RemoteException {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SynchronizationPU");            
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<ApplicationUser> users = (List<ApplicationUser>)em.createQuery("from ApplicationUser").getResultList();
        em.getTransaction().commit();
        em.close();
        emf.close();
        if (users.contains(user)) {
            authorizationPassed = true;
            return authorizationPassed;
        } else {
            authorizationPassed = false;
            return authorizationPassed;
        }
    }
    
   
    public boolean isAuthorizationPassed() {
        return authorizationPassed;
    }
    
    protected void sendFile(DataOutputStream os,String pth) {
        String path = pth;
        File sourceLocation = new File(pth); 
        if (!sourceLocation.isDirectory()) {
            try (FileInputStream is = new FileInputStream(path);) {
                File file = new File(path);
                long length = file.length();            
                System.out.print("Sending " + path + "("+length+"bytes)... ");
                os.writeLong(length);
                byte[] buffer = new byte[1000];
                while (true){
                    int readedBytesCount = is.read(buffer);
                    if (readedBytesCount == -1) {
                        break;
                    }
                    if (readedBytesCount > 0) {
                        os.write(buffer, 0, readedBytesCount);
                    }
                }            
                System.out.println("sending has been finished");
            } catch (SocketException ex) {
                System.out.print("sending has been finished*");
            } catch (IOException ex) {
                System.out.print("sending has been finished**");
            }
            System.out.println();
        }
        
    }
    
        /**
         * Принятие файла
         * @param is входной поток
         * @param pth путь файла
         * @param t директория или нет
         */
        
        protected void receiveFile(DataInputStream is, String pth,  boolean t) {
        String path = pth;
        File file = new File(pth);
        File targetLocation = new File(path); 
        if (t) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
        } else
            try {
                file.createNewFile();
            } catch (IOException ex) {
            //ignore;
            }
            try (FileOutputStream os = new FileOutputStream(file);){            
                System.out.print("Saving " + path + "... ");
                long length = is.readLong();
                long now = 0;
                int readedBytesCount, total = 0;
                byte[] buffer = new byte[1000];
                while ((readedBytesCount = is.read(buffer, 0, Math.min(buffer.length, (int)length-total))) != -1) {
                    total += readedBytesCount;
                    os.write(buffer, 0, readedBytesCount);
                    if (total == (int)length){
                        break;
                    }
                }
                System.out.println("file has been saved");
                } catch (SocketException ex) {            
                    System.out.print("file has been saved*");
                } catch (IOException ex) {            
                    System.out.print("file has been saved**");
                }
             System.out.println("");
    }

    /**
     * 
     * @param s1 текущая директория1
     * @param s2 текущая дир2ктория 2
     * @param dir1 прошлое состояние 1
     * @param dir2 прошлое состояние 2
     * @param conf 
     * @throws IOException 
     */
        
    protected void Synchr(Set<FileProperties> s1, Set<FileProperties> s2,  Set<FileProperties> dir1, Set<FileProperties> dir2, Config conf) throws IOException  {
       String d1 = conf.getProperty("dir1");
       String d2 = conf.getProperty("dir2");
       Set<FileProperties> syn = null; 
       syn = new TreeSet<>();
       if (s2.isEmpty()){
           for (FileProperties curdir1: s1) {
               if(!contain((String)curdir1.getPath(), dir2)) {
               SendFrom1.add(curdir1);
               } else {
                   String s = "C"+(String)curdir1.getPath();
                    File f = new File(s);
                    delete(f);
               }
           }
       }
       if (s1.isEmpty()){
           for (FileProperties curdir2: s2) {
               if(!contain((String)curdir2.getPath(), dir1)) {
               SendFrom2.add(curdir2);
               } else {
                   String s = "C"+(String)curdir2.getPath();
                                    File f = new File(s);
                                    delete(f);
               }
           }
       }
       if (s1.size() >= s2.size()) {
           for (FileProperties curdir2: s2){
                for (FileProperties curdir1: s1) {
                    if (curdir2.equals(curdir1)){
                       
                        Long t1 = (Long) curdir1.getModifiedTime();
                        Long t2 = (Long) curdir2.getModifiedTime();
                        String fPath = (String)curdir2.getPath();
                        syn.add(new FileProperties(fPath, null, null));
                        if (t1.compareTo(t2) == 1){ //директория 1 изменена позже
                            //Copy((String)curdir1.getPath(), d1, d2);
                            SendFrom1.add(curdir1);
                        } else //Copy((String)curdir2.getPath(), d2, d1);
                            SendFrom2.add(curdir2);
                    }
                }
            } 
            for (FileProperties curdir2: s2){
                for (FileProperties curdir1: s1) {
                    if (!curdir2.equals(curdir1)){
                        if (!syn.contains(curdir1)) {
                            if (!contain((String)curdir1.getPath(), s2)) {
                                if(!contain((String)curdir1.getPath(), dir2)){
                                    SendFrom1.add(curdir1);
                                } else  {
                                    String s = "C"+(String)curdir1.getPath();
                                    File f = new File(s);
                                    delete(f);
                                } 
                            }
                            if (!contain((String)curdir2.getPath(), s1)){ 
                                if(!contain((String)curdir2.getPath(), dir1)){
                                    SendFrom2.add(curdir2);
                                }    else {
                                    String s = "C"+(String)curdir2.getPath();
                                    File f = new File(s);
                                    delete(f);
                                } 
                            } 
                        }
                    }
                }
            }  
        } 
       if (s1.size() < s2.size()) {
           for (FileProperties curdir1: s1){
                for (FileProperties curdir2: s2) {
                    if (curdir1.equals(curdir2)){
                        Long t1 = (Long) curdir1.getModifiedTime();
                        Long t2 = (Long) curdir2.getModifiedTime();
                        String fPath =(String) curdir1.getPath();
                        syn.add(new FileProperties(fPath, null, null));
                        if (t2.compareTo(t1) == 1){ //директория 2 изменена позже
                             //Copy((String) curdir2.getPath(), d2, d1);
                            SendFrom2.add(curdir2);
                        } else //Copy((String) curdir1.getPath(), d1, d2);
                            SendFrom1.add(curdir1);
                    }
                }
            } 
            for (FileProperties curdir1: s1){
                for (FileProperties curdir2: s2) {
                    if (!curdir1.equals(curdir2)){
                        if (!syn.contains(curdir2)) {
                            if (!contain((String)curdir2.getPath(), s1)) {
                                if(!contain((String)curdir2.getPath(), dir1)){
                                    SendFrom2.add(curdir2);
                                } else {
                                    String s = "C"+(String)curdir2.getPath();
                                    File f = new File(s);
                                    delete(f);
                                } 
                            }
                            if (!contain((String)curdir1.getPath(), s2)){ 
                                if(!contain((String)curdir1.getPath(), dir2)){
                                    SendFrom1.add(curdir1);
                                } else {
                                    String s = "C"+(String)curdir1.getPath();
                                    File f = new File(s);
                                    delete(f);
                                } 
                            } 
                        }
                    }
                }
            }  
       }
   }

        /**
     * Метод отправляющий файлы согласно параметрам из коллекции set, 
     * лежащие в директории dir, на поток dataOutput.
     * @param dataOutput выходной поток
     * @param set файлы
     * @param from откуда
     * @param to куда
     */
    protected void sendFiles(DataOutputStream dataOutput, Set<FileProperties> set, String from, String to) {   
        for (FileProperties fp : set) { //String pth, String from, String to
            sendFile(dataOutput,"C"+(String)fp.getPath());
        }
    }
    
    /**
     * Метод, принимающий файлы с потока dataInput согласно параметрам из коллекции set
     * и сохраняющий их в директорию dir
     * @param dataInput входной поток
     * @param set файлы
     * @param from откуда
     * @param to куда
     */
    protected void receiveFiles(DataInputStream dataInput,Set<FileProperties> set, String from, String to) {   
        for (FileProperties fp : set) {
            boolean t = false;
            if ((boolean)fp.isDirectory()==true){
                t = true;
            }
            String p = (String)fp.getPath();
            String path = p.substring(0,18)+from+p.substring(19);
            receiveFile(dataInput,"C"+path,t);
        }
    } 
    
    /**
     * Метод удаления файла.
     * @param file файл, который удаляем.
     */
    private static void delete(File file) {
                if(file.isDirectory())
            {
              for(File f : file.listFiles())
                delete(f);
              file.delete();
            }
            else
            {
              file.delete();
            }
    }
    
    
    public static boolean contain(String s, Set<FileProperties> dir) {
        
        int i = 0;
            for (FileProperties d: dir) {
                
                String h = d.getPath().toString().substring(19);
                if (h.equals(s.substring(19))) 
                    i = i+1;
        }
            if (i>0) {
            return true; }
            else {
                return false; }
    }
    
        /**
     * Перевод строки в MD5 хеш
     * @param s строка
     * @return MD5 хеш
     */
    private static String MD5(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // ignore
        }
        return null;
    }
    
     @Override
    public void run() {
        
    }
}

