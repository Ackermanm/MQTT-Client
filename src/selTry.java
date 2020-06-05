

public class selTry {
    private static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                if (str.charAt(i) == '.')
                    return true;
                else
                    return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        System.out.println(isNumber("2323"));
    }
}
