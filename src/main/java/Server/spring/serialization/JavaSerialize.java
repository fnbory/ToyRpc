package Server.spring.serialization;

import java.io.*;

/**
 * @Author: fnbory
 * @Date: 2019/9/7 16:56
 */
public class JavaSerialize implements Iserialization {

    @Override
    public <T> byte[] serialize(T obj) {
        if(obj==null){
            return null;
        }
        ByteArrayOutputStream baos=new ByteArrayOutputStream(1024);
        try {
            ObjectOutputStream oos=new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  baos.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> tClass) {
        if(bytes==null){
            return null;
        }
        try {
            ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(bytes));
            return tClass.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to deserialize object");
        }
    }


    @Override
    public String toString() {
        return "jdk";
    }
}
