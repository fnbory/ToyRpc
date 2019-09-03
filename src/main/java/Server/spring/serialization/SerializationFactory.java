package Server.spring.serialization;

/**
 * @Author: fnbory
 * @Date: 2019/8/23 11:55
 */
public class SerializationFactory {

    public static Iserialization  resolve(String Serialname,String service){
        if(Serialname==null||Serialname==""){
            Serialname="jdk";
        }
        // log.info("服务:{} 将使用:{}作为序列方式", service, name);
        Iserialization iserialization=null;
        switch (Serialname){
            case "proto":
              //  iserialization=new Proto;
                break;
            case "jdk":
              //  iserialization = new JavaSerialize();
                break;
            default:
                //log.warn("未知的序列化方式:{}，使用jdk作为默认序列化方式", name);
                //iserialization = new JavaSerialize();
        }
        return iserialization;

    }
}
