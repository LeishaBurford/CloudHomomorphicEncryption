import sun.java2d.pipe.SpanShapeRenderer;

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

        // perform addition
        // SimpleData aXORb = a.add(b);
        SimpleData aANDb = a.multiply(b);
        SimpleData xANDb = aANDb.multiply(b);
        return xANDb;
    }

}
