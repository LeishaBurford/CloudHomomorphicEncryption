import java.math.BigInteger;
import java.util.Random;

public class Testing {

    public static long[] value;
    public static Random rand;
    public final static BigInteger ZERO = BigInteger.ZERO;
    public final static BigInteger TWO = BigInteger.valueOf(2);
    public final static BigInteger ONE = BigInteger.ONE;
    public final static BigInteger N_ONE = BigInteger.valueOf(-1);

    private static final int lambda = 4;
    private static final int rho = lambda; // 4
    private static final int rhoPrime = lambda * 2; // 8
    // TODO must be greater than 2^multiplications * rhoPrime
    private static final int eta = (int) Math.pow( (double) lambda, (double) 2); // 16
    private static final int gamma = (int) Math.pow( (double) lambda, (double) 5); // 1024
    private static final int tau = gamma + lambda; // 1028
    private static BigInteger p;

    private static BigInteger generatePrivateKey() {
        // Generate a random prime integer p of size η bits
        p = BigInteger.probablePrime(eta, rand);
            System.out.println("\tValue of p is: " + p);
        return p;
        // if (testing)

    }

    private static BigInteger modOp(BigInteger a, BigInteger b) {
        BigInteger result;
        if (a.compareTo(ZERO) < 0 || b.compareTo(ZERO) < 0) {
            // so i don't think this will happen, but it might and i want to know if it does


            // here the remainder of a % b is calc'd as r = a - toNearestInt(a / b) * b
            // result = a.subtract(divRoundNearestInt(a, b).multiply(b));
            BigInteger temp = a.divide(b);

            result = a.subtract(temp.multiply(b));
            if(result.compareTo(ZERO) < 0 ){ // this happens when a is < 0

                temp = temp.add(N_ONE); // rounding up instead of down
                result = a.subtract(temp.multiply(b));
            }
            return result;
        } else {
            // TODO make more efficient? if time
            return (a.mod(b));
        }
    }

    private static BigInteger generateR(int exponent) {

        // random between 0 and 2^exponent - 1, inclusive
        BigInteger r = new BigInteger(exponent, rand);
        // determine if it should be negative
        boolean positive = rand.nextBoolean();
        r = (positive) ? r : r.multiply(N_ONE);

        // if (testing)
        //     System.out.println("\tValue of r is: " + r);
        return r;
    }

    // generates and returns a random in range [ 0, (2^gamma)/p )
    // TODO, check if the above is allowed
    private static BigInteger generateQ() {

        BigInteger q;
        System.out.println("p is: " + p);
        BigInteger upperLimit = (TWO.pow(gamma)).divide(p);
        do {
            // random between 0 and 2^(gamma - eta)- 1, inclusive
            q = new BigInteger(gamma - eta, rand);
        } while (q.compareTo(upperLimit) > 0);

        // if (testing)
        //     System.out.println("\tValue of q is: " + q);
        return q;
    }

    public static BigInteger divRoundNearestInt(BigInteger a, BigInteger b){
        // calc the quotient
        BigInteger quotient =  a.divide(b); // note this is auto rounded to zero
        // calc the remainder
        BigInteger remainder = a.mod(b);
        boolean isNegative = a.compareTo(ZERO) < 0;
        if ((remainder.subtract(b)).abs().compareTo(remainder) > 0 && !isNegative) {
            // remainder / b < 0.5, return number rounded to zero
            return quotient;
        }
        if ((remainder.subtract(b)).abs().compareTo(remainder) > 0 && isNegative) {
            // remainder / b > 0.5, return number rounded away from zero
            quotient = quotient.add(N_ONE);
            return quotient;
        }
        if ((remainder.subtract(b)).abs().compareTo(remainder) < 0 && !isNegative) {
            // remainder / b > 0.5, return number rounded away from zero
            quotient = quotient.add(ONE);
            return quotient;
        }
        if ((remainder.subtract(b)).abs().compareTo(remainder) < 0 && isNegative){
            // remainder / b > 0.5, return number rounded towards  zero
            // quotient = quotient.add(N_ONE);
            return quotient;
        }

        // I really hope this doesn't happen

        return quotient;
    }
    public static void main(String[] args) {
        rand = new Random();
        BigInteger a = BigInteger.valueOf(-51);
        BigInteger b = BigInteger.valueOf(8);
        BigInteger c = BigInteger.valueOf(51);
        BigInteger d = BigInteger.valueOf(8);

        System.out.println(a.divide(c) + " " + a. divide(d));
        System.out.println(b.divide(c) + " " + b. divide(d));

        System.out.println(modOp(a, b) + " " + modOp(c, d));
        // System.out.println(modOp(b, c) + " " + modOp(b, d));

        // System.out.println(b.subtract(N_ONE));
        // Integer a = 5;
        // Integer b = -1;
        // // System.out.println(b.compareTo(a));
        // BigInteger c = new BigInteger(4, rand);
        // for(int i = 0; i < 20; i++) {
        //     System.out.println(c);
        //     c = new BigInteger(4, rand);
        // }

        // System.out.println(decrypted);








    }
}
