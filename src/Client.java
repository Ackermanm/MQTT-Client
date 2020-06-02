import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class Client {
    String host = "tcp://comp3310.ddns.net:1883";
    String clientID = "3310-u6483085";
    String topic = "$SYS/#";
    String userName = "students";
    String password = "33106331";
    MqttClient client;
    MqttConnectOptions option;


}
