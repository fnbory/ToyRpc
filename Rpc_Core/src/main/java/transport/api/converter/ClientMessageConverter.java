package transport.api.converter;

import common.domain.Message;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 17:02
 */
public abstract class ClientMessageConverter implements MessageConverter {

    public abstract Object convertMessage2Request(Message message);
    public abstract Message convertResponse2Message(Object response);

    @Override
    public Object convert2Object(Message message) {
        return convertMessage2Request(message);
    }

    @Override
    public Message convert2Message(Object object) {
        return convertResponse2Message(object);
    }

    public static ClientMessageConverter DEFAULT_IMPL = new ClientMessageConverter() {
        @Override
        public Object convertMessage2Request(Message message) {
            return message;
        }

        @Override
        public Message convertResponse2Message(Object response) {
            return (Message) response;
        }
    };
}
