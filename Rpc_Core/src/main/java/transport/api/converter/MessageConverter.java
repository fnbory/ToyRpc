package transport.api.converter;

import common.domain.Message;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 20:02
 */
public interface MessageConverter {

    Object convert2Object(Message message);

    Message convert2Message(Object object);
}
