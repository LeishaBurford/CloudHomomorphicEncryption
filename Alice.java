import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Alice {

    public static void main(String[] args) {

        // create the files
        // Data fileZero = new Data(BigInteger.ZERO, false);
        // Data fileOne = new Data(BigInteger.ONE, false);

        // this list stores the unencrypted and encrypted Id's of the files
        HashMap<String, Long> fileIdentifiers = new HashMap<>();

        // alice encrypts the files
        SimpleData fileZero = new SimpleData(0, false);
        SimpleData fileOne = new SimpleData(1, false);

        // alice stores the id's of the files, so they may be fetched
        fileIdentifiers.put("0", fileZero.getDataID());
        fileIdentifiers.put("1", fileOne.getDataID());

        // alice encrypts the files
        fileZero.encrypt();
        fileOne.encrypt();

        // checking that id works
        // System.out.println(fileOne.encrypt());
        // System.out.println(fileZero.encrypt());
        // System.out.println(fileOne.getDataID() + " " + fileZero.getDataID());
        // System.out.println(fileOne.decrypt() + " " + fileZero.decrypt());
        // System.out.println(fileIdentifiers.toString());

        // send files to the cloud
        // alice removes these files from her system, and just keeps their identifiers
        // from this point forward, we cannot access fileOne or fileZero

        // sometime later...
        // Alice can't remember what's in the zero file, what could it be

        // fetchedFileOne = Cloud.get(zeroFileID)

        // Alice can't remember what's in the one file
        // fetchedFileZero = Cloud.get(oneFileID)

        // decyrpt, and confirm that it did what it should
        // note that confirmation is a test, not something Alice can actually do
        // TODO look into a formal test, but totally not necessary yet
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
