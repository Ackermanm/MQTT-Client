
public class Q2 {
    public static void main(String[] args) {
        String[][] topicAll = {{"counter/slow/q0", "counter/slow/q1", "counter/slow/q2"}, {"counter/fast/q0", "counter/fast/q1", "counter/fast/q2"}};
        int duration = 1000 * 10;

        for (int i = 0; i < 3; i++) {
            try {
                Client client = new Client(topicAll[0][i], i, duration);
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

        for (int i = 0; i < 3; i++) {
            try {
                Client client = new Client(topicAll[1][i], i, duration);
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
