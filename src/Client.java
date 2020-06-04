import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class Client {
    String host = "tcp://comp3310.ddns.net:1883";
    String clientID = "3310-<u6483085>";
    String userName = "students";
    String password = "33106331";

    int qos;
    String topic;
    int duration;

    int index = 0;
    int previous = 0;
    List<Integer> messages = new ArrayList<>();
    List<Long> times = new ArrayList<>();
    List<Integer> indexOfDuplicates = new ArrayList<>();
    List<Integer> indexOfOutOfOrders = new ArrayList<>();
    List<Integer> indexOfNotConsecutive = new ArrayList<>();

    MqttClient client;
    MqttConnectOptions option;

    Client(String topic, int qos, int duration) {
        this.topic = topic;
        this.qos = qos;
        this.duration = duration;
    }

    public void read() {
        try {
            client = new MqttClient(host, clientID, new MemoryPersistence());
            option = new MqttConnectOptions();
            option.setUserName(userName);
            option.setPassword(password.toCharArray());
            option.setCleanSession(true);
            option.setConnectionTimeout(10);
            option.setKeepAliveInterval(20);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("*****" + topic + " Connection Lost \n");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Integer message = Integer.parseInt(mqttMessage.toString());
                    if (message - previous != 1) {
                        indexOfNotConsecutive.add(index);
                    }
                    if (message < previous) {
                        indexOfOutOfOrders.add(index);
                    }
                    if (messages.contains(message)) {
                        indexOfDuplicates.add(index);
                    }
                    previous = message;
                    messages.add(message);
                    times.add(System.currentTimeMillis());
                    index++;
                    System.out.println("Topic: " + s);
                    System.out.println("QoS: " + mqttMessage.getQos());
                    System.out.println("Message: " + message + "\n");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("complete");
                }
            });
            client.connect(option);
            client.subscribe(topic, qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    float averageRate;
    float lossRate;
    float dupeRate;
    float oooRate;
    float mean;
    double stdVariation;

    public void statistic() {
        averageRate = (float) messages.size() / (float) (duration / 1000);
        int shouldSee = messages.get(messages.size() - 1) - messages.get(0) + 1;
        lossRate = (float) (shouldSee - messages.size()) / (float) shouldSee;
        dupeRate = (float) indexOfDuplicates.size() / (averageRate * 10);
        oooRate = (float) indexOfOutOfOrders.size() / (float) messages.size();

        List<Long> validGap = new ArrayList<>();
        for (int i = 1; i < messages.size(); i++) {
            if (!indexOfDuplicates.contains(i) && !indexOfNotConsecutive.contains(i) && !indexOfOutOfOrders.contains(i)) {
                validGap.add(times.get(i) - times.get(i - 1));
            }
        }
        long sum = 0;
        for (long gap : validGap) {
            sum += gap;
        }
        mean = (float) sum / (float) validGap.size();
        long diffSquareSum = 0;
        for (long gap : validGap) {
            diffSquareSum += Math.pow(gap - mean, 2);
        }
        stdVariation = Math.sqrt((double) diffSquareSum / (double) validGap.size());

        System.out.println("*****The Statistic of " + topic + ":");
        System.out.println("Average Rate: " + averageRate + " messages per second");
        System.out.println("Loss Rate: " + lossRate * 100 + "%");
        System.out.println("Duplicate Rate (per 10 seconds): " + dupeRate * 100 + "%");
        System.out.println("Out Of Order Rate: " + oooRate + "%");
        System.out.println("Mean Gap: " + mean + " milliseconds");
        System.out.println("Gap Variation: " + stdVariation + " milliseconds \n");
    }

    public void disconnect() throws MqttException {
        client.disconnect();
        client.close();
    }
}
