

import java.math.BigInteger;
import java.util.ArrayList;

public class Cloud {
    private static final BigInteger ONE = BigInteger.ONE;

    private ArrayList<Data> files;
    private boolean testing = true;


    public Cloud() {
        files = new ArrayList<>();
    }

    public void addFile(Data file) {
        files.add(file);
    }

    // this function deals with the encrypted file request
    // so right now, the fileName and fileValue are the same thing
    public Data get(BigInteger dataID) {
        // so here we call on all the operations to find the right data object
        Data a = files.get(0);
        Data b = files.get(1);

        // the circuit to fetch the specified file
        // if q then a else b
        // (q and a) or ((not q) and b)
        // (q * a) + ( ( (q * q) + (1) ) * b
        // if the decrypted result of the circuit is 1, file a is the match, otherwise it's file b
        // note that we are decrypting here, but its not the decryption of a file,
        // just the result of the circuit
        BigInteger dID = dataID;
        BigInteger result = (a.getValue().multiply(dID)).add(((dID.multiply(dID)).add(ONE)).multiply(b.getValue()));
        Data resultOfCircuit = new Data(result, true);
        System.out.println("Decrypting result of circuit");
        int r = resultOfCircuit.decrypt();
        return (r == 0) ? b : a;
    }

}
