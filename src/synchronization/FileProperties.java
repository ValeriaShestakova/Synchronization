/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import java.io.Serializable;

/**
 *
 * @author Валерия
 */

/**
 * @param <P> Путь
 * @param <T> Время изменения
 * @param <D> Тип (директория или файл)
 */
public class FileProperties < P, T, D> implements Serializable, Comparable {
    private P fPath;
    private T fTime;
    private D fType;
    
     /**
     * Конструктор класса
     * @param fPath путь к файлу (каталогу)
     * @param fTime время модификации файла
     * @param fType тип файла
     */
    FileProperties(P fPath, T fTime, D fType) {
        this.fPath = fPath;
        this.fTime = fTime;
        this.fType = fType;
    }
    
    /**
     * Метод возвращает путь к файлу (каталогу).
     * @return путь к файлу
     */
    public P getPath() {
        return fPath;
    }
    
    
    /**
     * Метод возвращает время последней модификации файла.
     * @return время последней модификации
     */
    public T getModifiedTime() {
        return fTime;
    }
    
   
    /**
     * Метод возвращает тип файла, например, является он директорией или нет.
     * @return тип файла
     */
    public D isDirectory() {
        return fType;
    }
    
    
    /**
     * Метод испльзуется в паре с методом equals для установления равенства 
     * или неравенства двух экземпляров данного класса.
     * @return хеш-код для одноначной (уникальной) идентификации объекта.
     */
    @Override
    public int hashCode(){
        int hash = 37;
        hash = hash*17 + (fPath == null ? 0 : fPath.hashCode());
        //hash = hash*17 + (fType == null ? 0 : fType.hashCode());
        //hash = hash*17 + (fTime == null ? 0 : fTime.hashCode());
        return hash;
    }

    /**
     * Метод испльзуется в паре с методом hashCode для установления равенства 
     * или неравенства двух экземпляров данного класса.
     * @param obj экземпляр класса, который нужно сравнить с текущим.
     * @return возвращает true, если данный экземпляр класса эквивалентен 
     * экземпляру переданному во входном параметре.
     */
    @Override
   public boolean equals(Object obj) {
       if (obj == null) {
            return false;
        } 
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileProperties<P, T, D> other = (FileProperties<P, T, D>) obj;
        if (this.fPath != other.fPath && (!this.fPath.equals(other.fPath))) {
            return false;
        }
        if (this.fType != other.fType && (!this.fType.equals(other.fType))) {
            return false;
        }
        /*if (this.fTime != other.fTime && 
           (!this.fTime.equals(other.fTime))) {
            return false;
        } */
       return true;
    }
      /**
     * Переопределение метода сравнения объектов класса (используется в TreeSet)
     * @param t объект для сравнения
     * @return результат сравнения
     */
    @Override
    public int compareTo(Object t) {
        return ((String)this.getPath()).compareTo((String)((FileProperties)t).getPath());
    }
}
