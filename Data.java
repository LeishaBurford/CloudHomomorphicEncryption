import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Data {

    // TODO add a fileIdentifier that is also encrypted
    private boolean testing = true;
    public long value; // this is the value of the data object

    private boolean encrypted; // whether or not value is encrypted
    private static long p; // private key, this needs to be the same for all Data objects
    private static long[] x; // public key, this needs to be the same for all Data objects
    private static Random rand = new Random();

    // security parameters and resulting bit-lengths
    // lambda = 2
    // rho = 2
    // rhoPrime = 4
    // eta = 2^2 = 4
    // gamma = lambda ^ 5 = 32
    // t = gamma + lambda = 32 + 2 = 34
    private static final int lambda = 4;
    private static final int rho = lambda;
    private static final int rhoPrime = lambda * 2;
    // TODO must be greater than 2^multiplications * rhoPrime
    private static final int eta = (int) Math.pow( (double) lambda, (double) 2);
    private static final int gamma = (int) Math.pow( (double) lambda, (double) 5);;
    private static final int tau = gamma + lambda;

    public Data(long value, boolean encrypted) {
        if(testing)
            System.out.println("Initializing Data object, value = " + value
                    + " -- " + ((encrypted)? "encrypted" : "unencrypted"));
        this.value = value;
        this.encrypted = encrypted;
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


    // this doesn't make a difference
    // public long simpleEncrypt(){
    //     // implements c = m + p * q + 2r
    //     if(encrypted)
    //         return value;
    //     long q = generateQ();
    //     // the encryption of value
    //     long r = generateR(rhoPrime);
    //     System.out.println("c = (" + value + " + (2 * " + r + ") % " + x[0] + " + (" + p + " * " + q +") % " + x[0] +
    //             ") % " + x[0]);
    //     System.out.println("c = (" + value + " + " + modularMultiplication(2L, r, x[0]) + " + "
    //             + modularMultiplication(p, q, x[0]) + ") % " + x[0]);
    //
    //     value = (value + modularMultiplication(2L, r, x[0])
    //             + modularMultiplication(p, q, x[0])) % x[0];
    //     // value = modularMultiplication(1, value + (2L * generateR(rhoPrime)) + (2L * (sumOfS)) , x[0]);
    //
    //
    //     if (testing)
    //         System.out.println("\tValue encrypted to: " + value);
    //
    //     encrypted = true;
    //     return 0;
    // }

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
            System.out.println("\tValue of subsetSize is: " + subsetSize );
        long[] S = randomSubset(subsetSize);
        long sumOfS = 0;
        for (int i = 0; i < subsetSize; i++) {
            sumOfS = (sumOfS + S[i]) % x[0];
        }
        if (testing)
            System.out.println("\tValue of subsetSum is: " + sumOfS);

        // the encryption of value
        long r = generateR(rhoPrime);
        System.out.println("c = (" + value + " + (2 * " + r + ") % " + x[0] + " + (2 * " + sumOfS +") % " + x[0] +
                ") % " + x[0]);
        System.out.println("c = (" + value + " + " + modularMultiplication(2L, r, x[0]) + " + "
                + modularMultiplication(2L, sumOfS, x[0]) + ") % " + x[0]);

        value = (value + modularMultiplication(2L, r, x[0])
                + modularMultiplication(2L, sumOfS, x[0]));
        // value = modularMultiplication(1, value + (2L * generateR(rhoPrime)) + (2L * (sumOfS)) , x[0]);


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
        // TODO, take this out, its probably never called, just here for testing
        if (value < 0)
            System.out.println("******** value is negative! *********");
        System.out.print("\tValue " + value );



        value = (value % p) % 2;
        if (testing)
            System.out.println(" decrypted to: " + value);
        encrypted = false;
        return value;
    }

    // generates and returns a subset of x of size n
    // note a hashSet is used so that duplicates of x are not added
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
        if(testing)
            System.out.println("\tSubset S: " + Arrays.toString(S));
        return S;
    }

    public long modularMultiplication(long a, long b, long mod) {
        long result = 0;

        // Java doesn't handle % when of form (-a) % b, so here we fix that
        if (a < 0 || b < 0 || mod < 0) {
            if (testing && mod < 0)
                System.out.println("Mod is negative");
            long numerator = a * b;
            result = numerator - Math.floorDiv(numerator, mod) * mod;
            return result;
        }

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

    // TODO gamma is the bit length of the xi's -- (q * p) + r should be size gamma, determined by q.size
    private void generatePublicKey() {
        // For 0 ≤ i ≤ τ sample xi ← Dγ,ρ(p). (Outputx=q·p+r) Relabel the xi’s so that x0 is the largest.
        // Restart unless x0 is odd and [x0]p is even. Let pk = (x0,x1,...xτ) and sk = p.
        x = new long[tau];
        long max = 0;
        int maxLocation;
        // if (testing)
        //     System.out.println("Generating x's");
        do {

            max = maxLocation = 0;
            for (int i = 0; i < tau; i++) {
                long r = generateR(rho);
                long q = generateQ();

                x[i] = (q * p) + r;

                // if(testing)
                // System.out.println("\tComputed x_i is: " + x[i]);
                // keep track of the maximum element
                if (x[i] > max) {
                    maxLocation = i;
                    max = x[i];
                }
            }

        } while(max % 2 == 0 || (max % p) % 2 != 0); // Restart unless max is odd and max % p is even.

        if (testing)
            System.out.println("\tValue of x[0] is: " + max);
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
        long q = rand.nextLong() % ((long) Math.pow( (double) 2, gamma) / p);
        // if (testing)
        //     System.out.println("\tValue of q is: " + q);
        return q;
    }

    // takes as argument either rho or rhoPrime (first used in keyGen, second in encryption)
    private long generateR(long exponent) {
        // random number(long) between -(2^exponent) and 2^exponent (both exclusive)
        long r = rand.nextLong() % (long) (Math.pow( (double) 2, exponent));
        // if (testing)
        //     System.out.println("\tValue of r is: " + r);
        return r;
    }
}
