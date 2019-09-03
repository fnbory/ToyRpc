package Server;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/9/3 13:26
 */

@Getter
@Setter
public class Consumer implements Serializable {

    private String host;
    private String serviceName;
    private String version;
    private Integer timeout;
}
