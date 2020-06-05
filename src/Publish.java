import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Publish {
    private final String HOST = "tcp://comp3310.ddns.net:1883";
    private final String CLIENT_ID = "3310-u6483085";
    private final String USER_NAME = "students";
    private final String PASSWORD = "33106331";
    private final int QOS = 2;
    private final boolean RETAIN_FLAG = true;

    private MqttClient client;

    public Publish() throws MqttException {
        client = new MqttClient(HOST, CLIENT_ID, new MemoryPersistence());
    }

    public void connect() {
        MqttConnectOptions option = new MqttConnectOptions();
        option.setCleanSession(true);
        option.setUserName(USER_NAME);
        option.setPassword(PASSWORD.toCharArray());
        option.setConnectionTimeout(10);
        option.setKeepAliveInterval(20);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        try {
            client.connect(option);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) throws MqttException {
        MqttTopic mqttTopic = client.getTopic(topic);
        MqttDeliveryToken mqttDeliveryToken = mqttTopic.publish(message.getBytes(), QOS, RETAIN_FLAG);
        mqttDeliveryToken.waitForCompletion();
        System.out.println("Topic " + mqttTopic.toString() + " Publish Complete");
    }

    public void disconnect() throws MqttException {
        client.disconnect();
        client.close();
    }
}
