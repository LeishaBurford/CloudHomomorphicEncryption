import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Alice {

    public static void main(String[] args) {
        // Here we simulate the local system, belonging to Alice

        // This list stores the unencrypted and encrypted Id's of the files
        // Alice uses this to access the files after they have been sent to the Cloud
        HashMap<String, Long> fileIdentifiers = new HashMap<>();

        // This is the simulated cloud, where the encrypted files will be sent
        Cloud cloud = new Cloud();

        // Alice creates the files
        SimpleData fileZero = new SimpleData(0, false);
        SimpleData fileOne = new SimpleData(1, false);

        // Alice stores the ID's of the files, so they may be fetched later
        // Note that the dataID is an encryption of 0 or 1, different from the files encrypted value
        fileIdentifiers.put("0", fileZero.getDataID());
        fileIdentifiers.put("1", fileOne.getDataID());

        // Alice encrypts the files
        fileZero.encrypt();
        fileOne.encrypt();

        // Sending files to the cloud
        cloud.addFile(fileZero);
        cloud.addFile(fileOne);

        // Alice removes these files from her system, and just keeps their identifiers
        fileZero = null;
        fileOne = null;
        // From this point forward, we cannot access fileOne or fileZero

        // Sometime later...
        // Alice can't remember what's in the zero file, what could it be?

        // We might as well check what's in the one file too, fetch both of the files
        SimpleData fetchedFileZero = cloud.get(fileIdentifiers.get("0"));
        SimpleData fetchedFileOne = cloud.get(fileIdentifiers.get("1"));



        // Now Alice decrypts the files
        System.out.println("Decrypting file 0...");
        fetchedFileZero.decrypt();
        System.out.println("Decrypting file 1...");
        fetchedFileOne.decrypt();

        // System.out.println("***" + ((0 == fileZero.decrypt()) ? "Success" : "Fail") + "***");
        // System.out.println("***" + ((1 == fileOne.decrypt()) ? "Success" : "Fail") + "***");


        // testing code
    //     int fails = 0;
    //     int successes = 0;
    //     System.out.println();
    //     for(int i = 0; i < 10; i++) {
    //         // encrypt the files
    //         System.out.println("Run " + i);
    //         System.out.println();
    //         System.out.println("Encrypting value 0...");
    //         fileZero.encrypt();
    //
    //         System.out.println("Encrypting value 1...");
    //         fileOne.encrypt();
    //
    //
    //         System.out.println("Decrypting value 0...");
    //         System.out.println("***" + ((0 == fileZero.decrypt()) ? "Success" : "Fail") + "***");
    //         successes += (0 == fileZero.decrypt()) ? 1 : 0;
    //         fails += (0 == fileZero.decrypt()) ? 0 : 1;
    //
    //         System.out.println("Decrypting value 1...");
    //         System.out.println("***" + ((1 == fileOne.decrypt()) ? "Success" : "Fail") + "***");
    //         successes += (1 == fileOne.decrypt()) ? 1 : 0;
    //         fails += (1 == fileOne.decrypt()) ? 0 : 1;
    //         System.out.println();
    //
    //         // fileZero = new Data(BigInteger.ZERO, false);
    //         // fileOne = new Data(BigInteger.ONE, false);
    //         fileOne = new SimpleData(1, false);
    //         fileZero = new SimpleData(0, false);
    //     }
    //
    //     System.out.println("Success: " + successes + " Fails: " + fails);
    }
}
