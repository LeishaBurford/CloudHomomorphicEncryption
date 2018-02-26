import java.util.Arrays;
import java.util.Random;

public class Testing {

    public static long[] value;
    public static Random rand;


    public static long modularMultiplication(long a, long b, long mod) {
        long result = 0;

        // Update a if it is greater than or equal to mod
        a %= mod;

        while (b != 0)
        {
            // If b is odd, add a with result
            if (b % 2 != 0)
                result = (result + a) % mod;

            // Here we assume that doing 2*a
            // doesn't cause overflow
            a = (2L * a) % mod;

            b = b / 2;  // b = b / 2
        }

        return result;
    }
    public static void main(String[] args) {

        long a = 10123465234878998L;
        long b = 65746311545646431L;
        long  m = 10005412336548794L;
        System.out.println(modularMultiplication(a,b ,m));

    }
}
