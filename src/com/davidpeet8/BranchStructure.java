package com.davidpeet8;

import java.io.*;
import java.util.ArrayList;

public class BranchStructure implements Serializable {
    public static transient String  dataFilePath = "";
    public static transient String fileName = "/.branch_hist.txt";
    BranchNode rootNode = null;
    ArrayList<BranchNode> orphans = new ArrayList<>();

    public BranchStructure () {
        rootNode = new BranchNode("master", orphans);
    }

    private static void setDataFilePath() throws IOException, InterruptedException {
        // Get the root git directory
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "git rev-parse --show-toplevel");
        Process process = processBuilder.start();
        BufferedReader procOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        dataFilePath = procOut.readLine() + fileName;
        System.err.println("DataFilePath is: " +  dataFilePath);

        if (process.waitFor() != 0) { // Check exit code
            System.err.println("[ ERROR ]: Not currently in a git repository. Exiting");
            System.exit(1);
        }
    }

    private String getCurrentBranch() throws  IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD");
        Process proc = processBuilder.start();
        BufferedReader procOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        if (proc.waitFor() != 0) {
            System.err.println("[ ERROR ]: Not currently in a git directory");
            System.exit(1);
        }
        return procOut.readLine();
    }

    public void manipulate(String branchName, boolean isCreate) throws IOException, InterruptedException {
        // Do the manipulations, then persist them
        System.err.println("Performing requested manipulation");
        if (isCreate) {
            System.err.println("Inserting branch " + branchName + " under parent branch " + getCurrentBranch());
            rootNode.insertBranch(branchName, getCurrentBranch());
        } else {
            System.err.println("Removing branch " + branchName);
            rootNode.deleteBranch(branchName);
        }
        serialize();
    }

    public void serialize() throws  IOException {
        FileOutputStream fout = new FileOutputStream(dataFilePath);
        ObjectOutputStream oout = new ObjectOutputStream(fout);
        oout.writeObject(this);
        oout.close();
        fout.close();
        System.err.println("Serialization Complete.");
    }

    public static BranchStructure readData() throws IOException, ClassNotFoundException, InterruptedException {
        setDataFilePath();
        File file = new File(dataFilePath);
        BranchStructure struct;

        if (file.exists()) {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);

            struct = (BranchStructure) oin.readObject();
            fin.close();
            oin.close();
            return struct;
        }
        System.err.println("[ WARNING ]: history file not detected, generating default");
        struct = new BranchStructure();
        struct.serialize();
        return struct;
    }

    public void printFormat() {
        rootNode.printFormat();
    }
}
