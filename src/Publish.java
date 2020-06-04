import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Publish {
    String host = "tcp://comp3310.ddns.net:1883";
    String clientID = "3310-u6483085";
    String userName = "students";
    String password = "33106331";

    int qos;
    String topic;
    String content;

    MqttClient client;

    Publish(String topic, int qos, String content) throws MqttException {
        this.topic = topic;
        this.qos = qos;
        this.content = content;
        client = new MqttClient(host, clientID, new MemoryPersistence());
    }

    public void connect() {
        MqttConnectOptions option = new MqttConnectOptions();
        option.setCleanSession(true);
        option.setUserName(userName);
        option.setPassword(password.toCharArray());
        option.setConnectionTimeout(10);
        option.setKeepAliveInterval(20);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

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

    public void publish() throws MqttException {
        MqttTopic mqttTopic = client.getTopic(topic);
        MqttDeliveryToken courier = mqttTopic.publish(content.getBytes(), qos, true);
        courier.waitForCompletion();
        System.out.println("Publishing on topic: "+ mqttTopic.toString());
        System.out.println("Message: "+ content);
        System.out.println("Publish completely");
    }

    public void disconnect() throws MqttException {
        client.disconnect();
        client.close();
    }
}
