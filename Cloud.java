

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;

public class Cloud {
    // private static final BigInteger ONE = BigInteger.ONE;

    // the circuit needs a reference to the value 1
    private static final Data ONE = new Data(BigInteger.ONE, false);
    private ArrayList<Data> files;
    private ArrayList<Data[]> bigFiles;
    private boolean testing = true;


    public Cloud() {
        files = new ArrayList<>();
        bigFiles = new ArrayList<>();
    }

    public void addFile(Data file) {
        files.add(file);
    }

    public void addBigFile(Data[] file) {
        bigFiles.add(file);
    }

    // this function deals with the encrypted file request
    public Data get(BigInteger dID) {
        // so here we call on all the operations to find the right data object
        Data a = files.get(0);
        Data b = files.get(1);

        System.out.println("In the cloud: " + a.getDataID() + " " + b.getDataID() + " " + dID);
        // for reference
        ONE.encrypt();

        // the circuit to fetch the specified file
        // if q then a else b
        // (q and a) or ((not q) and b)
        // (q * a) + ( ( (q * q) + (1) ) * b
        // if the decrypted result of the circuit is 1, file a is the match, otherwise it's file b
        // note that we are decrypting here, but its not the decryption of a file,
        // just the result of the circuit
        // TODO, get value should be get id

        BigInteger result = (a.getValue().multiply(dID)).add(((dID.multiply(dID)).add(ONE.getValue())).multiply(b.getValue()));
        Data resultOfCircuit = new Data(result, true);


        // TODO maybe add a safe decrypt, which will only decrypt a file if the id is unknown
        int r = resultOfCircuit.decrypt();
        return (r == 0) ? b : a;
    }

    // for now, query is sent bit by bit, TODO change this if it works
    public Data[] get4Files(BigInteger dataID1, BigInteger dataID2) {
        Data[] a = bigFiles.get(0);
        Data[] b = bigFiles.get(1);
        Data[] c = bigFiles.get(2);
        Data[] d = bigFiles.get(3);

        // relevant Files stores the files that are compared as indexes 0, 1 are compared and 2 and 3
        ArrayList<Data[]> relevantFiles = new ArrayList<>(); // size is half of number of files
        // if a xor b == 0 then they have the same most significant bit, so use a with c or d
        // else use with b
        BigInteger fileSelector = a[0].getValue().add(b[0].getValue());
        Data circuitResult = new Data(fileSelector, true);
        int r = circuitResult.decrypt();
        // comparison hold the first bits of each of the data objects to be compared

        Data[] comparison1 = new Data[2];
        Data[] comparison2 = new Data[2];
        // set the comparisons for the circuits
        if (r == 0) {
            // pair a with c
            // b with d
            comparison1[0] = a[0];
            comparison1[1] = c[0];
            comparison2[0] = b[0];
            comparison2[1] = d[0];

            // also keep track of the entire file
            relevantFiles.add(a);
            relevantFiles.add(c);
            relevantFiles.add(b);
            relevantFiles.add(d);

        } else {
            // pair a with b
            // c with d
            comparison1[0] = a[1];
            comparison1[1] = b[1];
            comparison2[0] = c[1];
            comparison2[1] = d[1];
            // also keep track of the entire file
            relevantFiles.add(a);
            relevantFiles.add(b);
            relevantFiles.add(c);
            relevantFiles.add(d);
        }

        // now do the encryption with each of the pairs
        // first pair
        BigInteger x = comparison1[0].getDataID();
        BigInteger y = comparison1[1].getDataID();
        fileSelector = (x.multiply(dataID1)).add(((dataID1.multiply(dataID1)).add(ONE.getValue())).multiply(y));
        circuitResult = new Data(fileSelector, true);
        r = circuitResult.decrypt();
        // this should be the file with a matching msb
        Data[] fileSelection1 =  (r == 0) ? relevantFiles.get(1) : relevantFiles.get(0);

        // second pair
        x = comparison2[0].getDataID();
        y = comparison2[1].getDataID();
        fileSelector = (x.multiply(dataID1)).add(((dataID1.multiply(dataID1)).add(ONE.getValue())).multiply(y));
        circuitResult = new Data(fileSelector, true);
        r = circuitResult.decrypt();
        // this should be the file with a matching msb
        Data[] fileSelection2 =  (r == 0) ? relevantFiles.get(3) : relevantFiles.get(2);
        // ^ ahhh! repeated code!!!

        // so now we have 2 different files, they have the same msb, now we need to decide between the two
        // basically just do get() as above, where the query is the second bit
        x = fileSelection1[1].getDataID();
        y = fileSelection2[1].getDataID();
        fileSelector = (x.multiply(dataID2)).add(((dataID2.multiply(dataID2)).add(ONE.getValue())).multiply(y));

        // now make fileSelector into a Data object so we can get the result of the circuit,
        // note we are not decrypting a file, just the circuit
        circuitResult = new Data(fileSelector, true);
        r = circuitResult.decrypt();

        return (r == 0) ? fileSelection2 : fileSelection1;
        // return null;
    }

}
