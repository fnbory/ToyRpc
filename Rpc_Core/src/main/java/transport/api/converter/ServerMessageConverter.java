package transport.api.converter;

import common.domain.Message;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 20:01
 */
public abstract class ServerMessageConverter implements MessageConverter {

    public abstract Object convertMessage2Response(Message message);
    public abstract Message convertRequest2Message(Object response);

    @Override
    public Object convert2Object(Message message) {
        return convertMessage2Response(message);
    }


    @Override
    public Message convert2Message(Object object) {
        return convertRequest2Message(object);
    }

    public static ServerMessageConverter DEFAULT_IMPL=new ServerMessageConverter() {
        @Override
        public Object convertMessage2Response(Message message) {
            return message;
        }

        @Override
        public Message convertRequest2Message(Object response) {
            return (Message)response;
        }
    };
}
