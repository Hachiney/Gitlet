package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;


import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

public class Blob implements Serializable {

    private String UID;
    private String content;

    public  Blob() {
        this.UID = null;
        this.content = null;
    }


    public  Blob(String i, String c) {
        this.UID = i;
        this.content = c;
    }

    public static Blob fromFile(String id) {
        Blob b = new Blob();
        b = readObject(Utils.join(Repository.BLOB_FOLDER, id), b.getClass());

        return b;
    }

    public void saveBlob() {
        File b = Utils.join(Repository.BLOB_FOLDER, this.UID);
        try {
            b.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(b, this);
    }

    public static String  getContent(String id) {
        Blob b = fromFile(id);
        return b.content;

    }


}
