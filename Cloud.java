

import java.math.BigInteger;
import java.util.ArrayList;

public class Cloud {
    // private static final BigInteger ONE = BigInteger.ONE;

    // the circuit needs a reference to the value 1
    private static final Data ONE = new Data(BigInteger.ONE, false);
    private ArrayList<Data> files;
    private boolean testing = true;


    public Cloud() {
        files = new ArrayList<>();
    }

    public void addFile(Data file) {
        files.add(file);
    }

    // this function deals with the encrypted file request
    public Data get(BigInteger dataID) {
        // so here we call on all the operations to find the right data object
        Data a = files.get(0);
        Data b = files.get(1);

        ONE.encrypt();

        // the circuit to fetch the specified file
        // if q then a else b
        // (q and a) or ((not q) and b)
        // (q * a) + ( ( (q * q) + (1) ) * b
        // if the decrypted result of the circuit is 1, file a is the match, otherwise it's file b
        // note that we are decrypting here, but its not the decryption of a file,
        // just the result of the circuit
        // TODO, get value should be get id
        BigInteger dID = dataID;
        BigInteger result = (a.getValue().multiply(dID)).add(((dID.multiply(dID)).add(ONE.getValue())).multiply(b.getValue()));
        Data resultOfCircuit = new Data(result, true);


        // TODO maybe add a safe decrypt, which will only decrypt a file if the id is unknown
        int r = resultOfCircuit.decrypt();
        return (r == 0) ? b : a;
    }

}
