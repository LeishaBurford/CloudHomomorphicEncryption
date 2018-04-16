import javafx.util.Pair;

import java.math.BigInteger;
import java.util.HashMap;

public class Alice4Files {

    public static void main(String[] args) {
        // Here we simulate the local system, belonging to Alice

        // This list stores the unencrypted and encrypted Id's of the files
        // Alice uses this to access the files after they have been sent to the Cloud
        HashMap<String, Pair<BigInteger, BigInteger>> fileIdentifiers = new HashMap<>();

        // This is the simulated cloud, where the encrypted files will be sent
        Cloud cloud = new Cloud();

        // Alice creates the files
        // TODO this should be in an array or somthing
        System.out.println("Creating file '00'");
        Data fileZero1 = new Data(BigInteger.ZERO, false);
        Data fileZero2 = new Data(BigInteger.ZERO, false);
        System.out.println("Creating file '01'");
        Data fileOne1 = new Data(BigInteger.ZERO, false);
        Data fileOne2 = new Data(BigInteger.ONE, false);
        System.out.println("Creating file '10'");
        Data fileTwo1 = new Data(BigInteger.ONE, false);
        Data fileTwo2 = new Data(BigInteger.ZERO, false);
        System.out.println("Creating file '11'");
        Data fileThree1 = new Data(BigInteger.ONE, false);
        Data fileThree2 = new Data(BigInteger.ONE, false);


        System.out.println();

        // Alice stores the ID's of the files, so they may be fetched later
        // Note that the dataID is an encryption of 0 or 1, different from the files encrypted value
        // TODO could have less repeated code
        Pair encryptedData = new Pair(fileZero1.getDataID(), fileZero2.getDataID());
        fileIdentifiers.put("00", encryptedData);

        encryptedData = new Pair(fileOne1.getDataID(), fileOne2.getDataID());
        fileIdentifiers.put("01", encryptedData);

        encryptedData = new Pair(fileTwo1.getDataID(), fileTwo2.getDataID());
        fileIdentifiers.put("10", encryptedData);

        encryptedData = new Pair(fileThree1.getDataID(), fileThree2.getDataID());
        fileIdentifiers.put("11", encryptedData);


        Data[] allTheBits = new Data[]{fileZero1, fileZero2, fileOne1, fileOne2, fileTwo1, fileTwo2, fileThree1, fileThree2};
        // Alice encrypts the files
        System.out.println("Encrypting the files...");
        for(int file = 0; file < allTheBits.length; file++) {

            allTheBits[file].encrypt();
            System.out.println(allTheBits[file].getDataID() + " " + allTheBits[file].getValue());
        }


        // Sending files to the cloud
        System.out.println("Sending files to the cloud\n");

        for(int file = 0; file < allTheBits.length - 1; file += 2) {
            cloud.addBigFile(new Data[]{allTheBits[file], allTheBits[file + 1]});
            System.out.println("Sent file " + allTheBits[file].getDataID() + " " + allTheBits[file + 1].getDataID() );
        }


        // Alice removes these files from her system, and just keeps their identifiers
        allTheBits = null;
        // From this point forward, we cannot access the files directly, we must request them from the cloud
        // with their encrypted ID's

        // Sometime later...
        // Alice can't remember what's in the any of the files

        // fetch all the files, for completeness
        // TODO make this more compact (less repetitive...)
        System.out.println("Requesting file '00' from cloud");

        Data[] fetchedFile = cloud.get4Files(fileIdentifiers.get("00").getKey(), fileIdentifiers.get("00").getValue());
        // System.out.println("Encrypted contents of file '00': " + fetchedFile.getValue());
        System.out.println("Decrypting contents of file '00'...");
        fetchedFile[0].decrypt();
        fetchedFile[1].decrypt();
        System.out.println("Decrypted contents of file '00': " + fetchedFile[0].getValue() +fetchedFile[1].getValue());

        System.out.println();

        System.out.println("Requesting file '01' from cloud");

        fetchedFile = cloud.get4Files(fileIdentifiers.get("01").getKey(), fileIdentifiers.get("01").getValue());
        // System.out.println("Encrypted contents of file '00': " + fetchedFile.getValue());
        System.out.println("Decrypting contents of file '01'...");
        fetchedFile[0].decrypt();
        fetchedFile[1].decrypt();
        System.out.println("Decrypted contents of file '01': " + fetchedFile[0].getValue() +fetchedFile[1].getValue());

        System.out.println();

        System.out.println("Requesting file '10' from cloud");

        fetchedFile = cloud.get4Files(fileIdentifiers.get("10").getKey(), fileIdentifiers.get("10").getValue());
        // System.out.println("Encrypted contents of file '00': " + fetchedFile.getValue());
        System.out.println("Decrypting contents of file '10'...");
        fetchedFile[0].decrypt();
        fetchedFile[1].decrypt();
        System.out.println("Decrypted contents of file '10': " + fetchedFile[0].getValue() +fetchedFile[1].getValue());

        System.out.println();

        System.out.println("Requesting file '11' from cloud");

        fetchedFile = cloud.get4Files(fileIdentifiers.get("11").getKey(), fileIdentifiers.get("11").getValue());
        // System.out.println("Encrypted contents of file '00': " + fetchedFile.getValue());
        System.out.println("Decrypting contents of file '11'...");
        fetchedFile[0].decrypt();
        fetchedFile[1].decrypt();
        System.out.println("Decrypted contents of file '11': " + fetchedFile[0].getValue() +fetchedFile[1].getValue());

        System.out.println();
    }

}
