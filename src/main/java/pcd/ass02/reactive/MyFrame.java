package pcd.ass02.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class MyFrame extends JFrame {

    private final Controller controller;
    private DefaultMutableTreeNode rootNode;

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

    public void startGUI() {
        //setSize(150,60);

        // Creating the root node
        /*DefaultMutableTreeNode root
                = new DefaultMutableTreeNode("Root");

        // Creating child nodes
        DefaultMutableTreeNode parent1
                = new DefaultMutableTreeNode("Parent 1");
        DefaultMutableTreeNode child1_1
                = new DefaultMutableTreeNode("Child 1.1");
        DefaultMutableTreeNode child1_2
                = new DefaultMutableTreeNode("Child 1.2");

        // Adding child nodes to the parent1
        parent1.add(child1_1);
        parent1.add(child1_2);

        root.add(parent1);*/

        // Creating the JTree
        this.rootNode = new DefaultMutableTreeNode("root");
        JTree tree = new JTree(this.rootNode);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        this.getContentPane().add(tree);
        this.pack();
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
        });
        setVisible(true);
    }

    public void updateTree(final ClassDepsReport classDepsReport) {
        List<String> nodes = Arrays.asList(classDepsReport.getPath().split("\\\\"));
        nodes.remove(0);
        DefaultMutableTreeNode prevTreeNode = null;
        for (final String node: nodes) {
            if (prevTreeNode == null) {
                prevTreeNode = new DefaultMutableTreeNode(node);
                this.rootNode.add(prevTreeNode);
                continue;
            }
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
            prevTreeNode.add(treeNode);
        }
        this.repaint(); // TODO fix
    }
}
