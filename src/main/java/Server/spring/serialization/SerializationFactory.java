package Server.spring.serialization;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: fnbory
 * @Date: 2019/8/23 11:55
 */
@Slf4j
public class SerializationFactory {

    private SerializationFactory() {
        throw new RuntimeException("实例化异常");
    }

    public static Iserialization  resolve(String name,String service){
        if(name==null||name==""){
            name="jdk";
        }
        log.info("服务:{} 将使用:{}作为序列方式", service,name );
        Iserialization iserialization=null;
        switch (name){
            case "proto":
                //iserialization=new Proto;
                break;
            case "jdk":
                iserialization = new JavaSerialize();
                break;
            default:
                log.warn("未知的序列化方式:{}，使用jdk作为默认序列化方式", name);
                iserialization = new JavaSerialize();
        }
        return iserialization;

    }
}
