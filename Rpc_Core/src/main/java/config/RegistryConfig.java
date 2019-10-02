package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import registry.ServiceRegistry;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 19:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryConfig {


    private ServiceRegistry serviceRegistry;

    private String address;

    public  void init(){
        if(serviceRegistry!=null){
            serviceRegistry.init();
        }
    }

    public void close(){
        if(serviceRegistry!=null){
            serviceRegistry.close();
        }
    }

}
