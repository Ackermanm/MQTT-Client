
public class Q1 {
    static String[] topicSlow = {"counter/slow/q0", "counter/slow/q1", "counter/slow/q2"};
    public static void main(String[] args) {
        int duration = 1000 * 10;

        for (int i = 0; i < 3; i++) {
            try {
                Client client = new Client(topicSlow[i], i, duration);
                client.read();
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.disconnect();
                client.statistic();
            } catch (Exception e) {
            }
        }
    }
}
