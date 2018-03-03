import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Data {

    // cause BigInteger is annoying...
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private static final BigInteger N_ONE = BigInteger.valueOf(-1L);

    // TODO add a fileIdentifier that is also encrypted
    private boolean testing = false; // to print, or not to print all the data
    public BigInteger value; // this is the value of the data object

    private boolean encrypted; // whether or not value is encrypted
    private static BigInteger p; // private key, this needs to be the same for all Data objects
    private static BigInteger[] x; // public key, this needs to be the same for all Data objects
    private static Random rand;


    // security parameters and resulting bit-lengths
    private static final int lambda = 4;
    private static final int rho = lambda; // 4
    private static final int rhoPrime = lambda * 2; // 8
    // TODO must be greater than 2^multiplications * rhoPrime
    private static final int eta = (int) Math.pow( (double) lambda, (double) 2); // 16
    private static final int gamma = (int) Math.pow( (double) lambda, (double) 5);; // 1024
    private static final int tau = gamma + lambda; // 1028

    // value is the bit 1 or 0, or the encrypted value
    public Data(BigInteger value, boolean encrypted) {
        // initialize the random generator
        rand = new Random();
        if(testing)
            System.out.println("Initializing LongData object, value = " + value
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
    // value is 0 or 1 or the encrypted value
    public Data(BigInteger value) {
        this(value, false);
    }

    private void generatePrivateKey() {
        // Generate a random prime integer p of size η bits
        p = BigInteger.probablePrime(eta, rand);
        if (testing)
            System.out.println("\tValue of p is: " + p);
    }

    // TODO gamma is the bit length of the xi's -- (q * p) + r should be size gamma, determined by q.size
    private void generatePublicKey() {
        // For 0 ≤ i ≤ τ sample xi ← Dγ,ρ(p). (Outputx=q·p+r) Relabel the xi’s so that x0 is the largest.
        // Restart unless x0 is odd and [x0]p is even. Let pk = (x0,x1,...xτ) and sk = p.
        x = new BigInteger[tau];
        BigInteger max;
        int maxLocation;
        // if (testing)
        //     System.out.println("Generating x's");
        do {

            max = ZERO;
            maxLocation = 0;
            for (int i = 0; i < tau; i++) {
                BigInteger r = generateR(rho);
                BigInteger q = generateQ();

                x[i] = q.multiply(p).add(r);

                // if(testing)
                // System.out.println("\tComputed x_i is: " + x[i]);
                // keep track of the maximum element
                if (x[i].compareTo(max) == 1) {
                    maxLocation = i;
                    max = x[i];
                }
            }
        } while(max.mod(TWO).compareTo(ZERO) == 0 || (max.mod(p).mod(TWO)).compareTo(ZERO) != 0); // Restart unless max is odd and max % p is even.

        if (testing)
            System.out.println("\tValue of x[0] is: " + max);
        // swap x0 with the max element
        BigInteger temp = x[0];
        x[0] = max;
        x[maxLocation] = temp;
        if (testing)
            System.out.println("\tValue of x is: " + Arrays.toString(x));
    }

    // takes as argument either rho or rhoPrime (first used in keyGen, second in encryption)
    // generates and returns random integer between -(2^exponent) and 2^exponent (both exclusive)
    private BigInteger generateR(int exponent) {

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
    // so it's actually going to 2^(gamma - eta), where eta is the bit length of p
    // cause 2^gamma / 2^eta = 2^(gamma - eta)
    // TODO, check if the above is allowed
    private BigInteger generateQ() {

        // random between 0 and 2^(gamma - eta)- 1, inclusive
        BigInteger q = new BigInteger(gamma - eta, rand);
        // if (testing)
        //     System.out.println("\tValue of q is: " + q);
        return q;
    }

    // encrypts and returns the value according to DGHV scheme
    // anyone can access the encrypted value
    // TODO fix encryption
    public BigInteger encrypt() {
        if(encrypted)
            return value;

        // encryption is done as c = (value + 2r + 2 (sum of S)) mod x0
        // generate random subset S of x
        int subsetSize = rand.nextInt(x.length);
        if(testing)
            System.out.println("\tValue of subsetSize is: " + subsetSize );

        BigInteger[] S = randomSubset(subsetSize);
        BigInteger sumOfS = ZERO;
        // calculate sumOfS % x[0]
        for (int i = 0; i < subsetSize; i++) {
            // System.out.println("sumOfS " + sumOfS + " s[i] " + S[i] + " " + " x[0] " + x[0]);
            sumOfS = (sumOfS.add(S[i])).mod(x[0]);
        }
        if (testing)
            System.out.println("\tValue of subsetSum is: " + sumOfS);

        // the encryption of value
        BigInteger r = generateR(rhoPrime);
        System.out.println("c = (" + value + " + (2 * " + r + ") % " + x[0] + " + (2 * " + sumOfS +") % " + x[0] +
                ") % " + x[0]);

        // c = (m + 2* r + 2 * sumOfS ) % x[0]
        value = value.add(modOp(TWO.multiply(r), x[0])).add(modOp(TWO.multiply(sumOfS), x[0])).mod(x[0]);

        if (testing)
            System.out.println("\tValue encrypted to: " + value);

        encrypted = true;

        return value;
    }

    // so java doesn't calculate a % b correctly for our purposes when a is negative,
    // this fixes that
    private BigInteger modOp(BigInteger a, BigInteger b) {
        BigInteger result;
        if (a.compareTo(ZERO) < 0 || b.compareTo(ZERO) < 0) {
            // so i don't think this will happen, but it might and i want to know if it does
            if (testing && b.compareTo(ZERO) < 0)
                System.out.println(" ***** Mod is negative *****");

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



    // TODO this method should be restricted so only Alice class can use it
    // TODO fix decrypt -- likely that problem is in encrypt
    // returns the decrypted bit -- 0 or 1
    public int decrypt() {
        if(!encrypted)
            return value.intValue();
        // decryption is done as m = (c mod p) mod 2
        // TODO, take this out, its probably never called, just here for testing
        if (value.compareTo(ZERO) < 0)
            System.out.println("******** value is negative! *********");
        System.out.print("\tValue " + value );



        value = (value.mod(p)).mod(TWO);
        if (testing)
            System.out.println(" decrypted to: " + value);
        encrypted = false;
        return value.intValue();
    }

    // generates and returns a subset of x of size n
    // note a hashSet is used so that duplicates of x are not added
    public BigInteger[] randomSubset(int n) {
        HashSet<BigInteger> set = new HashSet<>(n);
        int filled = 0;
        int i = 0;
        boolean addElement = false;

        // go through x, randomly adding elements until n elements are added
        while (filled < n) {
            // randomly decide if element is added
            addElement = rand.nextBoolean();
            if (addElement) {
                if(set.add(x[i]))
                    filled++;
            }
            i = (i + 1) % x.length;
        }
        System.out.println("HashSet Size " + set.size() + " n " + n);
        // convert hashset back to array
        BigInteger[] S = new BigInteger[n];
        i = 0;
        for (BigInteger number : set) {
            S[i] = number;
            i++;
        }
        if(testing)
            System.out.println("\tSubset S: " + Arrays.toString(S));
        return S;
    }
}
