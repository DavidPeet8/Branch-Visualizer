package com.davidpeet8;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class BranchNode implements Serializable {
    private String branchName;
    ArrayList<BranchNode> orphans;
    // Arraylist is fine here, you should not have crazy amounts of branches locally anyway,
    // any speed improvements from using a map would be insignificant
    private ArrayList<BranchNode> children;

    public BranchNode (final String branchName, ArrayList<BranchNode> orphansRef) {
        this.orphans = orphansRef;
        this.branchName = branchName;
        this.children = new ArrayList<>();
    }

    public void printFormat(){
        TerminalColors.print(TerminalColors.ANSI_CYAN, System.out, "Branch Tree: ");
        System.out.println(getName());
        final String prefix = "|";
        final String delimiter = "  |";
        printFormat(prefix, delimiter);

        TerminalColors.print(TerminalColors.ANSI_CYAN, System.out, "\nOrphans:");
        for (BranchNode node : orphans) {
            System.out.println(node.getName());
            node.printFormat(prefix, delimiter);
        }
    }

    public void printFormat(final String prefix, final String delimiter) {
        for(BranchNode node : children) {
            System.out.println(prefix + "--" + node.getName());
            node.printFormat(prefix + delimiter, delimiter);
        }
    }

    public void insertBranch(final String branchName, final String parentBranch) {
        System.err.println("Inserting attempt under " + getName() + " with parent " + parentBranch);
        if (getName().equals(parentBranch)) {
            System.err.println("Adding Top level child branch");
            addChild(branchName);
        } else {
            insertBranchRec(branchName, parentBranch);
        }

        for (BranchNode node : orphans) {
            if (node.getName().equals(parentBranch)) {
                node.addChild(branchName);
            } else {
                node.insertBranchRec(branchName, parentBranch);
            }
        }

    }

    private void insertBranchRec(final String branchName, final String parentBranch) {
        for (BranchNode node : children) {
            if (node.getName().equals(parentBranch)) {
                node.addChild(branchName);
            } else {
                node.insertBranchRec(branchName, parentBranch);
            }
        }
    }

    public void deleteBranch (final String branchName) {
        Boolean isDeleted = false;
        System.err.println("Checking normal branch tree ...");
        deleteBranchRec(branchName, isDeleted);

        if (isDeleted) return;

        System.err.println("Checking orphans ...");
        Iterator<BranchNode> iter = orphans.iterator();
        while (iter.hasNext()) {
            BranchNode curNode = iter.next();
            if (isDeleted) return;
            if (curNode.getName().equals(branchName)) {
                curNode.orphanChildren();
                iter.remove();
            } else {
                curNode.deleteBranchRec(branchName, isDeleted);
            }
        }
    }

    private void deleteBranchRec(final String branchName, Boolean isDeleted) {
        Iterator<BranchNode> iter = children.iterator();
        while (iter.hasNext()) {
            if (isDeleted) return;
            BranchNode curNode = iter.next();
            if (curNode.getName().equals(branchName)) {
                curNode.orphanChildren();
                iter.remove();
                isDeleted = true;
            } else {
                curNode.deleteBranchRec(branchName, isDeleted);
            }
        }
    }

    public void addChild(final String branchName) {
        children.add(new BranchNode(branchName, orphans));
    }

    public void orphanChildren() {
        for (BranchNode node : children) {
            orphans.add(node);
        }
    }

    public String getName() {
        return branchName;
    }
}
