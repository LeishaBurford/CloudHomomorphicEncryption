import java.util.Random;

public class Testing {

    public static long[] value;
    public static Random rand;

    public static void main(String[] args) {

        rand = new Random();
        long numerator = rand.nextLong() % (long) Math.pow( (double) 2, 32);
        long q = numerator / 11;

        System.out.println(q + " " + numerator);
    }
}
