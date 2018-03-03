import java.math.BigInteger;
import java.util.Random;

public class Testing {

    public static long[] value;
    public static Random rand;
    public final static BigInteger ZERO = BigInteger.ZERO;
    public final static BigInteger N_ONE = BigInteger.valueOf(-1);

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

    private static BigInteger modOp(BigInteger a, BigInteger b) {
        BigInteger result;
        if (a.compareTo(ZERO) < 0 || b.compareTo(ZERO) < 0) {
            // so i don't think this will happen, but it might and i want to know if it does


            // here the remainder of a % b is calc'd as r = a - floor(a / b) * b
            // so when bigInteger does integer division, it rounds closest to zero, rather than to
            // lowest integer, so that causes problems when the numerator is negative
            // we fix that problem here
            BigInteger aOverMod = a.divide(b);
            // if the number is negative add -1
            if(aOverMod.compareTo(ZERO) < 0) {
                aOverMod = aOverMod.add(N_ONE);
            }
            result = a.subtract(aOverMod.multiply(b));
            return result;
        } else {
            // TODO make more efficient? if time
            return (a.mod(b));
        }
    }
    public static void main(String[] args) {

        long a = 10123465234878998L;
        long b = 65746311545646431L;
        BigInteger b2 = BigInteger.valueOf(10);
        BigInteger b1 = BigInteger.valueOf(3);
        BigInteger b3 = BigInteger.valueOf(6);

        System.out.println(modOp(b2, b1));

    }
}
