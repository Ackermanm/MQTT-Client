import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class selTry {

    private static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws MqttException {
        Map<String, List<Integer>> map = new HashMap<>();
        List<Integer> list0 = new ArrayList<>();
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        map.put("q0", list0);
        map.put("q1", list1);
        map.put("q2", list2);

        for (int i = 0; i < 3; i++) {
            list0.add(i);
        }
        for (int i = 3; i < 7; i++) {
            list1.add(i);
        }
        for (int i = 7; i < 12; i++) {
            list2.add(i);
        }

        map.get("q2").add(99);
        //map.put("q2", list0);

        System.out.println(map);
        System.out.println(map.size());

        System.out.println(map.get("q2"));
        System.out.println(map.get("q2").size());

        System.out.println(isNumber("2425235a"));


        List<Long> times = new ArrayList<>();
        times.add(1591281725447L);
        times.add(1591281726310L);
        times.add(1591281727392L);
        times.add(1591281728416L);
        times.add(1591281729440L);
        times.add(1591281730383L);
        times.add(1591281731387L);
        times.add(1591281732410L);
        times.add(1591281733332L);
        System.out.println(times);

        List<Long> validGap = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
                validGap.add(times.get(i) - times.get(i - 1));
        }
        System.out.println(validGap);
        long sum = 0;
        for (long gap : validGap) {
            sum += gap;
        }
        System.out.println(sum);
        float mean = (float) sum / (float) validGap.size();
        System.out.println(mean);
        long diffSquareSum = 0;
        for (long gap : validGap) {
            diffSquareSum += Math.pow(gap - mean, 2);
        }
        double stdVariation = Math.sqrt((double) diffSquareSum / (double) validGap.size());

        Publish p = new Publish("studentreport/u6483085/q0", 2, "Hello");
        p.connect();
        p.publish();
        p.disconnect();
    }
}
