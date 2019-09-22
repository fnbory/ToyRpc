package RpcTest.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/9/22 14:50
 */
@Data
public class User implements Serializable {

    private long id;
    private String name;
    private int sex;
    private LocalDate birthday;
    private String email;
    private String mobile;
    private String address;
    private String icon;
    private List<Integer> permissions;
    private int status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
