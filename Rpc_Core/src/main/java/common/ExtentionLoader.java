package common;

import common.enumeration.ERROR_ENUM;
import common.enumeration.ExtentionBaseType;
import common.exception.Rpcexception;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 20:26
 */
@Slf4j
public class ExtentionLoader {
    private static ExtentionLoader extentionLoader=new ExtentionLoader();

    private Map<String,Map<String,Object>> extentionMap=new HashMap<>();

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

    private void handleFile(File file) {
        log.info("开始读取文件:{}", file);
        String interfaceName=file.getName();
        try{
            Class<?> interfaceClass = Class.forName(interfaceName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split("=");
                if (kv.length != 2) {
                    log.error("配置行不是x=y的格式的:{}", line);
                    throw new Rpcexception(ERROR_ENUM.EXTENSION_CONFIG_FILE_ERROR, "配置行不是x=y的格式的:{}", line);
                }
                // 如果有任何异常，则跳过这一行
                try {
                    Class<?> impl = Class.forName(kv[1]);
                    if (!interfaceClass.isAssignableFrom(impl)) {
                        log.error("实现类{}不是该接口{}的子类", impl, interfaceClass);
                        throw new Rpcexception(ERROR_ENUM.EXTENSION_CONFIG_FILE_ERROR, "实现类{}不是该接口{}的子类", impl, interfaceClass);
                    }
                    Object o = impl.newInstance();
                    register(interfaceClass, kv[0], o);
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new Rpcexception(ERROR_ENUM.EXTENSION_CONFIG_FILE_ERROR, "实现类对象{}加载类或实例化失败", kv[1]);
                }
            }
            br.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Rpcexception(e, ERROR_ENUM.EXTENSION_CONFIG_FILE_ERROR, "接口对象{}加载类失败", file.getName());
        } catch (
        IOException e) {
            e.printStackTrace();
            throw new Rpcexception(e, ERROR_ENUM.EXTENSION_CONFIG_FILE_ERROR, "配置文件{}读取失败", file.getName());
        }
    }

    private void register(Class<?> interfaceClass, String alias, Object instance) {
        if(!extentionMap.containsKey(interfaceClass.getName())){
            extentionMap.put(interfaceClass.getName(),new HashMap<>());
        }
        log.info("注册bean: interface:" + interfaceClass + ",alias:{},instance:{}", alias, instance);
        extentionMap.get(interfaceClass.getName()).put(alias,instance);
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


    public <T> List<T> load(Class<T> interfaceClass) {
        if(!extentionMap.containsKey(interfaceClass.getName())){
            return Collections.EMPTY_LIST;
        }
        Collection<Object> values=extentionMap.get(interfaceClass.getName()).values();
        List<T> instances=new ArrayList<>();
        values.forEach(value -> instances.add(interfaceClass.cast(value)));
        return instances;
    }
}
