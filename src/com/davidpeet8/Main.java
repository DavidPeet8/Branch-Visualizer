package com.davidpeet8;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
	    // Check for argument c or d
        boolean isCreateMode = true;
        String branchName = "";

        if (args.length == 0 ) {
            System.err.println("[ INFO ]: Printing branch history info");
            BranchStructure.readData().printFormat();
            return;
        }

        if (args[0].equals("-c")) {
            if (args.length < 2) {
                System.err.println("[ ERROR ]: Missing a branch name. Exiting");
                return;
            } else {
                branchName = args[1];
            }
        } else if (args[0].equals("-d") || args[0].equals("-D")) {
            if (args.length < 2) {
                System.err.println("[ ERROR ]: Missing a branch to delete. Exiting");
                return;
            } else {
                isCreateMode = false;
                branchName = args[1];
            }
        } else {
            branchName = args[0];
        }

        BranchStructure data = BranchStructure.readData();

        // Do the data manipulation
        data.manipulate(branchName, isCreateMode);
        applyCommand(branchName, isCreateMode);
    }

    public static void applyCommand (String branchName, boolean isCreateMode) throws IOException, InterruptedException {
        System.err.println("Applying branch changes to git - " + (isCreateMode ? "ADDING " : "REMOVING ") + branchName);
        ProcessBuilder procBuilder;
        if (isCreateMode) {
            procBuilder = new ProcessBuilder("git", "branch", branchName);
        } else {
            procBuilder = new ProcessBuilder("git", "branch", "-D", branchName);
        }
        Process process = procBuilder.start();
        System.err.println("Exit code for applying command: " + process.waitFor());
    }
}
