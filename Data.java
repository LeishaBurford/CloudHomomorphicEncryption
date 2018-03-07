import java.math.BigInteger;
import java.util.Random;

public class Data {

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger N_ONE = BigInteger.valueOf(-1);


    private BigInteger value;
    private boolean encrypted;
    private BigInteger dataID; // this is essentially the encryption of value, with a different ciphertext


    private static BigInteger p;
    private Random rand;

    private static final int lambda = 4;
    private static final int eta = (lambda * lambda) - 1;
    private static final int multiplier = lambda;
    private static final int noise = lambda - 1;

    // for testing
    private boolean testing = true;

    public Data(BigInteger value, boolean encrypted) {
        rand = new Random();
        this.value = value;
        this.encrypted = encrypted;
        if(p == null) {
            System.out.println("generating p...");
            generatePrivateKey();
        }
        setDataID();
    }

    // this shouldn't be called on encrypted data
    private void setDataID() {
        if(encrypted) return;
        if (testing)
            System.out.println("Setting id not value");
        // get the value
        BigInteger temp = value;
        // now run the encryption to generate ciphertext
        encrypt();
        // value is now the dataID
        dataID = value;
        // restore data value
        value = temp;
        // restore encryption status
        encrypted = false;
    }

    // this one needs to be called if the data is already encrypted
    private void setDataID(BigInteger dataID) {

        if(!encrypted) return;
        this.dataID = dataID;
    }


    public BigInteger getDataID() {
        return dataID;
    }

    private void generatePrivateKey() {

        BigInteger lowerBound = BigInteger.valueOf( (long)Math.pow(2, eta - 1) );
        System.out.println("Lower bound is: " + lowerBound);
        // BigInteger p;
        // get an odd number in range [ 2^(eta - 1), 2^eta )
        do {
            // random in range [ 0, (2 ^ eta) )
            p = new BigInteger(eta, rand);
        } while (p.compareTo(lowerBound) < 0 || (p.mod(TWO)).compareTo(ZERO) == 0  );
        // ^ keep going until it satisfies conditions
        // TODO find more efficient solution to above loop

        // BigInteger b = BigInteger.probablePrime((int)Math.pow(eta, 2), rand);

        // p = b;


        if (testing)
            System.out.println("p is: "+ p );
    }

    public BigInteger encrypt(){
        if(encrypted)
            return value;
        if (testing)
            System.out.print("Value " + value);
        value = value.add(p.multiply(randomOfBits(multiplier))).add(TWO.multiply(randomOfBits(noise)));
        if (testing)
            System.out.println(" encrypted to " + value);
        encrypted = true;
        return value;
    }

    // There are two decrypt functions
    // This one decrypts with the provided private key
    // Should only be called by Alice
    public int decrypt() {
        if(!encrypted) {
            System.out.println("not encrypted");
            return value.intValue();
        }
        if (testing)
            System.out.print("Value " + value);
        value = (value.mod(p)).mod(TWO);
        if (testing)
            System.out.println(" decrypted to " + value);

        encrypted = false;
        return value.intValue();
    }

    private BigInteger randomOfBits(int numBits) {
        // BigInteger randomNumber = rand.nextBigInteger() % (BigInteger) Math.pow(2, numBits);
        BigInteger randomNumber = new BigInteger(numBits, rand);
        // return Math.abs(randomNumber);
        return randomNumber;
    }

    public BigInteger getValue() { return value;}
    // methods for circuit stuff

    // so because we don't worry about integer overflow, this is actually xor
    // this adds the value of a data object to this and returns this where value = this.value + other.value
    public Data add(BigInteger otherValue ){
        Data temp = new Data(this.value.add(otherValue), true);
        temp.dataID = this.dataID;
        return temp;
    }

    // this multiplies the value of a data object by this and returns
    // new data object where value = this.value * other.value
    public Data multiply(BigInteger otherValue) {
        // this.value = this.value * other.value;
        Data temp = new Data(this.value.multiply(otherValue), true);
        temp.dataID = this.dataID;
        return temp;
    }


}
