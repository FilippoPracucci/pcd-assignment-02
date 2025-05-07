package pcd.ass02.reactive;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import pcd.ass02.util.Pair;

public class View extends JFrame {

    private static final String DEPS_FOUND_LABEL = "Dependencies found: ";
    private static final String FILES_ANALYSED_LABEL = "Class/interfaces analysed: ";
    private static final int COLUMNS = 5;
    private static final String INITIAL_VALUE = "0";

    private final Controller controller;
    private DefaultMutableTreeNode rootNode;
    private JTree tree;
    private JScrollPane scrollPane;
    private final JPanel northPanel;
    private final JLabel depsFoundLabel;
    private final JLabel filesAnalysedLabel;
    private final JTextField depsFoundField;
    private final JTextField filesAnalysedField;

    public View(final Controller controller) {
        super("GUI");
        this.controller = controller;
        this.northPanel = new JPanel(new FlowLayout());
        this.depsFoundLabel = new JLabel(DEPS_FOUND_LABEL);
        this.filesAnalysedLabel = new JLabel(FILES_ANALYSED_LABEL);
        this.depsFoundField = new JTextField(INITIAL_VALUE, COLUMNS);
        this.filesAnalysedField = new JTextField(INITIAL_VALUE, COLUMNS);
    }

    public void startFileChooser() {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(".")); // start at application current directory
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File rootFolder = fc.getSelectedFile();
            this.controller.setRootPath(rootFolder.getPath());
        }
    }

    public void startView(final String rootPath) {
        this.getContentPane().setLayout(new BorderLayout());
        this.northPanel.add(this.filesAnalysedLabel);
        this.northPanel.add(this.filesAnalysedField);
        this.northPanel.add(this.depsFoundLabel);
        this.northPanel.add(this.depsFoundField);
        this.depsFoundField.setEditable(false);
        this.filesAnalysedField.setEditable(false);
        this.getContentPane().add(northPanel, BorderLayout.NORTH);
        this.rootNode = new DefaultMutableTreeNode(Paths.get(rootPath).getFileName());
        this.tree = new JTree(this.rootNode);
        this.scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.getContentPane().add(this.scrollPane, BorderLayout.CENTER);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Updates the view.
     * @param pair the pair of the class path elements and the class dependencies
     */
    public void update(final Pair<List<String>, Set<String>> pair) {
        this.updateFields(pair.getY().size());
        this.updateTree(pair.getX(), pair.getY());
        this.getContentPane().removeAll();
        this.getContentPane().add(this.northPanel, BorderLayout.NORTH);
        this.getContentPane().add(this.scrollPane, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void updateTree(final List<String> nodes, final Set<String> classDeps) {
        final TreeNode packageNode = getPackageNode(nodes, this.rootNode);
        DefaultMutableTreeNode prevTreeNode = null;
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
        for (final String dep: classDeps) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(dep);
            prevTreeNode.add(treeNode);
        }
        final DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
        model.reload(this.rootNode);
        for (int i = 0; i < this.tree.getRowCount(); i++) {
            this.tree.expandRow(i);
        }
    }

    private void updateFields(final int depsFound) {
        this.filesAnalysedField.setText(String.valueOf(Integer.parseInt(this.filesAnalysedField.getText()) + 1));
        this.depsFoundField.setText(String.valueOf(Integer.parseInt(this.depsFoundField.getText()) + depsFound));
    }

    private TreeNode getPackageNode(final List<String> nodes, final TreeNode currentNode) {
        for (int i = 0; i < currentNode.getChildCount(); i++) {
            final TreeNode child = currentNode.getChildAt(i);
            if (nodes.get(0).equals(child.toString())) {
                nodes.remove(0);
                TreeNode visited = getPackageNode(nodes, child);
                return visited == null ? child : visited;
            }
        }
        return null;
    }
}
