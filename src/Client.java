import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private final String HOST = "tcp://comp3310.ddns.net:1883";
    private final String CLIENT_ID = "3310-<u6483085>";
    private final String USER_NAME = "students";
    private final String PASSWORD = "33106331";

    private int QoS;
    private String TOPIC;
    private int duration;

    private int index = 0;
    private int previous = 0;
    private List<Integer> messages = new ArrayList<>();
    private List<Long> times = new ArrayList<>();
    private List<Integer> indexOfDuplicates = new ArrayList<>();
    private List<Integer> indexOfOutOfOrders = new ArrayList<>();
    private List<Integer> indexOfNotConsecutive = new ArrayList<>();

    private MqttClient client;
    private MqttConnectOptions option;

    public Client(String TOPIC, int QoS, int duration) {
        this.TOPIC = TOPIC;
        this.QoS = QoS;
        this.duration = duration;
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
                    if (s.contains("counter")) {
                    if (isNumber(mqttMessage.toString())) {
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
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("Complete");
                }
            });
            client.connect(option);
            client.subscribe(TOPIC, QoS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float averageRate;
    private float lossRate;
    private float dupeRate;
    private float oooRate;
    private float mean;
    private double stdVariation;

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

        System.out.println("*****The Statistic of " + TOPIC + ":");
        System.out.println("Average Rate: " + averageRate + " messages per second");
        System.out.println("Loss Rate: " + lossRate * 100 + "%");
        System.out.println("Duplicate Rate (per 10 seconds): " + dupeRate * 100 + "%");
        System.out.println("Out Of Order Rate: " + oooRate + "%");
        System.out.println("Mean Gap: " + mean + " milliseconds");
        System.out.println("Gap Variation: " + stdVariation + " milliseconds \n");
    }

    public void push() throws MqttException {
        String topic = "";
        if (TOPIC.contains("slow")) {
            topic = "studentreport/u6483085/slow/" + TOPIC.substring(TOPIC.length() - 1) + "/";
        }
        if (TOPIC.contains("fast")) {
            topic = "studentreport/u6483085/fast/" + TOPIC.substring(TOPIC.length() - 1) + "/";
        }
        Publish p = new Publish();
        p.connect();
        p.publish(topic + "recv", "" + averageRate);
        p.publish(topic + "loss", "" + lossRate * 100 + "%");
        p.publish(topic + "dupe", "" + dupeRate * 100 + "%");
        p.publish(topic + "ooo", "" + oooRate * 100 + "%");
        p.publish(topic + "gap", "" + mean);
        p.publish(topic + "gvar", "" + stdVariation);
        p.disconnect();
    }

    public void disconnect() throws MqttException {
        client.disconnect();
        client.close();
    }

    private boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }
}
