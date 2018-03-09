import java.math.BigInteger;
import java.util.Random;

public class Data {

    // Using these to make code cleaner
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger TWO = BigInteger.valueOf(2);

    // value is the contents of data
    private BigInteger value;
    private boolean encrypted;
    // this is essentially the encryption of value, with a different ciphertext
    private BigInteger dataID;

    private static BigInteger p;
    private Random rand;

    private static final int lambda = 4;
    private static final int eta = (lambda * lambda) - 1; // 15
    private static final int multiplier = lambda; // 4
    private static final int noise = lambda - 1; // 3

    // for testing
    private boolean testing = false;

    public Data(BigInteger value, boolean encrypted) {
        rand = new Random();
        this.value = value;
        this.encrypted = encrypted;
        if(p == null) {
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


        // get an odd number in range [ 2^(eta - 1), 2^eta )
        do {
            // random in range [ 0, (2 ^ eta) )
            p = new BigInteger(eta, rand);
        } while (p.compareTo(lowerBound) < 0 || (p.mod(TWO)).compareTo(ZERO) == 0  );
        // ^ keep going until it satisfies conditions
        // TODO find more efficient solution to above loop

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

        BigInteger randomNumber = new BigInteger(numBits, rand);
        return randomNumber;
    }

    public BigInteger getValue() { return value;}


}
