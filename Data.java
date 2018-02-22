import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Data {

    // TODO add a fileIdentifier that is also encrypted
    private boolean testing = false;
    private long value; // this is the value of the data object
    private boolean encrypted; // whether or not value is encrypted
    private static long p; // private key, this needs to be the same for all Data objects
    private static long[] x; // public key, this needs to be the same for all Data objects
    private static Random rand = new Random();

    // security parameters and resulting bit-lengths
    // lambda = 2
    // row = 2
    // rowPrime = 4
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

    public Data(long value, boolean encrypted) {
        if(testing)
            System.out.println("Initializing Data object, value = " + value
                    + " -- " + ((encrypted)? "encrypted" : "unencrypted"));
        this.value = value;
        this.encrypted = encrypted;
        if(testing)
            System.out.println("Private key: " + p);
        if(x == null) {
            if (testing) {
                System.out.println("Keys need initializing");
                System.out.println("Generating the private key, p");
            }
            generatePrivateKey();
            if (testing)
                System.out.println("Generating the public keys, x[]");
            generatePublicKey();
        }
    }

    // initialize Data, default assumes value is unencrypted
    public Data(long value) {
        this(value, false);
    }

    public long getValue() {
        return value;
    }

    // TODO this is mostly for testing, shouldn't be a thing
    public void setPrivateKey(long p) {
        this.p = p;
    }

    // encrypts and returns the value according to DGHV scheme
    // anyone can access the encrypted value
    // TODO fix encryption
    public long encrypt() {
        if(encrypted)
            return value;
        // encryption is done as c = (value + 2r + 2 (sum of S)) mod x0
        // generate random subset S of x
        int subsetSize = rand.nextInt(x.length);
        if(testing)
            System.out.println("Value of subsetSize is: " + subsetSize );
        long[] S = randomSubset(subsetSize);
        long sumOfS = 0;
        for (int i = 0; i < subsetSize; i++) {
            sumOfS += S[i];
        }
        if (testing)
            System.out.println("\tValue of sum is: " + sumOfS + " mod " + p + " = " + sumOfS % p);

        // the encryption of value
        value = (value + (2 * generateR(rowPrime)) + (2 *(sumOfS))) % x[0];
        if (testing)
            System.out.println("\tValue encrypted to: " + value);
        encrypted = true;
        return value;
    }

    // TODO this method should be restricted so only Alice class can use it
    // TODO fix decrypt -- likely that problem is in encrypt
    public long decrypt() {
        if(!encrypted)
            return value;
        // decryption is done as m = (c mod p) mod 2
        value = (value % p) % 2;
        if (testing)
            System.out.println("\tValue decrypted to: " + value);
        encrypted = false;
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
                long r = generateR(row);
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

    // takes as argument either row or rowPrime (first used in keyGen, second in encryption)
    private long generateR(long exponent) {
        // random number(long) between -2^exponent and 2^exponent (both exclusive)
        // if (testing)
            // System.out.println("In R, mod is: " + (long) (Math.pow( (double) 2, rowPrime)) );
        long r = rand.nextLong() % (long) (Math.pow( (double) 2, exponent));
        if (testing)
            System.out.println("\tValue of r is: " + r);
        return r;
    }
}
