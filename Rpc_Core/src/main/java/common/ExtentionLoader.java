package common;

import common.enumeration.ERROR_ENUM;
import common.enumeration.ExtentionBaseType;
import common.exception.Rpcexception;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 20:26
 */
@Slf4j
public class ExtentionLoader {
    private static ExtentionLoader extentionLoader=new ExtentionLoader();

    private Map<String,Map<String,Map<String,Object>>> extentionMap=new HashMap<>();

    public static ExtentionLoader getInstance(){
        return extentionLoader;
    }

    public void loadResurce() {
        URL url=this.getClass().getResource("/rpc.xml");
        String fileurl=url.getFile();
        File file=new File(fileurl);
        File[] files=file.listFiles();
        for(File file1:files){
            handleFile(file1);
        }
        log.info("配置文件读取完毕");
    }

    private void handleFile(File file1) {

    }

    public    <T> T load(Class enum_type,String type,Class<T> interfaceClass){
        ExtentionBaseType<T> extentionBaseType=(ExtentionBaseType) Enum.valueOf(enum_type,type.toUpperCase());
        if(extentionBaseType!=null){
            return extentionBaseType.getInstance();
        }
        if(!extentionMap.containsKey(interfaceClass.getName())){
            throw new Rpcexception(ERROR_ENUM.NO_SUPPORTED_INSTANCE, "{} 没有可用的实现类", interfaceClass);
        }
        Object o=extentionMap.get(interfaceClass.getName()).get(type);
        if(o==null){
            throw new Rpcexception(ERROR_ENUM.NO_SUPPORTED_INSTANCE, "{} 没有可用的实现类", interfaceClass);
        }
        return interfaceClass.cast(o);
    }


}
