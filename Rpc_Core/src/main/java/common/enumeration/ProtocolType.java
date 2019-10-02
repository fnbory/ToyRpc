package common.enumeration;

import protocol.api.Protocol;
import protocol.api.support.AbstractProtocol;
import protocol.http.HttpProtocol;
import protocol.injvm.InjvmProtocol;
import protocol.toy.ToyProtocol;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 17:52
 */
public enum ProtocolType implements  ExtentionBaseType{

    HTTP(new HttpProtocol()),TOY(new ToyProtocol()),INJVM(new InjvmProtocol());

    ProtocolType(AbstractProtocol protocol){
        this.protocol=protocol;
    }

    private AbstractProtocol protocol;

    @Override
    public Protocol getInstance() {
        return protocol;
    }
}
