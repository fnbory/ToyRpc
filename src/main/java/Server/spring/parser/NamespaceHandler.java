package Server.spring.parser;

import Server.spring.bean.AddressBean;
import Server.spring.bean.PortBean;
import Server.spring.bean.ReferenceBean;
import Server.spring.bean.ServiceBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @Author: fnbory
 * @Date: 2019/9/13 21:49
 */
public class NamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("protocol", new ProtocolParser(PortBean.class));
        registerBeanDefinitionParser("address", new AddressParser(AddressBean.class));
        registerBeanDefinitionParser("service", new ServerParser(ServiceBean.class));
        registerBeanDefinitionParser("reference", new ReferenceParser(ReferenceBean.class));
    }
}
