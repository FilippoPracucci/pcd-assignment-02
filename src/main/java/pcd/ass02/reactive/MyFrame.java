package pcd.ass02.reactive;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyFrame extends JFrame {

    private final Controller controller;
    private DefaultMutableTreeNode rootNode;
    private JTree tree;
    private JScrollPane scrollPane;

    public MyFrame(final Controller controller) {
        super("GUI");
        this.controller = controller;
    }

    public void startFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(".")); // start at application current directory
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File rootFolder = fc.getSelectedFile();
            this.controller.setRootPath(rootFolder.getPath());
        }
    }

    public void startGUI(final String rootPath) {
        this.rootNode = new DefaultMutableTreeNode(Paths.get(rootPath).getFileName());
        this.tree = new JTree(this.rootNode);
        this.scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.getContentPane().add(this.scrollPane);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void updateTree(final ClassDepsReport classDepsReport) {
        final List<String> tempNodes = Arrays.asList(classDepsReport.getPath().split("\\\\"));
        final List<String> nodes = new ArrayList<>(tempNodes);
        nodes.remove(0);
        final TreeNode packageNode = visitTree(nodes, this.rootNode);
        System.out.println("PackageNode: " + packageNode);
        DefaultMutableTreeNode prevTreeNode = null;
        DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
        for (final String node: nodes) {
            if (prevTreeNode == null) {
                prevTreeNode = new DefaultMutableTreeNode(node);
                if (packageNode == null) {
                    this.rootNode.add(prevTreeNode);
                } else {
                    ((DefaultMutableTreeNode) packageNode).add(prevTreeNode);
                }
                continue;
            }
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
            prevTreeNode.add(treeNode);
            prevTreeNode = treeNode;
        }
        model.reload(this.rootNode);
        for (int i = 0; i < this.tree.getRowCount(); i++) {
            this.tree.expandRow(i);
        }
        this.getContentPane().removeAll();
        this.getContentPane().add(this.scrollPane);
        this.pack();
        this.revalidate();
        this.repaint();
    }

    private TreeNode visitTree(final List<String> nodes, final TreeNode currentNode) {
        for (int i = 0; i < currentNode.getChildCount(); i++) {
            System.out.println("CURRENT_NODE: " + currentNode);
            final TreeNode child = currentNode.getChildAt(i);
            System.out.println("NODES: " + nodes);
            System.out.println("Nodes[0]: " + nodes.get(0) + " --- Child: " + child);
            if (nodes.get(0).equals(child.toString())) {
                System.out.println("[MATCH] Nodes[0]: " + nodes.get(0) + " --- Child: " + child);
                nodes.remove(0);
                TreeNode visited = visitTree(nodes, child);
                return visited == null ? child : visited;
            }
        }
        return null;
    }
}
