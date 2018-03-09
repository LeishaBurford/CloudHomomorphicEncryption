import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class Alice {

    public static void main(String[] args) {
        // Here we simulate the local system, belonging to Alice

        // This list stores the unencrypted and encrypted Id's of the files
        // Alice uses this to access the files after they have been sent to the Cloud
        HashMap<String, BigInteger> fileIdentifiers = new HashMap<>();

        // This is the simulated cloud, where the encrypted files will be sent
        Cloud cloud = new Cloud();

        // Alice creates the files
        System.out.println("Creating file '0'");
        Data fileZero = new Data(BigInteger.ZERO, false);
        System.out.println("Creating file '1'");
        Data fileOne = new Data(BigInteger.ONE, false);

        System.out.println();

        // Alice stores the ID's of the files, so they may be fetched later
        // Note that the dataID is an encryption of 0 or 1, different from the files encrypted value
        fileIdentifiers.put("0", fileZero.getDataID());
        fileIdentifiers.put("1", fileOne.getDataID());

        // Alice encrypts the files
        System.out.println("Encrypting the files...");
        fileZero.encrypt();
        fileOne.encrypt();

        // Sending files to the cloud
        System.out.println("Sending files to the cloud\n");
        cloud.addFile(fileZero);
        cloud.addFile(fileOne);

        // Alice removes these files from her system, and just keeps their identifiers
        fileZero = null;
        fileOne = null;
        // From this point forward, we cannot access fileOne or fileZero

        // Sometime later...
        // Alice can't remember what's in the zero file, what could it be?

        // We might as well check what's in the one file too, fetch both of the files


        System.out.println("Requesting file '0' from cloud");

        Data fetchedFileZero = cloud.get(fileIdentifiers.get("0"));
        System.out.println("Encrypted contents of file '0': " + fetchedFileZero.getValue());
        System.out.println("Decrypting contents of file '0'...");
        fetchedFileZero.decrypt();
        System.out.println("Decrypted contents of file '0': " + fetchedFileZero.getValue());

        System.out.println();

        System.out.println("Requesting file '1' from cloud");
        Data fetchedFileOne = cloud.get(fileIdentifiers.get("1"));
        System.out.println("Encrypted contents of file '1': " + fetchedFileOne.getValue());
        System.out.println("Decrypting contents of file '1'...");
        fetchedFileOne.decrypt();
        System.out.println("Decrypted contents of file '1': " + fetchedFileOne.getValue());









    }
}
