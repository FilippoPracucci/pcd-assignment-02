package pcd.ass02.async;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class AnalyserVerticle extends AbstractVerticle {

    private static final String CLASS_FILE_PATH = "src/main/java/pcd/ass02/async/DependencyAnalyserLib.java";

    @Override
    public void start() {
        DependencyAnalyserLib analyser = new DependencyAnalyserLib(this);
        Future<ClassDepsReport> future = analyser.getClassDependencies(CLASS_FILE_PATH);
        future.onSuccess(res -> System.out.println(res.getReport()));
    }

}
