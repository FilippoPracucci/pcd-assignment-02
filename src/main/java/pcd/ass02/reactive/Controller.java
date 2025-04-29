package pcd.ass02.reactive;

public class Controller {

    private final MyFrame view;
    private final DependencyAnalyser analyser;

    public Controller() {
        this.analyser = new DependencyAnalyser();
        this.view = new MyFrame(this);
        this.view.startFileChooser();
    }

    public void setRootPath(final String rootPath) {
        this.analyser.setRootPath(rootPath);
        this.view.startGUI(this.analyser.getSource());
    }
}
