package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Mesuna Hashelit
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            Utils.exitWithError("Must have at least one argument");

        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":

                validateNumArgs("init", args, 1);
                Repository.initCommand();
                break;

            case "commit":
                if (args.length < 1) {
                    System.out.println("Please enter a commit message.");
                } else {
                    Repository.commitCommand(args[1]);
                }
                break;
            case "add":
                validateNumArgs("add", args, 2);
                Repository.addCommand(args[1]);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                Repository.rmCommand(args[1]);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                Repository.logCommand();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                Repository.globalLogCommand();
                break;

            case "find":
                validateNumArgs("find", args, 2);
                Repository.findCommand(args[1]);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                Repository.statusCommand();
                break;
            case "checkout":
                if (validateNumArg("checkout", args, 2)) {
                    Repository.checkoutCommand(args[1]);
                } else if (validateNumArg("checkout", args, 3)) {
                    Repository.checkoutCommand(args[1], args[2]);
                } else {
                    Repository.checkoutCommand(args[1], args[2], args[3]);
                }
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                Repository.branchCommand(args[1]);
                break;
            case "rm-branch":
                validateNumArgs("branch", args, 2);
                Repository.rmBranchCommand(args[1]);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                Repository.resetCommand(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                return;
        }

    }




    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
    public static boolean validateNumArg(String cmd, String[] args, int n) {
        if (args.length != n) {
            return false;
        }
        return true;
    }

}

