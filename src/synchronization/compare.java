/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;
import java.io.Closeable;
import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.*;
import java.util.TreeSet;
/**
 *
 * @author Валерия
 */
public class compare implements Runnable {
 
    /**
     * Метод осуществляет рекурсивный поиск файлов и каталогов начиная с
     * указанного в параметре path.
     * @param path текущая директория сканирования.
     * @param s коллекция, содержащая список всех найденных файлов и каталогов.
     */
    public static void scanDir(String path, Set<FileProperties> s) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;
        for (File f : list) {
            boolean fType = f.isDirectory();
            long fTime = f.lastModified();
            String fPath = f.getPath();
            if (fType) {
                scanDir(fPath, s);
            }
            s.add(new FileProperties(fPath.substring(1), fTime, fType));
        }
    }
    
    /**
     * Вспомогательный метод используется для закрытия потоков.
     * @param closeable ссылка на поток.
     */
  public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }
    
    /**
     * Метод используется чтения из файла сериализованной коллекции.
     * @param f файл для чтения.
     * @return коллекция.
     */
   public static Set<FileProperties> loadFromBynaryFile(File f) {
        Set<FileProperties> s = null;
        FileInputStream fs = null;
        ObjectInputStream os = null;
        try {
            if (!f.exists()) return null;
            fs = new FileInputStream(f);
            os = new ObjectInputStream(fs);
            s = (Set<FileProperties>)os.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Synchronization.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Synchronization.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeQuietly(os);
            closeQuietly(fs);
        }
        return s;
    } 
    
     /**
     * Метод используется для сериализации (сохранения в файл) коллекции.
     * @param s коллекция, которую следуют сериализовать.
     * @param f файл для сохранения.
     * @return возвращает true в случае удачной сериализации.
     */
  public static boolean saveToBinaryFile(Set<FileProperties> s, File f) {
        boolean result = false;
        FileOutputStream fs = null;
        ObjectOutputStream os = null;
        try {
            fs = new FileOutputStream(f);
            os = new ObjectOutputStream(fs);
            os.writeObject(s);
            result = true;
        } catch (IOException ex) {
            Logger.getLogger(Synchronization.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeQuietly(os);
            closeQuietly(fs);
         }
        return result;
    }  
   
   
    /**
     * Метод синхронизация.
     * @param s1 коллекция первой директории, которую синхронизируем.
     * @param s2 коллекция второй директории, которую синхронизируем.
     * @param dir1 коллекция первой директории в прошлом состоянии.
     * @param dir2 коллекция второй директории в прошлом состоянии.
     * @param conf файл config XML
     * @throws java.io.IOException
     */
    public static void Synchr(Set<FileProperties> s1, Set<FileProperties> s2,  Set<FileProperties> dir1, Set<FileProperties> dir2, Config conf) throws IOException  {
       String d1 = conf.getProperty("dir1");
       String d2 = conf.getProperty("dir2");
       Set<FileProperties> syn = null;
       syn = new TreeSet<>();
       if (s1.size() >= s2.size()) {
           for (FileProperties curdir2: s2){
                for (FileProperties curdir1: s1) {
                    if (curdir2.equals(curdir1)){
                       
                        Long t1 = (Long) curdir1.getModifiedTime();
                        Long t2 = (Long) curdir2.getModifiedTime();
                        String fPath = (String)curdir2.getPath();
                        syn.add(new FileProperties(fPath, null, null));
                        if (t1.compareTo(t2) == 1){ //директория 1 изменена позже
                            Copy((String)curdir1.getPath(), d1, d2);
                        } else Copy((String)curdir2.getPath(), d2, d1);
                    }
                }
            } 
            for (FileProperties curdir2: s2){
                for (FileProperties curdir1: s1) {
                    if (!curdir2.equals(curdir1)){
                        if (!syn.contains(curdir1)) {
                            if (!contain((String)curdir1.getPath(), s2)) {
                                if(!contain((String)curdir1.getPath(), dir2)){
                                   try { 
                                       Copy((String)curdir1.getPath(), d1, d2);
                                   } catch (FileNotFoundException ex) {
                                        Logger.getLogger(compare.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else  {
                                    String s = d1+(String)curdir1.getPath();
                                    File f = new File(s);
                                    delete(f);
                                } 
                            }
                            if (!contain((String)curdir2.getPath(), s1)){ 
                                if(!contain((String)curdir2.getPath(), dir1)){
                                try { 
                                       Copy((String)curdir2.getPath(), d2, d1);
                                   } catch (FileNotFoundException ex) {
                                        Logger.getLogger(compare.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }    else {
                                    String s = d2+(String)curdir2.getPath();
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
                             Copy((String) curdir2.getPath(), d2, d1);
                        } else Copy((String) curdir1.getPath(), d1, d2);
                    }
                }
            } 
            for (FileProperties curdir1: s1){
                for (FileProperties curdir2: s2) {
                    if (!curdir1.equals(curdir2)){
                        if (!syn.contains(curdir2)) {
                            if (!contain((String)curdir2.getPath(), s1)) {
                                if(!contain((String)curdir2.getPath(), dir1)){
                                   try { 
                                       Copy((String) curdir2.getPath(), d2, d1);
                                   } catch (FileNotFoundException ex) {
                                        Logger.getLogger(compare.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    String s = d2+(String)curdir2.getPath();
                                    File f = new File(s);
                                    delete(f);
                                } 
                            }
                            if (!contain((String)curdir1.getPath(), s2)){ 
                                if(!contain((String)curdir1.getPath(), dir2)){
                                try { 
                                       Copy((String)curdir1.getPath(), d1, d2);
                                   } catch (FileNotFoundException ex) {
                                        Logger.getLogger(compare.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    String s = d1+(String)curdir1.getPath();
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
     * Метод копирование файлов.
     * @param pth путь к файлу, который копируем.
     * @param from название корневой директории, из которой копируем.
     * @param d название корневой директории, куда копируем.
     */
    
       public static void Copy(String pth, String from, String d) throws FileNotFoundException, IOException{
           String path = d+pth;
           //System.out.println(path);
           File sourceLocation = new File(from+pth);
           File targetLocation = new File(path);
           if (sourceLocation.isDirectory()) {
         if (!targetLocation.exists()) {
             targetLocation.mkdir();
         }
        
     } else {
           FileInputStream fis = new FileInputStream(from+pth);
           FileOutputStream fos = new FileOutputStream(path);
           FileChannel fcin = fis.getChannel();
           FileChannel fcout = fos.getChannel();    
            // выполнить копирование файла
            fcin.transferTo(0, fcin.size(), fcout);    
            // завершить
            fcin.close();
            fcout.close();
            fis.close();
            fos.close();
       }
       }
       
       
    /**
     * Метод проверки, содержится ли файл в коллекции.
     * @param ob путь к файлу, который ищем.
     * @param dir название корневой директории, в котрой ищем файл.
     * @return  true, если файл найден, false.
     */
    public static boolean contain(String s, Set<FileProperties> dir) {
        //String s = ob1.toString();
        int i = 0;
            for (FileProperties d: dir) {
                String h = "C"+d.getPath().toString();
                
                if (h.equals(s)) 
                    i = i+1;
        }
            if (i>0) {
            return true; }
            else {
                return false; }
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

    @Override
    public void run() {
        
    }
}    
