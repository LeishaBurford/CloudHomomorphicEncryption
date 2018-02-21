import java.util.Random;

public class Testing {

    public static long[] value;
    public static Random rand;

    public static void main(String[] args) {
        rand = new Random();
        long x = 0;
        for (int i = 0; i < 10; i++) {
            x = rand.nextLong();
            System.out.println(x + " " + x % 2);
        }

    }
}
