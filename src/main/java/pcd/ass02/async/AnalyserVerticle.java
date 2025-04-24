package pcd.ass02.async;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class AnalyserVerticle extends AbstractVerticle {

    private static final String CLASS_FILE_PATH = "src/main/java/pcd/ass02/async/DependencyAnalyserLib.java";
    private static final String PACKAGE_PATH = "src/main/java/pcd/ass02/async/";
    private static final String PROJECT_PATH = "src/main/java/";

    @Override
    public void start() {
        DependencyAnalyserLib analyser = new DependencyAnalyserLib(this);
        Future<ClassDepsReport> futureClass = analyser.getClassDependencies(CLASS_FILE_PATH);
        futureClass.onSuccess(res -> System.out.println(res.getReport() + "\n"));
        futureClass.onFailure(e -> System.out.println(e.getMessage()));

        Future<PackageDepsReport> futurePackage = analyser.getPackageDependencies(PACKAGE_PATH);
        futurePackage.onSuccess(res -> {
            System.out.println(res.getAllReports());
            System.out.println(res.getPackageReport() + "\n");
        });
        futurePackage.onFailure(e -> System.out.println(e.getMessage()));

        Future<ProjectDepsReport> futureProject = analyser.getProjectDependencies(PROJECT_PATH);
        futureProject.onSuccess(res -> {
            System.out.println(res.getAllReports());
            System.out.println(res.getProjectReport());
        });
        futureProject.onFailure(e -> System.out.println(e.getMessage()));
    }

}
