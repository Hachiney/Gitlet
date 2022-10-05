package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

public class Branch implements Serializable {


    private String name;
    private Map<String, String> branches;
    private String activeName;
    private String activeId;


    public Branch() {
        this.name = "b";
        this.branches = new HashMap<>();
        this.activeName = null;
        this.activeId = null;

    }

    public static void createMaster(String name, String head) {
        Branch b = getBranches();
        b.activeName = name;
        b.activeId = head;
        createBranch(name, head);
        Utils.writeContents(Repository.ACTIVE_BRANCH, name);
    }

    public static Branch getBranches() {
        Branch b = new Branch();
        if (Repository.BRANCH_FOLDER.exists()) {
            b = readObject(Utils.join(Repository.BRANCH_FOLDER, "b"), b.getClass());
        }
        return b;
    }


    public static void updateBranch(String head) {
        Branch b = getBranches();
        b.activeId = head;
        b.activeName = readContentsAsString(Repository.ACTIVE_BRANCH);
        b.branches.replace(b.activeName, b.activeId);
        b.saveBranches();
    }


    public static void createBranch(String name, String head) {
        Branch b = getBranches();
        if (b.branches.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        } else {
            b.branches.put(name, head);
        }
        b.saveBranches();
    }

    public static void setActiveBranch(Branch b, String n) {
        b.activeName = n;
        b.saveBranches();
    }


    public static Map checkoutBranch(Branch w) {
        return w.branches;
    }


    private void saveBranches() {
        File branch = Utils.join(Repository.BRANCH_FOLDER, this.name);
        try {
            branch.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(branch, this);
    }


    public static void printBranches() {
        String curActive = Utils.readContentsAsString(Repository.ACTIVE_BRANCH);
        Branch collection = getBranches();
        Map<String, String> b = collection.branches;
        if (curActive.equals("")) {
            System.out.println("*" + curActive);
        }
        for (Map.Entry<String, String> element : b.entrySet()) {
            String cur = element.getKey();
            if (!cur.equals(curActive)) {
                System.out.println(cur);
            }

        }
        System.out.println();


    }

    public static void removeBranch(String name) {
        Branch b = getBranches();
        String curBranch = Utils.readContentsAsString(Repository.ACTIVE_BRANCH);
        if (name.equals(curBranch)) {
            System.out.println("Cannot remove the current branch");
        } else if (!b.branches.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
        } else {
            b.branches.remove(name);
            b.saveBranches();
        }

    }
}
