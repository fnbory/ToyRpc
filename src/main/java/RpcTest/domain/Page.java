package RpcTest.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/9/22 14:51
 */
@Data
public class Page<T> implements Serializable {
    private int pageNo;
    private int total;
    private List<T> result;
}
