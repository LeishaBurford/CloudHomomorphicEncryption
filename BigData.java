import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class BigData {

    // cause BigInteger is annoying...
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger N_ONE = BigInteger.valueOf(-1);

    // TODO add a fileIdentifier that is also encrypted
    private boolean testing = true; // to print, or not to print all the data
    public BigInteger value; // this is the value of the data object

    private boolean encrypted; // whether or not value is encrypted
    private static BigInteger p; // private key, this needs to be the same for all BigData objects
    private static BigInteger[] x; // public key, this needs to be the same for all BigData objects
    private static Random rand;


    // security parameters and resulting bit-lengths
    private static final int lambda = 4;
    private static final int rho = lambda; // 4
    private static final int rhoPrime = lambda * 2; // 8
    // TODO must be greater than 2^multiplications * rhoPrime
    private static final int eta = (int) Math.pow( (double) lambda, (double) 2); // 16
    private static final int gamma = (int) Math.pow( (double) lambda, (double) 5); // 1024
    private static final int tau = gamma + lambda; // 1028

    // value is the bit 1 or 0, or the encrypted value
    public BigData(BigInteger value, boolean encrypted) {
        // initialize the random generator
        rand = new Random();
        if(testing)
            System.out.println("Initializing Big object, value = " + value
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

    // initialize BigData, default assumes value is unencrypted
    // value is 0 or 1 or the encrypted value
    public BigData(BigInteger value) {
        this(value, false);
    }

    private void generatePrivateKey() {
        // Generate a random integer in range [0, 2^eta)
        // now to limit the lower boundary to 2^(eta - 1) and ensure p is odd
        do {
            p = new BigInteger(eta, rand);
            // p = p.add(ONE.add((TWO.pow(eta).subtract(TWO.pow(eta - 1))).abs()));
        } while(p.mod(TWO).compareTo(ZERO) == 0 || p.compareTo(TWO.pow(eta - 1)) < 0);

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
        } while((max.mod(TWO)).compareTo(ZERO) == 0 || ((max.mod(p)).mod(TWO)).compareTo(ZERO) != 0);
        // ^ Restart unless max is odd and max % p is even.

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
        //         System.out.println("\tValue of r is: " + r);
        return r;
    }

    // generates and returns a random in range [ 0, (2^gamma)/p )
    // TODO, check if the above is allowed
    private BigInteger generateQ() {

        BigInteger q;
        BigInteger upperLimit = (TWO.pow(gamma)).divide(p);
        do {
            // random in range [0, 2^gamma)
            q = new BigInteger(gamma - 1, rand);
        } while (q.compareTo(upperLimit) >= 0 || q.mod(TWO).compareTo(BigInteger.ZERO) == 0);
        // ^ ensure its not odd, and not greater than (2^gamma) / p

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
        BigInteger r;
        BigInteger sumOfS;
        do {
            // encryption is done as c = (value + 2r + 2 (sum of S)) mod x0
            // generate random subset S of x
            int subsetSize = rand.nextInt(x.length);
            if (testing)
                System.out.println("\tValue of subsetSize is: " + subsetSize);

            BigInteger[] S = randomSubset(subsetSize);
            sumOfS = ZERO;
            // calculate sumOfS % x[0]
            for (int i = 0; i < subsetSize; i++) {
                // System.out.println("sumOfS " + sumOfS + " s[i] " + S[i] + " " + " x[0] " + x[0]);
                sumOfS = (sumOfS.add(S[i]));
            }
            if (testing)
                System.out.println("\tValue of subsetSum is: " + sumOfS);

            // the encryption of value
            r = generateR(rhoPrime);
            BigInteger q = generateQ();
            System.out.println("c = (" + value + " + (2 * " + r + ") + " + "(2 * " + sumOfS + ") " +
                    ") % " + x[0]);

            // c = (m + 2* r + 2 * sumOfS ) % x[0]
        } while ((TWO.multiply(r)).abs().compareTo(p.divide(TWO).abs()) >= 0);
        // ^ enforcing that |2r| < |p/2|
        if (testing)
            System.out.println(TWO.multiply(r) + " " + p.divide(TWO));

        // the encryption
        value = ((value.add(modOp(TWO.multiply(r), x[0]))).add(modOp(TWO.multiply(sumOfS), x[0]))).mod(x[0]);
        // value = modOp((value.add(TWO.multiply(r))).add(p.multiply(q)), x[0]);
        // if (testing)
            System.out.println("\tValue encrypted to: " + value);

        encrypted = true;
        return value;
    }

    // so java doesn't calculate a % b correctly for our purposes when a is negative,
    // this fixes that
    private BigInteger modOp(BigInteger a, BigInteger b) {
        BigInteger result;
        if (a.compareTo(ZERO) < 0 || b.compareTo(ZERO) < 0) {
            // here the remainder of a % b is calc'd as r = a - toNearestInt(a / b) * b
            // result = a.subtract(divRoundNearestInt(a, b).multiply(b));
            BigInteger temp = a.divide(b);

            result = a.subtract(temp.multiply(b));
            if(result.compareTo(ZERO) < 0 ){

                temp = temp.add(N_ONE); // rounding up instead of down
                result = a.subtract(temp.multiply(b));
            }
            return result;
        } else {
            // TODO make more efficient? if time
            return (a.mod(b));
        }
    }

    // so when bigInteger does integer division, it rounds closest to zero, rather than to
    // nearest integer, so that causes problems when the numerator is negative
    // computes a / b, rounded to nearest integer, rather than to 0
    public BigInteger divRoundNearestInt(BigInteger a, BigInteger b){
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
        if (testing)
            System.out.println("######## Decimal is 0.5 #######");
        return quotient;
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

        System.out.println("\tValue " + value );
        value = (value.mod(p)).mod(TWO);
        if (testing)
            System.out.println(" decrypted to: \n" + value);
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
