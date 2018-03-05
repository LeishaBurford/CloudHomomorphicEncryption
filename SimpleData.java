import java.math.BigInteger;
import java.util.Random;

public class SimpleData {
    private long value;
    private boolean encrypted;
    private long dataID; // this is essentially the encryption of value, with a different ciphertext
    private static long p;
    private Random rand;

    private static int lambda = 4;
    private static int multiplier = lambda;
    private static int noise = lambda - 1;

    // for testing
    private boolean testing = true;

    public SimpleData(long value, boolean encrypted) {
        rand = new Random();
        this.value = value;
        this.encrypted = encrypted;
        if(p == 0) {
            System.out.println("generating p...");
            generatePrivateKey();
        }
        setDataID();
    }

    // this shouldn't be called on encrypted data
    private void setDataID() {
        if(encrypted) return;
        // get the value
        long temp = value;
        // now run the encryption to generate ciphertext
        encrypt();
        // value is now the dataID
        dataID = value;
        // restore data value
        value = temp;
        // restore encryption status
        encrypted = false;

    }

    public long getDataID() {
        return dataID;
    }

    private void generatePrivateKey() {
        BigInteger b = BigInteger.probablePrime((int)Math.pow(lambda, 2), rand);
        p = b.longValue();
        if (testing)
            System.out.println("p is: "+ p );
    }

    public long encrypt(){
        if(encrypted)
            return value;
        if (testing)
            System.out.print("Value " + value);
        value = value + p * randomOfBits(multiplier) + 2 * randomOfBits(noise);
        if (testing)
            System.out.println(" encrypted to " + value);
        encrypted = true;
        return value;
    }

    public int decrypt() {
        if(!encrypted)
            return (int) value;
        if (testing)
            System.out.print("Value " + value);
        value = (value % p) % 2;
        if (testing)
            System.out.println(" decrypted to " + value);

        encrypted = false;
        return (int)value;
    }

    private long randomOfBits(int numBits) {
        long randomNumber = rand.nextLong() % (long) Math.pow(2, numBits);
        return Math.abs(randomNumber);
    }
}