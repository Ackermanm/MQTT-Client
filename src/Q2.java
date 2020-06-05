import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;

public class Q2 {
    public static void main(String[] args) throws MqttException {
        Publish p = new Publish();
        p.connect();
        p.publish("studentreport/u6483085/language", "Java");
        p.publish("studentreport/u6483085/network", "wifi");
        p.publish("studentreport/u6483085/location", "Canberra");
        p.disconnect();

        String[][] topicAll = {{"counter/slow/q0", "counter/slow/q1", "counter/slow/q2"}, {"counter/fast/q0", "counter/fast/q1", "counter/fast/q2"}};
        int duration = 1000 * 60 * 5;

        for (int i = 0; i < 3; i++) {
            try {
                Client client = new Client(topicAll[0][i], i, duration);
                client.execute();
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.disconnect();
                client.statistic();
                client.push();
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < 3; i++) {
            try {
                Client client = new Client(topicAll[1][i], i, duration);
                client.execute();
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.disconnect();
                client.statistic();
                client.push();
            } catch (Exception e) {
            }
        }
    }
}
