import java.math.BigInteger;

public class Alice {

    public static void main(String[] args) {

        // create the files -- this is the goal
        Data fileZero = new Data(BigInteger.ZERO, false);
        Data fileOne = new Data(BigInteger.ONE, false);

        int fails = 0;
        int successes = 0;
        System.out.println();
        for(int i = 0; i < 10; i++) {
            // encrypt the files
            System.out.println("Run " + i);
            System.out.println();
            System.out.println("Encrypting value 0...");
            fileZero.encrypt();

            System.out.println("Encrypting value 1...");
            fileOne.encrypt();


            System.out.println("Decrypting value 0...");
            System.out.println("***" + ((0 == fileZero.decrypt()) ? "Success" : "Fail") + "***");
            successes += (0 == fileZero.decrypt()) ? 1 : 0;
            fails += (0 == fileZero.decrypt()) ? 0 : 1;

            System.out.println("Decrypting value 1...");
            System.out.println("***" + ((1 == fileOne.decrypt()) ? "Success" : "Fail") + "***");
            successes += (1 == fileOne.decrypt()) ? 1 : 0;
            fails += (1 == fileOne.decrypt()) ? 0 : 1;
            System.out.println();

            fileZero = new Data(BigInteger.ZERO, false);
            fileOne = new Data(BigInteger.ONE, false);

        }

        System.out.println("Success: " + successes + " Fails: " + fails);
        // we're doing it this way for now, cause i know the values "work"
        // Data fileZero = new Data(-2807686888l, true);
        // Data fileOne = new Data(3107316629l, true);
        //
        // fileOne.setPrivateKey(13l);
        // fileZero.setPrivateKey(13l);

        // send files to the cloud
        // alice removes these files from her system, and just keeps their identifiers
        // for now, the id is the file's encrypted value

        // sometime later...
        // Alice can't remember what's in the zero file

        // fetchedFileOne = Cloud.get(zeroFileID)

        // Alice can't remember what's in the one file
        // fetchedFileZero = Cloud.get(oneFileID)

        // decyrpt, and confirm that it did what it should
        // note that confirmation is a test, not something Alice can actually do
        // TODO look into a formal test, but totally not necessary yet
        // System.out.println("***" + ((0 == fileZero.decrypt()) ? "Success" : "Fail") + "***");
        // System.out.println("***" + ((1 == fileOne.decrypt()) ? "Success" : "Fail") + "***");

    }
}
