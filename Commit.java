package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author Mesuna Hashelit
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    //static final File CWD = new File(System.getProperty("user.dir"));




    /** The message of this Commit. */
    private String message;


    private String parent;



    /** the timestamp for this commit */
    private String timestamp;

    private Map<String, String> savedData;

    private String UID;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy  Z");


    /** something with the files I track */



    /** make the initial commit .... constructor*/
    public Commit() {
        this.message = "initial commit";
        this.timestamp = dateFormat.format(new Date(0));
        this.parent = null;
        this.savedData = new HashMap<>();
        this.UID = null;
    }

    public void modifyCommit(String msg, Date d, String p) {
        this.message = msg;
        this.timestamp = dateFormat.format(new Date());
        this.parent = p;

    }

    public void setUID(String u) {
        this.UID = u;
    }

    public static String getUID(Commit c) {
        return c.UID;
    }


    public static String getMessage(Commit c) {
        return c.message;
    }
    public static String getDataId(Commit c, String file) {
        return c.savedData.get(file);
    }

    public static String getParent(Commit c) {
        return c.parent;
    }

    public static Map getFiles(Commit c) {
        return c.savedData;
    }



    public static Commit fromFile(String id) {
        Commit c = new Commit();
        if (Repository.COMMIT_FOLDER.exists()) {
            c = readObject(Utils.join(Repository.COMMIT_FOLDER, id), c.getClass());
        }
        return c;
    }



    public void updateTracked(Map s) {
        Map<String, String> content = s;
        for (Map.Entry<String, String> element : content.entrySet()) {
            this.savedData.put(element.getKey(), element.getValue());
        }

    }


    public void saveCommit() {
        File commits = Utils.join(Repository.COMMIT_FOLDER, this.UID);
        try {
            commits.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(commits, this);
    }

    public void removeFile(String fileName) throws IOException {
        if (this.savedData.containsKey(fileName)) {
            String remID = this.savedData.get(fileName);
            String removed = this.savedData.remove(fileName);
            Staging r = Staging.fromFile("staged", Repository.STAGING_REMOVE);
            r.insertStaging(fileName, removed, Repository.STAGING_REMOVE);
            File file = new File(System.getProperty("user.dir") + '\\' + fileName);
            file.delete();
            String commitUID = sha1(serialize(this));
            this.setUID(commitUID);
            Utils.writeContents(Repository.HEAD_FOLDER, commitUID);
            Branch.updateBranch(commitUID);
            this.saveCommit();
        } else {
            System.out.println("No reason to remove the file");
            return;
        }
    }

    public void commitInfo() {
        System.out.println("===");
        System.out.println("commit " + this.UID);
        // if merge, apply it here
        System.out.println("Date: " + this.timestamp);
        System.out.println(this.message);
        System.out.println();

    }



}
