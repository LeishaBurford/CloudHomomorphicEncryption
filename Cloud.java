import java.util.ArrayList;

public class Cloud {

    private ArrayList<LongData> files;
    private boolean testing = true;

    public Cloud() {
        files = new ArrayList<>();
    }

    public void addFile(LongData file) {
        files.add(file);
    }

    // this function deals with the encrypted file request
    // so right now, the fileName and fileValue are the same thing
    public LongData get(long value) {
        // so here we call on all the operations to find the right data object
        return null;
    }

}
