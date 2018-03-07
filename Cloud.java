

import java.util.ArrayList;

public class Cloud {

    private ArrayList<SimpleData> files;
    private boolean testing = true;


    public Cloud() {
        files = new ArrayList<>();
    }

    public void addFile(SimpleData file) {
        files.add(file);
    }

    // this function deals with the encrypted file request
    // so right now, the fileName and fileValue are the same thing
    public SimpleData get(long dataID) {
        // so here we call on all the operations to find the right data object
        SimpleData a = files.get(0);
        SimpleData b = files.get(1);

        // the circuit to fetch the specified file
        // if q then a else b
        // (q and a) or ((not q) and b)
        // (q * a) + ( ( (q * q) + (1) ) * b
        // if the decrypted result of the circuit is 1, file a is the match, otherwise it's file b
        // note that we are decrypting here, but its not the decryption of a file,
        // just the result of the circuit
        long result = (a.getValue() * dataID) + ( ( (dataID * dataID) + 1) * b.getValue());
        SimpleData resultOfCircuit = new SimpleData(result, true);
        result = resultOfCircuit.decrypt();
        return (result == 0) ? b : a;
    }

}
