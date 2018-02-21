import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
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
    private static final int lambda = 2;
    private static final int row = lambda;
    private static final int rowPrime = lambda * 2;
    // TODO must be greater than 2^multiplications * rowPrime
    private static final int eta = (int) Math.pow( (double) lambda, (double) 2);
    private static final int gamma = (int) Math.pow( (double) lambda, (double) 5);;
    private static final int tau = gamma + lambda;

    // default constructor assumes unencrypted value
    // TODO, check that keys are the same, even when using different constructors
    // TODO, change this to call other constructor
    public Data(long value) {
        if(testing)
            System.out.println("Initializing Data object, value = " + value + " -- unencrypted");
        this.value = value;
        encrypted = false;
        // check if keys have been assigned
        if(x == null) {
            if (testing)
                System.out.println("Generating the private key, p");
            generatePrivateKey();
            if (testing)
                System.out.println("Keys need initializing");
                System.out.println("Generating the public keys, x[]");
            generatePublicKey();
        }
    }

    public Data(long value, boolean encrypted) {
        if(testing)
            System.out.println("Initializing Data object, value = " + value
                    + " -- " + ((encrypted)? "encrypted" : "unencrypted"));
        this.value = value;
        this.encrypted = encrypted;
        if(x == null) {
            if (testing)
                System.out.println("Keys need initializing");
                System.out.println("Generating the private key, p");
            generatePrivateKey();
            if (testing)
                System.out.println("Generating the public keys, x[]");
            generatePublicKey();
        }
    }

    // encrypts and returns the value according to DGHV scheme
    // anyone can access the encrypted value
    // TODO actually encrypt
    public long encrypt() {
        if(encrypted)
            return value;
        // generate random subset S of x
        int subsetSize = rand.nextInt(x.length);
        long[] S = randomSubset(subsetSize);

        return value;
    }

    // generates and returns a subset of x of size n
    // note a hashSet is used to that duplicates of x are not added
    public long[] randomSubset(int n) {
        HashSet<Long> set = new HashSet<>();

        int filled = 0;
        int i = 0;
        boolean addElement = false;
        while (filled < n) {
            // randomly decide if element is added
            addElement = rand.nextBoolean();
            if (addElement) {
                set.add(x[i]);
                filled++;
            }
            i = (i + 1) % x.length;
        }

        // convert hashset back to array
        long[] S = new long[n];
        i = 0;
        for (Long value : set) {
            S[i] = value;
            i++;
        }
        return S;
    }


    // TODO this method should be restricted so only Alice class can use it
    // TODO actually decrypt
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

    // TODO gamma is the bit length of the xi's -- (q * p )+ r should be size gamma, determined by q.size
    private void generatePublicKey() {
        // For 0 ≤ i ≤ τ sample xi ← Dγ,ρ(p). (Outputx=q·p+r) Relabel the xi’s so that x0 is the largest.
        // Restart unless x0 is odd and [x0]p is even. Let pk = (x0,x1,...xτ) and sk = p.
        x = new long[tau];
        long max = 0;
        int maxLocation;
        do {
            if (testing)
                System.out.println("Generating x's");
            max = maxLocation = 0;
            for (int i = 0; i < tau; i++) {
                long r = generateR();
                long q = generateQ();
                x[i] = (q * p) + r;

                if(testing)
                    System.out.println("\tComputed x_i is: " + x[i]);
                // keep track of the maximum element
                if (x[i] > max) {
                    maxLocation = i;
                    max = x[i];
                }
            }
            if (testing)
                System.out.println("\tValue of max is: " + max);
        } while(max % 2 == 0 || (max % p) % 2 != 0); // Restart unless max is odd and max % p is even.

        // swap x0 with the max element
        long temp = x[0];
        x[0] = max;
        x[maxLocation] = temp;
        if (testing)
            System.out.println("\tValue of x is: " + Arrays.toString(x));
    }

    private void generatePrivateKey() {
        // Generate a random prime integer p of size η bits
        BigInteger b = BigInteger.probablePrime((int)eta, rand);
        p = b.longValue();
        if (testing)
            System.out.println("\tValue of p is: " + p);
    }
    private long generateQ() {
        // random integer(long) from 0 to (2^gamma)/p (exclusive)
        long q = rand.nextLong() % (long) (Math.pow( (double) 2, gamma) / p);
        if (testing)
            System.out.println("\tValue of q is: " + q);
        return q;
    }

    private long generateR() {
        // r←Z∩(−2ρ,2ρ)
        // random integer(long) between -2^row' and 2^row' (both exclusive)
        // if (testing)
            // System.out.println("In R, mod is: " + (long) (Math.pow( (double) 2, rowPrime)) );
        boolean positive = rand.nextBoolean();
        long r = rand.nextLong() % (long) (Math.pow( (double) 2, rowPrime));
        // long r = (positive) ? r_temp : (-1) * r_temp;
        if (testing)
            System.out.println("\tValue of r is: " + r);
        return r;
    }
}
