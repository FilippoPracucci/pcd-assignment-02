package pcd.ass02.reactive;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.ass02.util.Pair;

import java.util.*;

public class Controller {

    private final View view;
    private final DependencyAnalyser analyser;

    public Controller() {
        this.analyser = new DependencyAnalyser();
        this.view = new View(this);
        this.view.startFileChooser();
    }

    public void setRootPath(final String rootPath) {
        this.analyser.setRootPath(rootPath);
        this.analyser.getSource()
                .map(report -> {
                    final List<String> tempPathElements = Arrays.asList(report.getPath().split("\\\\"));
                    final List<String> pathElements = new ArrayList<>(tempPathElements);
                    pathElements.remove(0);
                    return new Pair<>(pathElements, report.getReport());
                })
                .subscribeOn(Schedulers.io())
                .subscribe(this.view::update);
        this.view.startView(rootPath);
    }
}
