import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class Listener {
    private final String HOST = "tcp://comp3310.ddns.net:1883";
    private final String CLIENT_ID = "3310-<u6483085>";
    private final String USER_NAME = "students";
    private final String PASSWORD = "33106331";

    private String TOPIC;

    private List<Integer> list = new ArrayList<>();

    private MqttClient client;
    private MqttConnectOptions option;

    public Listener(String TOPIC) {
        this.TOPIC = TOPIC;
    }

    public void execute() {
        try {
            client = new MqttClient(HOST, CLIENT_ID, new MemoryPersistence());
            option = new MqttConnectOptions();
            option.setUserName(USER_NAME);
            option.setPassword(PASSWORD.toCharArray());
            option.setCleanSession(true);
            option.setConnectionTimeout(10);
            option.setKeepAliveInterval(20);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("*****" + TOPIC + " Connection Lost \n");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    list.add(Integer.parseInt(mqttMessage.toString()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("Complete");
                }
            });
            client.connect(option);
            client.subscribe(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void statistic() {
        int sumActiveClient = 0;
        for (int i : list) {
            sumActiveClient += i;
        }
        int aveActiveClient = sumActiveClient / list.size();

        System.out.println("Average " + TOPIC + " is:" + aveActiveClient);
    }

    public void disconnect() throws MqttException {
        client.disconnect();
        client.close();
    }

    public static void main(String[] args) throws MqttException {
        String[] sysTopic = {"$SYS/broker/clients/active", "$SYS/broker/heap/current", "$SYS/broker/heap/maximum", "$SYS/broker/load/messages/received/5min", "$SYS/broker/load/messages/sent/5min"};

        for (int i = 0; i < sysTopic.length; i++) {
            Listener listener = new Listener(sysTopic[i]);
            listener.execute();
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listener.disconnect();
            listener.statistic();
        }
    }
}
