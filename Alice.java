public class Alice {

    public static void main(String[] args) {

        // create the files -- this is the goal
        Data fileZero = new Data(0, false);
        Data fileOne = new Data(1, false);

        // encrypt the files
        fileZero.encrypt();
        fileOne.encrypt();

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
        // TODO look into a formal test, but totally not necessary
        System.out.println("***" + ((0 == fileZero.decrypt()) ? "Success" : "Fail") + "***");
        System.out.println("***" + ((1 == fileOne.decrypt()) ? "Success" : "Fail") + "***");

    }
}
