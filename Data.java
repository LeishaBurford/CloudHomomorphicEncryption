import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class Data {

    private boolean testing = true;
    private long value; // this is the value of the data object
    private boolean encrypted; // whether or not value is encrypted
    // TODO, test that keys are the same for different objects
    private static long p; // private key, this needs to be the same for all Data objects
    private static long[] x; // public key, this needs to be the same for all Data objects
    private static Random rand = new Random();

    // security parameters and resulting bit-lengths
    // lambda = 2
    // row = 2
    // eta = 2^2 = 4
    // gamma = lambda ^ 5 = 32
    // t = gamma + lambda = 32 + 2 = 34
    private static int lambda = 2;
    private static int row = lambda;
    private static int eta = (int)Math.pow( (double) lambda, (double) 2); // must be > 2^multiplications * row'
    private static int gamma = (int) Math.pow( (double) lambda, (double) 5);;
    private static int tau = gamma + lambda;

    // default constructor assumes unencrypted value
    // TODO, check that keys are the same, even when using different constructors
    // TODO, change this to call other constructor
    public Data(long value) {
        this.value = value;
        encrypted = false;
        // check if keys have been assigned
        if(x == null) {

            generatePrivateKey();
            generatePublicKey();
        }
    }

    public Data(long value, boolean encrypted) {
        this.value = value;
        this.encrypted = encrypted;
        if(x == null) {

            generatePrivateKey();
            generatePublicKey();
        }
    }

    // encrypts and returns the value according to DGHV scheme
    // anyone can access the encrypted value
    public long encrypt() {
        if(encrypted)
            return value;

        return value;
    }


    // TODO this method should be restricted so only Alice class can use it
    public long decrypt() {
        if(!encrypted)
            return value;

        return value;
    }

    // lambda = 2
    // row = 2
    // eta = 2^2 = 4
    // gamma = lambda ^ 5 = 32
    // t = gamma + lambda = 32 + 2 = 34

    // TODO gamma is the bit length of the xi's
    private void generatePublicKey() {
        // For 0 ≤ i ≤ τ sample xi ← Dγ,ρ(p). (Outputx=q·p+r) Relabel the xi’s so that x0 is the largest.
        // Restart unless x0 is odd and [x0]p is even. Let pk = (x0,x1,...xτ) and sk = p.

        x = new long[tau];
        long max;
        int maxLocation;
        do {
            max = maxLocation = 0;
            for (int i = 0; i < tau; i++) {
                long r = generateR();
                long q = generateQ();
                x[i] = (q * p) + r;

                // keep track of the maximum element
                if (x[i] > max) {
                    maxLocation = i;
                    max = x[i];
                }
            }
        } while(max % 2 == 0 || max % p != 0); // Restart unless max is odd and [max]p is even.

        // swap x0 with the max element
        long temp = x[0];
        x[0] = max;
        x[maxLocation] = temp;
        if (testing)
            System.out.println("Value of x is: " + Arrays.toString(x));
    }
    private void generatePrivateKey() {
        // Generate a random prime integer p of size η bits
        BigInteger b = BigInteger.probablePrime((int)eta, rand);
        p = b.longValue();
        if (testing)
            System.out.println("Value of p is: " + p);
    }
    private long generateQ() {
        // random integer(long) from 0 to 2^gamma/p (exclusive)
        long q = rand.nextLong() % (long) (Math.pow( (double) gamma, 2) / p);
        if (testing)
            System.out.println("Value of q is: " + q);
        return q;
    }

    private long generateR() {
        // r←Z∩(−2ρ,2ρ)
        // random integer(long) between -2^row and 2^row (both exclusive)
        if (testing)
            System.out.println("In R, mod is: " + (long) (Math.pow( (double) 2, row)) );
        boolean positive = rand.nextBoolean();
        long r_temp = rand.nextLong() % (long) (Math.pow( (double) 2, row));
        long r = (positive) ? r_temp : (-1) * r_temp;
        if (testing)
            System.out.println("Value of r is: " + r);
        return r;
    }
}
