package Server.spring.serialization;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 17:06
 */
@Getter
@Setter
@ToString
public class Response implements Serializable {

    private String  requestId;

    private Boolean success;

    private Object object;

    private boolean async;

    private Throwable exception;

}
