package common.exception;

import common.enumeration.ERROR_ENUM;
import common.utils.PlaceHolderUtil;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 17:00
 */
public class Rpcexception extends RuntimeException{

    private ERROR_ENUM error_enum;

    public Rpcexception (ERROR_ENUM error_enum,String msg,Object... args){
        super(PlaceHolderUtil.replace(msg, args));
        this.error_enum=error_enum;
    }




}
