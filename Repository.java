package gitlet;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Mesuna Hashelit
 */
public class Repository implements Serializable {
    /**
     *  add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    static final File COMMIT_FOLDER = join(GITLET_DIR, "commit");
    public static final File HEAD_FOLDER = join(GITLET_DIR, "HEAD");
    public static final File BRANCH_FOLDER = join(GITLET_DIR, "branch");
    public static final File STAGING_ADD = join(GITLET_DIR, "Add");
    public static final File STAGING_REMOVE = join(GITLET_DIR, "Remove");
    static final File BLOB_FOLDER = join(GITLET_DIR, "Blobs");
    public static final File ACTIVE_BRANCH = join(GITLET_DIR, "active");


    /**
     * .gitlet
     *
     *
     *
     *
     *
     *
     */

    private static String HEAD;

    public static void setupPersistence() throws IOException {
        GITLET_DIR.mkdir();
        COMMIT_FOLDER.mkdir();
        STAGING_ADD.mkdir();
        STAGING_REMOVE.mkdir();
        BLOB_FOLDER.mkdir();
        BRANCH_FOLDER.mkdir();
        if (GITLET_DIR.exists()) {
            HEAD_FOLDER.createNewFile();
            ACTIVE_BRANCH.createNewFile();
        }

    }


    public static void initCommand() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            return;
        }
        Repository.setupPersistence();
        Commit initialCommit = new Commit();
        String commitUID = sha1(serialize(initialCommit));
        initialCommit.setUID(commitUID);
        initialCommit.saveCommit();
        Utils.writeContents(HEAD_FOLDER, commitUID);
        Branch.createMaster("master", commitUID);
    }


    public static void addCommand(String fileName) throws IOException {
        String readData = "";
        if (CWD.exists()) {
            readData = Utils.readContentsAsString(Utils.join(CWD, fileName));
        } else {
            System.out.println("File does not exist.");
            return;
        }

        String dataUID = sha1(serialize(readData));
        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        String last = Commit.getDataId(Commit.fromFile(HEAD), fileName);
        Staging forAdd = Staging.fromFile("staged", STAGING_ADD);
        if (dataUID.equals(last)) {
            if (forAdd.getStorage().containsKey(fileName)) {
                forAdd.getStorage().remove(fileName);
                forAdd.saveStaging(STAGING_ADD);
            }

        } else if (forAdd.getStorage().containsKey(fileName)) {
            forAdd.getStorage().replace(fileName, dataUID);
            forAdd.saveStaging(STAGING_ADD);
            Blob blob = new Blob(dataUID, readData);
            blob.saveBlob();

        } else {
            forAdd.setStorage(fileName, dataUID);
            forAdd.saveStaging(STAGING_ADD);
            Blob blob = new Blob(dataUID, readData);
            blob.saveBlob();
        }
    }

    public static void commitCommand(String message) throws IOException {
        Staging staged = Staging.fromFile("staged", STAGING_ADD);
        Map<String, String> content = staged.getStorage();
        if (content.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        Commit newCommit = Commit.fromFile(HEAD);
        newCommit.modifyCommit(message, new Date(), HEAD);
        String commitUID = sha1(serialize(newCommit));
        newCommit.updateTracked(content);
        newCommit.setUID(commitUID);
        Utils.writeContents(HEAD_FOLDER, commitUID);
        Branch.updateBranch(commitUID);
        newCommit.saveCommit();
        staged.clearStaging(STAGING_ADD);
        staged.clearStaging(STAGING_REMOVE);
    }

    public static void rmCommand(String fileName) throws IOException {
        Staging staged = Staging.fromFile("staged", STAGING_ADD);
        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        if (staged.getStorage().containsKey(fileName)) {
            staged.getStorage().remove(fileName);
            staged.saveStaging(STAGING_ADD);
            return;
        }
        Commit curr = Commit.fromFile(HEAD);
        curr.removeFile(fileName);
    }

    public static void logCommand() {
        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        Commit cur = Commit.fromFile(HEAD);
        while (Commit.getParent(cur) != null) {
            cur.commitInfo();
            cur = Commit.fromFile(Commit.getParent(cur));
        }
        cur.commitInfo();
    }

    public static void globalLogCommand()  {
        List<String> idCollection = plainFilenamesIn(COMMIT_FOLDER);
        for (String id : idCollection) {
            Commit.fromFile(id).commitInfo();
        }
    }

    public static void findCommand(String msg) {
        boolean empty = false;
        List<String> idCollection = plainFilenamesIn(COMMIT_FOLDER);

        for (String id : idCollection) {
            Commit cur = Commit.fromFile(id);
            if (Commit.getMessage(cur).equals(msg)) {
                System.out.println(Commit.getUID(cur));
                empty = true;
            }
        }
        if (!empty) {
            System.out.println("Found no commit with that message.");
        }

    }

    public static void statusCommand() {
        System.out.println("=== Branches ===");
        Branch.printBranches();
        Staging.printStaged(STAGING_ADD);
        Staging.printStaged(STAGING_REMOVE);
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutCommand(String pre, String fileName) throws IOException {
        Commit cur;
        String fileId;
        String content;
        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        try {
            cur = Commit.fromFile(HEAD);
            fileId = Commit.getDataId(cur, fileName);
            content = Blob.getContent(fileId);
        } catch (Exception e) {
            System.out.println("FIle does not exist in that commit.");
            return;
        }

        File copy = new File(fileName);
        if (copy.exists()) {
            FileWriter fooWriter = new FileWriter(copy, false);
            fooWriter.write(content);
            fooWriter.close();
        } else {
            return;
        }

    }

    public static void checkoutCommand(String commitId, String pre, String fileName) {
        Commit cur;
        String fileId;
        String content;
        try {
            cur = Commit.fromFile(commitId);
            fileId = Commit.getDataId(cur, fileName);
            content = Blob.getContent(fileId);
        } catch (Exception e) {
            System.out.println("No commit with that id exists.");
            return;
        }
        try {
            File copy = new File(fileName);
            FileWriter fooWriter = new FileWriter(copy, false);
            fooWriter.write(content);
            fooWriter.close();

        } catch (Exception e) {
            return;
        }

    }

    public static void checkoutCommand(String branchName) throws IOException {
        String curBranch = Utils.readContentsAsString(ACTIVE_BRANCH);
        if (branchName.equals(curBranch)) {
            System.out.println("No need to checkout the current branch");
            return;
        }
        Branch allBranches = Branch.getBranches();
        Map<String, String> b = Branch.checkoutBranch(allBranches);
        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        Commit c = Commit.fromFile(HEAD);

        if (!b.containsKey(branchName)) {
            System.out.println("No such branch exists.");
        } else { // if branch exists in branch collection
            String branchNameId = b.get(branchName);

            Map<String, String> atBranch = Commit.getFiles(Commit.fromFile(branchNameId));
            Map<String, String> atCur = Commit.getFiles(c);
            for (Map.Entry<String, String> element : atBranch.entrySet()) {
                String prevData = Blob.getContent(element.getValue());
                if (atCur.containsKey(element.getKey())) {
                    File copy = new File(element.getKey());
                    if (copy.exists()) {
                        FileWriter fooWriter = new FileWriter(copy, false);
                        fooWriter.write(prevData);
                        fooWriter.close();
                    } else {
                        return;
                    }

                } else {
                    // create a file
                    File newFile = join(CWD, element.getKey());
                    newFile.createNewFile();
                    File copy = new File(String.valueOf(newFile));
                    FileWriter fooWriter = new FileWriter(copy, false);
                    fooWriter.write(prevData);
                    fooWriter.close();
                }
            }

            for (Map.Entry<String, String> element : atCur.entrySet()) {
                if (!atBranch.containsKey(element.getKey())) {
                    rmCommand(element.getKey());
                }
            }

            Branch.setActiveBranch(allBranches, branchName);
            Utils.writeContents(ACTIVE_BRANCH, branchName);
            Utils.writeContents(HEAD_FOLDER, branchNameId);
            Staging.clearStaging(STAGING_ADD);
            Staging.clearStaging(STAGING_REMOVE);
        }

    }

    public static void branchCommand(String branch) {
        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        Branch.createBranch(branch, HEAD);
    }


    public static void rmBranchCommand(String branchName) {
        Branch.removeBranch(branchName);
    }

    public static void resetCommand(String commitId) throws IOException {
        Commit prev = Commit.fromFile(commitId);
        Map<String, String> p = Commit.getFiles(prev);
        if (p.isEmpty()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        HEAD = Utils.readContentsAsString(HEAD_FOLDER);
        Commit cur = Commit.fromFile(HEAD);
        Map<String, String> c = Commit.getFiles(cur);
        for (Map.Entry<String, String> element : c.entrySet()) {

            if (!p.containsKey(element.getKey())) {
                rmCommand(element.getKey());
            } else {
                String storedData = Blob.getContent(element.getValue());
                File copy = new File(element.getKey());
                if (copy.exists()) {
                    FileWriter fooWriter = new FileWriter(copy, false);
                    fooWriter.write(storedData);
                    fooWriter.close();
                }
            }
        }
        for (Map.Entry<String, String> element : p.entrySet()) {
            if (!c.containsKey(element.getKey())) {
                String storedData = Blob.getContent(element.getValue());
                File newFile = join(CWD, String.valueOf(element.getKey()));
                newFile.createNewFile();
                File copy = new File(String.valueOf(newFile));
                FileWriter fooWriter = new FileWriter(copy, false);
                fooWriter.write(storedData);
                fooWriter.close();

            }
        }

        Utils.writeContents(HEAD_FOLDER, commitId);
        Staging.clearStaging(STAGING_ADD);
        Staging.clearStaging(STAGING_REMOVE);
    }

}

