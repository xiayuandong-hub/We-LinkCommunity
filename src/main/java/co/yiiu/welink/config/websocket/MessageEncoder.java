package co.yiiu.welink.config.websocket;

import co.yiiu.welink.util.JsonUtil;
import co.yiiu.welink.util.Message;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {
    @Override
    public String encode(Message o) {
        return JsonUtil.objectToJson(o);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
