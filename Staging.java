package gitlet;



import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;

public class Staging implements Serializable {

    private Map<String, String> storage;
    private String name;


    public Staging() {
        this.name = "staged";
        this.storage = new TreeMap<>();
    }

    public void setStorage(String n, String i) {
        this.storage.put(n, i);
    }
    public Map getStorage() {
        return this.storage;
    }

    public static Staging fromFile(String name, File folder) {
        Staging s = new Staging();
        if (folder.exists()) {
            s = readObject(Utils.join(folder, name), s.getClass());
        }
        return s;
    }

    public void saveStaging(File folder) throws IOException {
        File s = Utils.join(folder, this.name);
        if (s.exists()) {
            s.createNewFile();
        }
        writeObject(s, this);
    }


    public void insertStaging(String n, String id, File folder) throws IOException {
        this.storage.put(n, id);
        this.saveStaging(folder);
    }

    public static void clearStaging(File folder) throws IOException {
        Staging s = fromFile("staged", folder);
        s.storage.clear();
        s.saveStaging(folder);

    }

    public static void printStaged(File folder) {
        Staging s = fromFile("staged", folder);
        Map<String, String> content = s.getStorage();

        if (folder == Repository.STAGING_ADD) {
            System.out.println("=== Staged Files ===");
        } else {
            System.out.println("=== Removed Files ===");
        }

        for (Map.Entry<String, String> element : content.entrySet()) {
            System.out.println(element.getKey());
        }
        System.out.println();

    }


}
