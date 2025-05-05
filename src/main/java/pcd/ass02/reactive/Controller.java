package pcd.ass02.reactive;

import io.reactivex.rxjava3.schedulers.Schedulers;

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
        this.analyser.getSource()
                .subscribeOn(Schedulers.io())
                .subscribe(this.view::update);
        this.view.startGUI(rootPath);
    }
}
